# Ad Booking System

Java Spring Boot application that allows advertisers to browse available advertising spaces (billboards, bus stops, mall displays) and create booking requests. The space owner can then approve or reject these requests.

## API Main Features
* List Ad Spaces - Retrieve all available advertising spaces with optional filtering by type and city
* Get Ad Space Details - View detailed information about a specific advertising space
* Manage Booking Requests - Create and manage advertising space booking requests
* Approve/Reject Bookings - Handle booking request approvals and rejections
* View Booking History - Access a list of all booking requests and their details

## Assumptions and Simplifications
* The application focuses on core features such as submitting booking requests. It does not include an admin portal for capabilities like creating ad spaces; instead, it relies on a [script](./src/main/resources/db/dml/insert_ad_spaces.sql) to insert ad spaces directly into the PostgreSQL database.
* An advertiser cannot submit a new booking request if its dates overlap with any existing approved bookings for the same ad space. Likewise, before approving a request, the application verifies that the requested dates do not conflict with other approved bookings.
* The current version of the application allows multiple users to submit booking requests without checking for conflicts with other pending requests. However, administrators may approve events based on a FIFO principle.

## Ad Spaces Booking API – Specification

```GET /api/v1/ad-spaces```
* Retrieves the list of available ad spaces, optionally filtered by city.
* **Query Params**
    * `city` (optional): Filters ad spaces by city (e.g. `Oradea`).
    * `type` (optional): Filters ad spaces by type (e.g. `Billboard`).
* **Response**: Returns a JSON array of ad spaces, each containing details such as identifier, location, city, and availability information.

```GET /api/v1/ad-spaces/{adSpaceUuid}```
* Retrieves detailed information for a specific ad space.
* **Path Params**
    * `adSpaceUuid` (required): The UUID of the ad space (e.g. `f1ba6be7-b085-4bf3-8145-ebc198a9fa`).
* **Response**: Returns a JSON object with full details of the requested ad space.

```GET /api/v1/booking-requests```
* Retrieves all booking requests, optionally filtered by city.
* **Response**: Returns a JSON array of booking requests, including status, requested period, advertiser details, and associated ad space.

```GET /api/v1/booking-requests/{bookingRequestUuid}```
* Retrieves detailed information for a specific booking request.
* **Path Params**
    * `bookingRequestUuid` (required): The UUID of the booking request (e.g. `6b1d2e31-894c-418d-82fe-4ddb9ab7569d`).
* **Response**: Returns a JSON object with full details of the requested booking, including status and requested dates.

```POST /api/v1/booking-requests```
* Creates a new booking request for a given ad space.
* **Request Body**
    * example:
      ```json
      {
        "adSpaceUuid": "283146da-626b-4fb4-8fb5-27a775c20dea",
        "advertiserName": "Ella",
        "advertiserEmail": "Ella@gmail.com",
        "startDate": "2026-01-08",
        "endDate": "2026-03-01"
      }
      ```
* **Response**: Returns a JSON object representing the newly created booking request, including its UUID and initial status (e.g. `PENDING`).

```PATCH /api/v1/booking-requests/{bookingRequestUuid}/approve```
* Approves a pending booking request.
* **Path Params**
    * `bookingRequestUuid` (required): The UUID of the booking request to approve (e.g. `27ab5b82-92e0-4807-b7e8-05ab41008ddb`).
* **Response**: Returns a JSON object with the updated booking request, reflecting the `APPROVED` status and any related metadata.

```PATCH /api/v1/booking-requests/{bookingRequestUuid}/reject```
* Rejects a pending booking request.
* **Path Params**
    * `bookingRequestUuid` (required): The UUID of the booking request to reject (e.g. `fda1bc11-2c9a-4764-aa10-62ef59f372b3`).
* **Response**: Returns a JSON object with the updated booking request, reflecting the `REJECTED` status and any related metadata.

## Project Structure Overview
The project structure follows the Controller-Service-Repository architectural pattern, which provides high scalability and reusability, with each layer having a single responsibility.
````
ad-booking/
├── pom.xml                         # Maven configuration file
├── README.md
├── .gitignore
├── HELP.md
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/generatik/adbooking/
│   │   │       ├── adspace/                     # Domain package for operations related to ad spaces
│   │   │       │   ├── controller/              # Handles HTTP requests for ad space endpoints
│   │   │       │   ├── dto/                     # Data Transfer Objects for ad spaces
│   │   │       │   │   ├── enums/               # Enum definitions used in ad space DTOs
│   │   │       │   ├── entities/                # JPA entities representing ad space tables
│   │   │       │   ├── exceptions/              # Custom exceptions related to ad space operations
│   │   │       │   ├── handlers/                # Exception handlers for returning proper HTTP responses
│   │   │       │   ├── mappers/                 # Maps entities ↔ DTOs for ad space modules
│   │   │       │   ├── repositories/            # Spring Data JPA repositories for ad spaces
│   │   │       │   └── services/                # Business logic layer for ad space functionality
│   │   │       │
│   │   │       ├── bookingrequest/              # Domain package for booking request operations
│   │   │       │   ├── controller/              # Handles HTTP requests for booking requests
│   │   │       │   ├── dto/                     # DTOs for booking requests
│   │   │       │   │   ├── enums/               # Enum definitions used in booking DTOs
│   │   │       │   ├── entities/                # JPA entities for booking request domain
│   │   │       │   ├── mappers/                 # Mapping logic between booking entities and DTOs
│   │   │       │   ├── repositories/            # JPA repositories for booking requests
│   │   │       │   └── services/                # Business logic for booking request processing
│   │   │       │
│   │   │       └── AdbookingApplication.java    # Main application entry point (Spring Boot)
│   │   │
│   │   └── resources/
│   │       ├── db/
│   │       │   ├── ddl/                         # SQL scripts for creating database schema
│   │       │   │   ├── create_ad_space_table.sql
│   │       │   │   └── create_booking_request_table.sql
│   │       │   └── dml/                         # SQL scripts for inserting data (initial seeds)
│   │       │       └── insert_ad_spaces.sql
│   │       │
│   │       ├── application.properties           # Application configuration (DB connection, server config)
│   │       ├── static/                          # Static resources (if applicable)
│   │       └── templates/                       # Templates for views (if applicable)
│   │
│   └── test/                                    # Unit and integration tests
│
└── target/                                      # Compiled classes and build output
````

## Build & Run
1. Navigate to the project root.
2. ```mvn clean install```
3. ```mvn spring-boot:run```

