import { useEffect, useRef, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { format } from "date-fns";
import { Send, ArrowLeft, ShieldAlert } from "lucide-react";
import { getMessages, connectToChat, leaveConversation } from "../api/chat";
import { useAuth } from "../context/AuthContext";
import { blockUser } from "../api/safety";

export default function ChatRoom() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { profile } = useAuth();
  const [messages, setMessages] = useState([]);
  const [draft, setDraft] = useState("");
  const chatRef = useRef(null);
  const bottomRef = useRef(null);

  useEffect(() => {
    let mounted = true;

    getMessages(id).then((msgs) => {
      if (mounted) setMessages(msgs);
    });

    const chat = connectToChat(id, (message) => {
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

  async function handleLeave() {
    await leaveConversation(id);
    navigate("/chat");
  }

  async function handleBlock() {
    const otherId = messages.find((m) => m.senderId !== profile?.userId)?.senderId;
    if (otherId) {
      await blockUser(otherId);
      navigate("/chat");
    }
  }

  return (
    <div className="flex h-[calc(100vh-8rem)] flex-col md:h-[calc(100vh-5rem)]">
      <div className="flex items-center justify-between border-b border-night/10 pb-3 dark:border-ivory/10">
        <button onClick={() => navigate("/chat")} className="flex items-center gap-1.5 text-sm font-medium text-night/70 dark:text-ivory/70">
          <ArrowLeft size={16} /> Back
        </button>
        <div className="flex gap-3 text-sm">
          <button onClick={handleBlock} className="flex items-center gap-1 text-rani">
            <ShieldAlert size={15} /> Block
          </button>
          <button onClick={handleLeave} className="text-night/60 dark:text-ivory/60">
            Leave
          </button>
        </div>
      </div>

      <div className="flex-1 space-y-3 overflow-y-auto py-4">
        {messages.map((m) => {
          const mine = m.senderId === profile?.userId;
          return (
            <div key={m.id} className={`flex ${mine ? "justify-end" : "justify-start"}`}>
              <div
                className={`max-w-[75%] rounded-2xl px-4 py-2.5 text-sm ${
                  mine
                    ? "bg-marigold text-ivory"
                    : "bg-night/5 text-night dark:bg-ivory/10 dark:text-ivory"
                }`}
              >
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
          placeholder="Type a message..."
        />
        <button type="submit" className="btn-primary !px-4">
          <Send size={18} />
        </button>
      </form>
    </div>
  );
}
