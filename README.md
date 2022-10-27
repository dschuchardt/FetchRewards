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


Required software to run this project (Windows):
1) Download this repository to your machine and take note of the file location
    - Click the green "Code" dropdown button and select "Download Zip"
2) Install Java JDK 17 for Windows, select: https://download.oracle.com/java/17/archive/jdk-17.0.5_windows-x64_bin.exe
    - Click the downloaded file to run the installer
    - Note the installation location, by default the location is: C:\Program Files\Java\jdk-17.0.5
    - Add this location to your environment variables for your user account by:
        1) Click start, go to settings
        2) Search for environment variables, select "Edit the System Environment Variables"
        3) In the window that pops up, select the button "Environment Variables"
        4) System variables are located in the bottom pane of the pop up window
        5) Click new
        6) Under Variable name, type JAVA_HOME
        7) Under Variable value, type C:\Program Files\Java\jdk-17.0.5
        8) Click ok to close the window
3) Install Postman for testing the web service- https://www.postman.com/downloads/
    - Click the Windows 64-bit button to download
    - Click the file to run the installer. Once the installation is complete, Postman should launch automatically
    - Click "Skip and go to the app" near the bottom of left side of the window
    - Download "FetchRewardsPostmanCollection" from https://github.com/dschuchardt/FetchRewardsPostmanCollection
        - Click the green "Code" dropdown button and select "Download Zip"
        - It will download as a zip, extract the files from the zip
        - Click the import button in Postman
        - Select the "FetchRewards.postman_collection.json" file
        - Expand the FetchRewards collection using the > symbol next to it


Instructions to start this web service project via the command line in Windows:
1) Open the command line by clicking the search button in the bottom left corner of the screen
2) Type cmd and hit enter
3) Navigate to the project folder
4) Run using command : mvnw clean spring-boot:run



To test the web service in Postman:
- Click the request that you wish to test
    - For the "GET" requests, you can simply click "Send", there is nothing that needs updating
    - For the "POST" addTransaction route, you must update the request body with a transaction payload
        -   Here are some sample request body payloads (do not include the bullet points)
            - {"payer":"DANNON","points":300,"timestamp":"2020-10-31T10:00:00Z"}
            - {"payer":"UNILEVER","points":200,"timestamp": "2020-10-31T11:00:00Z"}
            - {"payer":"DANNON","points": -200,"timestamp": "2020-10-31T15:00:00Z"}
            - {"payer":"MILLER COORS","points": 10000,"timestamp": "2020-11-01T14:00:00Z"}
            - {"payer":"DANNON","points":1000,"timestamp":"2020-11-02T14:00:00Z"}  
    - For the "POST" spendPoints route, you must update the request URL after "spend/" with the number of points you wish to spend. 
        - Here are some sample requests (do not include the bullet points)
            - localhost:8080/spend/1500
            - localhost:8080/spend/100
            - localhost:8080/spend/10000

Opportunities for improvement
1) Reuse code around the spend method for adding negative points in the addTransactions route
