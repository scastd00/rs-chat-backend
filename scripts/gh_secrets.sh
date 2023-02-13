#!/bin/bash
#
# Brief description of your script
# Copyright 2023 samuel

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
  export_env_variables ../env/.env.gh
  node update_gh_secrets.js
}

# Call the main function
main "$@"
