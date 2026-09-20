import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { searchGroups } from "../api/groups";
import GroupCard from "../components/GroupCard";
import ActivityChip, { ACTIVITY_LABELS } from "../components/ActivityChip";
import { Plus } from "lucide-react";

const ACTIVITIES = Object.keys(ACTIVITY_LABELS);

export default function Groups() {
  const [activity, setActivity] = useState(null);
  const [groups, setGroups] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    searchGroups({ activity: activity || undefined, size: 30 })
      .then((r) => setGroups(r.content))
      .finally(() => setLoading(false));
  }, [activity]);

  return (
    <div>
      <div className="flex items-start justify-between gap-4">
        <div>
          <h1 className="font-display text-3xl font-semibold">Groups</h1>
          <p className="mt-1 text-night/60 dark:text-ivory/60">
            Comfortable, no-pressure participation for people who'd rather not go one-on-one.
          </p>
        </div>
        <Link to="/groups/new" className="btn-primary shrink-0 !px-4">
          <Plus size={18} /> New group
        </Link>
      </div>

      <div className="mt-5 flex flex-wrap gap-2">
        <button
          onClick={() => setActivity(null)}
          className={`inline-flex items-center rounded-full border px-3.5 py-1.5 text-sm font-medium transition ${
            !activity
              ? "border-marigold bg-marigold text-ivory"
              : "border-night/15 bg-white/60 text-night/80 hover:border-marigold/50 dark:border-ivory/15 dark:bg-white/5 dark:text-ivory/80"
          }`}
        >
          All
        </button>
        {ACTIVITIES.map((a) => (
          <ActivityChip key={a} activity={a} selected={activity === a} onClick={() => setActivity(a)} />
        ))}
      </div>

      <div className="mt-6 grid gap-4 sm:grid-cols-2">
        {loading ? (
          <p className="text-sm text-night/50 dark:text-ivory/50">Loading groups...</p>
        ) : groups.length === 0 ? (
          <p className="text-sm text-night/50 dark:text-ivory/50">No groups yet — be the first to start one.</p>
        ) : (
          groups.map((g) => <GroupCard key={g.id} group={g} />)
        )}
      </div>
    </div>
  );
}
