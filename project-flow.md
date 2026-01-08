# PullChord Report - Project Data Flow

## Overview
This document explains the complete data flow from the SQL Server database to the web browser for the VECV SCADA Pull Chord Report Viewer application.

---

## 📊 Complete Data Flow Architecture

### **1. Database Layer (SQL Server in Docker)**

```
┌─────────────────────────────────────────┐
│    VECV_Scada_DB (SQL Server)          │
│    Host: localhost:1433                 │
│    User: sa / Password: Ats1234@        │
├─────────────────────────────────────────┤
│  📁 Physical Tables (Normalized)        │
│   ├── Z3_Pullchord_T2                   │
│   ├── Z5_Pullchord_T                    │
│   ├── Z7_Pullchord_T                    │
│   └── Z9_Pullchord_T                    │
│                                          │
│  Each table has:                        │
│   • SrNo (Primary Key)                  │
│   • Date_Time, Shift, Line, Zone        │
│   • Station, Side                       │
│   • Maintenance_Call, Material_Call     │
│   • Production_Call, Pull_Cord          │
│   • Quality_Call, Remark                │
└─────────────────────────────────────────┘
             ↓
┌─────────────────────────────────────────┐
│  📊 Database Views (Fixed to point to   │
│      VECV_Scada_DB instead of _NEW)     │
│   ├── vw_Z3_Pullchord_All               │
│   ├── vw_Z5_Pullchord_All               │
│   ├── vw_Z7_Pullchord_All               │
│   └── vw_Z9_Pullchord_All               │
│                                          │
│  Purpose: Add 'TableName' column        │
│  Example:                                │
│   CREATE VIEW vw_Z3_Pullchord_All AS    │
│   SELECT 'Z3' AS TableName,             │
│          [SrNo], [Date_Time], [Shift],  │
│          [Line], [Zone], [Station],     │
│          [Side], [Maintenance_Call],    │
│          [Material_Call],               │
│          [Production_Call],             │
│          [Pull_Cord], [Quality_Call],   │
│          [Remark]                       │
│   FROM [VECV_Scada_DB].[dbo]...        │
│        [Z3_Pullchord_T2];               │
└─────────────────────────────────────────┘
```

---

### **2. Backend Layer (Spring Boot on port 8070)**

#### **2.1 Entity Classes (JPA/Hibernate)**
```java
┌─────────────────────────────────────────┐
│  🔧 Entity Classes                      │
│   ├── Z3PullchordT2Entity.java          │
│   ├── Z5PullchordTEntity.java           │
│   ├── Z7PullchordTEntity.java           │
│   └── Z9PullchordTEntity.java           │
│                                          │
│  @Entity                                │
│  @Table(name="Z3_Pullchord_T2")         │
│  public class Z3PullchordT2Entity {     │
│      @Id                                │
│      @Column(name="SrNo")               │
│      private int srNo;                  │
│                                          │
│      @Column(name="Date_Time")          │
│      private String dateTime;           │
│                                          │
│      @Column(name="Maintenance_Call")   │
│      private int maintenanceCall;       │
│      ...                                │
│  }                                      │
│                                          │
│  Purpose: Maps Java objects to DB rows  │
└─────────────────────────────────────────┘
```

#### **2.2 Repository Interfaces**
```java
┌─────────────────────────────────────────┐
│  📦 Repository Interfaces               │
│   ├── Z3PullchordT2Repository          │
│   ├── Z5PullchordTRepository            │
│   ├── Z7PullchordTRepository            │
│   └── Z9PullchordTRepository            │
│                                          │
│  public interface Z3PullchordT2Repository│
│      extends JpaRepository<             │
│          Z3PullchordT2Entity, Integer> {│
│  }                                      │
│                                          │
│  Auto-provides methods:                 │
│   • findAll()                           │
│   • findById(id)                        │
│   • save(entity)                        │
│   • delete(entity)                      │
└─────────────────────────────────────────┘
```

