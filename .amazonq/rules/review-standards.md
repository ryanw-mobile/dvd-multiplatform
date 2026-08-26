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

## General guidance

- This repository is a small KMP demo app (`dvd-multiplatform`), not a security-sensitive
  production service. Favor concrete, reproducible bugs and crash risks over theoretical or
  style-preference findings.
- Prefer flagging genuinely new code/logic introduced by a PR's diff over pre-existing code that
  merely changed location, file name, or surrounding module wiring.
