import { client, unwrap, setTokens, clearTokens } from "./client";

export function register({ email, password, firstName }) {
  return unwrap(client.post("/auth/register", { email, password, firstName })).then((data) => {
    setTokens(data);
    return data;
  });
}

export function login({ email, password }) {
  return unwrap(client.post("/auth/login", { email, password })).then((data) => {
    setTokens(data);
    return data;
  });
}

export async function logout() {
  try {
    await client.post("/auth/logout");
  } finally {
    clearTokens();
  }
}

export function sendPhoneOtp(phoneNumber) {
  return unwrap(client.post("/auth/phone/send-otp", { phoneNumber }));
}

export function verifyPhoneOtp(phoneNumber, code) {
  return unwrap(client.post("/auth/phone/verify-otp", { phoneNumber, code }));
}

export function googleLoginUrl() {
  // This has to be an ABSOLUTE url to the backend, not a path that goes through
  // the Vite dev proxy: clicking this is a full browser navigation (not an axios
  // call), and Vite's proxy in vite.config.js only forwards /api and /ws -- it
  // never sees a request to /oauth2/authorization/google, so a relative URL here
  // would silently hit the frontend's own dev server instead of the backend.
  const backendUrl = import.meta.env.VITE_BACKEND_URL || "http://localhost:8080";
  return `${backendUrl}/oauth2/authorization/google`;
}
