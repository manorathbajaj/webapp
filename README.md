## webapp
Web Application for the Cloud Computing.

## Prerequisites
1. Gradle <br/>
2. Java 11 <br/>
3. mysql  Ver 15.1 Distrib 10.3.21-MariaDB<br/>
4. Postman to check endpoints <br/>

## Build and Deploy instructions 
1. Create <code>csye6225</code> database in mySQL and change the root password to <code>password</code> <br/>
2. Go to project Directory <br>
3. Run the following commands:<br/>
	<code>./gradlew clean</code><br/>
	<code>./gradlew build</code><br/>
	<code>./gradlew bootRun</code><br/>
4. Verify the application is running by using postman to access apporpriate endpoints given in the <a href= "https://app.swaggerhub.com/apis-docs/csye6225/spring2020/assignment-03">Swagger Docs</a>at 8080 port.
update
