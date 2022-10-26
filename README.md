# FetchRewards
Fetch Rewards Coding Exercise - Sr Backend Software Engineering - 
Daniel Schuchardt

This web service project is built using the SpringBoot Java Framework.
Instructions to run the Project
Required software:
1) Download this repository to your machine
2) Install Java JDK 17 - https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
3) Install Eclipse or any Spring Boot compatible IDE - https://www.eclipse.org/downloads/
4) InstallPostman to test the API - https://www.postman.com/downloads/


Run the Project using cmd
Navigate to the project folder.
Run using command : mvnw clean spring-boot:run
Test the api in Postman by sending request and checking the responses.



Assumptions
1) requests to the addTransaction route with future-dated timestamps will be denied. Timestamp uses Java Instant time class, so time is universal.
2) requests to the spend route with a negative point value will be denied
3) requests to the addTransaction route with a negative point total for a payer where the points available for that payer is less than the negative point total in the request will be denied
4) unsuccessful requests will not be stored in the transaction repository
5) this web service is for transactions for one user, the assumption being authentication is handled elsewhere








Opportunities for improvement
1) reuse code around the spend method for adding negative points in the addTransactions route
