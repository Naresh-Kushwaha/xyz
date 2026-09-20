import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { myConversations } from "../api/chat";
import { myGroups } from "../api/groups";
import { MessageCircle, Users } from "lucide-react";

export default function ChatList() {
  const [conversations, setConversations] = useState([]);
  const [groups, setGroups] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([myConversations(), myGroups()])
      .then(([c, g]) => {
        setConversations(c);
        setGroups(g);
      })
      .finally(() => setLoading(false));
  }, []);

  const nothingYet = !loading && conversations.length === 0 && groups.length === 0;

  return (
    <div>
      <h1 className="font-display text-3xl font-semibold">Chats</h1>
      <p className="mt-1 text-night/60 dark:text-ivory/60">
        Direct chats open once you both accept a match. Group chats open as soon as you join a group.
      </p>

      {loading ? (
        <p className="mt-6 text-sm text-night/50 dark:text-ivory/50">Loading...</p>
      ) : nothingYet ? (
        <p className="mt-6 text-sm text-night/50 dark:text-ivory/50">
          Nothing yet — accept a match or join a group to start chatting.
        </p>
      ) : (
        <div className="mt-6 space-y-8">
          {conversations.length > 0 && (
            <section>
              <h2 className="mb-3 font-display text-lg font-semibold">Direct messages</h2>
              <div className="space-y-2">
                {conversations.map((c) => (
                  <Link key={c.id} to={`/chat/${c.id}`} className="card flex items-center gap-3 !py-3.5">
                    <div className="flex h-10 w-10 items-center justify-center rounded-full bg-marigold/10 text-marigold">
                      <MessageCircle size={18} />
                    </div>
                    <div>
                      <p className="font-medium">{c.otherUserFirstName || "Someone"}</p>
                      {c.closed && <p className="text-xs text-night/50 dark:text-ivory/50">Closed</p>}
                    </div>
                  </Link>
                ))}
              </div>
            </section>
          )}

          {groups.length > 0 && (
            <section>
              <h2 className="mb-3 font-display text-lg font-semibold">Group chats</h2>
              <div className="space-y-2">
                {groups.map((g) => (
                  <Link key={g.id} to={`/groups/${g.id}/chat`} className="card flex items-center gap-3 !py-3.5">
                    <div className="flex h-10 w-10 items-center justify-center rounded-full bg-rani/10 text-rani">
                      <Users size={18} />
                    </div>
                    <div>
                      <p className="font-medium">{g.name}</p>
                      <p className="text-xs text-night/50 dark:text-ivory/50">{g.approvedMemberCount} members</p>
                    </div>
                  </Link>
                ))}
              </div>
            </section>
          )}
        </div>
      )}
    </div>
  );
}
