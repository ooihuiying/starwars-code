# starwars-code

A simple API `/information` that returns a response indicating the starship that Darth Vader is using, the number of crews on board the death star, and if
Princess Leia is on Alderaan.

API Documentation:
https://swapi.dev/

# How to set up
- This project uses Java 17 Springboot. Ensure that you have the required SDK in your running IDE environment.
- Run the MainApplication file and the application will start running at port 8080.
- To test it on your browser, type `localhost:8080/information`. You will then be able to view the printed logs in your IDE and a response will be returned.

# How to develop
- The entry point to this project is in StarshipController where you will see a method receiving a GET request.
- There is a models folder which contains all the classes that are used in the controller.
- There is a Config folder which contains some hardcoded constants which are used as beans in the project.
- There is a application.properties which contains a hardcoded URL.
- After making a change, you can write some tests under the folder to check that the code works. Tests uses Mockito and Junit.