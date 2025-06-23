# RISE - Women Leadership Development Program Backend

## Overview

RISE is a women-only leadership development program initiated by the National Bank of Kuwait (NBK). This backend MVP supports invite-only access, allows applicants to view public content, and offers tailored content for participants and alumni.

## Tech Stack

- **Spring Boot 3.5.0** with **Kotlin**
- **PostgreSQL** (configurable to H2 for testing)
- **Spring Security** with **JWT authentication**
- **BCrypt** for password hashing
- **Spring Data JPA** for data persistence
- **RESTful APIs**
- **Swagger/OpenAPI** documentation

## Features

### Authentication & Authorization

- JWT-based authentication
- Role-based access control (Applicant, Participant, Alumna)
- BCrypt password hashing
- Secure endpoint protection

### User Management

- User registration and login
- Profile management with skills, bio, and company info
- Role-based content access
- User search and networking

### Event Management

- Create and manage events
- Public and private event types
- RSVP functionality with status tracking
- Event attendee management

### Resource Management

- Role-based resource access
- Multiple resource types (PDF, Video, Link)
- Module-based organization
- Search functionality

### Cohort Management

- Manage program cohorts
- Track participants and alumni
- Cohort status tracking (Active, Upcoming, Completed)

## Database Schema

### Users

- `id` (UUID, Primary Key)
- `email` (Unique)
- `password_hash`
- `role` (APPLICANT | PARTICIPANT | ALUMNA)
- `cohort_id` (UUID, nullable)

### Profiles

- `user_id` (UUID, FK to Users)
- `name`
- `position`
- `company`
- `skills` (Array of strings)
- `bio`
- `image_url`

### Cohorts

- `id` (UUID, Primary Key)
- `name`
- `start_date`
- `end_date`

### Events

- `id` (UUID, Primary Key)
- `title`
- `description`
- `date`
- `location`
- `is_public` (Boolean)

### UserEvents

- `user_id` (FK to Users)
- `event_id` (FK to Events)
- `rsvp_status` (GOING | NOT_GOING | INTERESTED)

### Resources

- `id` (UUID, Primary Key)
- `title`
- `description`
- `type` (PDF | VIDEO | LINK)
- `url`
- `target_roles` (Array of UserRole)
- `module` (Optional)

## API Endpoints

### Authentication

- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login

### Users & Profiles

- `GET /api/users/me` - Get current user
- `GET /api/users/{userId}` - Get user by ID
- `GET /api/users` - Get all users
- `GET /api/users/role/{role}` - Get users by role
- `GET /api/users/cohort/{cohortId}` - Get users by cohort
- `GET /api/profiles/{userId}` - Get user profile
- `PUT /api/profiles/{userId}` - Update user profile
- `GET /api/profiles/search?query={query}` - Search profiles

### Events

- `GET /api/events` - Get all events (authenticated)
- `GET /api/events/upcoming` - Get upcoming events
- `GET /api/events/public` - Get public events (no auth required)
- `GET /api/events/{eventId}` - Get event by ID
- `POST /api/events` - Create event (Participants/Alumni only)
- `PUT /api/events/{eventId}` - Update event
- `DELETE /api/events/{eventId}` - Delete event
- `POST /api/events/rsvp` - RSVP to event
- `GET /api/events/{eventId}/attendees` - Get event attendees
- `GET /api/events/user/{userId}` - Get user's events

### Resources

- `GET /api/resources` - Get accessible resources (role-based)
- `GET /api/resources/{resourceId}` - Get resource by ID
- `GET /api/resources/type/{type}` - Get resources by type
- `GET /api/resources/module/{module}` - Get resources by module
- `GET /api/resources/search?query={query}` - Search resources
- `POST /api/resources` - Create resource (Participants/Alumni only)
- `PUT /api/resources/{resourceId}` - Update resource
- `DELETE /api/resources/{resourceId}` - Delete resource

### Cohorts

