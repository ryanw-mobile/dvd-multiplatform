# Amazon Q Review Standards — dvd-multiplatform

Persistent instructions for Amazon Q Developer's automated PR review. Q has no memory between
review rounds — this file is the only durable record of prior review decisions. Read it before
raising a finding.

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
"path traversal" finding against this line.

## General guidance

- This repository is a small KMP demo app (`dvd-multiplatform`), not a security-sensitive
  production service. Favor concrete, reproducible bugs and crash risks over theoretical or
  style-preference findings.
- Prefer flagging genuinely new code/logic introduced by a PR's diff over pre-existing code that
  merely changed location, file name, or surrounding module wiring.
