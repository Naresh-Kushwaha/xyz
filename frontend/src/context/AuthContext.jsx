import { createContext, useContext, useEffect, useState, useCallback } from "react";
import * as authApi from "../api/users";
import { clearTokens } from "../api/client";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);

  const refreshProfile = useCallback(async () => {
    const token = localStorage.getItem("accessToken");
    if (!token) {
      setProfile(null);
      setLoading(false);
      return;
    }
    try {
      const me = await authApi.getMyProfile();
      setProfile(me);
    } catch {
      setProfile(null);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    refreshProfile();
  }, [refreshProfile]);

  const signOut = useCallback(() => {
    clearTokens();
    setProfile(null);
  }, []);

  return (
    <AuthContext.Provider value={{ profile, setProfile, loading, refreshProfile, signOut }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}
