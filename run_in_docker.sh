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
  export_env_variables env/.env.dev

  sleep 10

  ./mvnw spring-boot:run
}

main "$@"
