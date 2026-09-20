import { NavLink, useNavigate } from "react-router-dom";
import { Home, CalendarDays, Users, MessageCircle, User, LogOut, Sparkles } from "lucide-react";
import { useAuth } from "../context/AuthContext";
import { logout } from "../api/auth";

const NAV_ITEMS = [
  { to: "/", label: "Home", icon: Home, end: true },
  { to: "/events", label: "Events", icon: CalendarDays },
  { to: "/groups", label: "Groups", icon: Users },
  { to: "/chat", label: "Chats", icon: MessageCircle },
  { to: "/profile", label: "Profile", icon: User },
];

export default function AppShell({ children }) {
  const { profile, signOut } = useAuth();
  const navigate = useNavigate();

  async function handleLogout() {
    await logout();
    signOut();
    navigate("/login");
  }

  return (
    <div className="min-h-screen">
      {/* Desktop sidebar */}
      <aside className="fixed inset-y-0 left-0 hidden w-60 flex-col border-r border-night/10 bg-white/60 px-4 py-6 backdrop-blur-sm dark:border-ivory/10 dark:bg-night-soft/60 md:flex">
        <div className="mb-8 flex items-center gap-2 px-2">
          <Sparkles className="text-marigold" size={22} />
          <span className="font-display text-lg font-semibold">Navratri Co.</span>
        </div>

        <nav className="flex-1 space-y-1">
          {NAV_ITEMS.map(({ to, label, icon: Icon, end }) => (
            <NavLink
              key={to}
              to={to}
              end={end}
              className={({ isActive }) =>
                `flex items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-medium transition ${
                  isActive
                    ? "bg-marigold/10 text-marigold"
                    : "text-night/70 hover:bg-night/5 dark:text-ivory/70 dark:hover:bg-ivory/5"
                }`
              }
            >
              <Icon size={18} /> {label}
            </NavLink>
          ))}
        </nav>

        <div className="border-t border-night/10 pt-4 dark:border-ivory/10">
          <p className="truncate px-2 text-sm font-medium">{profile?.firstName}</p>
          <button
            onClick={handleLogout}
            className="mt-2 flex w-full items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-medium text-night/70 transition hover:bg-night/5 dark:text-ivory/70 dark:hover:bg-ivory/5"
          >
            <LogOut size={18} /> Log out
          </button>
        </div>
      </aside>

      {/* Content */}
      <main className="min-h-screen pb-20 md:ml-60 md:pb-0">
        <div className="mx-auto max-w-4xl px-4 py-6 md:px-8 md:py-10">{children}</div>
      </main>

      {/* Mobile bottom nav */}
      <nav className="fixed inset-x-0 bottom-0 z-20 flex border-t border-night/10 bg-white/90 backdrop-blur-sm dark:border-ivory/10 dark:bg-night-soft/90 md:hidden">
        {NAV_ITEMS.map(({ to, label, icon: Icon, end }) => (
          <NavLink
            key={to}
            to={to}
            end={end}
            className={({ isActive }) =>
              `flex flex-1 flex-col items-center gap-0.5 py-2.5 text-xs font-medium ${
                isActive ? "text-marigold" : "text-night/60 dark:text-ivory/60"
              }`
            }
          >
            <Icon size={20} />
            {label}
          </NavLink>
        ))}
      </nav>
    </div>
  );
}
