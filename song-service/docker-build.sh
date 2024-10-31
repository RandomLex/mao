#!/usr/bin/env bash

CURRENT_DIR=$(pwd)
cd ..
docker build -f song-service/Dockerfile -t song-service:latest .
cd "$CURRENT_DIR" || exit
