import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import { client, unwrap } from "./client";

export function myConversations() {
  return unwrap(client.get("/conversations"));
}

export function getMessages(conversationId, { page = 0, size = 50 } = {}) {
  return unwrap(client.get(`/conversations/${conversationId}/messages`, { params: { page, size } }));
}

export function sendMessageRest(conversationId, content) {
  return unwrap(client.post(`/conversations/${conversationId}/messages`, { content }));
}

export function leaveConversation(conversationId) {
  return unwrap(client.post(`/conversations/${conversationId}/leave`));
}

const WS_URL = import.meta.env.VITE_WS_URL || "/ws";

/**
 * Thin wrapper around a STOMP-over-SockJS connection for one conversation screen.
 * Usage:
 *   const chat = connectToChat(conversationId, (message) => setMessages(prev => [...prev, message]));
 *   chat.send("hello");
 *   chat.disconnect();
 */
export function connectToChat(conversationId, onMessage) {
  const accessToken = localStorage.getItem("accessToken");

  const stompClient = new Client({
    webSocketFactory: () => new SockJS(`${WS_URL}?token=${encodeURIComponent(accessToken || "")}`),
    reconnectDelay: 4000,
    onConnect: () => {
      stompClient.subscribe(`/topic/conversations/${conversationId}`, (frame) => {
        onMessage(JSON.parse(frame.body));
      });
    },
  });

  stompClient.activate();

  return {
    send(content) {
      stompClient.publish({
        destination: `/app/chat.send/${conversationId}`,
        body: JSON.stringify({ content }),
      });
    },
    disconnect() {
      stompClient.deactivate();
    },
  };
}
