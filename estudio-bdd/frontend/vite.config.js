import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// A SPA roda em http://localhost:5173 (porta fixa) porque a API só libera CORS para essa origem.
export default defineConfig({
  plugins: [react()],
  server: { port: 5173, strictPort: true },
});
