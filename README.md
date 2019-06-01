# Course-Service

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/11125825afae42dcb243fac2f496ba5b)](https://www.codacy.com/app/mp-access/Course-Service?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=mp-access/Course-Service&amp;utm_campaign=Badge_Grade) [![Codacy Badge](https://api.codacy.com/project/badge/Coverage/11125825afae42dcb243fac2f496ba5b)](https://www.codacy.com/app/mp-access/Course-Service?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=mp-access/Course-Service&amp;utm_campaign=Badge_Coverage) [![CircleCI](https://circleci.com/gh/mp-access/Course-Service/tree/master.svg?style=svg)](https://circleci.com/gh/mp-access/Course-Service/tree/master)

Repo cloner + Data Parser + Spring server

## Properties

`resources/application.properties`

```properties
# Can be set to false to disable security during local development
rest.security.enabled=true

# Initialize course participants, if true, will connect to keycloak to create user accounts and course groups
course.users.init-on-startup=false 
```
