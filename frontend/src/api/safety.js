import { client, unwrap } from "./client";

export function blockUser(userId) {
  return unwrap(client.post("/safety/block", { userId }));
}

export function unblockUser(userId) {
  return unwrap(client.post("/safety/unblock", { userId }));
}

export function submitReport(payload) {
  return unwrap(client.post("/safety/reports", payload));
}
