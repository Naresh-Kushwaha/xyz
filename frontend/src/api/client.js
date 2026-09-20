import axios from "axios";

const BASE_URL = import.meta.env.VITE_API_BASE_URL || "/api";

export const client = axios.create({ baseURL: BASE_URL });

function getTokens() {
  return {
    accessToken: localStorage.getItem("accessToken"),
    refreshToken: localStorage.getItem("refreshToken"),
  };
}

export function setTokens({ accessToken, refreshToken }) {
  if (accessToken) localStorage.setItem("accessToken", accessToken);
  if (refreshToken) localStorage.setItem("refreshToken", refreshToken);
}

export function clearTokens() {
  localStorage.removeItem("accessToken");
  localStorage.removeItem("refreshToken");
}

client.interceptors.request.use((config) => {
  const { accessToken } = getTokens();
  if (accessToken) {
    config.headers.Authorization = `Bearer ${accessToken}`;
  }
  return config;
});

// Single-flight refresh so concurrent 401s don't each trigger their own refresh call.
let refreshPromise = null;

client.interceptors.response.use(
  (response) => response,
  async (error) => {
    const original = error.config;
    const status = error.response?.status;

    if (status === 401 && !original._retry && !original.url?.includes("/auth/")) {
      original._retry = true;
      const { refreshToken } = getTokens();

      if (!refreshToken) {
        clearTokens();
        return Promise.reject(error);
      }

      try {
        if (!refreshPromise) {
          refreshPromise = axios
            .post(`${BASE_URL}/auth/refresh`, { refreshToken })
            .finally(() => {
              refreshPromise = null;
            });
        }
        const { data } = await refreshPromise;
        setTokens(data.data);
        original.headers.Authorization = `Bearer ${data.data.accessToken}`;
        return client(original);
      } catch (refreshError) {
        clearTokens();
        window.location.href = "/login";
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

// Every backend response is wrapped as { success, message, data, error } -- unwrap it here
// so call sites just get the payload (or a thrown Error with a readable message).
export async function unwrap(promise) {
  try {
    const { data } = await promise;
    return data.data;
  } catch (error) {
    const message =
      error.response?.data?.error ||
      error.response?.data?.message ||
      error.message ||
      "Something went wrong";
    throw new Error(message);
  }
}
