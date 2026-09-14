# Kotlin Android Banking

A portfolio project demonstrating a native Android banking client built with modern Android
tooling, backed by a small Kotlin server it actually talks to end-to-end (login, balance,
statement, transfers, notifications).

## What's in the box

- **`:app`** — a single-module Android app (Kotlin + Jetpack Compose) organized with Clean
  Architecture / MVVM, using DDD-flavored bounded contexts (`auth`, `account`, `transfer`,
  `notification`) instead of a Java-style technical split.
- **`:backend`** — a Ktor server exposing the REST API the app consumes, with JWT auth and an
  in-memory data store seeded with a demo user.

Both modules share the same Gradle/Kotlin toolchain, so there's a single `./gradlew` for
everything — no separate Node/Python stack for the backend.

## Architecture (`:app`)

```
presentation/   Compose screens + StateFlow ViewModels, per feature (login, dashboard,
                statement, transfer, notifications)
domain/         Entities, use cases, repository interfaces — pure Kotlin, no Android
                dependencies, fully unit-testable on the JVM
data/           Retrofit services + DTOs, Room entities/DAOs, repository implementations,
                mappers between DTOs/entities and domain models
di/             Hilt modules (network, database, repositories)
core/           Theme (Material 3, light/dark), navigation graph, typed error hierarchy,
                dispatcher provider, session-expiry event bus
```

Key decisions:

- **One Gradle module for the app.** Bounded contexts are Kotlin packages, not Gradle modules —
  splitting a portfolio-sized app into a dozen modules adds build complexity without a real payoff.
- **Typed errors, not exceptions leaking upward.** Every repository method returns
  `Result<T>`, and network/HTTP failures are mapped to a small `AppError` sealed hierarchy
  (`Validation`, `Unauthorized`, `NotFound`, `Network`, `Server`, `Unknown`) so ViewModels render
  specific UI states instead of crashing on unexpected exceptions.
- **JWT session handling.** The token is stored in `EncryptedSharedPreferences`, attached to every
  request by an OkHttp interceptor, and a 401 response clears the session and emits a
  `SessionManager` event that the navigation graph observes to force a return to the login screen.
- **Offline-friendly caching.** The account balance and the first page of the statement are
  cached in Room; if the first statement page fails due to a network error, the cached copy is
  served instead of an error screen.

## Tech stack

- Kotlin, Jetpack Compose (Material 3, light + dark themes), Navigation Compose
- Coroutines + Flow, StateFlow-based UI state
- Retrofit + OkHttp + kotlinx.serialization
- Hilt for dependency injection
- Room for local caching
- JWT authentication
- Ktor (server), in-memory store, `com.auth0:java-jwt`
- JUnit4, MockK, kotlinx-coroutines-test, Turbine, MockWebServer, Robolectric (app);
  Ktor `testApplication` (backend)
- GitHub Actions CI, Podman for containerizing the backend

## Running the backend

### With Gradle

```bash
./gradlew :backend:run
```

The server starts on port `8080` by default (override with the `PORT` env var). Try it:

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"demo@bank.com","password":"password123"}'
```

The seeded demo user is `demo@bank.com` / `password123`, with a starting balance of `2500.00`.

### With Podman

```bash
podman build -t kotlin-android-banking-backend -f backend/Dockerfile .
podman run --rm -p 18090:8080 --name banking-backend kotlin-android-banking-backend
```

The container listens on `8080` internally; the host port `18090` was chosen to avoid clashing
with other local projects. The Android app's Retrofit base URL
(`BuildConfig.BASE_URL`, see `app/build.gradle.kts`) points at
`http://10.0.2.2:18090/`, which is the Android emulator's alias for the host machine.

## Running the app

1. Start the backend (see above) so the emulator has something to talk to.
2. Open the project in Android Studio (or run from the CLI):

   ```bash
   ./gradlew :app:assembleDebug
   ```

3. Install `app/build/outputs/apk/debug/app-debug.apk` on an emulator/device and log in with the
   demo credentials above.

## Running tests

```bash
./gradlew :backend:test           # Ktor route tests + TransferService unit tests
./gradlew :app:testDebugUnitTest  # domain use cases, ViewModels, Room DAOs, Retrofit repositories
./gradlew :app:lintDebug
```

## API surface (backend)

| Method | Path                          | Notes                                   |
|--------|-------------------------------|------------------------------------------|
| POST   | `/auth/login`                 | 400 invalid body, 401 wrong credentials  |
| POST   | `/auth/register`              | 400 invalid body / duplicate email       |
| GET    | `/accounts/{id}`               | 401 no token, 404 not the caller's account |
| GET    | `/accounts/{id}/statement`     | `page`, `size` query params, 400 invalid pagination |
| POST   | `/transfers`                   | 400 validation/insufficient funds, 404 unknown recipient |
| GET    | `/transfers/{id}`              | 404 if not a party to the transfer       |
| DELETE | `/transfers/{id}`              | cancels a **pending** transfer and refunds the sender; 400 if not pending |
| GET    | `/transfers?accountId=`        | lists transfers where the caller is sender or recipient |
| GET    | `/notifications`               | 401 no token                             |
| PUT    | `/notifications/{id}`          | marks as read, 404 if missing            |

Transfers stay `PENDING` (funds are held immediately) until cancelled — there is no separate
"complete" endpoint in this demo, mirroring how a real transfer sits in a processing queue before
settlement.

## CI/CD

`.github/workflows/ci.yml` runs on every push/PR to `master`:

- **backend** job: JDK 17, `./gradlew :backend:test`, `./gradlew :backend:buildFatJar`.
- **android** job: JDK 17 + `android-actions/setup-android`, `./gradlew :app:testDebugUnitTest`,
  `./gradlew :app:lintDebug`, `./gradlew :app:assembleDebug`.

## Known simplifications

This is a demo backend, not a production one:

- Storage is an in-memory map that resets on restart (no Postgres/Redis — overkill for a
  portfolio demo).
- Password hashing is salted SHA-256 rather than bcrypt/argon2.
- All account balance mutations go through a single lock (`TransferService`) rather than
  per-account locks — fine at demo scale, would need sharded locking under real throughput.
