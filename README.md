# Electricity Statistics Dashboard

A full-stack application for viewing electricity consumption and production statistics, built with Vue 3, Spring Boot, and PostgreSQL. The project runs using Docker Compose.

## Features

- View daily electricity statistics (consumption, production, average price, etc.).
- Search for specific dates using a date picker.
- Paginated view for easy navigation.
- Interactive chart displaying monthly averages.
- PostgreSQL database with Adminer for easy management.

## Technologies Used

- **Frontend:** Vue 3, Chart.js, Axios
- **Backend:** Spring Boot, Java 21
- **Database:** PostgreSQL 16 with Adminer
- **Containerization:** Docker, Docker Compose

## Running the Project

### Prerequisites

- Install **Docker** and **Docker Compose**.

### Start the application

1. In the project root directory, run the following command to build and start the application:

docker-compose up --build

2. Access the services:

- **Frontend (Vue.js app):** [http://localhost:5173](http://localhost:5173)
- **Backend API:** [http://localhost:8080](http://localhost:8080)
- **Database Adminer:** [http://localhost:8088](http://localhost:8088) (Login with `academy / academy`)

### Stopping the Application

To stop the running containers, use:

docker-compose down

## Project Structure

/backend # Spring Boot backend
/frontend # Vue.js frontend
/database # PostgreSQL database setup
/docker-compose.yml # Docker configuration

## To-Do

- Add tests
- Add error handling
- Add comments
- Refactor and add new endpoints in backend
- Improve frontend
- Find more free time to do this all !
