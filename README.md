# AWS DynamoDB with SpringData demo

## 1. Configure the local environment
### a. Run DynamoDBLocal .jar
Download the archive from the following link and extract the contents of the archive to a preferred location: https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.DownloadingAndRunning.html
Run the following command from command line:
```
java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb
```

### b. Install AWS CLI
Guidelines to installing AWS CLI version 2 (latest version): https://docs.aws.amazon.com/cli/latest/userguide/install-cliv2.html

### c. Create a new DynamoDB table needed for the demo
Run the following command from command line:
```
aws dynamodb create-table 
--table-name Playlist 
--attribute-definitions AttributeName=id,AttributeType=S 
		AttributeName=genre,AttributeType=S 
		AttributeName=playlistName,AttributeType=S 
--key-schema AttributeName=id,KeyType=HASH 
--provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 
--global-secondary-indexes IndexName=genrePlaylists,
KeySchema=["{AttributeName=genre,KeyType=HASH},{AttributeName=id,KeyType=RANGE}"],
Projection="{ProjectionType=ALL}",
ProvisionedThroughput="{ReadCapacityUnits=5,WriteCapacityUnits=5}" 
--endpoint-url http://localhost:8000
```

### 2. Run the project 
The project includes a Gradle wrapper, so there's no need to have Gradle previously installed.
Supported versions are Java 10 and Gradle 5.6.
From the command line or from the IntelliJ terminal run:
```
.\gradlew clean build bootRun
```
This will build the project, run the tests and start the project locally.
You should have the service running locally on port 8080 (http://localhost:8080).

### 3. Postman API test suite
A full test suite can be found in the "resources" folder, under "postman" directory.
This should be imported as a collection in Postman.

