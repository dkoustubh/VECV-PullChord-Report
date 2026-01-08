package com.example.PullChord_Report.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import com.example.PullChord_Report.entity.Z5PullchordTEntity;

public interface Z5PullchordTRepository extends JpaRepository<Z5PullchordTEntity, Integer>{

	@Query(value = "SELECT * FROM Z5_Pullchord_T WHERE date_time BETWEEN ?1 AND ?2", 
            nativeQuery = true)
     Page<Z5PullchordTEntity> findFiltered(String fromDateTime, String toDateTime, Pageable pageable);
    
    
    @Query(value = "SELECT * FROM Z5_Pullchord_T WHERE date_time BETWEEN ?1 AND ?2 AND shift = ?3 ", 
            nativeQuery = true)
     Page<Z5PullchordTEntity> findFilteredWithShift(String fromDateTime, String toDateTime, String shiftName, Pageable pageable);
    
    
    
    @Query("SELECT s FROM Z5PullchordTEntity s WHERE s.dateTime BETWEEN :from AND :to")
    List<Z5PullchordTEntity> findFiltered(@Param("from") String from, @Param("to") String to);

    // With shift
    @Query("SELECT s FROM Z5PullchordTEntity s WHERE s.dateTime BETWEEN :from AND :to AND s.shift = :shift")
    List<Z5PullchordTEntity> findFilteredWithShift(@Param("from") String from, @Param("to") String to, @Param("shift") String shiftName);
    
    @Query(value = "SELECT * FROM Z5_Pullchord_T WHERE station = :station", 
    	       countQuery = "SELECT count(*) FROM Z5_Pullchord_T"
    	       		+ " WHERE station = :station", 
    	       nativeQuery = true)
    Page<Z5PullchordTEntity> findByStation(@Param("station") String station,Pageable pageable);
    
    @Query("SELECT s FROM Z5PullchordTEntity s WHERE s.station = :station" )
    List<Z5PullchordTEntity> findByStation(@Param("station") String station);

    @Query(value = "SELECT * FROM Z5_Pullchord_T WHERE station = :station AND shift = :shift", 
 	       countQuery = "SELECT count(*) FROM Z5_Pullchord_T WHERE station = :station AND shift = :shift", 
 	       nativeQuery = true)

	Page<Z5PullchordTEntity>  findByStationAndShift(@Param("station") String station,
            @Param("shift") String shiftName,
            Pageable pageable);
	

    @Query("SELECT e FROM Z5PullchordTEntity e WHERE " +
           "(:station IS NULL OR :station = '' OR e.station = :station) AND " +
           "(:shift IS NULL OR :shift = '' OR e.shift = :shift) AND " +
           "(:from IS NULL OR :from = '' OR e.dateTime >= :from) AND " +
           "(:to IS NULL OR :to = '' OR e.dateTime <= :to)")
    Page<Z5PullchordTEntity> searchReports(
           @Param("station") String station,
           @Param("shift") String shift,
           @Param("from") String from,
           @Param("to") String to,
           Pageable pageable);

}