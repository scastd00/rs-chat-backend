#!/bin/bash

run_jar() {
	mvn clean package -DskipTests
	java -jar target/rs-chat-backend-0.0.1.jar
}

export_env() {
	# Read the .env file and export the variables
	while read -r line; do
		export "$line"
	done < .env
}

create_s3_bucket() {
	# Check that localstack is running
	if ! curl -s http://localhost:4566/health | grep -q "available"; then
		echo "Localstack is not running"
		exit 1
	fi

	# Create the S3 bucket if it doesn't exist
	aws s3api head-bucket --bucket "$AWS_S3_BUCKET_NAME" 2> /dev/null || aws --endpoint-url=http://localhost:4566 s3 mb s3://"$AWS_S3_BUCKET_NAME"
}

export_env
create_s3_bucket
run_jar
