import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getGroup, updateGroup } from "../api/groups";
import ActivityChip, { ACTIVITY_LABELS } from "../components/ActivityChip";

const ACTIVITIES = Object.keys(ACTIVITY_LABELS);

export default function GroupEdit() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [form, setForm] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    getGroup(id).then((g) =>
      setForm({
        name: g.name,
        description: g.description || "",
        activities: g.activities || [],
        openSlots: g.openSlots,
      })
    );
  }, [id]);

  function toggleActivity(a) {
    setForm((f) => ({
      ...f,
      activities: f.activities.includes(a) ? f.activities.filter((x) => x !== a) : [...f.activities, a],
    }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    if (form.activities.length === 0) {
      setError("Pick at least one activity");
      return;
    }
    setSubmitting(true);
    try {
      await updateGroup(id, { ...form, openSlots: Number(form.openSlots) });
      navigate(`/groups/${id}`);
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  }

  if (!form) return <p className="text-sm text-night/50 dark:text-ivory/50">Loading group...</p>;

  return (
    <div className="mx-auto max-w-lg">
      <h1 className="font-display text-2xl font-semibold">Edit group</h1>
      <p className="mt-1 text-night/60 dark:text-ivory/60">Only visible to you as the group's organizer.</p>

      <form onSubmit={handleSubmit} className="mt-6 space-y-4">
        {error && <p className="rounded-lg bg-rani/10 px-3 py-2 text-sm text-rani">{error}</p>}

        <div>
          <label className="label">Group name</label>
          <input
            className="input"
            required
            value={form.name}
            onChange={(e) => setForm({ ...form, name: e.target.value })}
          />
        </div>

        <div>
          <label className="label">Description</label>
          <textarea
            className="input"
            rows={3}
            value={form.description}
            onChange={(e) => setForm({ ...form, description: e.target.value })}
          />
        </div>

        <div>
          <label className="label">Activities</label>
          <div className="flex flex-wrap gap-2">
            {ACTIVITIES.map((a) => (
              <ActivityChip key={a} activity={a} selected={form.activities.includes(a)} onClick={() => toggleActivity(a)} />
            ))}
          </div>
        </div>

        <div>
          <label className="label">Open slots (not counting you)</label>
          <input
            type="number"
            min={1}
            className="input"
            value={form.openSlots}
            onChange={(e) => setForm({ ...form, openSlots: e.target.value })}
          />
        </div>

        <button type="submit" disabled={submitting} className="btn-primary w-full">
          {submitting ? "Saving..." : "Save changes"}
        </button>
      </form>
    </div>
  );
}
