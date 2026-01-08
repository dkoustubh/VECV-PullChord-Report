# VECV SCADA Pull Chord Report Viewer

## Overview
This project is a comprehensive SCADA reporting solution designed for VECV. It visualizes Pull Chord data from the shop floor, providing real-time insights into production efficiency, maintenance calls, and quality alerts. The application fetches data from a SQL Server database, processes it via a Spring Boot backend, and displays it on a dynamic Thymeleaf-based frontend.

## 🚀 Features
- **Real-time Reporting**: View live data from Z3, Z5, Z7, and Z9 Pullchord tables.
- **Dynamic Filtering**: Filter by Station, Shift (A, B, C), and Date/Time ranges.
- **Interactive Dashboard**: A modern dashboard with Key Performance Indicators (KPIs).
- **Excel Export**: Download detailed reports for offline analysis.
- **Responsive Design**: optimized for shop floor displays and desktop monitoring.

## 📊 System Architecture & Flow Chart

The following diagram illustrates the complete data flow from the physical database tables to the user interface.

```mermaid
graph TD
    subgraph Database["SQL Server (VECV_Scada_DB)"]
        Table[("Physical Tables<br/>(Z3, Z5, Z7, Z9_Pullchord_T)")]
        View[("Database Views<br/>(vw_Pullchord_All)")]
        Table --> View
    end

    subgraph Backend["Spring Boot Application (Port 8070)"]
        Repo["JPA Repositories"]
        Entity["Entity Classes<br/>(@Entity)"]
        Controller["REST Controllers<br/>(Z3PullchordT2Controller)"]
        
        View -. JDBC Connection .-> Repo
        Repo --> Entity
        Entity --> Controller
    end

    subgraph Frontend["Frontend Layer"]
        Thymeleaf["Thymeleaf Engine"]
        Process["HTML Rendering"]
        UI["User Interface<br/>(Browser)"]
        
        Controller --> Thymeleaf
        Thymeleaf --> Process
        Process --> UI
    end

    User((User)) -->|HTTP Request| UI
    UI -->|GET /report| Controller
```

## 🛠️ How It Works

### 1. Data Layer
The system connects to a **SQL Server** database (`VECV_Scada_DB`). Raw data from the PLC/SCADA systems is stored in normalized tables (e.g., `Z3_Pullchord_T2`). We use JPA Repositories to interact with these tables efficiently.

### 2. Backend Processing
The backend is built with **Spring Boot**.
- **Controllers**: Handle incoming HTTP requests and manage application routing.
- **Services/Repositories**: Execute SQL queries to fetch specific datasets based on user filters (Shift, Station, Time).
- **Model**: Data is mapped to Java Objects (Entities) for easy manipulation.

### 3. Frontend Visualization
Server-side rendering is handled by **Thymeleaf**.
- The `report.html` template dynamically renders tables based on the fetched data.
- The Dashboard (`KD_VECV_NewClientDemoUI.html`) embeds these reports and provides high-level metrics.
- Users can interact with the UI to drill down into specific production lines or download data.

## 💻 Technologies Used
- **Language**: Java 17
- **Framework**: Spring Boot 3.3.11
- **Database**: SQL Server
- **Template Engine**: Thymeleaf
- **Build Tool**: Maven
- **Frontend**: HTML5, CSS3, JavaScript

## 👥 Credits
Developed by **Koustubh Deodhar**.
