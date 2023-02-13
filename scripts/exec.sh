#!/bin/bash

# Runs the application in Docker
# Copyright 2022 samuel

#######################################
# Main function of the script
# Arguments:
#  None
#######################################
function main() {
  cd .. \
    && docker build -t rs-chat . \
    && docker-compose up -d --force-recreate
}

# Run the main function
main "$@"
