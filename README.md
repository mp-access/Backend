# Course-Service

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/93c9240be39f4677863ceb23bb0e9d60)](https://app.codacy.com/app/mp-access/Course-Service?utm_source=github.com&utm_medium=referral&utm_content=mp-access/Course-Service&utm_campaign=Badge_Grade_Settings)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/11125825afae42dcb243fac2f496ba5b)](https://www.codacy.com/app/mp-access/Course-Service?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=mp-access/Course-Service&amp;utm_campaign=Badge_Coverage)

Repo cloner + Data Parser + Spring server

## Properties

`resources/application.properties`

```properties
# Can be set to false to disable security during local development
rest.security.enabled=true

# Initialize course participants, if true, will connect to keycloak to create user accounts and course groups
course.users.init-on-startup=false 
```
