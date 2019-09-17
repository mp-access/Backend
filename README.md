# Course-Service

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/11125825afae42dcb243fac2f496ba5b)](https://www.codacy.com/app/mp-access/Course-Service?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=mp-access/Course-Service&amp;utm_campaign=Badge_Grade) [![Codacy Badge](https://api.codacy.com/project/badge/Coverage/11125825afae42dcb243fac2f496ba5b)](https://www.codacy.com/app/mp-access/Course-Service?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=mp-access/Course-Service&amp;utm_campaign=Badge_Coverage) [![CircleCI](https://circleci.com/gh/mp-access/Course-Service/tree/master.svg?style=svg)](https://circleci.com/gh/mp-access/Course-Service/tree/master)

Repo cloner + Data Parser + Spring server

## Properties

Application specific properties: `resources/application.properties` 

```properties
# Can be set to false to disable security during local development
rest.security.enabled=true

# Initializing users
# Initialize course participants, if true, will connect to the identity provider to create user accounts and course groups
course.users.init-on-startup=false
# Use a default password when creating new users for development
course.users.use-default-password-for-new-accounts=true
# Value of the default password
course.users.default-password=<default-pwd-for-development>

# OpenID: URL to identity provider
rest.security.auth-server=http://localhost:9999/auth

# Evaluation worker queue configuration
submission.eval.thread-pool-size=10
submission.eval.max-pool-size=20
submission.eval.queue-capacity=500

submission.eval.user-rate-limit=false # If set to true users have to wait for their submission to be corrected before they can submit again

# Activate performance test logging
logging.level.org.springframework.aop.interceptor.PerformanceMonitorInterceptor=trace
```

## API Docs
With the server application running: http://localhost:8080/api/swagger-ui.html

Works also using `rest.security.enabled=true` or `rest.security.enabled=false`

When the flag is set to true, click on `authorize`, for `client_id` enter `access-frontend` and click on `Authorize`