#### **2.3 Controllers**
```java
┌─────────────────────────────────────────┐
│  🎮 Z3PullchordT2Controller.java        │
│                                          │
│  @Controller                            │
│  public class Z3PullchordT2Controller { │
│                                          │
│    @Autowired                           │
│    private Z3PullchordT2Repository      │
│             z3Repository;               │
│    @Autowired                           │
│    private Z5PullchordTRepository       │
│             z5Repository;               │
│    // ... Z7, Z9 repositories           │
│                                          │
│    @GetMapping("/")                     │
│    public String home() {               │
│        return "index";                  │
│    }                                    │
│                                          │
│    @GetMapping("/report")               │
│    public String viewReport(            │
│        @RequestParam String selectedTable,│
│        @RequestParam String station,    │
│        @RequestParam String shift,      │
│        Model model) {                   │
│                                          │
│        // Fetch data based on table     │
│        List<Entity> records = ...;      │
│                                          │
│        // Apply filters                 │
│        // Add pagination                │
│                                          │
│        model.addAttribute("records",    │
│                           records);      │
│        model.addAttribute(              │
│            "selectedTable",             │
│            selectedTable);              │
│                                          │
│        return "report";                 │
│    }                                    │
│                                          │
│    @GetMapping("/download")             │
│    public void downloadExcel(...) {     │
│        // Generate Excel file           │
│    }                                    │
│  }                                      │
└─────────────────────────────────────────┘

┌─────────────────────────────────────────┐
│  🎮 DashboardController.java            │
│                                          │
│  @Controller                            │
│  public class DashboardController {     │
│                                          │
│    @GetMapping("/dashboard")            │
│    public String dashboard() {          │
│        return "KD_VECV_NewClientDemoUI";│
│    }                                    │
│  }                                      │
└─────────────────────────────────────────┘
```

---

### **3. Frontend Layer (Thymeleaf Templates)**

#### **3.1 Homepage**
```html
┌─────────────────────────────────────────┐
│  🌐 index.html                          │
│                                          │
│  <!DOCTYPE html>                        │
│  <html>                                 │
│    <body>                               │
│      <h1>Welcome</h1>                   │
│      <a href="/report">                 │
│        View Reports                     │
│      </a>                               │
│    </body>                              │
│  </html>                                │
└─────────────────────────────────────────┘
```

#### **3.2 Report Viewer**
```html
┌─────────────────────────────────────────┐
│  📝 report.html (SCADA REPORT VIEWER)  │
│                                          │
│  1. Form Controls:                      │
│     <form method="get" action="/report">│
│       <select name="selectedTable">     │
│         <option>Z3 Pullchord T2</option>│
│         <option>Z5 Pullchord T</option> │
│         <option>Z7 Pullchord T</option> │
│         <option>Z9 Pullchord T</option> │
│       </select>                         │
│                                          │
│       <select name="station">           │
│         <option>All Stations</option>   │
│         ...                             │
│       </select>                         │
│                                          │
│       <select name="shift">             │
│         <option>All Shifts</option>     │
│         <option>A</option>              │
│         <option>B</option>              │
│         <option>C</option>              │
│       </select>                         │
│                                          │
│       <input type="datetime-local"      │
│              name="fromDateTime">       │
│       <input type="datetime-local"      │
│              name="toDateTime">         │
│                                          │
│       <button type="submit">Filter      │
│       </button>                         │
│     </form>                             │
│                                          │
│  2. Thymeleaf Data Binding:             │
│     <div th:switch="${selectedTable}">  │
│       <div th:case="'Z3 Pullchord T2'"> │
│         <table>                         │
│           <thead>                       │
│             <tr>                        │
│               <th>Sr No</th>            │
│               <th>Date Time</th>        │
│               <th>Shift</th>            │
│               ...                       │
│             </tr>                       │
│           </thead>                      │
│           <tbody>                       │
│             <tr th:each="record:        │
│                          ${records}">   │
│               <td th:text=              │
│                   "${record.srNo}">     │
│               </td>                     │
│               <td th:text=              │
│                   "${record.dateTime}"> │
│               </td>                     │
│               ...                       │
│             </tr>                       │
│           </tbody>                      │
│         </table>                        │
│       </div>                            │
│       <!-- Similar for Z5, Z7, Z9 -->   │
│     </div>                              │
│                                          │
│  3. Pagination:                         │
│     <div class="pagination">            │
│       <!-- Page numbers -->             │
│     </div>                              │
│                                          │
│  4. Download Button:                    │
│     <button onclick="downloadExcel()">  │
│       Download Excel                    │
│     </button>                           │
└─────────────────────────────────────────┘
```

