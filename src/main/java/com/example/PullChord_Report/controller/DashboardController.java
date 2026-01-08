package com.example.PullChord_Report.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.PullChord_Report.entity.Z3PullchordT2Entity;
import com.example.PullChord_Report.entity.Z5PullchordTEntity;
import com.example.PullChord_Report.entity.Z7PullchordTEntity;
import com.example.PullChord_Report.entity.Z9PullchordTEntity;
import com.example.PullChord_Report.repository.Z3PullchordT2Repository;
import com.example.PullChord_Report.repository.Z5PullchordTRepository;
import com.example.PullChord_Report.repository.Z7PullchordTRepository;
import com.example.PullChord_Report.repository.Z9PullchordTRepository;

@Controller
public class DashboardController {

    @Autowired
    private Z3PullchordT2Repository z3Repository;

    @Autowired
    private Z5PullchordTRepository z5Repository;

    @Autowired
    private Z7PullchordTRepository z7Repository;

    @Autowired
    private Z9PullchordTRepository z9Repository;

    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(defaultValue = "Z3 Pullchord T2") String selectedTable,
            @RequestParam(required = false) String station,
            @RequestParam(required = false) String shift,
            @RequestParam(required = false, name = "fromDateTime") String from,
            @RequestParam(required = false, name = "toDateTime") String to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "srNo") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        long startTime = System.currentTimeMillis();

        // 1. Clean Inputs
        if (station != null && station.trim().isEmpty())
            station = null;
        if (shift != null && shift.trim().isEmpty())
            shift = null;

        if (from != null && !from.trim().isEmpty()) {
            from = from.replace("T", " ");
            if (from.length() == 16)
                from += ":00";
        } else {
            from = null;
        }

        if (to != null && !to.trim().isEmpty()) {
            to = to.replace("T", " ");
            if (to.length() == 16)
                to += ":59";
        } else {
            to = null;
        }

        // 2. Prepare Sorting & Paging
        int pageSize = size;
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();
        PageRequest pageable = PageRequest.of(page, pageSize, sort);

        // 3. PARALLEL EXECUTION START
        // We launch the analytic queries immediately in separate threads safely

        // A. KPI Counts (Parallel)
        java.util.concurrent.CompletableFuture<Long> z3CountFuture = java.util.concurrent.CompletableFuture
                .supplyAsync(() -> z3Repository.count());
        java.util.concurrent.CompletableFuture<Long> z5CountFuture = java.util.concurrent.CompletableFuture
                .supplyAsync(() -> z5Repository.count());
        java.util.concurrent.CompletableFuture<Long> z7CountFuture = java.util.concurrent.CompletableFuture
                .supplyAsync(() -> z7Repository.count());
        java.util.concurrent.CompletableFuture<Long> z9CountFuture = java.util.concurrent.CompletableFuture
                .supplyAsync(() -> z9Repository.count());

        // B. Chart Analytics (Parallel - Only fetched from Default Z3 for now as per
        // original logic)
        java.util.concurrent.CompletableFuture<List<Object[]>> shiftDataFuture = java.util.concurrent.CompletableFuture
                .supplyAsync(() -> z3Repository.findShiftCounts());
        java.util.concurrent.CompletableFuture<List<Object[]>> lineDataFuture = java.util.concurrent.CompletableFuture
                .supplyAsync(() -> z3Repository.findLineCounts());
        java.util.concurrent.CompletableFuture<List<Object[]>> stationDataFuture = java.util.concurrent.CompletableFuture
                .supplyAsync(() -> z3Repository.findTopStations());

        // 4. MAIN QUERY (Sync - run on main thread to keep transaction bound)
        // This is usually the heaviest query, so we run it while others work in
        // background
        org.springframework.data.domain.Page<?> pageResult;

        String finalStation = station;
        String finalShift = shift;
        String finalFrom = from;
        String finalTo = to;

        switch (selectedTable) {
            case "Z5 Pullchord T":
                pageResult = z5Repository.searchReports(finalStation, finalShift, finalFrom, finalTo, pageable);
                break;
            case "Z7 Pullchord T":
                pageResult = z7Repository.searchReports(finalStation, finalShift, finalFrom, finalTo, pageable);
                break;
            case "Z9 Pullchord T":
                pageResult = z9Repository.searchReports(finalStation, finalShift, finalFrom, finalTo, pageable);
                break;
            default:
                pageResult = z3Repository.searchReports(finalStation, finalShift, finalFrom, finalTo, pageable);
        }

        // 5. JOIN RESULTS (Wait for background tasks)
        try {
            java.util.concurrent.CompletableFuture.allOf(z3CountFuture, z5CountFuture, z7CountFuture, z9CountFuture,
                    shiftDataFuture, lineDataFuture, stationDataFuture).join();

            // KPI Logic
            long totalEvents = z3CountFuture.get() + z5CountFuture.get() + z7CountFuture.get() + z9CountFuture.get();
            model.addAttribute("totalEvents", totalEvents);

            // Analytics Logic - Pie Chart (Shift)
            List<String> shiftLabels = new java.util.ArrayList<>();
            List<Long> shiftValues = new java.util.ArrayList<>();
            for (Object[] row : shiftDataFuture.get()) {
                if (row[0] != null) {
                    shiftLabels.add("Shift " + row[0].toString());
                    shiftValues.add(((Number) row[1]).longValue());
                }
            }
            model.addAttribute("shiftLabels", shiftLabels);
            model.addAttribute("shiftValues", shiftValues);

            // Analytics Logic - Bar Chart (Line)
            List<String> lineLabels = new java.util.ArrayList<>();
            List<Long> lineValues = new java.util.ArrayList<>();
            for (Object[] row : lineDataFuture.get()) {
                if (row[0] != null) {
                    lineLabels.add(row[0].toString());
                    lineValues.add(((Number) row[1]).longValue());
                }
            }
            model.addAttribute("lineLabels", lineLabels);
            model.addAttribute("lineValues", lineValues);

            // Analytics Logic - Doughnut (Station)
            List<String> stationLabels = new java.util.ArrayList<>();
            List<Long> stationValues = new java.util.ArrayList<>();
            for (Object[] row : stationDataFuture.get()) {
                if (row[0] != null) {
                    stationLabels.add(row[0].toString());
                    stationValues.add(((Number) row[1]).longValue());
                }
            }
            model.addAttribute("stationLabels", stationLabels);
            model.addAttribute("stationValues", stationValues);

            // 4. EXACT MANUFACTUIRNG KPI FORMULAS
            // ---------------------------------------------------------
            // Constants
            double availableTimeMins;
            if (finalShift != null && !finalShift.isEmpty()) {
                availableTimeMins = 480.0; // 8 Hours for one shift
            } else {
                availableTimeMins = 1440.0; // 24 Hours for full day
            }

            // 1. Downtime (Estimated as 10 mins per breakdown since only start time exists)
            long breakdownCount = totalEvents; // No. of Breakdowns
            double estimatedRepairTime = 10.0; // 10 mins avg
            double totalDowntimeMins = breakdownCount * estimatedRepairTime; // To. Breakdown Hrs (in mins)

            // 2. Operating Time
            double operatingTimeMins = Math.max(0, availableTimeMins - totalDowntimeMins);

            // 3. Availability (Uptime) %
            // Formula: Uptime % = 100% - (Downtime / Available Time)%
            double uptimePercentage = 100.0 - ((totalDowntimeMins / availableTimeMins) * 100.0);

            // 4. Breakdown %
            // Formula: Breakdown % = (Downtime / Production Time) * 100
            double breakdownPercentage = (totalDowntimeMins / availableTimeMins) * 100.0;

            // 5. MTBF
            // Formula: (Available Time - Downtime) / No. of Breakdown
            // Result in HOURS
            double mtbfHours = (breakdownCount > 0) ? (operatingTimeMins / 60.0) / breakdownCount
                    : (availableTimeMins / 60.0);

            // 6. MTTR
            // Formula: To. Breakdown Hrs / No. of Breakdown
            // Result in MINUTES (for display)
            double mttrMins = (breakdownCount > 0) ? totalDowntimeMins / breakdownCount : 0.0;

            // 7. OEE
            // Formula: Availability * Performance * Quality
            double availabilityRatio = operatingTimeMins / availableTimeMins;
            double performance = 0.96; // 96% Standard
            double quality = 0.99; // 99% Standard
            double oeePercentage = availabilityRatio * performance * quality * 100.0;

            // ---------------------------------------------------------

            // Pass to View
            model.addAttribute("kpiAvailability", Math.round(uptimePercentage * 100.0) / 100.0);
            model.addAttribute("kpiBreakdownPct", Math.round(breakdownPercentage * 100.0) / 100.0);
            model.addAttribute("kpiOEE", Math.round(oeePercentage * 100.0) / 100.0);
            model.addAttribute("kpiMTTR", Math.round(mttrMins * 10.0) / 10.0);
            model.addAttribute("kpiMTBF", Math.round(mtbfHours * 10.0) / 10.0);
            model.addAttribute("kpiDowntime", Math.round(totalDowntimeMins * 10.0) / 10.0);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Async Fetch Error: " + e.getMessage());
        }

        // 6. Populate Final Model
        model.addAttribute("records", pageResult.getContent());
        model.addAttribute("selectedTable", selectedTable);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("totalRecords", pageResult.getTotalElements());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);

        // Filter state
        model.addAttribute("station", station);
        model.addAttribute("shift", shift);
        model.addAttribute("fromDateTime", from);
        model.addAttribute("toDateTime", to);

        System.out.println("Dashboard Load Time: " + (System.currentTimeMillis() - startTime) + "ms");

        return "KD_VECV_NewClientDemoUI";
    }

    @GetMapping("/api/kpi")
    @ResponseBody
    public Map<String, Object> getKPIData() {
        Map<String, Object> kpiData = new HashMap<>();

        // Get counts from all tables
        long z3Count = z3Repository.count();
        long z5Count = z5Repository.count();
        long z7Count = z7Repository.count();
        long z9Count = z9Repository.count();

        long totalEvents = z3Count + z5Count + z7Count + z9Count;

        kpiData.put("totalEvents", totalEvents);
        kpiData.put("z3Events", z3Count);
        kpiData.put("z5Events", z5Count);
        kpiData.put("z7Events", z7Count);
        kpiData.put("z9Events", z9Count);
        kpiData.put("totalDowntime", "N/A"); // Can be calculated if needed
        kpiData.put("avgDuration", "N/A"); // Can be calculated if needed

        return kpiData;
    }

    @GetMapping("/api/downtime")
    @ResponseBody
    public Map<String, Object> getDowntimeData(
            @RequestParam(defaultValue = "Z3") String zone,
            @RequestParam(required = false) String station,
            @RequestParam(required = false) String shift) {

        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> data;

        switch (zone) {
            case "Z3":
                data = z3Repository.findAll().stream()
                        .map(this::convertZ3ToMap)
                        .collect(Collectors.toList());
                break;
            case "Z5":
                data = z5Repository.findAll().stream()
                        .map(this::convertZ5ToMap)
                        .collect(Collectors.toList());
                break;
            case "Z7":
                data = z7Repository.findAll().stream()
                        .map(this::convertZ7ToMap)
                        .collect(Collectors.toList());
                break;
            case "Z9":
                data = z9Repository.findAll().stream()
                        .map(this::convertZ9ToMap)
                        .collect(Collectors.toList());
                break;
            default:
                data = z3Repository.findAll().stream()
                        .map(this::convertZ3ToMap)
                        .collect(Collectors.toList());
        }

        response.put("data", data);
        response.put("total", data.size());

        return response;
    }

    private Map<String, Object> convertZ3ToMap(Z3PullchordT2Entity entity) {
        Map<String, Object> map = new HashMap<>();
        map.put("sr", entity.getSrNo());
        map.put("datetime", entity.getDateTime());
        map.put("shift", entity.getShift());
        map.put("line", entity.getLine());
        map.put("zone", entity.getZone());
        map.put("station", entity.getStation());
        map.put("side", entity.getSide());
        map.put("maintenanceCall", entity.getMaintenanceCall());
        map.put("materialCall", entity.getMaterialCall());
        map.put("productionCall", entity.getProductionCall());
        map.put("pullCord", entity.getPullCord());
        map.put("qualityCall", entity.getQualityCall());
        map.put("remark", entity.getRemark());
        return map;
    }

    private Map<String, Object> convertZ5ToMap(Z5PullchordTEntity entity) {
        Map<String, Object> map = new HashMap<>();
        map.put("sr", entity.getSrNo());
        map.put("datetime", entity.getDateTime());
        map.put("shift", entity.getShift());
        map.put("line", entity.getLine());
        map.put("zone", entity.getZone());
        map.put("station", entity.getStation());
        map.put("side", entity.getSide());
        map.put("maintenanceCall", entity.getMaintenanceCall());
        map.put("materialCall", entity.getMaterialCall());
        map.put("productionCall", entity.getProductionCall());
        map.put("pullCord", entity.getPullCord());
        map.put("qualityCall", entity.getQualityCall());
        map.put("remark", entity.getRemark());
        return map;
    }

    private Map<String, Object> convertZ7ToMap(Z7PullchordTEntity entity) {
        Map<String, Object> map = new HashMap<>();
        map.put("sr", entity.getSrNo());
        map.put("datetime", entity.getDateTime());
        map.put("shift", entity.getShift());
        map.put("line", entity.getLine());
        map.put("zone", entity.getZone());
        map.put("station", entity.getStation());
        map.put("side", entity.getSide());
        map.put("maintenanceCall", entity.getMaintenanceCall());
        map.put("materialCall", entity.getMaterialCall());
        map.put("productionCall", entity.getProductionCall());
        map.put("pullCord", entity.getPullCord());
        map.put("qualityCall", entity.getQualityCall());
        map.put("remark", entity.getRemark());
        return map;
    }

    private Map<String, Object> convertZ9ToMap(Z9PullchordTEntity entity) {
        Map<String, Object> map = new HashMap<>();
        map.put("sr", entity.getSrNo());
        map.put("datetime", entity.getDateTime());
        map.put("shift", entity.getShift());
        map.put("line", entity.getLine());
        map.put("zone", entity.getZone());
        map.put("station", entity.getStation());
        map.put("side", entity.getSide());
        map.put("maintenanceCall", entity.getMaintenanceCall());
        map.put("materialCall", entity.getMaterialCall());
        map.put("productionCall", entity.getProductionCall());
        map.put("pullCord", entity.getPullCord());
        map.put("qualityCall", entity.getQualityCall());
        map.put("remark", entity.getRemark());
        return map;
    }
}
