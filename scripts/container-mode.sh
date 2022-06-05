#!/usr/bin/env bash
# Run the app using local stack SQS and SNS

docker compose -f docker-compose.yml -f docker-compose.container.yml up -d --build
