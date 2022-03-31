#!/bin/bash

set -B

while getopts n:r: flag; do
  case "${flag}" in
  n) num_vehicles=${OPTARG} ;;
  r) rate=${OPTARG} ;;
  esac
done
echo "Number of vehicles: $num_vehicles"
echo "Rate (POSTs/sec): $rate"
printf "\n"

# Read in a list of GPS coordinates from a file, into an array of latitude and an array of longitude coordinates
IFS=$'\n' read -d '' -r -a coordinates <./around_the_block_gps_coordinates.txt
lats=()
lons=()
for t in "${!coordinates[@]}"; do
  IFS=',' read -r -a line <<<"${coordinates[t]}"
  lats+=("${line[0]}")
  lons+=("${line[1]}")
done

arr_size="${#lons[@]}"
counter=0
post_position() {
  echo -e "Vehicle Number: $3 \t lat: $1    \t lon: $2"
  #echo -n "."
  curl -s -o /dev/null -X POST --header "Content-Type:application/json" \
    -d '{"lat":"'"$1"'",
         "lon": "'"$2"'"
        }' \
    http://localhost:8080/vehicle/$3
}
export -f post_position

while sleep $rate; do
  echo "--${counter}----------------------------------------------------------------------------"
  seq 1 $num_vehicles | xargs -n1 -P$num_vehicles bash -c 'post_position "$@"' vin ${lats[counter % arr_size]} ${lons[counter % arr_size]}
  let "counter+=1"
done
