# advanced-todo
To-Do project with authentication using JWT

### Overview
This project is a To-Do application that allows users to create, read, update, and delete tasks. It includes user authentication using JSON Web Tokens (JWT) for secure access.

### Features (branch: v1 - version 1.0.0)
- User registration and login
- JWT-based authentication
- CRUD operations for tasks
- Error handling and validation
- Database integration (PostgreSQL)
- CORS support for cross-origin requests
- Deployment-ready with Docker support

### Technologies Used
- Spring Boot with JPA
- PostgreSQL
- JWT (jsonwebtoken)
- bcrypt for password hashing 
- Docker for containerization

### Getting Started

1. Clone the repository:
   ```bash
   git clone
   ```
2. Use Docker to build and run the application: (install Docker and Docker Compose if not already installed)
   ```bash
   docker-compose up --build
   ``` 
    With Docker, we manage the database and application in containers, ensuring a consistent environment across development and production.
    
    Docker Compose will automatically set up the PostgreSQL database and the Spring Boot application. You only need to change the `docker-compose.yaml` file to configure the database connection settings and jwt secret key:
    - POSTGRES_USER
    - POSTGRES_PASSWORD
    - SPRING_DATASOURCE_USERNAME (should match POSTGRES_USER)
    - SPRING_DATASOURCE_PASSWORD (should match POSTGRES_PASSWORD)
    - JWT_SECRET

    ##### Note
    healthcheck.test property in docker-compose.yaml has by default *pg_isready -U user*, but user should match POSTGRES_USER, so if you change POSTGRES_USER