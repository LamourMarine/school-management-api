# School Management System - REST API

A REST API built with Spring Boot for managing students, courses, and grades.

## Technologies

- **Java 21**
- **Spring Boot 3.4**
- **Spring Data JPA** (Hibernate)
- **PostgreSQL 15**
- **Maven**
- **Docker** (for PostgreSQL)

## Features

### Complete CRUD operations
- ✅ Student management
- ✅ Course management
- ✅ Grade management

### Advanced features
- ✅ Retrieve all grades for a student
- ✅ Calculate student average
- ✅ Retrieve all grades for a course
- ✅ ManyToOne relationships (Grade → Student, Grade → Course)

## Installation

### Prerequisites
- Java 21
- Docker & Docker Compose
- Maven

### Setup

1. **Clone the repository**
```bash
git clone https://github.com/LamourMarine/school-management-api.git
cd school-management-api
```

2. **Start PostgreSQL with Docker**
```bash
docker-compose up -d
```

3. **Run the application**
```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`

## API Endpoints

### Students
- `GET /api/students` - List all students
- `GET /api/students/{id}` - Get a student by ID
- `POST /api/students` - Create a student
- `PUT /api/students/{id}` - Update a student
- `DELETE /api/students/{id}` - Delete a student
- `GET /api/students/{id}/grades` - Get all grades for a student
- `GET /api/students/{id}/average` - Calculate student average

### Courses
- `GET /api/courses` - List all courses
- `GET /api/courses/{id}` - Get a course by ID
- `POST /api/courses` - Create a course
- `PUT /api/courses/{id}` - Update a course
- `DELETE /api/courses/{id}` - Delete a course
- `GET /api/courses/{id}/grades` - Get all grades for a course

### Grades
- `GET /api/grades` - List all grades
- `GET /api/grades/{id}` - Get a grade by ID
- `POST /api/grades` - Create a grade
- `PUT /api/grades/{id}` - Update a grade
- `DELETE /api/grades/{id}` - Delete a grade

## Data Model
```
Student (1) ←──── (N) Grade (N) ────→ (1) Course
```

**Student**
- id (Long)
- lastName (String)
- firstName (String)

**Course**
- id (Long)
- title (String)
- code (String, unique)
- teacher (String)

**Grade**
- id (Long)
- score (Double)
- student (Student)
- course (Course)

## Example Requests

### Create a student
```json
POST /api/students
{
  "lastName": "Doe",
  "firstName": "John"
}
```

### Create a course
```json
POST /api/courses
{
  "title": "Mathematics",
  "code": "MATH101",
  "teacher": "Mr. Smith"
}
```

### Create a grade
```json
POST /api/grades
{
  "score": 15.5,
  "studentId": 1,
  "courseId": 1
}
```

## Project Goals

Learning project built to:
- Master Spring Boot architecture (MVC pattern)
- Understand JPA/Hibernate and entity relationships
- Build a complete REST API
- Prepare for a Java developer apprenticeship

## Next Steps

- [ ] Data validation (Bean Validation)
- [ ] Centralized error handling
- [ ] React frontend
- [ ] Spring Security (JWT authentication)
- [ ] Unit tests (JUnit)
- [ ] Swagger documentation
- [ ] Deployment

## Author

**Marine** - Full-Stack Developer in training  
https://ml-dev.netlify.app/

## License

This project is open source and available under the MIT License.