- `GET /api/cohorts` - Get all cohorts
- `GET /api/cohorts/{cohortId}` - Get cohort by ID
- `GET /api/cohorts/active` - Get active cohorts
- `GET /api/cohorts/upcoming` - Get upcoming cohorts
- `GET /api/cohorts/completed` - Get completed cohorts
- `POST /api/cohorts` - Create cohort (Participants/Alumni only)
- `PUT /api/cohorts/{cohortId}` - Update cohort
- `DELETE /api/cohorts/{cohortId}` - Delete cohort

## Configuration

### Database Configuration

The application supports both PostgreSQL and H2 databases. Configure in `application.properties`:

**PostgreSQL (Production):**

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/rise_db
spring.datasource.username=postgres
spring.datasource.password=your_password
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

**H2 (Testing):**

```properties
spring.datasource.url=jdbc:h2:mem:rise_db
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
```

### JWT Configuration

```properties
jwt.secret=your-secret-key
jwt.expiration=86400000
```

## Running the Application

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL (if using PostgreSQL database)

### Steps

1. Clone the repository
2. Configure database in `application.properties`
3. Run the application:

```bash
./mvnw spring-boot:run
```

The application will start on port 8000.

## Mock Data

The application includes a `DataLoaderService` that automatically loads mock data on startup:

### Test Accounts

- **Applicant**: `applicant@example.com` / `password123`
- **Participant**: `participant@example.com` / `password123`
- **Alumna**: `alumna@example.com` / `password123`

### Sample Data

- 2 Cohorts (RISE 2024, RISE 2025)
- 3 Users with complete profiles
- 3 Events (1 public, 2 private)
- 4 Resources with role-based access
- Sample RSVPs and user events

## Access Control

### Role-Based Permissions

**Applicant:**

- Can register and login
- Can view public events
- Can view general resources
- Can update own profile

**Participant:**

- All Applicant permissions
- Can view all events and RSVP
- Can access participant-specific resources
- Can create events, resources, and cohorts

**Alumna:**

- All Participant permissions
- Can access alumni-specific resources
- Full access to all features

## API Documentation

Once the application is running, you can access:

- **Swagger UI**: http://localhost:8000/swagger-ui.html
- **API Docs**: http://localhost:8000/api-docs
- **H2 Console** (if using H2): http://localhost:8000/h2-console

## Project Structure

```
src/main/kotlin/com/zaina/zaina/
├── controller/          # REST controllers
├── dto/                # Data transfer objects
├── entity/             # JPA entities
├── mapper/             # Entity-DTO mappers
├── repository/         # Data repositories
├── security/           # Security configuration
├── service/            # Business logic
└── ZainaApplication.kt # Main application class
```

## Security Features

- JWT-based stateless authentication
- CORS configuration for frontend integration
- BCrypt password encryption
- Role-based method security
- Request validation and sanitization
- SQL injection prevention through JPA

## Business Rules

1. **Event Management**: Only Participants and Alumni can create/edit events
2. **Resource Access**: Resources are filtered based on user role
3. **Cohort Management**: Only Participants and Alumni can manage cohorts
4. **Profile Updates**: Users can only update their own profiles
5. **Public Access**: Certain endpoints (registration, public events) don't require authentication

## Next Steps for Production

1. **Environment Configuration**: Setup different profiles for dev/staging/production
2. **External Authentication**: Integrate with LDAP or OAuth providers
3. **File Upload**: Implement file upload for profile images and resources
4. **Email Notifications**: Add email service for event reminders and announcements
5. **Advanced Search**: Implement full-text search capabilities
6. **API Rate Limiting**: Add rate limiting for security
7. **Monitoring**: Add application monitoring and health checks
8. **Admin Dashboard**: Create admin interface for user and content management

## Contributing

1. Follow Kotlin coding conventions
2. Add unit tests for new features
3. Update API documentation
4. Follow the existing project structure
5. Ensure all endpoints have proper security annotations

## License

This project is developed for the National Bank of Kuwait (NBK) RISE program.
