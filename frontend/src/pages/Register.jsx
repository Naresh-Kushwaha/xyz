import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Sparkles } from "lucide-react";
import { register } from "../api/auth";
import { useAuth } from "../context/AuthContext";

export default function Register() {
  const [firstName, setFirstName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();
  const { refreshProfile } = useAuth();

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setSubmitting(true);
    try {
      await register({ firstName, email, password });
      await refreshProfile();
      navigate("/", { replace: true });
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-gradient-to-br from-night via-night to-marigold-dark/40 px-4 text-ivory">
      <div className="w-full max-w-sm">
        <div className="mb-8 flex items-center justify-center gap-2">
          <Sparkles className="text-gold" size={26} />
          <span className="font-display text-2xl font-semibold">Navratri Companion</span>
        </div>

        <form onSubmit={handleSubmit} className="rounded-2xl bg-white/5 p-6 backdrop-blur-sm">
          <h1 className="mb-1 font-display text-xl font-semibold">Join the celebration</h1>
          <p className="mb-5 text-sm text-ivory/60">Never dance, walk, or eat alone this Navratri.</p>

          {error && <p className="mb-4 rounded-lg bg-rani/20 px-3 py-2 text-sm text-rani-light">{error}</p>}

          <label className="label !text-ivory/70">First name</label>
          <input
            className="input mb-4 !bg-white/10 !text-ivory !border-ivory/15"
            required
            value={firstName}
            onChange={(e) => setFirstName(e.target.value)}
            placeholder="Priya"
          />

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
            minLength={8}
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="At least 8 characters"
          />

          <button type="submit" disabled={submitting} className="btn-primary w-full">
            {submitting ? "Creating account..." : "Create account"}
          </button>

          <p className="mt-6 text-center text-sm text-ivory/60">
            Already have an account?{" "}
            <Link to="/login" className="font-semibold text-gold">
              Sign in
            </Link>
          </p>
        </form>
      </div>
    </div>
  );
}
