#!/bin/bash
# VAULT_TOKEN and VAULT_ADDR should have been defined as environment variables

set -euo pipefail
IFS=$'\n\t'

APP_ENV=$1
APP_VERSION=$2
IMAGE_TAG=$3

if [[ $APP_ENV == '' || $APP_VERSION == '' || $IMAGE_TAG == '' ]];
then
    echo "Usage: $0 <env> <app version>"
    exit 1
fi

# ./gradlew clean build -x test --no-daemon
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJ_DIR=${DIR}/..

APP_NAME=oms-deploy
MODULE_DIR=${PROJ_DIR}/deploy
docker build -t $APP_NAME:${APP_ENV}_${IMAGE_TAG} \
    -f ${MODULE_DIR}/Dockerfile --force-rm \
    --build-arg VAULT_TOKEN="$VAULT_TOKEN" \
    --build-arg APP_NAME="$APP_NAME" \
    --build-arg APP_VERSION="$APP_VERSION" \
    --build-arg APP_ENV="$APP_ENV" ${MODULE_DIR}


# build oms-graphql image
APP_NAME=oms-graphql
echo "Building image for ${APP_NAME}"
MODULE_DIR=${PROJ_DIR}/${APP_NAME}
docker build -t $APP_NAME:${APP_ENV}_${IMAGE_TAG} \
    -f ${MODULE_DIR}/Dockerfile --force-rm \
    --build-arg VAULT_TOKEN="$VAULT_TOKEN" \
    --build-arg APP_NAME="$APP_NAME" \
    --build-arg APP_VERSION="$APP_VERSION" \
    --build-arg APP_ENV="$APP_ENV" ${MODULE_DIR}

# build oms-rest image
APP_NAME=oms-rest
echo "Building image for ${APP_NAME}"
MODULE_DIR=${PROJ_DIR}/${APP_NAME}
docker build -t $APP_NAME:${APP_ENV}_${IMAGE_TAG} \
    -f ${MODULE_DIR}/Dockerfile --force-rm \
    --build-arg VAULT_TOKEN="$VAULT_TOKEN" \
    --build-arg APP_NAME="$APP_NAME" \
    --build-arg APP_VERSION="$APP_VERSION" \
    --build-arg APP_ENV="$APP_ENV" ${MODULE_DIR}