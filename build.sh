#!/bin/bash

# build image
docker build \
    --file ./Dockerfile \
    --tag micronaut \
    --build-arg APP_NAME=blank \
    --build-arg APP_VERSION=0.0.1 \
    .
