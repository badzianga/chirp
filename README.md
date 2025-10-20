# Chirp

Chirp is a work-in-progress backend for Twitter clone written in Java and Spring Boot. I don't understand all concepts
of backend development (and Spring Boot) so I use this project as a playground, but with a plan to complete this app
with proper security, testing, and front-end.

## Endpoints

All endpoints start with prefix `/api/v1`. When something is returned, it is wrapped in JSON containing a message and
(optionally) an object.

### auth

- POST /user/register - add a new user using request body {"email", "username", "password"} and return JSON with created
user data


### user

- GET /user/all - return list of all registered users
- GET /user/find/{username} - return list of users with username similar to passed as path variable
