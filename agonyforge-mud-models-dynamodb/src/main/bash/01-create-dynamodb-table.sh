#!/usr/bin/env bash

set -x

# create DynamoDB table in DynamoDbLocal
# If you remove the $AWS_ENDPOINT argument, this ought to work for real in AWS too.
# My personal preference would be to use the CloudFormation template instead, though.

aws $AWS_ENDPOINT \
    dynamodb create-table \
        --no-paginate \
        --table-name agonyforge \
        --billing-mode PAY_PER_REQUEST \
        --sse-specification "Enabled=true" \
        --attribute-definitions \
            AttributeName=pk,AttributeType=S \
            AttributeName=sk,AttributeType=S \
            AttributeName=gsi1pk,AttributeType=S \
            AttributeName=gsi2pk,AttributeType=S \
        --key-schema \
            AttributeName=pk,KeyType=HASH \
            AttributeName=sk,KeyType=RANGE \
        --global-secondary-indexes \
            "[
                {
                    \"IndexName\": \"gsi1\",
                    \"KeySchema\": [{\"AttributeName\":\"gsi1pk\",\"KeyType\":\"HASH\"},{\"AttributeName\":\"pk\",\"KeyType\":\"RANGE\"}],
                    \"Projection\": {
                        \"ProjectionType\":\"ALL\"
                    }
                },
                {
                    \"IndexName\": \"gsi2\",
                    \"KeySchema\": [{\"AttributeName\":\"gsi2pk\",\"KeyType\":\"HASH\"},{\"AttributeName\":\"pk\",\"KeyType\":\"RANGE\"}],
                    \"Projection\": {
                        \"ProjectionType\":\"ALL\"
                    }
                }
            ]"

# sets the TTL attribute in table
aws $AWS_ENDPOINT \
    dynamodb update-time-to-live \
        --table-name agonyforge \
        --time-to-live-specification "Enabled=true, AttributeName=ttl"
