#!/bin/bash
#
# Brief description of your script
# Copyright 2022 samuel

#######################################
# description
# Arguments:
#  None
#######################################
function main() {
  mvn clean package -DskipTests
  docker image rm rs-chat
  docker build -t rs-chat .
  docker-compose up
}

main "$@"
