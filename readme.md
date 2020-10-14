### How to use this spring-boot project

- Install packages with `mvn package`
- Run `mvn spring-boot:run` for starting the application (or use your IDE)

Application (with the embedded H2 database) is ready to be used ! You can access the url below for testing it :

- Swagger UI : http://localhost:8080/swagger-ui.html
- H2 UI : http://localhost:8080/h2-console

> Don't forget to set the `JDBC URL` value as `jdbc:h2:mem:testdb` for H2 UI.



### Instructions

- download the zip file of this project
- create a repository in your own github named 'java-challenge'
- clone your repository in a folder on your machine
- extract the zip file in this folder
- commit and push

- Enhance the code in any ways you can see, you are free! Some possibilities:
  - Add tests
  - Change syntax
  - Protect controller end points
  - Add caching logic for database calls
  - Improve doc and comments
  - Fix any bug you might find
- Edit readme.md and add any comments. It can be about what you did, what you would have done if you had more time, etc.
- Send us the link of your repository.

#### Restrictions
- use java 8


#### What we will look for
- Readability of your code
- Documentation
- Comments in your code 
- Appropriate usage of spring boot
- Appropriate usage of packages
- Is the application running as expected
- No performance issues

#### My comments
What I have done:
-added validation to endpoints
-added integration tests
-added transactions
-added HTTP response codes
-added exception handling
-added lombok annotation
-added basic logging
-changed employeeId to UUID format

If I would have more time I would:
-improve the exception handling
-improve the test coverage
-add user roles and JWT support (at the moment I can only assume what the users/admin should be able to do)
-add filtering and pagination to retrieveEmployees endpoint
-add .yaml documentation

I've noticed that one of the suggestions was to add comments in the code, however most of the code
is self-explanatory and useless comments can be a bad thing. Overall it depends on the approach that
the team adopts, as it's important to keep the code consistent while working in a team environment.

When it comes to the documentation, I wanted to write it in .yaml format however unfortunately I've
run out of time because of the amount of work at my current company.

I think one of the difficulties was lack of strict business requirements as that would make interesting
and could showcase the use of Java/Spring features better.