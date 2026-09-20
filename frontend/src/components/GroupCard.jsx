import { Link } from "react-router-dom";
import { Users } from "lucide-react";
import ActivityChip from "./ActivityChip";

export default function GroupCard({ group }) {
  return (
    <Link to={`/groups/${group.id}`} className="card block transition hover:-translate-y-0.5 hover:shadow-glow">
      <h3 className="font-display text-lg font-semibold">{group.name}</h3>
      {group.eventName && <p className="text-sm text-night/60 dark:text-ivory/60">For {group.eventName}</p>}

      <div className="mt-3 flex flex-wrap gap-1.5">
        {group.activities?.map((a) => (
          <ActivityChip key={a} activity={a} size="sm" />
        ))}
      </div>

      <div className="mt-4 flex items-center justify-between border-t border-night/10 pt-3 text-sm dark:border-ivory/10">
        <span className="flex items-center gap-1.5 text-night/60 dark:text-ivory/60">
          <Users size={14} /> {group.approvedMemberCount} in the group
        </span>
        <span className="font-medium text-rani">{group.openSlots} open slot{group.openSlots === 1 ? "" : "s"}</span>
      </div>
    </Link>
  );
}
