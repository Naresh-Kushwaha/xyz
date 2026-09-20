import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import { updateMyProfile } from "../api/users";
import { sendPhoneOtp, verifyPhoneOtp } from "../api/auth";
import ActivityChip, { ACTIVITY_LABELS } from "../components/ActivityChip";

const ACTIVITIES = Object.keys(ACTIVITY_LABELS);
const GROUP_SIZES = ["ONE_ON_ONE", "SMALL_GROUP", "LARGE_GROUP", "NO_PREFERENCE"];

export default function Profile() {
  const { profile, setProfile } = useAuth();
  const [form, setForm] = useState(null);
  const [interestInput, setInterestInput] = useState("");
  const [saving, setSaving] = useState(false);
  const [saved, setSaved] = useState(false);

  const [phone, setPhone] = useState("");
  const [otpSent, setOtpSent] = useState(false);
  const [otpCode, setOtpCode] = useState("");
  const [phoneStatus, setPhoneStatus] = useState("");

  useEffect(() => {
    if (profile) setForm(profile);
  }, [profile]);

  if (!form) return null;

  function toggleActivity(a) {
    setForm((f) => ({
      ...f,
      favoriteActivities: f.favoriteActivities.includes(a)
        ? f.favoriteActivities.filter((x) => x !== a)
        : [...f.favoriteActivities, a],
    }));
  }

  function addInterest() {
    if (!interestInput.trim()) return;
    setForm((f) => ({ ...f, interests: [...new Set([...f.interests, interestInput.trim()])] }));
    setInterestInput("");
  }

  function removeInterest(i) {
    setForm((f) => ({ ...f, interests: f.interests.filter((x) => x !== i) }));
  }

  async function handleSave(e) {
    e.preventDefault();
    setSaving(true);
    setSaved(false);
    try {
      const updated = await updateMyProfile(form);
      setProfile(updated);
      setSaved(true);
    } finally {
      setSaving(false);
    }
  }

  async function handleSendOtp() {
    setPhoneStatus("");
    await sendPhoneOtp(phone);
    setOtpSent(true);
    setPhoneStatus("Code sent — check the backend console log for it in this dev build.");
  }

  async function handleVerifyOtp() {
    setPhoneStatus("");
    try {
      await verifyPhoneOtp(phone, otpCode);
      setPhoneStatus("Phone verified! ✅");
    } catch (err) {
      setPhoneStatus(err.message);
    }
  }

  return (
    <div className="mx-auto max-w-xl space-y-10">
      <div>
        <h1 className="font-display text-3xl font-semibold">Your profile</h1>
        <p className="mt-1 text-night/60 dark:text-ivory/60">
          Only your first name, approximate area, and interests are ever shown to others.
        </p>
      </div>

      <form onSubmit={handleSave} className="space-y-4">
        <div>
          <label className="label">First name</label>
          <input className="input" value={form.firstName || ""} onChange={(e) => setForm({ ...form, firstName: e.target.value })} />
        </div>

        <div>
          <label className="label">Approximate area</label>
          <input
            className="input"
            value={form.approximateArea || ""}
            onChange={(e) => setForm({ ...form, approximateArea: e.target.value })}
            placeholder="e.g. Vijay Nagar, Indore"
          />
        </div>

        <div className="grid grid-cols-2 gap-3">
          <div>
            <label className="label">Age range: min</label>
            <input
              type="number"
              className="input"
              value={form.ageRangeMin || ""}
              onChange={(e) => setForm({ ...form, ageRangeMin: Number(e.target.value) })}
            />
          </div>
          <div>
            <label className="label">Age range: max</label>
            <input
              type="number"
              className="input"
              value={form.ageRangeMax || ""}
              onChange={(e) => setForm({ ...form, ageRangeMax: Number(e.target.value) })}
            />
          </div>
        </div>

        <div>
          <label className="label">Bio</label>
          <textarea className="input" rows={3} value={form.bio || ""} onChange={(e) => setForm({ ...form, bio: e.target.value })} />
        </div>

        <div>
          <label className="label">Preferred group size</label>
          <select
            className="input"
            value={form.preferredGroupSize || ""}
            onChange={(e) => setForm({ ...form, preferredGroupSize: e.target.value })}
          >
            <option value="">No preference</option>
            {GROUP_SIZES.map((g) => (
              <option key={g} value={g}>
                {g.replaceAll("_", " ").toLowerCase()}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label className="label">Favorite activities</label>
          <div className="flex flex-wrap gap-2">
            {ACTIVITIES.map((a) => (
              <ActivityChip key={a} activity={a} selected={form.favoriteActivities?.includes(a)} onClick={() => toggleActivity(a)} />
            ))}
          </div>
        </div>

        <div>
          <label className="label">Interests</label>
          <div className="mb-2 flex flex-wrap gap-2">
            {form.interests?.map((i) => (
              <button
                key={i}
                type="button"
                onClick={() => removeInterest(i)}
                className="rounded-full bg-night/5 px-3 py-1 text-sm dark:bg-ivory/10"
              >
                {i} ✕
              </button>
            ))}
          </div>
          <div className="flex gap-2">
            <input
              className="input"
              value={interestInput}
              onChange={(e) => setInterestInput(e.target.value)}
              placeholder="e.g. Bollywood music"
              onKeyDown={(e) => e.key === "Enter" && (e.preventDefault(), addInterest())}
            />
            <button type="button" onClick={addInterest} className="btn-secondary">
              Add
            </button>
          </div>
        </div>

        <button type="submit" disabled={saving} className="btn-primary w-full">
          {saving ? "Saving..." : "Save profile"}
        </button>
        {saved && <p className="text-center text-sm font-medium text-leaf">Profile updated!</p>}
      </form>

      <div className="border-t border-night/10 pt-6 dark:border-ivory/10">
        <h2 className="font-display text-xl font-semibold">Verify your phone</h2>
        <p className="mt-1 text-sm text-night/60 dark:text-ivory/60">
          Verified badges help other people trust your profile.
        </p>

        <div className="mt-4 flex gap-2">
          <input className="input" value={phone} onChange={(e) => setPhone(e.target.value)} placeholder="+91XXXXXXXXXX" />
          <button onClick={handleSendOtp} className="btn-secondary shrink-0">
            Send code
          </button>
        </div>

        {otpSent && (
          <div className="mt-3 flex gap-2">
            <input className="input" value={otpCode} onChange={(e) => setOtpCode(e.target.value)} placeholder="6-digit code" />
            <button onClick={handleVerifyOtp} className="btn-primary shrink-0">
              Verify
            </button>
          </div>
        )}

        {phoneStatus && <p className="mt-2 text-sm text-night/60 dark:text-ivory/60">{phoneStatus}</p>}
      </div>
    </div>
  );
}
