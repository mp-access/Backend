# Course-Service
Repo cloner + Data Parser + Spring server

## Properties

`resources/application.properties`

```properties
# Can be set to false to disable security during local development
rest.security.enabled=true

# Initialize course participants, if true, will connect to keycloak to create user accounts and course groups
course.users.init-on-startup=false 
```