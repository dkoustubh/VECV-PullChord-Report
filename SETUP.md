# Project Setup Guide

This guide describes how to import, configure, and run the VECV Pull Chord Report project on your local machine, including setting up the database.

## Prerequisites

Before running the project, ensure you have the following installed:
1.  **Java Development Kit (JDK) 17** or higher.
2.  **Maven** (for building the project).
3.  **SQL Server** (Express or Developer edition).
4.  **Git** (system version control).
5.  **SQL Server Management Studio (SSMS)** or **DBeaver** (for database management).

## 📥 Installation

### 1. Clone the Repository
Open your terminal and run:
```bash
git clone https://github.com/dkoustubh/VECV.git
cd VECV
```

### 2. Database Setup
The application requires a SQL Server database named `VECV_Scada_DB` with specific tables.

#### Option A: Running the Setup Script (Recommended)
1.  Open **SQL Server Management Studio (SSMS)**.
2.  Connect to your local SQL Server instance.
3.  Open the file `database_setup.sql` located in the root folder of this project.
4.  Execute the script. This will:
    *   Create the `VECV_Scada_DB` database.
    *   Create the 4 required tables: `Z3_Pullchord_T2`, `Z5_Pullchord_T`, `Z7_Pullchord_T`, `Z9_Pullchord_T`.
    *   Insert a dummy test record.

#### Option B: Manual Setup
If you prefer to create it manually:
1.  Create a database named `VECV_Scada_DB`.
2.  Import your existing schema or create tables matching the Entity classes in `src/main/java/com/example/PullChord_Report/entity/`.

### 3. Application Configuration
Open `src/main/resources/application.properties` and update the connection details to match your local SQL Server:

```properties
# Update 'localhost:1433' if your port is different
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=VECV_Scada_DB;encrypt=true;trustServerCertificate=true;

# Your SQL Server username (default 'sa', but change if needed)
spring.datasource.username=sa

# Your SQL Server password
spring.datasource.password=Ats1234@
```

### 4. Build the Project
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

- **Database Connection Error**:
  - Ensure SQL Server service is running.
  - Verify `username` and `password` in `application.properties`.
  - Check if TCP/IP is enabled in SQL Server Configuration Manager.
- **Port 8070 in use**:
  - If the app fails to start, another service might be using port 8070. Change `server.port=8070` in `application.properties` to a different value (e.g., 8081).
