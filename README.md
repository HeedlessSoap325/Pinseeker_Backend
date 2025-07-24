# Table of contents

**1. [What Is This Project?](./README.md#what-is-this-project)**   

**2. [What Is This Project Used For?](./README.md#what-is-this-project-used-for)**

**3. [What Dependencies Are Used?](./README.md#what-dependencies-are-used)**

**4. [How to Run This Project](./README.md#how-to-run-this-project)**  

- **4.1 [Clone the GitHub Repository](./README.md#1-clone-the-github-repository)**
- **4.2 [Install Java 21 or Higher](./README.md#2-install-java-21-or-higher)**
- **4.3 [Install PostgreSQL and PostGIS (Locally or in Docker)](./README.md#3-install-postgresql-and-postgis-locally-or-in-docker)**
  - **4.3.1 [Option A: Installing PostgreSQL with Docker](./README.md#option-a-installing-postgresql-with-docker)**
  - **4.3.2 [Option B: Installing PostgreSQL Locally](./README.md#option-b-installing-postgresql-locally)**
  - **4.3.3 [Enabling the PostGIS Extension](./README.md#enabling-the-postgis-extension)**
- **4.4 [Updating Configuration (if needed)](./README.md#4-updating-configuration-if-needed)**
- **4.5 [Run the Application](./README.md#5-run-the-application)**

**5. [Configure the Project](./README.md#configure-the-project)**

- **5.1 [Configure Database](./README.md#1-configure-database)**
- **5.2 [Configure Messages](./README.md#2-configure-messages)**
- **5.3 [Configure Default Profile Pictures](./README.md#3-configure-default-profile-pictures)**

**6. [What Endpoints does the Backend have?](./README.md#what-endpoints-does-the-backend-have)**

---

# What Is This Project?
This GitHub repository contains the code for a backend application. Together with the frontend—currently still in development—it forms a fully functional website called **"Pinseeker"**.

The website is inspired by [geocaching.com](https://www.geocaching.com), a global game where players search for hidden caches using geographic coordinates and hints. These caches can be containers of any size or shape, hidden almost anywhere.

**Pinseeker** shares the same core concept as geocaching, but it's much simpler and entirely self-coded.

---

# What Is This Project Used For?
This project serves as a learning platform, allowing me to experiment with new technologies, implement various features, and establish a foundation for future projects. It helps demonstrate both effective practices and lessons on what to avoid.

I also wanted to deepen my understanding of **Spring Boot** and **JPA**, both of which are used extensively in this project.

---

# What Dependencies Are Used?
This project uses the following dependencies:

- **Java 21**
- **spring-data-jpa** – for database communication and data management
- **spring-security** and **spring-oauth2-resource-server** – for basic user authentication and access control
- **spring-web** – for REST API functionality
- **hibernate-spatial** and **jts-core** – for working with geographic coordinates in the database
- **PostgreSQL** – as the relational database system  

---

# How to Run This Project

To run this project, several steps are required. The following sections explain each step in detail.

---

## 1. Clone the GitHub Repository

Run the following command to clone the repository to your local system, or download it as a ZIP file if you don't have Git installed:

```shell
git clone https://github.com/HeedlessSoap325/Pinseeker_Backend.git
```

---

## 2. Install Java 21 or Higher

If Java isn't already installed on your system, download an installer from a trusted source such as Oracle.

After downloading, run the installer and follow the setup instructions.

To verify the installation, open a terminal and run:
```shell
java --version
```

If Java is installed correctly, the version number should be displayed in the terminal.

---

## 3. Install PostgreSQL and PostGIS (Locally or in Docker)

This project uses PostgreSQL with PostGIS for spatial database support. While other databases may work if they support PostGIS, this guide covers PostgreSQL only.

This section explains how to install PostgreSQL 16.

### Option A: Installing PostgreSQL with Docker

**1.** Create a file named docker-compose.yml in your project directory.

**2.** Paste the following content:

```yaml
version: '3.8'
services:
  postgres:
    image: postgis/postgis:16-3.4  # PostgreSQL 16 with PostGIS 3.4
    container_name: PostgreSQL
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "2345:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data

volumes:
  pgdata:
  ```

This configuration sets up a PostgreSQL container with default credentials.

**3.** Open a terminal in the same directory and run:

```shell
docker-compose up
```

If successful, the database server will be running on port 2345 and accessible by the backend and any management tools.

---

### Option B: Installing PostgreSQL Locally

**1.** Download the installer for your preferred version of PostgreSQL from [EnterpriseDB](https://www.enterprisedb.com/downloads/postgres-postgresql-downloads).

**2.** Run the installer, select all components, and choose a port (e.g. 2345) for your PostgreSQL server.

**3.** After installation, use pgAdmin 4 (which should be installed automatically) to manage your database.

---

### Enabling the PostGIS Extension

Once PostgreSQL is installed, enable PostGIS by running the following command in your ```psql``` shell:

```postgresql
CREATE EXTENSION IF NOT EXISTS postgis;
```

This activates PostGIS support in your database.

---

## 4. Updating Configuration (if needed)

If your PostgreSQL installation uses non-default settings, update the following lines in your ```application.properties``` file:

```properties
spring.datasource.url=jdbc:postgresql://{server-url}:{server-port}/{database-name}

spring.datasource.username={database-username}

spring.datasource.password={database-password}
```

The database user must have the privileges to create Databases and manage the one called **{database-name}** or by default **pinseeker**.

Replace the placeholders with your actual PostgreSQL configuration values.

---

## 5. Run the Application

**1.** Start your PostgreSQL Server.

**2.** Navigate to the directory called ```pinseekerbackend```, witch you previously cloned.

**3.** Open a terminal in this directory and run
```shell
mvnw spring-boot:run
```

You can also open the project in any code editor and start the Project from there.

---

# Configure the Project

## 1. Configure Database
To keep the Database data after a restart of the Backend.

**1.** Locate the ```application.properties``` File of the Project.

**2.** update the following line to either ```none``` or ```validate```:
```properties
spring.jpa.hibernate.ddl-auto=create
```

NOTE: The Backend must first be started with the ```create``` Option to initially create the Database. 
After that, the Option should be switched to avoid dropping and re-creating the Database at Startup.

---

## 2. Configure Messages

**1.** Head to the ```Constants.java``` File, located in the **utils** Folder of the Project.

**2.** Change any Property's Value to change the Error Message displayed to the User.

For Example:

```java
public static final String USERNAME_NOT_FOUND = "There is no user with this name.";
```

To

```java
public static final String USERNAME_NOT_FOUND = "I don't know anybody by that Name.";
```

---

## 3. Configure Default Profile Pictures

**1.** After the first launch of the Project, an **uploads** Folder, and in it a **profile_pictures** Folder should have been automatically created.
Move the two images for **deleted** and **default** Profile Pictures into the **profile_picture** Folder.

**2.** Locate the following Lines of Code in the ```Constants.java``` File, located in the **utils** Folder of the Project.
```java
public static final String DEFAULT_PROFILE_PICTURE = "{file_name_and_extension}";

public static final String DELETED_PROFILE_PICTURE = "{file_name_and_extension}";
```

**3.** Replace **{file_name_and_extension}** with the actual Names of the Files such as **default.jpg** for example, 
or with ```null```, if you do not wish to have default pictures.

---

# What Endpoints does the Backend have?

//TODO