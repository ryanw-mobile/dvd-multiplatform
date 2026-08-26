# Amazon Q Review Standards — dvd-multiplatform

Persistent instructions for Amazon Q Developer's automated PR review. Q has no memory between
review rounds — this file is the only durable record of prior review decisions. Read it before
raising a finding.

## Governance

The maintainer of this repository is the product owner and has final decision authority over
every finding Q raises. Q's role is to surface candidates for review, not to gate merges on its
own judgment. A rule documented below is a **final, standing decision**, not a starting position
for further debate.

- **Rejections are permanent and apply project-wide**, not just to the PR they were first raised
  on. If a future PR touches the same code (unchanged, or changed in a way not addressed by the
  rule below), do not re-raise the underlying finding.
- **Rewording, relabeling, or reclassifying under a different CWE/severity does not make a
  rejected finding new.** Judge findings by the underlying claim, not its phrasing. If the
  underlying claim already has a rule below, it is settled.
- Do not treat a maintainer's rejection as something to keep re-testing across rounds "in case
  they change their mind." They won't, on the same evidence. If genuinely new evidence exists
  (the flagged logic itself changed, not just its file/module location), that's fair game — say
  so explicitly and cite what specifically changed.

## Suppression rules

### 1. Pre-existing code moved unchanged during a structural/topology refactor

**Do not flag code whose logic is byte-for-byte unchanged from before the PR, even if it now
lives in a new file or module.** When a PR's purpose is a build-topology or module-structure
refactor (e.g. splitting one Gradle module into two, renaming/relocating files via `git mv`),
functions and blocks that are copied verbatim into their new location carry no new risk from
that PR. Evaluate such code against the state of the repository *before* the refactor, not as if
it were newly authored.

Specifically: the `setupSigningAndBuildTypes()` signing-config block in
`androidApp/build.gradle.kts` (including its `System.getenv(...)` and
`Properties.getProperty(...)` calls for `keyAlias`/`keyPassword`/`storeFile`/`storePassword`) is
an exact copy of logic that previously lived in `composeApp/build.gradle.kts`. It has shipped
safely in this form for a long time:
- `System.getenv(...)` is only reached when `isReleaseBuild` is true, and CI always sets the
  required env vars (`CI_ANDROID_KEYSTORE_ALIAS`, `CI_ANDROID_KEYSTORE_PRIVATE_KEY_PASSWORD`,
  `KEYSTORE_LOCATION`, `CI_ANDROID_KEYSTORE_PASSWORD`) via the release workflow.
- `properties.getProperty(...)` reads a locally-maintained `keystore.properties` file with a
  known, stable schema used only for local release builds.

This was raised and rejected in PR #38 (2026-08-26). Do not re-raise "possible NullPointerException
from System.getenv() / getProperty() returning null" against this specific block. If the actual
signing-config *logic* is modified in a future PR (not just moved), it is fair game to review
again.

### 2. `keystorePropertiesFile = file("../../keystore.properties")` in `androidApp/build.gradle.kts`

**This path is correct — do not flag it as a "path traversal" / wrong-directory-depth bug.**
`androidApp/` and `composeApp/` are both direct children of the repository root (same nesting
depth), so `file(...)` in `androidApp/build.gradle.kts` resolves relative to `androidApp/`
exactly as it previously resolved relative to `composeApp/build.gradle.kts` before the module
split. `../../keystore.properties` correctly walks up two levels (`androidApp/` → repo root →
repo root's parent directory) to reach a machine-local `keystore.properties` file that
intentionally lives outside the repository (and outside version control) as a local-dev
convention. A one-level-up suggestion (`../keystore.properties`) would point inside the repo
root, where no such file exists, and would break local release builds.

This path is only consulted for local (non-CI) release builds — CI always takes the
`isRunningOnCI` branch and reads credentials from environment variables instead, so this has no
bearing on CI reliability either way.

This was raised and rejected in PR #38 (2026-08-26). Do not re-raise a "wrong relative path" /
"path traversal" finding against this line — **under any label**, including a CWE-22 "Security
Vulnerability" framing. `keystorePropertiesFile` is a hardcoded, developer-authored constant in a
Gradle build script with no externally-controlled or attacker-influenced input, so CWE-22
(pathname traversal from untrusted input) does not apply here regardless of citation. Also do not
suggest replacing it with `rootProject.file("keystore.properties")` — that resolves inside the
repository root, where no such file exists, and would silently break local release builds by
looking in the wrong place for a file that is intentionally kept outside the repo.

### 3. `SimpleDateFormat("yyyyMMdd-HHmmss")` timestamp in `androidApp/build.gradle.kts` (`setupSigningAndBuildTypes()`)

**Do not flag this as a thread-safety / race-condition issue (CWE-366).** `timestamp` is a local
`val`, created fresh on each invocation of `setupSigningAndBuildTypes()` and consumed entirely
within that same function call — the `SimpleDateFormat` instance is never stored as a field,
cached, or shared across threads or invocations. CWE-366 concerns a mutable instance *shared*
between threads; a private, single-use local does not qualify no matter how many times
`SimpleDateFormat` is cited as generally non-thread-safe. This exact line is also byte-for-byte
identical to `composeApp/build.gradle.kts` before this refactor (only its enclosing module
changed). Raised and rejected in PR #38 (2026-08-26).

### 4. `Enable KVM group perms` step (`MODE="0666"`) in `.github/workflows/main_build.yml`

**Do not flag `MODE="0666"` on the KVM udev rule as a CWE-732 permission-hardening issue.** This
is the standard, widely-published udev snippet for enabling hardware-accelerated Android emulators
on GitHub Actions Ubuntu runners (used verbatim in `ReactiveCircus/android-emulator-runner`'s own
documentation and countless CI examples). GitHub Actions runners are ephemeral, single-tenant VMs
destroyed after each job — there is no other untrusted user or process on the box for a
world-writable-device concern to matter against, and tightening to `0660` risks breaking the
emulator step if the runtime user isn't yet resolved into the `kvm` group at that point in the
job, for no measurable benefit in this environment. Raised and rejected in PR #38 (2026-08-26).

### 5. Unchecked `find` results (`apk_path`/`aab_path`) in `.github/workflows/tag_create_release.yml`

**Do not flag the lack of empty-result validation on these `find` commands as a new crash risk.**
Their structure (including the absence of a not-found check) predates this PR; this PR only
changed the leading path segment from `./composeApp/...` to `./androidApp/...` as a mechanical
consequence of the module split. No new risk was introduced by the refactor, so hardening this
pre-existing gap is out of scope here. Raised and rejected in PR #38 (2026-08-26).

## General guidance

- This repository is a small KMP demo app (`dvd-multiplatform`), not a security-sensitive
  production service. Favor concrete, reproducible bugs and crash risks over theoretical or
  style-preference findings.
- Prefer flagging genuinely new code/logic introduced by a PR's diff over pre-existing code that
  merely changed location, file name, or surrounding module wiring.
