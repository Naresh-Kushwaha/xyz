import { client, unwrap } from "./client";

export function sendInterest(theirCompanionRequestId) {
  return unwrap(client.post("/matches/interest", { theirCompanionRequestId }));
}

export function pendingMatches() {
  return unwrap(client.get("/matches/pending"));
}

export function myMatches() {
  return unwrap(client.get("/matches"));
}

export function respondToMatch(id, decision) {
  return unwrap(client.post(`/matches/${id}/respond`, { decision }));
}
