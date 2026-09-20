import { Link } from "react-router-dom";
import { format } from "date-fns";
import { MapPin, Users, Sparkles } from "lucide-react";
import ActivityChip from "./ActivityChip";

export default function EventCard({ event }) {
  return (
    <Link to={`/events/${event.id}`} className="card block transition hover:-translate-y-0.5 hover:shadow-glow">
      <div className="flex items-start justify-between gap-3">
        <div>
          <h3 className="font-display text-lg font-semibold leading-snug">{event.name}</h3>
          <p className="mt-1 flex items-center gap-1.5 text-sm text-night/60 dark:text-ivory/60">
            <MapPin size={14} /> {event.approximateLocation}
          </p>
        </div>
        {event.featured && (
          <span className="flex items-center gap-1 rounded-full bg-gold/20 px-2.5 py-1 text-xs font-semibold text-gold-dark">
            <Sparkles size={12} /> Featured
          </span>
        )}
      </div>

      <p className="mt-3 text-sm text-night/70 dark:text-ivory/70">
        {format(new Date(event.startTime), "EEE, d MMM")} · {format(new Date(event.startTime), "h:mm a")} –{" "}
        {format(new Date(event.endTime), "h:mm a")}
      </p>

      <div className="mt-3 flex flex-wrap gap-1.5">
        {event.activities?.slice(0, 4).map((a) => (
          <ActivityChip key={a} activity={a} size="sm" />
        ))}
      </div>

      <div className="mt-4 flex items-center justify-between border-t border-night/10 pt-3 text-sm dark:border-ivory/10">
        <span className="flex items-center gap-1.5 text-night/60 dark:text-ivory/60">
          <Users size={14} /> {event.attendingCount} going
        </span>
        {event.lookingForCompanionsCount > 0 && (
          <span className="font-medium text-rani">
            {event.lookingForCompanionsCount} looking for companions
          </span>
        )}
      </div>
    </Link>
  );
}
