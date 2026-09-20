import { client, unwrap } from "./client";

export function createCompanionRequest(payload) {
  return unwrap(client.post("/companion-requests", payload));
}

export function myCompanionRequests() {
  return unwrap(client.get("/companion-requests/mine"));
}

export function cancelCompanionRequest(id) {
  return unwrap(client.delete(`/companion-requests/${id}`));
}

export function findCandidates(companionRequestId) {
  return unwrap(client.get(`/companion-requests/${companionRequestId}/candidates`));
}
