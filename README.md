# Chirp

Chirp is a work-in-progress backend for Twitter clone written in Java and Spring Boot. I don't understand all concepts
of backend development (and Spring Boot) so I use this project as a playground, but with a plan to complete this app
with proper security, testing, and front-end.

## Endpoints

All endpoints start with prefix `/api/v1`. When something is returned, it is wrapped in JSON containing a message and
(optionally) an object.

### auth

- `POST /auth/register` - add a new user using request body {"email", "username", "password"} and return JSON with
created user data

### posts
- `GET /posts` - return list of all posts
- `GET /posts/{postId}` - return json with data of one post with given id
- `POST /posts` - create a new post using request body {"content", "userId"} and return JSON with created post data
- `DELETE /posts/{postID}` - delete post from the database

### search
- `GET /search/users?query={username}` - return list of users with username similar to passed as request param
- `GET /search/posts?query={phrase}` - return list of posts containing phrase passed as request param

### users

- `GET /users` - return list of all registered users
- `GET /users/{username}` - return json with data of the found user
- `DELETE /users/{userId}` - delete user from the database
