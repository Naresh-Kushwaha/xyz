import { client, unwrap } from "./client";

export function discoverEvents({ q, activity, page = 0, size = 20 } = {}) {
  return unwrap(client.get("/events", { params: { q, activity, page, size } }));
}

export function getEvent(id) {
  return unwrap(client.get(`/events/${id}`));
}

export function createEvent(payload) {
  return unwrap(client.post("/events", payload));
}

export function joinEvent(id, { lookingForCompanions, companionActivity }) {
  return unwrap(client.post(`/events/${id}/join`, { lookingForCompanions, companionActivity }));
}
