import { useEffect, useState } from "react";
import { discoverEvents } from "../api/events";
import EventCard from "../components/EventCard";
import ActivityChip, { ACTIVITY_LABELS } from "../components/ActivityChip";
import { Search } from "lucide-react";

const ACTIVITIES = Object.keys(ACTIVITY_LABELS);

export default function Events() {
  const [query, setQuery] = useState("");
  const [activity, setActivity] = useState(null);
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);

  async function load(q, act) {
    setLoading(true);
    try {
      const result = await discoverEvents({ q: q || undefined, activity: act || undefined, size: 30 });
      setEvents(result.content);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load(query, activity);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [activity]);

  return (
    <div>
      <h1 className="font-display text-3xl font-semibold">Discover events</h1>
      <p className="mt-1 text-night/60 dark:text-ivory/60">Garba nights, decoration walks, and more near you.</p>

      <form
        onSubmit={(e) => {
          e.preventDefault();
          load(query, activity);
        }}
        className="mt-5 flex gap-2"
      >
        <div className="relative flex-1">
          <Search className="pointer-events-none absolute left-3.5 top-1/2 -translate-y-1/2 text-night/40 dark:text-ivory/40" size={18} />
          <input
            className="input pl-10"
            placeholder="Search by name, area, or description"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
          />
        </div>
        <button type="submit" className="btn-secondary">
          Search
        </button>
      </form>

      <div className="mt-3 flex flex-wrap gap-2">
        <button
          onClick={() => setActivity(null)}
          className={`inline-flex items-center rounded-full border px-3.5 py-1.5 text-sm font-medium transition ${
            !activity
              ? "border-marigold bg-marigold text-ivory"
              : "border-night/15 bg-white/60 text-night/80 hover:border-marigold/50 dark:border-ivory/15 dark:bg-white/5 dark:text-ivory/80"
          }`}
        >
          All activities
        </button>
        {ACTIVITIES.map((a) => (
          <ActivityChip key={a} activity={a} selected={activity === a} onClick={() => setActivity(a === activity ? null : a)} />
        ))}
      </div>

      <div className="mt-6 grid gap-4 sm:grid-cols-2">
        {loading ? (
          <p className="text-sm text-night/50 dark:text-ivory/50">Loading events...</p>
        ) : events.length === 0 ? (
          <p className="text-sm text-night/50 dark:text-ivory/50">No events found — try a different search or activity.</p>
        ) : (
          events.map((e) => <EventCard key={e.id} event={e} />)
        )}
      </div>
    </div>
  );
}
