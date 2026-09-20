import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import { client, unwrap } from "./client";

const WS_URL = import.meta.env.VITE_WS_URL || "/ws";

export function searchGroups({ activity, page = 0, size = 20 } = {}) {
  return unwrap(client.get("/groups", { params: { activity, page, size } }));
}

export function getGroup(id) {
  return unwrap(client.get(`/groups/${id}`));
}

export function myGroups() {
  return unwrap(client.get("/groups/mine"));
}

export function createGroup(payload) {
  return unwrap(client.post("/groups", payload));
}

export function updateGroup(id, payload) {
  return unwrap(client.put(`/groups/${id}`, payload));
}

export function deleteGroup(id) {
  return unwrap(client.delete(`/groups/${id}`));
}

export function groupMembers(id) {
  return unwrap(client.get(`/groups/${id}/members`));
}

export function pendingGroupMembers(id) {
  return unwrap(client.get(`/groups/${id}/pending-members`));
}

export function joinGroup(id) {
  return unwrap(client.post(`/groups/${id}/join`));
}

export function decideMembership(groupId, memberUserId, approve) {
  return unwrap(client.post(`/groups/${groupId}/members/${memberUserId}/decision`, { approve }));
}

export function leaveGroup(id) {
  return unwrap(client.post(`/groups/${id}/leave`));
}

export function getGroupMessages(groupId, { page = 0, size = 50 } = {}) {
  return unwrap(client.get(`/groups/${groupId}/messages`, { params: { page, size } }));
}

export function sendGroupMessageRest(groupId, content) {
  return unwrap(client.post(`/groups/${groupId}/messages`, { content }));
}

/**
 * Same STOMP-over-SockJS pattern as connectToChat() in api/chat.js, but for a
 * group's shared room instead of a 1:1 conversation.
 */
export function connectToGroupChat(groupId, onMessage) {
  const accessToken = localStorage.getItem("accessToken");

  const stompClient = new Client({
    webSocketFactory: () => new SockJS(`${WS_URL}?token=${encodeURIComponent(accessToken || "")}`),
    reconnectDelay: 4000,
    onConnect: () => {
      stompClient.subscribe(`/topic/groups/${groupId}`, (frame) => {
        onMessage(JSON.parse(frame.body));
      });
    },
  });

  stompClient.activate();

  return {
    send(content) {
      stompClient.publish({
        destination: `/app/group.send/${groupId}`,
        body: JSON.stringify({ content }),
      });
    },
    disconnect() {
      stompClient.deactivate();
    },
  };
}
