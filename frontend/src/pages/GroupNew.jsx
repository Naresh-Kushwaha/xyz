import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { createGroup } from "../api/groups";
import ActivityChip, { ACTIVITY_LABELS } from "../components/ActivityChip";

const ACTIVITIES = Object.keys(ACTIVITY_LABELS);

export default function GroupNew() {
  const navigate = useNavigate();
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [activities, setActivities] = useState([]);
  const [openSlots, setOpenSlots] = useState(2);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");

  function toggleActivity(a) {
    setActivities((prev) => (prev.includes(a) ? prev.filter((x) => x !== a) : [...prev, a]));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    if (activities.length === 0) {
      setError("Pick at least one activity");
      return;
    }
    setSubmitting(true);
    try {
      const group = await createGroup({ name, description, activities, openSlots: Number(openSlots) });
      navigate(`/groups/${group.id}`);
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="mx-auto max-w-lg">
      <h1 className="font-display text-2xl font-semibold">Start a group</h1>
      <p className="mt-1 text-night/60 dark:text-ivory/60">Great for people who'd rather join a squad than go one-on-one.</p>

      <form onSubmit={handleSubmit} className="mt-6 space-y-4">
        {error && <p className="rounded-lg bg-rani/10 px-3 py-2 text-sm text-rani">{error}</p>}

        <div>
          <label className="label">Group name</label>
          <input className="input" required value={name} onChange={(e) => setName(e.target.value)} placeholder="Friday Garba Squad" />
        </div>

        <div>
          <label className="label">Description</label>
          <textarea className="input" rows={3} value={description} onChange={(e) => setDescription(e.target.value)} />
        </div>

        <div>
          <label className="label">Activities</label>
          <div className="flex flex-wrap gap-2">
            {ACTIVITIES.map((a) => (
              <ActivityChip key={a} activity={a} selected={activities.includes(a)} onClick={() => toggleActivity(a)} />
            ))}
          </div>
        </div>

        <div>
          <label className="label">Open slots (not counting you)</label>
          <input type="number" min={1} className="input" value={openSlots} onChange={(e) => setOpenSlots(e.target.value)} />
        </div>

        <button type="submit" disabled={submitting} className="btn-primary w-full">
          {submitting ? "Creating..." : "Create group"}
        </button>
      </form>
    </div>
  );
}
