import { useEffect, useRef, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { format } from "date-fns";
import { Send, ArrowLeft } from "lucide-react";
import { getGroup, getGroupMessages, connectToGroupChat } from "../api/groups";
import { useAuth } from "../context/AuthContext";

export default function GroupChatRoom() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { profile } = useAuth();
  const [group, setGroup] = useState(null);
  const [messages, setMessages] = useState([]);
  const [draft, setDraft] = useState("");
  const chatRef = useRef(null);
  const bottomRef = useRef(null);

  useEffect(() => {
    let mounted = true;

    getGroup(id).then((g) => mounted && setGroup(g));
    getGroupMessages(id).then((msgs) => mounted && setMessages(msgs));

    const chat = connectToGroupChat(id, (message) => {
      setMessages((prev) => [...prev, message]);
    });
    chatRef.current = chat;

    return () => {
      mounted = false;
      chat.disconnect();
    };
  }, [id]);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  function handleSend(e) {
    e.preventDefault();
    if (!draft.trim()) return;
    chatRef.current?.send(draft.trim());
    setDraft("");
  }

  return (
    <div className="flex h-[calc(100vh-8rem)] flex-col md:h-[calc(100vh-5rem)]">
      <div className="flex items-center justify-between border-b border-night/10 pb-3 dark:border-ivory/10">
        <button
          onClick={() => navigate(`/groups/${id}`)}
          className="flex items-center gap-1.5 text-sm font-medium text-night/70 dark:text-ivory/70"
        >
          <ArrowLeft size={16} /> {group?.name || "Group chat"}
        </button>
      </div>

      <div className="flex-1 space-y-3 overflow-y-auto py-4">
        {messages.length === 0 && (
          <p className="text-center text-sm text-night/40 dark:text-ivory/40">
            No messages yet — say hi to the group!
          </p>
        )}
        {messages.map((m) => {
          const mine = m.senderId === profile?.userId;
          return (
            <div key={m.id} className={`flex ${mine ? "justify-end" : "justify-start"}`}>
              <div
                className={`max-w-[75%] rounded-2xl px-4 py-2.5 text-sm ${
                  mine ? "bg-marigold text-ivory" : "bg-night/5 text-night dark:bg-ivory/10 dark:text-ivory"
                }`}
              >
                {!mine && <p className="mb-0.5 text-xs font-semibold text-rani">{m.senderFirstName}</p>}
                <p>{m.content}</p>
                <p className={`mt-1 text-[10px] ${mine ? "text-ivory/70" : "text-night/40 dark:text-ivory/40"}`}>
                  {format(new Date(m.createdAt), "h:mm a")}
                </p>
              </div>
            </div>
          );
        })}
        <div ref={bottomRef} />
      </div>

      <form onSubmit={handleSend} className="flex gap-2 border-t border-night/10 pt-3 dark:border-ivory/10">
        <input
          className="input"
          value={draft}
          onChange={(e) => setDraft(e.target.value)}
          placeholder="Message the group..."
        />
        <button type="submit" className="btn-primary !px-4">
          <Send size={18} />
        </button>
      </form>
    </div>
  );
}
