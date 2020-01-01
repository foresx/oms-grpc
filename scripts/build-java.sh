#!/bin/bash
# VAULT_TOKEN and VAULT_ADDR should have been defined as environment variables

set -euo pipefail
IFS=$'\n\t'

./gradlew clean build -x test --no-daemon