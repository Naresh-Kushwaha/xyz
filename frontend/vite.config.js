import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig({
  plugins: [react()],
  // sockjs-client (used by the chat websocket) expects Node's `global` object.
  // Vite doesn't polyfill that for the browser like Webpack used to, so without
  // this you'll hit "Uncaught ReferenceError: global is not defined" at runtime.
  define: {
    global: "globalThis",
  },
  server: {
    port: 5173,
    proxy: {
      // So the frontend can call relative /api/* paths in dev without CORS headaches.
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
      "/ws": {
        target: "http://localhost:8080",
        ws: true,
        changeOrigin: true,
      },
      // The "Continue with Google" link points at a relative /oauth2/authorization/google
      // URL (see googleLoginUrl() in src/api/auth.js) so it works in both dev and prod.
      // Without this proxy entry, that click never reaches the backend at all -- it hits
      // Vite's own dev server, which has no route for it and falls through to the SPA's
      // catch-all "not found" page. (Deliberately NOT proxying "/login" here -- that IS a
      // real frontend route, /login, and blanket-proxying it would break the login page.)
      "/oauth2": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
});
