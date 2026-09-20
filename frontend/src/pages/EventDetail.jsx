import { useEffect, useState } from "react";
import { useParams, useNavigate, Link } from "react-router-dom";
import { format } from "date-fns";
import { MapPin, Users, IndianRupee } from "lucide-react";
import { getEvent, joinEvent } from "../api/events";
import ActivityChip from "../components/ActivityChip";

export default function EventDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [event, setEvent] = useState(null);
  const [joining, setJoining] = useState(false);
  const [joined, setJoined] = useState(false);

  useEffect(() => {
    getEvent(id).then(setEvent);
  }, [id]);

  async function handleJoin(lookingForCompanions) {
    setJoining(true);
    try {
      await joinEvent(id, { lookingForCompanions, companionActivity: event.activities?.[0] });
      setJoined(true);
    } finally {
      setJoining(false);
    }
  }

  if (!event) return <p className="text-sm text-night/50 dark:text-ivory/50">Loading event...</p>;

  return (
    <div>
      <h1 className="font-display text-3xl font-semibold">{event.name}</h1>
      <p className="mt-2 flex items-center gap-1.5 text-night/60 dark:text-ivory/60">
        <MapPin size={16} /> {event.approximateLocation}
      </p>
      <p className="mt-1 text-night/70 dark:text-ivory/70">
        {format(new Date(event.startTime), "EEEE, d MMMM · h:mm a")} – {format(new Date(event.endTime), "h:mm a")}
      </p>
      {event.entryFee != null && (
        <p className="mt-1 flex items-center gap-1 text-night/70 dark:text-ivory/70">
          <IndianRupee size={14} /> {event.entryFee} entry
        </p>
      )}

      <div className="mt-4 flex flex-wrap gap-1.5">
        {event.activities?.map((a) => (
          <ActivityChip key={a} activity={a} />
        ))}
      </div>

      {event.description && <p className="mt-4 text-night/80 dark:text-ivory/80">{event.description}</p>}

      <div className="mt-6 flex items-center gap-4 text-sm">
        <span className="flex items-center gap-1.5 text-night/60 dark:text-ivory/60">
          <Users size={16} /> {event.attendingCount} going
        </span>
        <span className="font-medium text-rani">{event.lookingForCompanionsCount} looking for companions</span>
      </div>

      <div className="mt-6 flex flex-wrap gap-3">
        {!joined ? (
          <>
            <button onClick={() => handleJoin(false)} disabled={joining} className="btn-secondary">
              I'm going
            </button>
            <button onClick={() => handleJoin(true)} disabled={joining} className="btn-primary">
              I'm going & need a companion
            </button>
          </>
        ) : (
          <p className="text-sm font-medium text-leaf">You're marked as going — see you there! 🎉</p>
        )}
        <button
          onClick={() => navigate(`/companion-requests/new?eventId=${event.id}&activity=${event.activities?.[0] || ""}`)}
          className="btn-secondary"
        >
          Find a companion for this event
        </button>
      </div>

      <p className="mt-6 text-sm text-night/50 dark:text-ivory/50">
        Not the right fit?{" "}
        <Link to="/groups" className="font-medium text-marigold">
          Browse groups
        </Link>{" "}
        going to similar events instead.
      </p>
    </div>
  );
}
