import { useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { setTokens } from "../api/client";
import { useAuth } from "../context/AuthContext";

export default function OAuthCallback() {
  const [params] = useSearchParams();
  const navigate = useNavigate();
  const { refreshProfile } = useAuth();

  useEffect(() => {
    const accessToken = params.get("accessToken");
    const refreshToken = params.get("refreshToken");

    (async () => {
      if (accessToken && refreshToken) {
        setTokens({ accessToken, refreshToken });
        await refreshProfile();
        navigate("/", { replace: true });
      } else {
        navigate("/login", { replace: true });
      }
    })();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <div className="flex min-h-screen items-center justify-center">
      <div className="h-8 w-8 animate-spin rounded-full border-2 border-marigold border-t-transparent" />
    </div>
  );
}
