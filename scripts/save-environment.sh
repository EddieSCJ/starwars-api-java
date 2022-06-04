#!/usr/bin/env bash
# Run the app using local stack SQS and S3

docker save /tmp/cache-docker/localstack.tar localstack/localstack

docker save /tmp/cache-docker/mongo.tar mongo