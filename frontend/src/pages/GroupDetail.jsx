import { useEffect, useState, useCallback } from "react";
import { useParams, useNavigate, Link } from "react-router-dom";
import { getGroup, groupMembers, pendingGroupMembers, joinGroup, decideMembership, leaveGroup, deleteGroup } from "../api/groups";
import { useAuth } from "../context/AuthContext";
import ActivityChip from "../components/ActivityChip";
import { LogOut, Check, X, MessageCircle, Pencil, Trash2 } from "lucide-react";

export default function GroupDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { profile } = useAuth();
  const [group, setGroup] = useState(null);
  const [members, setMembers] = useState([]);
  const [pending, setPending] = useState([]);
  const [busy, setBusy] = useState(false);

  const isOwner = group && profile && group.ownerId === profile.userId;

  const load = useCallback(async () => {
    const [g, m] = await Promise.all([getGroup(id), groupMembers(id)]);
    setGroup(g);
    setMembers(m);

    // Only the owner is allowed to see pending requests -- the backend rejects this
    // call with a 403 for anyone else, so only fire it once we know we're the owner.
    if (g.ownerId === profile?.userId) {
      setPending(await pendingGroupMembers(id));
    } else {
      setPending([]);
    }
  }, [id, profile]);

  useEffect(() => {
    load();
  }, [load]);

  async function handleJoin() {
    setBusy(true);
    try {
      await joinGroup(id);
      await load();
    } finally {
      setBusy(false);
    }
  }

  async function handleDecision(memberUserId, approve) {
    setBusy(true);
    try {
      await decideMembership(id, memberUserId, approve);
      await load();
    } finally {
      setBusy(false);
    }
  }

  async function handleLeave() {
    setBusy(true);
    try {
      await leaveGroup(id);
      await load();
    } finally {
      setBusy(false);
    }
  }

  async function handleDelete() {
    if (!window.confirm(`Delete "${group.name}"? This removes the group, its members, and its chat history for everyone. This can't be undone.`)) {
      return;
    }
    setBusy(true);
    try {
      await deleteGroup(id);
      navigate("/groups");
    } finally {
      setBusy(false);
    }
  }

  if (!group) return <p className="text-sm text-night/50 dark:text-ivory/50">Loading group...</p>;

  return (
    <div>
      <div className="flex items-start justify-between gap-4">
        <h1 className="font-display text-3xl font-semibold">{group.name}</h1>
        {isOwner && (
          <div className="flex shrink-0 gap-2">
            <Link to={`/groups/${id}/edit`} className="btn-secondary !px-3 !py-1.5 text-sm">
              <Pencil size={15} /> Edit
            </Link>
            <button onClick={handleDelete} disabled={busy} className="btn-secondary !px-3 !py-1.5 text-sm !text-rani !border-rani/30">
              <Trash2 size={15} /> Delete
            </button>
          </div>
        )}
      </div>
      {group.eventName && <p className="mt-1 text-night/60 dark:text-ivory/60">For {group.eventName}</p>}
      {group.description && <p className="mt-3 text-night/80 dark:text-ivory/80">{group.description}</p>}

      <div className="mt-4 flex flex-wrap gap-1.5">
        {group.activities?.map((a) => (
          <ActivityChip key={a} activity={a} />
        ))}
      </div>

      <p className="mt-4 text-sm text-night/60 dark:text-ivory/60">
        {group.approvedMemberCount} members · {group.openSlots} open slot{group.openSlots === 1 ? "" : "s"} · started by{" "}
        {group.ownerFirstName}
      </p>

      <div className="mt-6 flex gap-3">
        {!group.myStatus && (
          <button onClick={handleJoin} disabled={busy} className="btn-primary">
            Request to join
          </button>
        )}
        {group.myStatus === "PENDING" && (
          <p className="text-sm font-medium text-gold-dark">Your request is pending approval.</p>
        )}
        {group.myStatus === "APPROVED" && (
          <Link to={`/groups/${id}/chat`} className="btn-primary">
            <MessageCircle size={16} /> Group chat
          </Link>
        )}
        {group.myStatus === "APPROVED" && !isOwner && (
          <button onClick={handleLeave} disabled={busy} className="btn-secondary">
            <LogOut size={16} /> Leave group
          </button>
        )}
      </div>

      <div className="mt-8">
        <h2 className="mb-3 font-display text-xl font-semibold">Members</h2>
        <div className="space-y-2">
          {members.map((m) => (
            <div key={m.id} className="card flex items-center justify-between !py-3">
              <span className="font-medium">
                {m.firstName} {m.role === "OWNER" && <span className="text-xs text-marigold">· organizer</span>}
              </span>
            </div>
          ))}
        </div>
      </div>

      {isOwner && (
        <div className="mt-8">
          <h2 className="mb-3 font-display text-xl font-semibold">
            Pending requests {pending.length > 0 && `(${pending.length})`}
          </h2>
          {pending.length === 0 ? (
            <p className="text-sm text-night/50 dark:text-ivory/50">No pending requests right now.</p>
          ) : (
            <div className="space-y-2">
              {pending.map((m) => (
                <div key={m.id} className="card flex items-center justify-between !py-3">
                  <span className="font-medium">{m.firstName}</span>
                  <div className="flex gap-2">
                    <button
                      onClick={() => handleDecision(m.userId, true)}
                      disabled={busy}
                      className="btn-primary !px-3 !py-1.5 text-sm"
                    >
                      <Check size={15} /> Approve
                    </button>
                    <button
                      onClick={() => handleDecision(m.userId, false)}
                      disabled={busy}
                      className="btn-secondary !px-3 !py-1.5 text-sm"
                    >
                      <X size={15} /> Reject
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
}
