# Authentication and academic freshness

`SessionCoordinator` is the application-scoped owner of restoration, manual and
automatic login, encrypted session writes, cookie updates, and logout. Presentation
observes its `StateFlow`; HTTP requests do not subscribe UI objects to a singleton.
`MainViewModel` loads student identity separately and opens academic navigation only
after that identity belongs to the current account generation.

## Requests and recovery

The named raw Retrofit API is reserved for the coordinator and
`SessionAcademicoAPI`. Academic use cases use the latter. Login bypasses the
protected gate, and protected requests carry an immutable account/authentication
ticket. The interceptor only attaches matching, unexpired cookies from that ticket;
it performs no authentication, persistence, or blocking coroutine work.

A session is reusable strictly before 15 minutes from successful login. Persisted
epoch milliseconds support process restoration, and a monotonic anchor bounds its
remaining lifetime within a running process. Missing, future, or invalid timestamps
and missing usable cookies require automatic login without a validation probe.
Cookie traffic never changes this clock.

Concurrent work shares an authentication result. An initial protected 401 gets one
recovery and one retry; a late old-version 401 can reuse a newer authentication
without another login. A current-version retry 401, or the documented empty-key
HTTP 400 `Senha Inválida!` login response, requires manual login. Unknown 400s,
malformed responses, timeouts, server failures, and throttling preserve credentials.
Retry and subsequent foreground transitions can restart failed recovery, respecting
`Retry-After` rather than spinning automatically.

## Ownership, storage, and logout

Each account lifetime has a generation and cancellation job. Protected calls,
profile publication, and academic cache loads check ownership even when a source
ignores cancellation. Academic cache keys include generation, freshness revision,
and query parameters. Cache publication and logout revocation share an ownership
lock. Same-account authentication versions change without revoking displayed data.

Successful login commits credentials, scoped cookies, and timestamp in one encrypted
record. Cookie merges and logout cleanup use the same serialized write boundary.
Logout revokes access and cancels account work immediately; cleanup runs on the
application I/O scope. Subsequent login waits for cleanup. Storage failure remains
visible and retryable until cleanup succeeds. Terminal automatic recovery retains
only the registration for prefilling the form; explicit logout forgets it too.

Legacy readable encrypted credentials are retained, while their cookies and synthetic
expiry are ignored. The first automatic login writes schema 2. Invalid ciphertext,
JSON, or Keystore decryption resets the record to login. DataStore I/O errors remain
recoverable. Backup and transfer rules exclude only `datastore/session.preferences_pb`,
preserving nonsensitive settings.

## Foreground and presentation

The application's activity lifecycle observer counts stopped-to-started transitions,
excluding configuration recreation and the initial launch. Pausing/resuming for an
in-app sheet does not refresh. Every genuine return checks authentication and then
invalidates academic freshness once; authentication's own invalidation is coalesced
with that return.

Visible tabs react to the revision. Other retained tabs refresh when settled into
view. Profile checks its revision on opening, and timetable data refreshes while
open or on its next opening. Calendar month, selected academic period, weekday,
scroll state, and available same-account content survive refresh failures. Retry
is shown for errors; successful screens do not gain a general refresh action.

## Verification

The JVM suite covers expiry boundaries and clock changes, recovery/retry budgets,
shared concurrent login, late 401s, domain error classification, logout/account
races, serialized storage cleanup, encrypted record migration/corruption, startup
identity readiness, and retained feature data. MockWebServer exercises the production
client and Retrofit gate, including concurrency, scoped cookies, throttling, malformed
payloads, and a bounded timeout. Instrumentation includes real activity lifecycle
transitions, accessible recovery feedback, and preservation of the visible tab.

The resolved debug networking dependencies are OkHttp 5.5.0 and Okio 3.18.1. The
[upstream changelog](https://lysine.dev/okhttp/changelogs/changelog/) records the
HTTP/1 request-body timeout fix in 5.5.0 and the delayed-timeout regression fix in
5.3.2 (Okio 3.16.4). The current resolution contains both fixes; no dependency upgrade
is needed. The academic client explicitly configures 15-second connect, 20-second
read/write, and 45-second whole-call timeouts, with connection retries disabled.

Run `:app:assembleDebug :app:testDebugUnitTest :app:lintDebug
:app:assembleDebugAndroidTest`, then focused connected tests on an API 33+ test
device. Platform Keystore and OS backup/transfer behavior require device validation;
JVM encryption mocks do not establish those platform guarantees.
