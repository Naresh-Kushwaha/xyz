import { useEffect, useState } from "react";
import { pendingMatches, myMatches, respondToMatch } from "../api/matches";
import MatchCard from "../components/MatchCard";

export default function Matches() {
  const [pending, setPending] = useState([]);
  const [accepted, setAccepted] = useState([]);
  const [loading, setLoading] = useState(true);

  async function load() {
    setLoading(true);
    try {
      const [p, a] = await Promise.all([pendingMatches(), myMatches()]);
      setPending(p);
      setAccepted(a);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, []);

  async function handleRespond(id, decision) {
    await respondToMatch(id, decision);
    load();
  }

  return (
    <div className="space-y-8">
      <div>
        <h1 className="font-display text-3xl font-semibold">Your matches</h1>
        <p className="mt-1 text-night/60 dark:text-ivory/60">Companion requests, without the pressure.</p>
      </div>

      <section>
        <h2 className="mb-3 font-display text-xl font-semibold">Waiting for your response</h2>
        {loading ? (
          <p className="text-sm text-night/50 dark:text-ivory/50">Loading...</p>
        ) : pending.length === 0 ? (
          <p className="text-sm text-night/50 dark:text-ivory/50">Nothing pending right now.</p>
        ) : (
          <div className="grid gap-4 sm:grid-cols-2">
            {pending.map((m) => (
              <MatchCard key={m.id} match={m} onRespond={handleRespond} />
            ))}
          </div>
        )}
      </section>

      <section>
        <h2 className="mb-3 font-display text-xl font-semibold">Matched 🎉</h2>
        {!loading && accepted.length === 0 ? (
          <p className="text-sm text-night/50 dark:text-ivory/50">No matches yet — send some interest first!</p>
        ) : (
          <div className="grid gap-4 sm:grid-cols-2">
            {accepted.map((m) => (
              <MatchCard key={m.id} match={m} />
            ))}
          </div>
        )}
      </section>
    </div>
  );
}
