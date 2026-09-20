/** @type {import('tailwindcss').Config} */
export default {
  darkMode: "class",
  content: ["./index.html", "./src/**/*.{js,jsx}"],
  theme: {
    extend: {
      colors: {
        // Festive palette: a deep indigo night sky as the base, with marigold/rani-pink/gold
        // as the three accent colors (echoing the diyas + garba dupattas rather than a generic SaaS brand color).
        night: {
          DEFAULT: "#1A1025",
          soft: "#241733",
          softer: "#2E1E40",
        },
        ivory: {
          DEFAULT: "#FFF8ED",
          soft: "#FBF1E0",
        },
        marigold: {
          DEFAULT: "#E8590C",
          light: "#FF8A3D",
          dark: "#B8420A",
        },
        rani: {
          DEFAULT: "#C9184A",
          light: "#E63E6D",
          dark: "#960F35",
        },
        gold: {
          DEFAULT: "#FFBA08",
          light: "#FFD34D",
        },
        leaf: {
          DEFAULT: "#2D6A4F",
        },
      },
      fontFamily: {
        display: ["Fraunces", "ui-serif", "Georgia", "serif"],
        sans: ["Manrope", "ui-sans-serif", "system-ui", "sans-serif"],
      },
      boxShadow: {
        glow: "0 0 40px -10px rgba(232, 89, 12, 0.35)",
      },
    },
  },
  plugins: [],
};
