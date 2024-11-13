# gps-tracker
A simple application that track user location and store it in database. 
It also checks if user either entered or left geofence.

## Requirements
- Java 21
- any docker runtime environment like colima
- aws cli installed - only for testing queue
- it may also require to create link to docker.sock in my case:
```bash
sudo ln -s /Users/<my_user>/.colima/default/docker.sock /var/run/docker.sock
```

## Running
- go to `gps-tracker/docker` directory
- run `docker compose up -d`
- in main directory run `./mvnw spring-boot:run`
- to run sample requests execute script `./http/sample-requests.sh` 

## Testing
- to run all tests execute `./mvnw test`
- to run unit tests execute `./mvnw test -Dtests=unit`
- to run integration tests execute `./mvnw test -Dtests=integration`
- The test [UserLocationIntegrationTest](src/test/java/org/net/gpstracer/infrastructure/api/UserLocationIntegrationTest.java) 
contains examples of possible flows that application can handle. Integration tests does not 
require to run docker compose but requires to have docker environment up and running.

