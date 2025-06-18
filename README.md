# advanced-todo
To-Do project with authentication using JWT

## Overview
This project is a To-Do application that allows users to create, read, 
update, and delete tasks. It includes user authentication using JSON Web
Tokens (JWT) for secure access. When user completes a task, the application
send a notification to the user via email.

## Features
#### Branch: v1
- User registration and login
- JWT-based authentication
- CRUD operations for tasks
- Error handling and validation
- Database integration (PostgreSQL)
- CORS support for cross-origin requests
- Deployment-ready with Docker support

#### Branch: v2 (master)
- Kafka integration for asynchronous task notification
- Email notification when a task is completed
- Improved error handling and logging
- Save notifications in redis database

### Technologies Used
- Spring Boot with JPA
- PostgreSQL
- JWT (jsonwebtoken)
- bcrypt for password hashing 
- Docker for containerization
- Kafka for messaging
- Redis for caching notifications
- Resend for email notifications

### Getting Started

1. Clone the repository:
   ```bash
   git clone
   ```
2. Use Docker to build and run the application: (install Docker and Docker
Compose if not already installed)
   ```bash
   docker-compose up --build
   ``` 
   If you want to stop the application, you can use:
   ```bash
    docker-compose down
    ```
   

With Docker, we manage the database and application in containers, 
ensuring a consistent environment across development and production.
    
Docker Compose will automatically set up the PostgreSQL database and the
Spring Boot application. You only need to change the `docker-compose.yaml`
file to configure the database connection settings and jwt secret key:

    - POSTGRES_USER
    - POSTGRES_PASSWORD
    - SPRING_DATASOURCE_USERNAME (should match POSTGRES_USER)
    - SPRING_DATASOURCE_PASSWORD (should match POSTGRES_PASSWORD)
    - JWT_SECRET

In v2 version, we configure kafka and zookeeper, but you can use the default
settings to run localhost. Resend needs an API key, which you can get from 
official website and set in the `docker-compose.yaml` file changing the following
environment variables:

    - RESEND_API_KEY
    
##### Note
    healthcheck.test property in docker-compose.yaml has by default *pg_isready -U user*, but user should match POSTGRES_USER, so if you change POSTGRES_USER