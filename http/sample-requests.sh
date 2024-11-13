#!/bin/bash

# create geofence
curl -i -X POST \
  http://localhost:8080/geofence \
  -H 'Content-Type: application/json' \
  -d '
{
  "id" : "office-geofence",
  "latitude" : 50.07672471302663,
  "longitude" : 19.94672587408553,
  "radiusInMeters" : 100
}
'

# request position outside geofence
curl -i -X POST \
  http://localhost:8080/user-location \
  -H 'Content-Type: application/json' \
  -d '
  {
    "userId" : "38400000-8cf0-11bd-b23e-10b96e4ef00d",
    "latitude" : 51.07702766750613,
    "longitude" : 19.947519807887176
  }
'

# request inside inside geofence
curl -i -X POST \
  http://localhost:8080/user-location \
  -H 'Content-Type: application/json' \
  -d '
  {
    "userId" : "38400000-8cf0-11bd-b23e-10b96e4ef00d",
    "latitude" : 50.07702766750613,
    "longitude" : 19.947519807887176
  }
'

# pull queue
aws sqs receive-message --queue-url http://localhost:4566/000000000000/geofence-triggered-queue --endpoint-url http://localhost:4566

aws sqs purge-queue \
  --queue-url http://localhost:4566/000000000000/geofence-triggered-queue \
  --endpoint-url http://localhost:4566
