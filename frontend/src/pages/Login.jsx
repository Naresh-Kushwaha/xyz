import { useState } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import { Sparkles } from "lucide-react";
import { login, googleLoginUrl } from "../api/auth";
import { useAuth } from "../context/AuthContext";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const { refreshProfile } = useAuth();

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setSubmitting(true);
    try {
      await login({ email, password });
      await refreshProfile();
      navigate(location.state?.from?.pathname || "/", { replace: true });
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-night via-night to-rani-dark/40 px-4 text-ivory">
      <div className="w-full max-w-sm">
        <div className="mb-8 flex items-center justify-center gap-2">
          <Sparkles className="text-gold" size={26} />
          <span className="font-display text-2xl font-semibold">Navratri Companion</span>
        </div>

        <form onSubmit={handleSubmit} className="rounded-2xl bg-white/5 p-6 backdrop-blur-sm">
          <h1 className="mb-1 font-display text-xl font-semibold">Welcome back</h1>
          <p className="mb-5 text-sm text-ivory/60">Find your Garba squad for tonight.</p>

          {error && <p className="mb-4 rounded-lg bg-rani/20 px-3 py-2 text-sm text-rani-light">{error}</p>}

          <label className="label !text-ivory/70">Email</label>
          <input
            className="input mb-4 !bg-white/10 !text-ivory !border-ivory/15"
            type="email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="you@example.com"
          />

          <label className="label !text-ivory/70">Password</label>
          <input
            className="input mb-6 !bg-white/10 !text-ivory !border-ivory/15"
            type="password"
            required
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="••••••••"
          />

          <button type="submit" disabled={submitting} className="btn-primary w-full">
            {submitting ? "Signing in..." : "Sign in"}
          </button>

          <div className="my-4 flex items-center gap-3 text-xs text-ivory/40">
            <span className="h-px flex-1 bg-ivory/15" /> or <span className="h-px flex-1 bg-ivory/15" />
          </div>

          <a href={googleLoginUrl()} className="btn-secondary w-full !bg-white/10 !text-ivory !border-ivory/15">
            Continue with Google
          </a>

          <p className="mt-6 text-center text-sm text-ivory/60">
            New here?{" "}
            <Link to="/register" className="font-semibold text-gold">
              Create an account
            </Link>
          </p>
        </form>
      </div>
    </div>
  );
}
