import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { discoverEvents } from "../api/events";
import { searchGroups } from "../api/groups";
import EventCard from "../components/EventCard";
import GroupCard from "../components/GroupCard";
import ActivityChip from "../components/ActivityChip";

const QUICK_ACTIVITIES = ["GARBA", "DANDIYA", "DECORATION", "FOOD", "PHOTOGRAPHY"];

export default function Dashboard() {
  const { profile } = useAuth();
  const [events, setEvents] = useState([]);
  const [groups, setGroups] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    (async () => {
      try {
        const [eventsPage, groupsPage] = await Promise.all([
          discoverEvents({ size: 4 }),
          searchGroups({ size: 3 }),
        ]);
        setEvents(eventsPage.content);
        setGroups(groupsPage.content);
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  const greeting = new Date().getHours() < 17 ? "Good day" : "Good evening";

  return (
    <div className="space-y-10">
      <div>
        <h1 className="font-display text-3xl font-semibold">
          {greeting}, {profile?.firstName} 👋
        </h1>
        <p className="mt-1 text-night/60 dark:text-ivory/60">What are you planning tonight?</p>

        <div className="mt-4 flex flex-wrap gap-2">
          {QUICK_ACTIVITIES.map((a) => (
            <Link key={a} to={`/companion-requests/new?activity=${a}`}>
              <ActivityChip activity={a} />
            </Link>
          ))}
          <Link to="/groups">
            <ActivityChip activity="COMMUNITY" />
          </Link>
        </div>
      </div>

      <section>
        <div className="mb-3 flex items-center justify-between">
          <h2 className="font-display text-xl font-semibold">Upcoming events</h2>
          <Link to="/events" className="text-sm font-medium text-marigold">
            See all
          </Link>
        </div>
        {loading ? (
          <p className="text-sm text-night/50 dark:text-ivory/50">Loading...</p>
        ) : events.length === 0 ? (
          <p className="text-sm text-night/50 dark:text-ivory/50">No events yet — check back soon.</p>
        ) : (
          <div className="grid gap-4 sm:grid-cols-2">
            {events.map((e) => (
              <EventCard key={e.id} event={e} />
            ))}
          </div>
        )}
      </section>

      <section>
        <div className="mb-3 flex items-center justify-between">
          <h2 className="font-display text-xl font-semibold">Groups you may like</h2>
          <Link to="/groups" className="text-sm font-medium text-marigold">
            See all
          </Link>
        </div>
        {!loading && groups.length === 0 ? (
          <p className="text-sm text-night/50 dark:text-ivory/50">No open groups right now — start one!</p>
        ) : (
          <div className="grid gap-4 sm:grid-cols-2">
            {groups.map((g) => (
              <GroupCard key={g.id} group={g} />
            ))}
          </div>
        )}
      </section>
    </div>
  );
}
