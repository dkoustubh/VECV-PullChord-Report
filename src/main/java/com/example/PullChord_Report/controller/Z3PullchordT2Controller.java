package com.example.PullChord_Report.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class Z3PullchordT2Controller {

	@Autowired
	private Z3PullchordT2Repository z3PullchordT2RepositoryInstance;

	@Autowired
	private Z5PullchordTRepository z5PullchordTRepositoryInstance;

	@Autowired
	private Z7PullchordTRepository z7PullchordTRepositoryInstance;

	@Autowired
	private Z9PullchordTRepository z9PullchordTRepositoryInstance;

	@GetMapping("/")
	public String home() {
		return "report"; // name of your HTML file without `.html`
	}

	@GetMapping("/report")
	public String viewReport(@RequestParam(defaultValue = "Z3 Pullchord T2") String selectedTable,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String fromDateTime, @RequestParam(required = false) String toDateTime,
			@RequestParam(required = false) String shiftName, @RequestParam(required = false) String station,
			@RequestParam(required = false) String objectName, Model model) {

		System.out.println("in report");

		Pageable pageable = PageRequest.of(page, size);
		Page<?> recordsPage = Page.empty(pageable);

		// Add filter parameters to model
		model.addAttribute("fromDateTime", fromDateTime);
		model.addAttribute("toDateTime", toDateTime);
		model.addAttribute("shiftName", shiftName);
		model.addAttribute("station", station);
		model.addAttribute("objectName", objectName);

		// Map of stations per table
		Map<String, List<String>> stationMap = new HashMap<>();
		stationMap.put("Z3 Pullchord T2",
				List.of("MZ_01", "MZ_02", "MZ_03", "MZ_04", "MZ_05", "MZ_06", "MZ_07", "MZ_08", "MZ_09"));
		stationMap.put("Z5 Pullchord T", List.of("PL_ 01", "PL_ 02", "PL_ 03", "PL_ 04", "PL_ 05", "PL_ 06", "PL_ 07",
				"PL_ 08", "PL_ 09", "PL_ 10", "PL_ 11", "PL_ 12", "PL_ 13", "PL_ 14", "PL_ 15"));
		stationMap.put("Z7 Pullchord T", List.of("UB_17", "UB_18", "UB_19", "UB_20", "UB_21", "UB_22", "UB_23", "UB_24",
				"UB_25", "UB_26", "UB_26", "UB_27", "UB_28", "UB_29", "UB_30"));
		stationMap.put("Z9 Pullchord T",
				List.of("FL 27", "FL 28", "FL 29", "FL 30", "FL 31", "FL 32", "FL 33", "FL 34", "FL 35", "FL 36"));

		List<String> stations = stationMap.getOrDefault(selectedTable, new ArrayList<>());
		model.addAttribute("stations", stations);

		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
		DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		// Date handling
		boolean hasFilters = fromDateTime != null && toDateTime != null && !fromDateTime.isEmpty()
				&& !toDateTime.isEmpty();
		String fromStr = null;
		String toStr = null;

		if (hasFilters) {
			try {

				LocalDateTime from = LocalDateTime.parse(fromDateTime, inputFormatter);
				LocalDateTime to = LocalDateTime.parse(toDateTime, inputFormatter);
				fromStr = from.format(dbFormatter);
				toStr = to.format(dbFormatter);
			} catch (Exception e) {
				model.addAttribute("error", "Invalid date format: " + e.getMessage());
				e.printStackTrace();
			}
		}

		// Switch table logic
		switch (selectedTable) {
			case "Z3 Pullchord T2":
				if (hasFilters && fromStr != null && toStr != null) {
					System.out.println("in if");
					if (station != null && !station.isEmpty() && shiftName != null && !shiftName.isEmpty()) {
						// Filter only by station
						System.out.println("in all have paramertr");
						recordsPage = z3PullchordT2RepositoryInstance.findByStationAndShift(station, shiftName,
								pageable);
						System.out.println("In filter by station");

					} else if (shiftName != null && !shiftName.isEmpty()) {
						recordsPage = z3PullchordT2RepositoryInstance.findFilteredWithShift(fromStr, toStr, shiftName,
								pageable);
						System.out.println("in filter with shift");
					} else if (station != null && !station.isEmpty()) {
						// Filter only by station
						System.out.println("in station");
						recordsPage = z3PullchordT2RepositoryInstance.findByStation(station, pageable);
						System.out.println("In filter by station");

					}

					else {
						recordsPage = z3PullchordT2RepositoryInstance.findFiltered(fromStr, toStr, pageable);
					}

				} else if (station != null && !station.isEmpty()) {
					// Filter only by station
					System.out.println("in station");
					recordsPage = z3PullchordT2RepositoryInstance.findByStation(station, pageable);
					System.out.println("In filter by station");

				} else {
					recordsPage = z3PullchordT2RepositoryInstance.findAll(pageable);
				}
				break;

			case "Z5 Pullchord T":
				if (hasFilters && fromStr != null && toStr != null) {
					System.out.println("in if");

					if (station != null && !station.isEmpty() && shiftName != null && !shiftName.isEmpty()) {
						// Filter only by station
						System.out.println("in all have paramertr");
						recordsPage = z5PullchordTRepositoryInstance.findByStationAndShift(station, shiftName,
								pageable);
						System.out.println("In filter by station");

					} else if (shiftName != null && !shiftName.isEmpty()) {
						recordsPage = z5PullchordTRepositoryInstance.findFilteredWithShift(fromStr, toStr, shiftName,
								pageable);
						System.out.println("in filter with shift");
					} else if (station != null && !station.isEmpty()) {
						// Filter only by station
						System.out.println("in station");
						recordsPage = z5PullchordTRepositoryInstance.findByStation(station, pageable);
						System.out.println("In filter by station");

					} else {
						recordsPage = z5PullchordTRepositoryInstance.findFiltered(fromStr, toStr, pageable);
					}

				} else if (station != null && !station.isEmpty()) {
					// Filter only by station
					System.out.println("in station");
					recordsPage = z5PullchordTRepositoryInstance.findByStation(station, pageable);
					System.out.println("In filter by station");

				} else {
					recordsPage = z5PullchordTRepositoryInstance.findAll(pageable);
				}
				break;
			//
			// case "Z7 Pullchord T":
			// if (hasFilters && fromStr != null && toStr != null) {
			// if (shift != null && !shift.isEmpty()) {
			// recordsPage =
			// z7PullchordTRepositoryInstance.findByFromDateTimeBetweenAndShiftAndStation(fromStr,
			// toStr, shift, station, pageable);
			// } else {
			// recordsPage = z7PullchordTRepositoryInstance.findFiltered(fromStr, toStr,
			// shift, station, pageable);
			// }
			// } else {
			// recordsPage = z7PullchordTRepositoryInstance.findAll(pageable);
			// }
			// break;
			//
			// case "Z9 Pullchord T":
			// if (hasFilters && fromStr != null && toStr != null) {
			// if (shift != null && !shift.isEmpty()) {
			// recordsPage =
			// z9PullchordTRepositoryInstance.findByFromDateTimeBetweenAndShiftAndStation(fromStr,
			// toStr, shift, station, pageable);
			// } else {
			// recordsPage = z9PullchordTRepositoryInstance.findFiltered(fromStr, toStr,
			// shift, station, pageable);
			// }
			// } else {
			// recordsPage = z9PullchordTRepositoryInstance.findAll(pageable);
			// }
			// break;
		}

		model.addAttribute("records", recordsPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", recordsPage.getTotalPages());
		model.addAttribute("selectedTable", selectedTable);

		return "report";
	}

	@GetMapping("/download")
	@ResponseBody
	public void downloadExcel(@RequestParam String selectedTable,
			@RequestParam(required = false) String fromDateTime,
			@RequestParam(required = false) String toDateTime,
			@RequestParam(required = false, name = "shift") String shiftName,
			@RequestParam(required = false) String station,
			@RequestParam(required = false) String objectName, HttpServletResponse response) throws IOException {

		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Sheet1");

		// Clean parameters
		if (station != null && station.trim().isEmpty())
			station = null;
		if (shiftName != null && shiftName.trim().isEmpty())
			shiftName = null;

		if (fromDateTime != null && !fromDateTime.trim().isEmpty()) {
			fromDateTime = fromDateTime.replace("T", " ");
			if (fromDateTime.length() == 16)
				fromDateTime += ":00";
		} else {
			fromDateTime = null;
		}

		if (toDateTime != null && !toDateTime.trim().isEmpty()) {
			toDateTime = toDateTime.replace("T", " ");
			if (toDateTime.length() == 16)
				toDateTime += ":59";
		} else {
			toDateTime = null;
		}

		// Use a large page size to fetch "all" matching records without crashing memory
		// 50k limit is reasonable for Excel; adjust if needed.
		Pageable filePageable = PageRequest.of(0, 50000);

		List<?> allRecords = new ArrayList<>();

		switch (selectedTable) {
			case "Z5 Pullchord T":
				allRecords = z5PullchordTRepositoryInstance
						.searchReports(station, shiftName, fromDateTime, toDateTime, filePageable).getContent();
				break;
			case "Z7 Pullchord T":
				allRecords = z7PullchordTRepositoryInstance
						.searchReports(station, shiftName, fromDateTime, toDateTime, filePageable).getContent();
				break;
			case "Z9 Pullchord T":
				allRecords = z9PullchordTRepositoryInstance
						.searchReports(station, shiftName, fromDateTime, toDateTime, filePageable).getContent();
				break;
			default: // Z3
				allRecords = z3PullchordT2RepositoryInstance
						.searchReports(station, shiftName, fromDateTime, toDateTime, filePageable).getContent();
				break;
		}

		if (!allRecords.isEmpty()) {
			// Load image (logo)
			try (InputStream inputStream = new FileInputStream(
					"src/main/resources/static/new_loho_VECV-removebg-preview.png")) {
				byte[] imageBytes = inputStream.readAllBytes();
				int pictureIdx = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);

				Drawing<?> drawing = sheet.createDrawingPatriarch();
				CreationHelper helper = workbook.getCreationHelper();
				ClientAnchor anchor = helper.createClientAnchor();
				anchor.setCol1(0);
				anchor.setRow1(0);
				Picture picture = drawing.createPicture(anchor, pictureIdx);
				picture.resize(2);
			} catch (Exception e) {
				System.out.println("Logo not found or error loading: " + e.getMessage());
			}

			// Styles
			CellStyle titleStyle = workbook.createCellStyle();
			Font titleFont = workbook.createFont();
			titleFont.setBold(true);
			titleFont.setFontHeightInPoints((short) 20);
			titleFont.setColor(IndexedColors.BLACK.getIndex());
			titleStyle.setFont(titleFont);
			titleStyle.setAlignment(HorizontalAlignment.CENTER);
			titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			titleStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
			titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			CellStyle headerStyle = workbook.createCellStyle();
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 12);
			headerStyle.setFont(headerFont);
			headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			headerStyle.setBorderBottom(BorderStyle.THIN);
			headerStyle.setBorderTop(BorderStyle.THIN);
			headerStyle.setBorderLeft(BorderStyle.THIN);
			headerStyle.setBorderRight(BorderStyle.THIN);

			CellStyle dataStyle = workbook.createCellStyle();
			dataStyle.setBorderBottom(BorderStyle.THIN);
			dataStyle.setBorderTop(BorderStyle.THIN);
			dataStyle.setBorderLeft(BorderStyle.THIN);
			dataStyle.setBorderRight(BorderStyle.THIN);

			CellStyle dateStyle = workbook.createCellStyle();
			dateStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));
			dateStyle.setBorderBottom(BorderStyle.THIN);
			dateStyle.setBorderTop(BorderStyle.THIN);
			dateStyle.setBorderLeft(BorderStyle.THIN);
			dateStyle.setBorderRight(BorderStyle.THIN);

			Object first = allRecords.get(0);
			Field[] fields = first.getClass().getDeclaredFields();
			for (Field f : fields)
				f.setAccessible(true);

			// Title Row (Row 2)
			Row titleRow = sheet.createRow(2);
			Cell titleCell = titleRow.createCell(0);
			String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			titleCell.setCellValue("SCADA Report for " + selectedTable + " (Downloaded at: " + timestamp + ")");
			titleCell.setCellStyle(titleStyle);
			sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, fields.length - 1));

			// Header Row (Row 3)
			Row headerRow = sheet.createRow(3);
			for (int i = 0; i < fields.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(fields[i].getName());
				cell.setCellStyle(headerStyle);
			}

			// Data Rows (Row 4 onwards)
			for (int i = 0; i < allRecords.size(); i++) {
				Row dataRow = sheet.createRow(i + 4);
				Object record = allRecords.get(i);
				for (int j = 0; j < fields.length; j++) {
					Cell cell = dataRow.createCell(j);
					try {
						Object value = fields[j].get(record);
						if (value instanceof java.util.Date) {
							cell.setCellValue((java.util.Date) value);
							cell.setCellStyle(dateStyle);
						} else {
							if (value instanceof Number) {
								cell.setCellValue(((Number) value).doubleValue());
							} else {
								cell.setCellValue(value != null ? value.toString() : "");
							}
							cell.setCellStyle(dataStyle);
						}
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}

			// Auto-size columns
			for (int i = 0; i < fields.length; i++) {
				sheet.autoSizeColumn(i);
			}
		}

		// Set response headers
		response.setHeader("Content-Disposition",
				"attachment; filename=\"" + selectedTable.replace(" ", "_") + "_filtered.xlsx\"");
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

		workbook.write(response.getOutputStream());
		workbook.close();
	}
}
