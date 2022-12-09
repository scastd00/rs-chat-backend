#!/bin/bash

# This script is used to run the application inside a docker container

#######################################
# Exports the environment variables
# Globals:
#   line - the line to be exported
# Arguments:
#   1 - the file to be read
#######################################
function export_env_variables() {
  while IFS= read -r line; do
    export "$line"
  done < "$1"
}

#######################################
# Main function
# Arguments:
#  None
#######################################
function main() {
  export_env_variables env/.env.prod

  ./mvnw package -DskipTests
  echo "Sleeping for 5 seconds to allow the database to start"
  sleep 5
  java -jar -Dspring.profiles.active=prod target/rs-chat-backend-0.0.1.jar
}

main "$@"
