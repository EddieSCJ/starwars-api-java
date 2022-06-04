#!/usr/bin/env bash
# Run the app using local stack SQS and S3

docker load -i /tmp/cache-docker/localstack.tar

docker load -i /tmp/cache-docker/mongo.tar