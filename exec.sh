#!/bin/bash

run_application_jar() {
	mvn clean package -DskipTests
	java -jar target/rs-chat-backend-0.0.1.jar &> /dev/null &
}

export_env() {
	# Read the corresponding env file and export the variables
	while read -r line; do
	export $line &> /dev/null
done < .env."$1"
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

main() {
	echo 'You are starting the application'
	echo 'Possible commands:'
	echo '  1) Deploy to development environment'
	echo '  2) Deploy to production environment'

	read -rp 'Enter the command number: ' command

	case $command in
		1)
			export_env dev
			create_s3_bucket
			run_application_jar
			;;
		2)
			export_env prod
			create_s3_bucket
			run_application_jar
			;;
		*)
			echo 'Invalid command'
			;;
	esac
}

main
