# To run the program

Code has been built and compiled on java 17

### COMPILE : mvn clean install

### RUN : mvn spring-boot:run
Alternatively one can also run the main Application.java class from the editor

## H2 database can be accessed at
http://localhost:8180/h2-console

url : jdbc:h2:mem:testdb <br/>
username : sa <br/>
password : password

## REST calls

There is basic authentication for running REST calls

The user called user with password lowsecure (defined in properties) cannot access the create and delete books. <br/>
This role can only view book details and borow or return <br/>

The user called admin with password highlysecure (defined in properties) can perform all activities

### POST (create) : http://localhost:8180/books

{
    "author" : "jatin",
    "isbn" : "BOOK002",
    "publicationYear" : 2023,
    "title" : "Second book",
    "totalAvaliableCopies" : 10
}

OR

{
    "author" : "jatin",
    "isbn" : "BOOK001",
    "publicationYear" : 2024,
    "title" : "First book"
}
NOTE - Total Avaliable copies defaults to 0 if not provided
A bad request (401) is sent back if this request doesnt have the required not null values, and unique isbn

### GET (by isbn) : http://localhost:8180/books/isbn/BOOK001
### GET (by author) : http://localhost:8180/books/author/jatin

A not found (404) is sent back for the above if a isbn or author doesnt exist

### PUT (borrow) : http://localhost:8180/books/borrow/isbn/BOOK002
 for the borrow/return

### PUT (return) : http://localhost:8180/books/return/isbn/BOOK002

A not found (404) is sent back for the above if copies exceed the initial total copies

### DELETE : http://localhost:8180/books/isbn/BOOK002

A not found (404) is sent back for the above if a isbn doesnt exist or book has already been deleted

## NOTES
Basic authentication has been implemented, but not using JWT tokens from database or services like keycloak <br/>
Full integration tests for the advice has not been implemented, but all code paths have been tried to be covered <br/>
Caching has been enabled on the Library methods for creation of a book, which are updated on return and borrow and evicted on delete <br/>
Basic rate limiting mechanism for API is not present
