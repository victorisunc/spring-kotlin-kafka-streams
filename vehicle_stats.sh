#!/bin/bash

set -B

while getopts v: flag; do
  case "${flag}" in
  v) vehicle=${OPTARG} ;;
  esac
done
echo "Streaming stats for vehicle: $vehicle"
printf "\n"

curl -X GET http://localhost:8080/vehicle/$vehicle/stream -H "Accept: text/event-stream"