#### **3.3 Dashboard UI**
```html
┌─────────────────────────────────────────┐
│  🎨 KD_VECV_NewClientDemoUI.html        │
│     (Dashboard Wrapper)                 │
│                                          │
│  Structure:                             │
│  • Navbar                               │
│    - VECV Logo                          │
│    - Theme Toggle (Light/Dark)          │
│    - Live Clock                         │
│                                          │
│  • Sidebar                              │
│    - 🏠 Dashboard                       │
│    - 🗺️ Heatmap                        │
│    - 📊 Pull Chord Reports ← REAL DATA │
│    - ⬇️ Download CSV                   │
│    - 📄 Download PDF                   │
│                                          │
│  • Main Content Area                    │
│    - KPI Cards (placeholders)           │
│    - Charts (demo data)                 │
│    - Heatmap (demo data)                │
│                                          │
│    - 📊 Pull Chord Reports Section:    │
│      <div id="reportsSection"           │
│           style="display:none;">        │
│        <h3>Pull Chord Reports</h3>      │
│        <iframe src="/report"            │
│                style="width:100%;       │
│                       height:800px;">   │
│        </iframe>                        │
│      </div>                             │
│      ↑ This iframe loads the working    │
│        report.html page with REAL data! │
│                                          │
│    - Downtime Table (shows message      │
│      to use Reports section)            │
│                                          │
│  • Footer                               │
│    - Copyright info                     │
│                                          │
│  JavaScript:                            │
│  function showPullChordReports() {      │
│    reportsSection.style.display="block";│
│    tableSection.style.display="none";   │
│  }                                      │
└─────────────────────────────────────────┘
```

---

## **🔄 Complete Request Flow Example**

### Scenario: User selects "Z5 Pullchord T" with Shift "A" and clicks Filter

```
┌──────────────────────────────────────────────────────────┐
│ Step 1: Browser → HTTP Request                          │
└──────────────────────────────────────────────────────────┘
   GET /report?selectedTable=Z5+Pullchord+T&station=&shift=A&fromDateTime=&toDateTime=

┌──────────────────────────────────────────────────────────┐
│ Step 2: Spring Boot DispatcherServlet                   │
└──────────────────────────────────────────────────────────┘
   • Routes request to Z3PullchordT2Controller.viewReport()
   • Extracts request parameters:
     - selectedTable = "Z5 Pullchord T"
     - station = ""
     - shift = "A"

┌──────────────────────────────────────────────────────────┐
│ Step 3: Controller Logic                                │
└──────────────────────────────────────────────────────────┘
   if (selectedTable.equals("Z5 Pullchord T")) {
       // Call repository
       List<Z5PullchordTEntity> allRecords = 
           z5Repository.findAll();
       
       // Filter by shift
       filteredRecords = allRecords.stream()
           .filter(r -> r.getShift().equals("A"))
           .collect(Collectors.toList());
       
       // Add to model
       model.addAttribute("records", filteredRecords);
       model.addAttribute("selectedTable", "Z5 Pullchord T");
   }

┌──────────────────────────────────────────────────────────┐
│ Step 4: Repository → JPA/Hibernate                      │
└──────────────────────────────────────────────────────────┘
   z5Repository.findAll() triggers:
   
   • Hibernate generates SQL:
     SELECT * FROM Z5_Pullchord_T
   
   • Opens JDBC connection to:
     jdbc:sqlserver://localhost:1433;
     databaseName=VECV_Scada_DB;
     user=sa;password=Ats1234@

┌──────────────────────────────────────────────────────────┐
│ Step 5: SQL Server Execution                            │
└──────────────────────────────────────────────────────────┘
   Database: VECV_Scada_DB
   
   1. Executes query on Z5_Pullchord_T table
   2. Reads all rows from disk/memory
   3. Returns ResultSet:
      ┌────┬──────────┬───────┬──────┬─────┬─────────┐
      │SrNo│Date_Time │Shift  │Line  │Zone │Station  │
      ├────┼──────────┼───────┼──────┼─────┼─────────┤
      │229 │2025-11-17│A      │TITAN │Prep │MZ_01    │
      │230 │2025-11-17│A      │TITAN │Prep │MZ_02    │
      │237 │2025-11-17│A      │TITAN │Prep │MZ_03    │
      └────┴──────────┴───────┴──────┴─────┴─────────┘

┌──────────────────────────────────────────────────────────┐
│ Step 6: Hibernate → Entity Objects                      │
└──────────────────────────────────────────────────────────┘
   ResultSet rows → Z5PullchordTEntity objects
   
   new Z5PullchordTEntity(
       srNo: 229,
       dateTime: "2025-11-17 14:51:37.34",
       shift: "A",
       line: "TITAN",
       zone: "Prep Line",
       station: "MZ_01",
       side: "LH",
       maintenanceCall: 1,
       materialCall: 0,
       ...
   )

┌──────────────────────────────────────────────────────────┐
│ Step 7: Controller → View (Thymeleaf)                   │
└──────────────────────────────────────────────────────────┘
   return "report";  // Returns view name
   
   Spring resolves to:
   /src/main/resources/templates/report.html

┌──────────────────────────────────────────────────────────┐
│ Step 8: Thymeleaf Template Engine                       │
└──────────────────────────────────────────────────────────┘
   Processes template with model data:
   
   <div th:case="'Z5 Pullchord T'">
     <tr th:each="record : ${records}">
       <td th:text="${record.srNo}">229</td>
       <td th:text="${record.dateTime}">2025-11-17...</td>
       <td th:text="${record.shift}">A</td>
       ...
     </tr>
   </div>
   
   Generates final HTML:
   <tr>
     <td>229</td>
     <td>2025-11-17 14:51:37.34</td>
     <td>A</td>
     <td>TITAN</td>
     ...
   </tr>

┌──────────────────────────────────────────────────────────┐
│ Step 9: HTTP Response                                   │
└──────────────────────────────────────────────────────────┘
   Spring Boot sends complete HTML to browser:
   
   HTTP/1.1 200 OK
   Content-Type: text/html;charset=UTF-8
   
   <!DOCTYPE html>
   <html>
   ...complete rendered page...
   </html>

┌──────────────────────────────────────────────────────────┐
│ Step 10: Browser Rendering                              │
└──────────────────────────────────────────────────────────┘
   Browser receives HTML and displays:
   • SCADA REPORT VIEWER header
   • Filters (with Z5 selected, Shift A selected)
   • Table with filtered data (only Shift A records)
   • Pagination controls
   • Download button
```

