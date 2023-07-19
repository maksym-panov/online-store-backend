# Online store (backend)
Java Spring Core, MVC, Security, JPA/Hibernate, Flyway, PostgreSQL, REST-API Online Strore

Before starting you should already have installed JDK 19+ ([Download](https://www.oracle.com/java/technologies/javase/jdk19-archive-downloads.html)), 
Apache Maven and Apache Tomcat.

## Apache Tomcat installation

1. Download binary zip archive with Tomcat 10 from official website for your machine ([Download](https://tomcat.apache.org/download-10.cgi))
2. Unzip downloaded archive into the filesystem
3. Go to the Tomcat configuration directory and open `context.xml` file
   #### On Windows

   ```
   cd apache-tomcat-10.x.x\conf
   ```
   - Open `context.xml` file in your favourite redactor.
   
   #### On Unix
   
   ```
   cd apache-tomcat-10.x.x/conf
   vim context.xml
   ```

4. Change maximal cache size for server
   Add Resources element inside the Context and set the `cacheMaxSize` you wish (minimal recomended - 128000) 
   ```xml
   <Context>
       ...
       <Resources cachingAllowed="true" cacheMaxSize="128000" />
       ...
   </Context>
   ```

## Apache Maven installation
1. Download binary zip archive from official Apache Maven website for your machine ([Download](https://maven.apache.org/download.cgi))
2. Unzip downloaded archive into the filesystem
3. Update enviroment variables

  #### On Windows
  
  - Go *Home* - *Edit the system environment variables* - *Advanced* - *Environment variables* -
  *System variables : New*. 
  
  - Set *Variable name* to MAVEN_HOME and *Variable value* to path
  to your Maven directory (for example `C:\Program Files\apache-maven-3.9.x`) and press OK.
  
  - In *Environment variables* page find system variable *Path*, select it and click edit button,
  click *New* and write `%MAVEN_HOME\bin%` in the prompt. Click OK on *Environment variables* page.


  #### On MacOS

  - In the terminal open zsh startup file with your favourite text editor.
  ```
  vim ~/.zshenv
  ```
  - Write here the script that will add the path to bin in your Maven directory to the PATH variable.
  ```
  export PATH=$PATH:/library/apache-maven-3.9.x/bin
  ```
  - Save changes and exit. Execute created script.
  ```
  source ~/.zshenv
  ```

4. In cmd or terminal check if Maven is installed
```
mvn --version
```

## Application running

1. Clone the repository
   ```
   git clone https://github.com/maksym-panov/online-store-backend
   ```
   
2. Create new database in PgAdmin
3. Go to the server configuration files folder

   #### On Windows

   ```
   cd online-store-backend\src\main\resources\META-INF
   ```
   
   #### On Unix
   
   ```
   cd online-store-backend/src/main/resources/META-INF
   ```
   
5. Configure data source
   Use `Persistence.xml.template` file. Remove `.template` part from file name and open it in text editor. Fill all the gaps with your database information. 
   ```xml
    <properties>
       ...
        <property name="jakarta.persistence.jdbc.url" value="jdbc:postgresql://localhost:5432/NAME_OF_DATABASE_FROM_CHAPTER_2"/>
        <property name="jakarta.persistence.jdbc.user" value="YOUR_POSTGRES_USERNAME"/>
        <property name="jakarta.persistence.jdbc.password" value="YOUR_POSTGRES_PASSWORD"/>
       ...
    </properties>
   ```
6. Configure FlyWay DB migration tool
   Use `flyway.conf.template` file. Remove `.template` part from file name and open it in text editor. Fill all the gaps with your database information.
   ```yml
    flyway.url=jdbc:postgresql://localhost:5432/NAME_OF_DATABASE_FROM_CHAPTER_2
    flyway.user=YOUR_POSTGRES_USERNAME
    flyway.password=YOUR_POSTGRES_PASSWORD
   ```
   
7. Allow database seeding. 
   If you want to test the application with some initial data, you can edit `app.properties` file. If you set `fillDatabaseWithInitialData`
   variable to `true` then when application will be launched, it will execute database seeding code.

   **Remember that you should set this variable to false after successful seeding**
8. Go back to the root directory

   #### On Windows

   ```
   cd ..\..\..\..
   ```
   
   #### On Unix
   
   ```
   cd ../../../..
   ```
9. Migrate the database to the newest version
   ```
   mvn -Dflyway.configFiles=src/main/resources/META-INF/flyway.conf flyway:migrate
   ```
9. Build the application
   ```
   mvn clean package
   ```

10. Copy compiled .war archive to the Tomcat applications folder as ROOT.war

    #### On Windows
    ```
    copy target\online-store-backend.war C:\path\to\tomcat\webapps\ROOT.war
    ```

    #### On Unix
    ```
    cp target/online-store-backend.war /path/to/tomcat/webapps/ROOT.war
    ```

11. Start the Tomcat server 

    #### On Windows
    ```
    C:\path\to\tomcat\bin\startup.bat
    ```

    #### On Unix
    ```
    /path/to/tomcat/bin/startup.sh
    ```
12. Start the frontend
    [Frontend repository](https://github.com/maksym-panov/online-store-frontend)
