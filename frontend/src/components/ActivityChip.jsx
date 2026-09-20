import clsx from "clsx";

const LABELS = {
  GARBA: "🕺 Garba",
  DANDIYA: "💃 Dandiya",
  DECORATION: "🏮 Decorations",
  LIGHTING: "✨ Lighting",
  FOOD: "🍽️ Food",
  PHOTOGRAPHY: "📸 Photography",
  REELS: "🎬 Reels",
  CULTURAL: "🎭 Cultural",
  CITY_WALK: "🚶 City walk",
  COMMUNITY: "👥 Community",
  SOCIALIZING: "💬 Socializing",
};

export default function ActivityChip({ activity, selected, onClick, size = "md" }) {
  const label = LABELS[activity] || activity;
  const isButton = typeof onClick === "function";
  const Tag = isButton ? "button" : "span";

  return (
    <Tag
      type={isButton ? "button" : undefined}
      onClick={onClick}
      className={clsx(
        "inline-flex items-center rounded-full border font-medium transition",
        size === "sm" ? "px-2.5 py-1 text-xs" : "px-3.5 py-1.5 text-sm",
        selected
          ? "border-marigold bg-marigold text-ivory"
          : "border-night/15 bg-white/60 text-night/80 hover:border-marigold/50 dark:border-ivory/15 dark:bg-white/5 dark:text-ivory/80"
      )}
    >
      {label}
    </Tag>
  );
}

export { LABELS as ACTIVITY_LABELS };
