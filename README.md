# notifications-service
An engine for notifying the users about good wind conditions

#Build
docker build -t wkicior/notifications-service .

# Run
docker run --privileged=true -it -v [absolute path to project directory]:/app:rw --rm -p 9999:9000 wkicior/notifications-service

#Usage
curl -v -X POST http://localhost:9999/notifications -H "Content-Type: application/json" -d "{ \"name\" : \"wojtek\", \"favoriteNumber\" : 12 }"

