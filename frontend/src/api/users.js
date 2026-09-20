import { client, unwrap } from "./client";

export function getMyProfile() {
  return unwrap(client.get("/users/me"));
}

export function updateMyProfile(payload) {
  return unwrap(client.put("/users/me", payload));
}

export function getProfile(userId) {
  return unwrap(client.get(`/users/${userId}`));
}
