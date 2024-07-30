# WorkAgencyAPI
**WorkAgencyAPI** is an API for handling job vacancies, developed using Spring Boot and Hibernate. This application provides a REST interface for managing job vacancy data.

Technologies:
- Java 22,
- Maven 3.6.3,
- PostgreSQL 13

Plugins:
SonarLint 10.7.0.78874

## Dependencies
The project uses the following key dependencies:
- **Spring Boot**: `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-log4j2`
- **PostgreSQL**: `postgresql` for database connectivity
- **Hibernate**: `hibernate-validator` for data validation
- **Jackson**: `jackson-databind` for JSON processing
- **Apache HttpClient**: `httpclient` for HTTP requests
- **Lombok**: `lombok` for reducing boilerplate code
- **Log4j**: `log4j-api`, `log4j-core` for logging
- **Testing**: `spring-boot-starter-test`, `mockito-core`, `assertj-core` for unit testing

## Deployment to Google Cloud and Kubernetes
This project deployed to Google Cloud using Google Cloud Run and Kubernetes. 
The Docker image for the application was built and pushed to Google Container Registry (GCR). The image is tagged as `gcr.io/job-vacancy-service/job-vacancy-service`.

## Project info
- Project name:
  job-vacancy-service
- Project number:
  170166687816
- Project ID:
  job-vacancy-service
- Service URL: https://job-vacancy-service-nvi4vhwu3a-uc.a.run.app

## API 
1. Retrieves a paginated list of vacancies based on the specified page number.
   The pagination vacancies count set by 20.
  "/api/v1/vacancies"

Example: https://job-vacancy-service-nvi4vhwu3a-uc.a.run.app/api/v1/vacancies?page=1

2. Retrieves a map of city names and the number of vacancies in each city.
"/api/v1/vacancies/city-counts"

Example: https://job-vacancy-service-nvi4vhwu3a-uc.a.run.app/api/v1/vacancies/city-counts

3. Retrieves the top 10 most popular vacancy titles based on their frequency.   
 "/api/v1/top-popular-titles"

Example: https://job-vacancy-service-nvi4vhwu3a-uc.a.run.app/api/v1/top-popular-titles
