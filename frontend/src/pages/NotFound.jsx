import { Link } from "react-router-dom";

export default function NotFound() {
  return (
    <div className="flex min-h-screen flex-col items-center justify-center gap-3 text-center">
      <h1 className="font-display text-3xl font-semibold">Page not found</h1>
      <p className="text-night/60 dark:text-ivory/60">Looks like this Garba floor doesn't exist.</p>
      <Link to="/" className="btn-primary mt-2">
        Back to dashboard
      </Link>
    </div>
  );
}
