# Navratri Companion & Community — MVP

A hyperlocal Navratri companion-discovery and community platform: find people (or
groups) to go to Garba, Dandiya, decoration walks, food crawls, or cultural events
with — without it feeling like a dating app.

This is the **MVP slice** of the full product spec, built in the order the spec
itself recommends (see "Development Approach" / section 22): auth, profiles, event
discovery, companion requests, explainable matching, match acceptance, real-time
chat, groups, and block/report safety. Decoration maps, the AI assistant, the
Navratri night planner, Kafka-driven notifications, and the full admin dashboard
are intentionally left for Phase 2 — see [What's not built yet](#whats-not-built-yet-phase-2).

## Stack

- **Backend:** Java 21, Spring Boot 3.3 (single deployable "modular monolith" —
  clean package-per-domain, not literal microservices; see [Architecture note](#architecture-note-monolith-not-microservices)),
  Spring Security + JWT, Spring Data JPA, PostgreSQL, Redis, WebSocket/STOMP, Spring
  Security OAuth2 Client (Google login), Resilience4j, springdoc-openapi.
- **Frontend:** React 18 + Vite + Tailwind CSS, react-router-dom, axios, @stomp/stompjs + sockjs-client.

## Project layout

```
navratri-app/
├── backend/     Spring Boot app (Maven)
├── frontend/    Vite + React app
└── docker-compose.yml   Postgres + Redis for local dev
```

## Running it locally

### 1. Start Postgres + Redis

```bash
docker compose up -d
```

This starts Postgres on `5432` (db `navratri`, user/pass `navratri`/`navratri`) and
Redis on `6379`. The backend's defaults in `application.yml` already point at
these, so no `.env` is required for a first run.

### 2. Run the backend

```bash
cd backend
./mvnw spring-boot:run
```

(If you don't have the Maven wrapper jar locally, use your own `mvn` install —
`mvn spring-boot:run` — instead.) The API comes up on `http://localhost:8080`;
Swagger UI is at `http://localhost:8080/docs`.

Environment variables you'll want to set for anything beyond a quick local test
(all have dev-only defaults otherwise — **do not use the defaults in production**):

| Variable | Purpose |
|---|---|
| `JWT_SECRET` | Signing key for access/refresh tokens. Generate with `openssl rand -base64 64`. |
| `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET` | Google OAuth2 credentials — see [Setting up Google login](#setting-up-google-login) below. Without these set to real values, the button will reach Google and fail with `invalid_client`. |
| `CORS_ORIGINS` | Comma-separated allowed origins (defaults to `http://localhost:5173`). |
| `OAUTH2_SUCCESS_REDIRECT` | Where to send the browser after Google login (defaults to the frontend's `/oauth2/callback`). |

### Setting up Google login

Google login needs two things, on top of the code: real credentials, and the frontend
knowing your backend's real address (`VITE_BACKEND_URL`) since this flow is a full
page redirect that can't go through the Vite dev proxy.

1. In the [Google Cloud Console](https://console.cloud.google.com/apis/credentials),
   create an **OAuth 2.0 Client ID** (Application type: **Web application**).
2. Under **Authorized redirect URIs**, add exactly:
   ```
   http://localhost:8080/login/oauth2/code/google
   ```
   (This is Spring Security's default callback path — `{backend}/login/oauth2/code/{registrationId}` — get it wrong/mismatched and Google will reject the redirect with `redirect_uri_mismatch`.)
3. Copy the generated **Client ID** and **Client secret** into env vars `GOOGLE_CLIENT_ID`
   and `GOOGLE_CLIENT_SECRET` before starting the backend.
4. Set `frontend/.env` (copy from `.env.example`):
   ```
   VITE_BACKEND_URL=http://localhost:8080
   ```
   This has to be the backend's real, absolute origin — not `/api` or anything
   Vite-proxied — because clicking "Continue with Google" is a real browser
   navigation, not an API call.
5. Restart both the backend (to pick up the new env vars) and the frontend (env
   changes need a dev-server restart, not just a hot reload).

If it still fails after this, check the browser's address bar at the point of
failure: an error page on `accounts.google.com` means the credentials/redirect URI
are wrong (steps 1–3); staying on `localhost:5173` with a 404 means the frontend
never actually navigated to the backend (step 4).
| `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` | Postgres connection, if not using the defaults from docker-compose. |

### 3. Run the frontend

```bash
cd frontend
npm install
npm run dev
```

Opens on `http://localhost:5173`. The Vite dev server proxies `/api` and `/ws` to
`http://localhost:8080` (see `vite.config.js`), so the frontend's default
`VITE_API_BASE_URL=/api` just works without CORS headaches in dev.

### 4. Try it out

1. Register an account (or use "Continue with Google" once you've set the OAuth2
   env vars above).
2. Fill in your profile — area, favorite activities, interests.
3. Create an event from Swagger/Postman (there's no "create event" UI yet — events
   are meant to be organizer/admin-created; see Phase 2 notes) or just create a
   companion request directly from the dashboard's activity chips.
4. Open a second browser/incognito window, register a second account with an
   overlapping activity/area/time, and create a matching companion request.
5. From the first account, you should see the second as a match candidate with
   plain-language reasons ("Both interested in Garba", "Both in the X area", ...).
   Send interest, accept from the other account, and the chat opens up — try it
   with both windows open side-by-side to see the WebSocket delivery live.

## What's built

- **Auth:** email/password with JWT access + refresh tokens; Google OAuth2 login;
  phone number verification via OTP (the OTP is logged to the backend console
  instead of actually being SMS'd — see [Known limitations](#known-limitations--things-to-wire-up-before-production)).
- **Profiles:** first name, approximate area (never exact location), age *range*,
  bio, favorite activities, interests, group-size preference. Phone number, email,
  and exact address are never exposed via the profile API.
- **Events:** create, discover/search — matches name, description, *and*
  approximate location, plus an optional activity filter — mark "I'm going" /
  "I'm going and need a companion."
- **Companion requests + matching:** "I want a Garba companion" → an explainable,
  rule-based scoring engine (same event, same activity, overlapping time window,
  same area, shared interests, compatible group size) surfaces candidates with
  plain-language reasons, never a raw score or the algorithm internals.
- **Matches:** send interest → recipient accepts/declines/blocks → accepting spins
  up a private conversation.
- **Chat:** two kinds, both with REST history + live delivery over STOMP/WebSocket
  (SockJS fallback), and both listed together under the Chats tab:
  - **Direct**, tied to an accepted match (see above).
  - **Group**, one shared room per group, open to any APPROVED member the moment
    they join — no accept/decline step, joining the group is the gate.
- **Groups:** create, browse/filter by activity, request to join, owner
  approves/rejects, owner edits (name/description/activities/open slots) or
  deletes the group (cascades to its members and chat history), leave.
- **Safety:** block/unblock, report (harassment, fake profile, spam, inappropriate
  behavior, scam, other), a minimal `/api/admin/safety/*` moderation surface
  (list/triage reports, suspend a user) gated behind `ROLE_ADMIN`.

## What's not built yet (Phase 2)

Matches the spec's own phasing (section 22, items 11–18):

- Decoration & lighting map, "decoration walk" itineraries
- The AI assistant (natural-language → structured companion request, icebreakers)
  — would plug into Spring AI, per the spec
- The Navratri night planner
- Kafka-backed async notifications (the current build has no notification system
  at all — Phase 1 stopped at in-app data only)
- The full admin dashboard UI (a couple of moderation endpoints exist under
  `/api/admin/safety`, but there's no frontend for them yet, and no event/place
  management)
- Recommendation service, analytics

## Known limitations / things to wire up before production

- **Phone OTP is a stub.** `PhoneVerificationService.sendViaSms()` just logs the
  code. Swap in Twilio/MSG91/etc. before relying on this.
- **JWT logout doesn't revoke tokens.** It's stateless-JWT logout (client discards
  the tokens). For real revocation, maintain a denylist of refresh-token IDs in
  Redis and check it in `JwtAuthFilter`.
- **`ddl-auto: update`** is used for convenience while learning the schema. Switch
  to `validate` + a real migration tool (Flyway/Liquibase) before production.
- **No rate limiting** on auth endpoints yet — Resilience4j is on the classpath
  but not yet wired into a rate limiter for login/OTP.
- **Redis is included but lightly used** (wired up as a bean, ready for caching
  or "online status," but nothing currently reads/writes it) — genuinely optional
  to run locally right now; the app works fine without it if you comment out the
  dependency, though the docker-compose service costs nothing to just leave running.

## Architecture note: monolith, not microservices

The spec's own section 16 says to "avoid unnecessary microservices for simple CRUD
operations," so this MVP is one Spring Boot deployable with a clean
package-per-domain structure (`user`, `event`, `companion`, `matching`, `chat`,
`group`, `safety`) — each with its own `entity/repository/dto/service/controller`
sub-packages, so pulling any one of them out into its own service later (once
there's an actual scaling reason to) is a matter of moving a package, not a
rewrite.

## Data model at a glance

`User` (1:1) `Profile` · `Event` (1:N) `EventParticipant` · `CompanionRequest` →
scored by `MatchingService` → `Match` → (on accept) `Conversation` (1:N) `Message`
· `NavratriGroup` (1:N) `GroupMember` · `Report`, `BlockedUser`.

Full OpenAPI docs (all request/response shapes) are generated automatically at
`http://localhost:8080/docs` once the backend is running.
