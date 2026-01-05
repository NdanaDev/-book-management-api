# Book Management REST API

A comprehensive RESTful API for managing books built with Spring Boot, JPA, and MySQL. This application provides full CRUD operations, advanced search capabilities, and robust error handling.

## Features

- **Complete CRUD Operations**: Create, Read, Update, and Delete books
- **Advanced Search**: Search by title, author, ISBN, publication year, and year ranges
- **Bulk Operations**: Add multiple books at once
- **Data Validation**: Input validation and business logic enforcement
- **Error Handling**: Comprehensive exception handling with proper HTTP status codes
- **Database Integration**: Support for MySQL and H2 databases
- **RESTful Design**: Clean, intuitive API endpoints following REST principles

## Technologies Used

- **Java 17+**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **MySQL** (Production)
- **H2 Database** (Development/Testing)
- **Maven** - Dependency management
- **Jakarta Persistence API** (JPA)

## Prerequisites

Before running this application, make sure you have:

- Java 17 or higher installed
- Maven 3.6+ installed
- MySQL 8.0+ (if using MySQL instead of H2)
- Git

## Installation & Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/NdanaDev/-book-management-api.git
   cd -book-management-api
   ```

2. **Configure the database**

   Edit `src/main/resources/application.properties`:

   **For MySQL:**
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/bookdb
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   ```

   **For H2 (in-memory):**
   ```properties
   spring.datasource.url=jdbc:h2:mem:testdb
   spring.datasource.driverClassName=org.h2.Driver
   spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
   ```

3. **Build the project**
   ```bash
   ./mvnw clean install
   ```

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

The API will start on `http://localhost:8082`

## API Endpoints

### Create Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/books` | Add a new book |
| POST | `/api/books/bulk` | Add multiple books |

### Read Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/books` | Get all books |
| GET | `/api/books/{id}` | Get book by ID |
| GET | `/api/books/isbn/{isbn}` | Get book by ISBN |
| GET | `/api/books/search?title={title}` | Search books by title |
| GET | `/api/books/search/advanced?term={term}` | Search by title or author |
| GET | `/api/books/author/{author}` | Get books by author |
| GET | `/api/books/year/{year}` | Get books by publication year |
| GET | `/api/books/year-range?start={year}&end={year}` | Get books by year range |
| GET | `/api/books/authors` | Get all unique authors |
| GET | `/api/books/count` | Get total book count |
| GET | `/api/books/stats` | Get database statistics |

### Update Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| PUT | `/api/books/{id}` | Update a book (full update) |
| PATCH | `/api/books/{id}` | Partial update (specific fields) |

### Delete Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| DELETE | `/api/books/{id}` | Delete book by ID |
| DELETE | `/api/books/isbn/{isbn}` | Delete book by ISBN |
| DELETE | `/api/books/author/{author}` | Delete all books by author |
| DELETE | `/api/books/all` | Delete all books |

### Utility

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/books/health` | Health check |

## Request/Response Examples

### Create a Book
```bash
POST http://localhost:8082/api/books
Content-Type: application/json

{
  "title": "Spring Boot in Action",
  "author": "Craig Walls",
  "isbn": "978-1617292545",
  "publicationYear": 2016
}
```

**Response:** `201 CREATED`
```json
{
  "id": 1,
  "title": "Spring Boot in Action",
  "author": "Craig Walls",
  "isbn": "978-1617292545",
  "publicationYear": 2016,
  "createdAt": "2026-01-05T10:30:00",
  "updatedAt": "2026-01-05T10:30:00"
}
```

### Get All Books
```bash
GET http://localhost:8082/api/books
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "title": "Spring Boot in Action",
    "author": "Craig Walls",
    "isbn": "978-1617292545",
    "publicationYear": 2016
  }
]
```

### Search Books
```bash
GET http://localhost:8082/api/books/search?title=spring
```

### Update a Book
```bash
PUT http://localhost:8082/api/books/1
Content-Type: application/json

{
  "title": "Spring Boot in Action - 2nd Edition",
  "author": "Craig Walls",
  "isbn": "978-1617292545",
  "publicationYear": 2019
}
```

## Database Schema

### Book Entity

| Column | Type | Constraints |
|--------|------|-------------|
| id | BIGINT | Primary Key, Auto Increment |
| title | VARCHAR(200) | NOT NULL |
| author | VARCHAR(100) | NOT NULL |
| isbn | VARCHAR(20) | UNIQUE |
| publication_year | INT | - |
| created_at | TIMESTAMP | NOT NULL, Auto-generated |
| updated_at | TIMESTAMP | NOT NULL, Auto-updated |

## Error Handling

The API uses standard HTTP status codes:

- `200 OK` - Request successful
- `201 CREATED` - Resource created successfully
- `204 NO CONTENT` - Successful deletion or no data found
- `400 BAD REQUEST` - Invalid input or business logic error
- `404 NOT FOUND` - Resource not found
- `500 INTERNAL SERVER ERROR` - Unexpected server error

## Testing

Run tests with:
```bash
./mvnw test
```

## Project Structure

```
src/
├── main/
│   ├── java/com/example/bookmanagement/
│   │   ├── controller/
│   │   │   └── BookController.java
│   │   ├── entity/
│   │   │   └── Book.java
│   │   ├── repository/
│   │   │   └── BookRepository.java
│   │   ├── service/
│   │   │   └── BookService.java
│   │   └── BookmanagementApplication.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/example/bookmanagement/
        └── BookmanagementApplicationTests.java
```

## Future Enhancements

- [ ] Add authentication and authorization
- [ ] Implement pagination for large datasets
- [ ] Add book categories/genres
- [ ] Implement book borrowing/lending system
- [ ] Add file upload for book covers
- [ ] Create a frontend interface
- [ ] Add Docker support
- [ ] Implement caching with Redis
- [ ] Add comprehensive unit and integration tests

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is open source and available under the [MIT License](LICENSE).

## Contact

Your Name - [@YourTwitter](https://twitter.com/yourhandle)

Project Link: [https://github.com/NdanaDev/-book-management-api](https://github.com/NdanaDev/-book-management-api)

## Acknowledgments

- Spring Boot Documentation
- Spring Data JPA Documentation
- REST API Best Practices
