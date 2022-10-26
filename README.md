# FetchRewards
Fetch Rewards Coding Exercise - Sr Backend Software Engineering - 
Daniel Schuchardt

This web service project is built using the Spring Boot Java Framework

Assumptions made for this exercise:
1) Requests to the addTransaction route with future-dated timestamps will be denied. Timestamp uses Java Instant time class, so time is universal
2) Requests to the spend route with a negative point value will be denied
3) Requests to the addTransaction route with a negative point total for a payer where the points available for that payer is less than the negative point total in the request will be denied
4) Unsuccessful requests will not be stored in the transaction repository
5) This web service is for transactions for one user. It is assumed that authentication is handled elsewhere


Required software to run this project (Windows or Mac):
1) Download this repository to your machine and take note of the file location
2) Install Java JDK 17 for Windows, select: https://download.oracle.com/java/17/archive/jdk-17.0.5_windows-x64_bin.exe
    - Note the installation location, by default the location is: C:\Program Files\Java\jdk-17.0.5
    - Add this location to your environment variables for your user account by:
        1) click start, go to settings
        2) search for system environment variables
        3) in the window that pops up, select the button "Environment Variables"
        4) system variables are located in the bottom pane of the pop up window
        5) click new
        6) under Variable name, type JAVA_HOME
        7) under Value name, type C:\Program Files\Java\jdk-17.0.5
4) Install Eclipse or any Spring Boot compatible IDE - https://www.eclipse.org/downloads/
5) Install Postman to test the API - https://www.postman.com/downloads/


Instructions to run this web service project via the command line in Windows:
1) Open the command line by clicking the search button in the bottom left corner of the screen
2) Type cmd and hit enter
3) Navigate to the project folder
4) Run using command : mvnw clean spring-boot:run
5) Test the web service in Postman by sending requests and checking the responses

Instructions to run this web service project via the command line on Mac:









Opportunities for improvement
1) Reuse code around the spend method for adding negative points in the addTransactions route
