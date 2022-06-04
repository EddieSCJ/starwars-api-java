#!/usr/bin/env bash
# Run the app using local stack SQS and S3

docker save -o /tmp/cache-docker/localstack.tar docker.io/library/localstack/localstack:latest

docker save -o  /tmp/cache-docker/mongo.tar docker.io/library/mongo:latest