---

## **🔑 Key Components Summary**

### **Database (SQL Server)**
- **Physical Tables**: Store actual data
- **Views**: Optional - provide convenient query interface
- **Connection**: JDBC via HikariCP connection pool

### **Backend (Spring Boot)**
- **Entities**: Java objects that map to database tables
- **Repositories**: Provide database operations (CRUD)
- **Controllers**: Handle HTTP requests, orchestrate data flow
- **Thymeleaf**: Server-side template engine for HTML

### **Frontend**
- **report.html**: Working report viewer with real data
- **KD_VECV_NewClientDemoUI.html**: Dashboard UI wrapper
- **iframe**: Embeds report.html inside dashboard

---

## **📁 File Structure**

```
PullChord-Report/Scadda-Report/
├── src/main/
│   ├── java/com/example/PullChord_Report/
│   │   ├── controller/
│   │   │   ├── Z3PullchordT2Controller.java  ← Main controller
│   │   │   └── DashboardController.java      ← Dashboard UI
│   │   ├── entity/
│   │   │   ├── Z3PullchordT2Entity.java      ← DB mapping
│   │   │   ├── Z5PullchordTEntity.java
│   │   │   ├── Z7PullchordTEntity.java
│   │   │   └── Z9PullchordTEntity.java
│   │   └── repository/
│   │       ├── Z3PullchordT2Repository.java  ← Data access
│   │       ├── Z5PullchordTRepository.java
│   │       ├── Z7PullchordTRepository.java
│   │       └── Z9PullchordTRepository.java
│   └── resources/
│       ├── application.properties            ← DB config
│       └── templates/
│           ├── index.html                    ← Homepage
│           ├── report.html                   ← Report viewer
│           └── KD_VECV_NewClientDemoUI.html  ← Dashboard
└── pom.xml                                   ← Maven dependencies
```

---

## **🌐 URL Endpoints**

| URL | Description | Returns |
|-----|-------------|---------|
| `http://localhost:8070/` | Homepage | index.html |
| `http://localhost:8070/report` | Report viewer with real data | report.html with database data |
| `http://localhost:8070/dashboard` | Dashboard UI | KD_VECV_NewClientDemoUI.html |
| `http://localhost:8070/download` | Excel export | Excel file download |

---

## **🔧 Configuration**

### application.properties
```properties
server.port=8070

spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=VECV_Scada_DB;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=Ats1234@
spring.datasource.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver

spring.jpa.hibernate.dialect=org.hibernate.dialect.SQLServer2012Dialect
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=none
```

---

## **✅ Data Flow Status**

- ✅ Database connection: **WORKING**
- ✅ Entity mapping: **WORKING**
- ✅ Repository queries: **WORKING**
- ✅ Controller logic: **WORKING**
- ✅ Thymeleaf rendering: **WORKING**
- ✅ Report page: **WORKING** (shows real data)
- ✅ Dashboard: **WORKING** (embeds report via iframe)
- ✅ Filtering: **WORKING**
- ✅ Pagination: **WORKING**
- ✅ Excel download: **WORKING**

---

**Last Updated**: January 5, 2026
**Project**: VECV SCADA Pull Chord Report Viewer
**Technology Stack**: Spring Boot 3.3.11, SQL Server, Thymeleaf, Bootstrap
