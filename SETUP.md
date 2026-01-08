# Project Setup Guide

This guide describes how to import, configure, and run the VECV Pull Chord Report project on your local machine.

## Prerequisites

Before running the project, ensure you have the following installed:
1.  **Java Development Kit (JDK) 17** or higher.
2.  **Maven** (for building the project).
3.  **SQL Server** (with the `VECV_Scada_DB` database restored).
4.  **Git** (system version control).

## 📥 Installation

### 1. Clone the Repository
Open your terminal and run:
```bash
git clone https://github.com/dkoustubh/VECV.git
cd VECV
```

### 2. Database Configuration
The application connects to a local SQL Server instance. You need to verify the connection settings.

Open `src/main/resources/application.properties` and update the following fields if your credentials differ:

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=VECV_Scada_DB;encrypt=true;trustServerCertificate=true;
spring.datasource.username=sa
spring.datasource.password=Ats1234@
```
*Note: Ensure your SQL Server is running and the database `VECV_Scada_DB` exists.*

### 3. Build the Project
Use Maven to install dependencies and build the application:
```bash
./mvnw clean install
```
*(On Windows, use `mvnw.cmd clean install`)*

## 🚀 Running the Application

### Option 1: Using Maven (Recommended)
You can run the application directly with the Maven Spring Boot plugin:
```bash
./mvnw spring-boot:run
```

### Option 2: Running the JAR
After building, a JAR file is generated in the `target/` directory. Run it using:
```bash
java -jar target/PullChord-Report-0.0.1-SNAPSHOT.jar
```

## 🌐 Accessing the Application

Once the server starts (you will see "Started PullChordReportApplication" in the logs), open your web browser and navigate to:

- **Dashboard**: [http://localhost:8070/dashboard](http://localhost:8070/dashboard)
- **Report Viewer**: [http://localhost:8070/report](http://localhost:8070/report)

## 🐛 Troubleshooting

- **Port 8070 in use**: If the application fails to start, check if port 8070 is occupied. You can change the port in `application.properties` (`server.port=8070`).
- **Database Connection Error**: Verify that SQL Server is running and the username/password in `application.properties` matches your local setup.
