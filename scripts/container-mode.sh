#!/usr/bin/env bash
# Run the app using local stack SQS and S3

docker compose -f docker-compose.yml -f docker-compose.container.yml up -d --build
