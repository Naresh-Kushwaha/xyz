import { Link } from "react-router-dom";
import { CheckCircle2, XCircle, MessageCircle } from "lucide-react";

export default function MatchCard({ match, onRespond }) {
  return (
    <div className="card">
      <div className="flex items-center justify-between">
        <h3 className="font-display text-lg font-semibold">{match.otherUserFirstName || "Someone"}</h3>
        <span className="rounded-full bg-night/5 px-2.5 py-1 text-xs font-medium dark:bg-ivory/10">
          {match.status}
        </span>
      </div>

      {match.reasons?.length > 0 && (
        <ul className="mt-2 space-y-1 text-sm text-night/70 dark:text-ivory/70">
          {match.reasons.map((r, i) => (
            <li key={i}>· {r}</li>
          ))}
        </ul>
      )}

      <div className="mt-4 flex gap-2">
        {match.status === "PENDING" && onRespond && (
          <>
            <button onClick={() => onRespond(match.id, "ACCEPTED")} className="btn-primary !px-4 !py-2 text-sm">
              <CheckCircle2 size={16} /> Accept
            </button>
            <button onClick={() => onRespond(match.id, "DECLINED")} className="btn-secondary !px-4 !py-2 text-sm">
              <XCircle size={16} /> Decline
            </button>
          </>
        )}
        {match.status === "ACCEPTED" && match.conversationId && (
          <Link to={`/chat/${match.conversationId}`} className="btn-primary !px-4 !py-2 text-sm">
            <MessageCircle size={16} /> Open chat
          </Link>
        )}
      </div>
    </div>
  );
}
