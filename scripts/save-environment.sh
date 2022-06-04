#!/usr/bin/env bash
# Run the app using local stack SQS and S3

docker save -o /tmp/cache-docker/localstack.tar localstack/localstack

docker save -o  /tmp/cache-docker/mongo.tar mongo