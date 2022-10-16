#!/bin/bash

#######################################
# Kills the process with the given port
# Arguments:
#   port number of the process to kill
#######################################
function kill_process_at_port() {
    local port=$1
    local pid=$(lsof -i:"$port" | grep LISTEN | awk '{print $2}')
    if [ -n "$pid" ]; then
        kill -TERM $pid
    fi
}

#######################################
# Builds the application
# Arguments:
#  None
#######################################
function build() {
  mvn clean package -DskipTests
}

#######################################
# Executes the jar of the application
# Arguments:
#  None
#######################################
function run_application_jar() {
  java -jar target/rs-chat-backend-0.0.1.jar &> /dev/null &
}

#######################################
# Exports the environment variables needed for the application
# Globals:
#   line
# Arguments:
#   1 - The file to be exported
#######################################
function export_env() {
  # Read the corresponding env file and export the variables
  while read -r line; do
    export $line &> /dev/null
  done < .env."$1"
}

#######################################
# Creates the S3 bucket to store files.
# Globals:
#   AWS_S3_BUCKET_NAME
# Arguments:
#  None
#######################################
function create_s3_bucket() {
  # Check that localstack is running
  if ! curl -s http://localhost:4566/health | grep -q "available"; then
    echo "Localstack is not running"
    exit 1
  fi

  # Create the S3 bucket if it doesn't exist
  aws s3api head-bucket --bucket "$AWS_S3_BUCKET_NAME" 2> /dev/null || aws --endpoint-url=http://localhost:4566 s3 mb s3://"$AWS_S3_BUCKET_NAME"
}

#######################################
# Main function
# Globals:
#   command
# Arguments:
#  None
#######################################
function main() {
  echo 'You are starting the application'
  echo 'Possible commands:'
  echo '  1) Deploy to development environment'
  echo '  2) Deploy to production environment'
  echo '  3) Deploy to all environments'

  local command
  read -rp 'Enter the command number: ' command

  case $command in
    1)
      build
      export_env dev
      create_s3_bucket
      kill_process_at_port "$PORT"
      run_application_jar
      ;;
    2)
      build
      export_env prod
      create_s3_bucket
      kill_process_at_port "$PORT"
      run_application_jar
      ;;
    3)
      build
      export_env dev
      create_s3_bucket
      kill_process_at_port "$PORT"
      run_application_jar

      export_env prod
      create_s3_bucket
      kill_process_at_port "$PORT"
      run_application_jar
      ;;
    *)
      echo 'Invalid command'
      ;;
  esac

}

main "$@"
