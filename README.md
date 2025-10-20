# Oblig #2 – Web Frameworks and Services with JWT Token and OAuth2

In this obligatory task, you will complete the implementation of the RESTful APIs for the eLibrary App and secure the endpoints.

The obligatory project consists of three projects

1. [library-spring-ws-rest](library-spring-ws-rest) project
2. [library-spring-ws-rest-security-oauth](library-spring-ws-rest-security-oauth) project

Preambles: Clone the repository `https://github.com/tosdanoye/dat152-project2.git`. Then, import the maven projects into your preferred IDE.
Some ways to run your project:

1. Run the project from a command prompt (e.g., Mac Terminal). Navigate to the root folder of your project and run the maven command: ./mvnw spring-boot:run
2. From your IDE: Right click on ‘LibraryApplication.java’ in the “no.hvl.dat152.rest.ws.main” package and “Run As” Java Application.

### Task 0: (Lab 4)

- This oblig #2 builds on Lab #4 with advanced features and functionalities. Therefore, you are expected to have a working solution for the Book and Author REST APIs from Lab #4

### Task 1: RESTful API Services

Project: [library-spring-ws-rest](library-spring-ws-rest)

Implement APIs for users to list, order (borrow) and return a book. You will complete the “TODOs” in the following classes:

1. UserService
   - updateUser
   - deleteUser
   - createOrdersForUser
   - getUserOrder
   - getUserOrders
2. UserController
   - createUser
   - updateUser
   - deleteUser
   - createUserOrder
   - deleteUserOrder
   - getUserOrder
3. OrderService
   - deleteOrder
   - findAllOrders()
   - findOrdersByExpiryDate
   - updateOrder
4. OrderController
   - getAllBorrowOrders
   - getBorrowOrder
   - updateOrder
   - deleteBookOrder

Tips:

- Note that there are custom CRUD methods already created in the repositories for each model. Feel free to add new ones, if you have needs for them.
- Look at the JUnit tests for clues

### Task 2: HATEOAS, Pagination and Filtering

You will provide support to list all ‘orders’ that expire by the specified date (date format: yyyy-MM-dd). Also, your Rest API will provide support to paginate the list of orders (e.g., Pageable).

For these tasks, you will complete the “TODOs” in the following classes and methods:

1. OrderService
   - findOrdersByExpiryDate (same as in Task 1)
2. OrderController
   - getAllBorrowOrders (same as in Task 1)
3. Provide HATEOAS support for the createUserOrder such that there are links to other endpoints with actions possible for the resource.
   - Add your links to orders in createUserOrder

### Task 3: Securing resource endpoints with JWT Access tokens from 3rd party OAuth2 Server

Project: [library-spring-ws-rest-security-oauth](library-spring-ws-rest-security-oauth)

For this task, you will require the Keycloak Identity Provider server.

- Follow the instruction [here](https://github.com/tosdanoye/dat152-lab/tree/main/keycloak-docker) to start and run the Keycloak IdP server using docker container.
  - `https://github.com/tosdanoye/dat152-lab/tree/main/keycloak-docker`
- When you have started the server, you can obtain an access_token for the admin (user2) and normal users (user1 and user3) by sending a POST request to the keycloak token endpoint:
  ```
  curl -X POST http://localhost:8080/realms/DAT152/protocol/openid-connect/token --data 'grant_type=password&client_id=dat152oblig2&username=user1&password=user1'
  ```
  Or use Postman to send the post request.

You will then receive a response with the access_token.

- Copy the access_token and replace the `admin.token.test` , `user.token.test` , `user3.token.test` in the `application.properties` with these new values. When they expire, you need to request for new tokens and replace the old ones.
- Copy your solutions from Tasks 1 & 2 and secure the endpoints by using @PreAuthorize annotation.
- All /authors, /books , and /order endpoints can only be accessed by ADMIN user role
- All /users/{id} endpoints must be accessible only to the correct authenticated user or an admin user. For example, user1 (id=1) can only access /users/1 but not /users/2. An admin role must be able to access all the endpoints. Only admin role can access /users endpoint. hasRole('ADMIN') |s| id owns reource
- Test your solutions.

### Testing

JUnit tests are provided, and you can run them in your IDE or from a terminal.

Tips:

- Watch out for the specific HttpStatus codes in the Junit tests and ensure that they correspond to what you are returning in the controller methods.
- Use Postman in addition, to support your api testing.
