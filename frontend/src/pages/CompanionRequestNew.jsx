import { useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import { createCompanionRequest, findCandidates } from "../api/companions";
import { sendInterest } from "../api/matches";
import ActivityChip, { ACTIVITY_LABELS } from "../components/ActivityChip";

const ACTIVITIES = Object.keys(ACTIVITY_LABELS);

export default function CompanionRequestNew() {
  const [params] = useSearchParams();
  const navigate = useNavigate();

  const [activity, setActivity] = useState(params.get("activity") || "GARBA");
  const [area, setArea] = useState("");
  const [plannedTime, setPlannedTime] = useState("");
  const [companionsNeeded, setCompanionsNeeded] = useState(1);
  const [message, setMessage] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [candidates, setCandidates] = useState(null);
  const [requestId, setRequestId] = useState(null);
  const [error, setError] = useState("");

  const eventId = params.get("eventId") || null;

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setSubmitting(true);
    try {
      const created = await createCompanionRequest({
        eventId: eventId ? Number(eventId) : null,
        activity,
        plannedTime: new Date(plannedTime).toISOString(),
        approximateArea: area,
        companionsNeeded: Number(companionsNeeded),
        message,
      });
      setRequestId(created.id);
      const found = await findCandidates(created.id);
      setCandidates(found);
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  }

  async function handleSendInterest(theirRequestId) {
    await sendInterest(theirRequestId);
    setCandidates((prev) => prev.filter((c) => c.theirRequest.id !== theirRequestId));
  }

  if (candidates) {
    return (
      <div>
        <h1 className="font-display text-2xl font-semibold">People who might be a great match</h1>
        <p className="mt-1 text-night/60 dark:text-ivory/60">
          We only show why — never a raw score. Send interest and wait for them to accept.
        </p>

        <div className="mt-6 space-y-4">
          {candidates.length === 0 && (
            <p className="text-sm text-night/50 dark:text-ivory/50">
              No one matches yet — your request is saved and will surface as people join.
            </p>
          )}
          {candidates.map((c) => (
            <div key={c.theirRequest.id} className="card">
              <div className="flex items-center justify-between">
                <h3 className="font-display text-lg font-semibold">{c.theirRequest.userFirstName || "Someone"}</h3>
                <ActivityChip activity={c.theirRequest.activity} size="sm" />
              </div>
              <ul className="mt-2 space-y-1 text-sm text-night/70 dark:text-ivory/70">
                {c.reasons.map((r, i) => (
                  <li key={i}>· {r}</li>
                ))}
              </ul>
              {c.theirRequest.message && (
                <p className="mt-2 text-sm italic text-night/60 dark:text-ivory/60">"{c.theirRequest.message}"</p>
              )}
              <button onClick={() => handleSendInterest(c.theirRequest.id)} className="btn-primary mt-3 !px-4 !py-2 text-sm">
                Send interest
              </button>
            </div>
          ))}
        </div>

        <button onClick={() => navigate("/")} className="btn-secondary mt-8">
          Back to dashboard
        </button>
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-lg">
      <h1 className="font-display text-2xl font-semibold">Find a companion</h1>
      <p className="mt-1 text-night/60 dark:text-ivory/60">
        Tell us what you're planning and we'll surface people looking for the same thing.
      </p>

      <form onSubmit={handleSubmit} className="mt-6 space-y-4">
        {error && <p className="rounded-lg bg-rani/10 px-3 py-2 text-sm text-rani">{error}</p>}

        <div>
          <label className="label">Activity</label>
          <div className="flex flex-wrap gap-2">
            {ACTIVITIES.map((a) => (
              <ActivityChip key={a} activity={a} selected={activity === a} onClick={() => setActivity(a)} />
            ))}
          </div>
        </div>

        <div>
          <label className="label">Approximate area</label>
          <input className="input" required value={area} onChange={(e) => setArea(e.target.value)} placeholder="e.g. Vijay Nagar, Indore" />
        </div>

        <div>
          <label className="label">Planned date & time</label>
          <input
            type="datetime-local"
            className="input"
            required
            value={plannedTime}
            onChange={(e) => setPlannedTime(e.target.value)}
          />
        </div>

        <div>
          <label className="label">Companions needed</label>
          <input
            type="number"
            min={1}
            className="input"
            value={companionsNeeded}
            onChange={(e) => setCompanionsNeeded(e.target.value)}
          />
        </div>

        <div>
          <label className="label">Short message (optional)</label>
          <textarea
            className="input"
            rows={3}
            value={message}
            onChange={(e) => setMessage(e.target.value)}
            placeholder="Looking for 2 people to do Garba at Rajwade Palace grounds!"
          />
        </div>

        <button type="submit" disabled={submitting} className="btn-primary w-full">
          {submitting ? "Finding companions..." : "Find companions"}
        </button>
      </form>
    </div>
  );
}
