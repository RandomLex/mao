#!/usr/bin/env bash

CURRENT_DIR=$(pwd)
cd ..
docker build -f resource-service/Dockerfile -t resource-service:latest .
cd "$CURRENT_DIR" || exit
