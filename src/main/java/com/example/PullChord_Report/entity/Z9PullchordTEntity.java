package com.example.PullChord_Report.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Z9_Pullchord_T")
public class Z9PullchordTEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SrNo")
	private int srNo;

	@Column(name = "Date_Time")
	private String dateTime;

	@Column(name = "Shift")
	private String shift;

	@Column(name = "Line")
	private String line;

	@Column(name = "Zone")
	private String zone;

	@Column(name = "Station")
	private String station;

	@Column(name = "Side")
	private String side;

	@Column(name = "Maintenance_Call")
	private String maintenanceCall;

	@Column(name = "Material_Call")
	private String materialCall;

	@Column(name = "Production_Call")
	private String productionCall;

	@Column(name = "Pull_Cord")
	private String pullCord;

	@Column(name = "Quality_Call")
	private String qualityCall;

	@Column(name = "Remark")
	private String remark;
}
