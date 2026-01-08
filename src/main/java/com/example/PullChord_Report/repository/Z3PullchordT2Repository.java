package com.example.PullChord_Report.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.PullChord_Report.entity.Z3PullchordT2Entity;
;





public interface Z3PullchordT2Repository extends JpaRepository<Z3PullchordT2Entity, Integer> {
	
	
	
    
    @Query(value = "SELECT * FROM Z3_Pullchord_T2 WHERE date_time BETWEEN ?1 AND ?2", 
            nativeQuery = true)
     Page<Z3PullchordT2Entity> findFiltered(String fromDateTime, String toDateTime, Pageable pageable);
    
    
    @Query(value = "SELECT * FROM Z3_Pullchord_T2 WHERE date_time BETWEEN ?1 AND ?2 AND shift = ?3 ", 
            nativeQuery = true)
     Page<Z3PullchordT2Entity> findFilteredWithShift(String fromDateTime, String toDateTime, String shiftName, Pageable pageable);
    
    
    
    @Query("SELECT s FROM Z3PullchordT2Entity s WHERE s.dateTime BETWEEN :from AND :to")
    List<Z3PullchordT2Entity> findFiltered(@Param("from") String from, @Param("to") String to);

    // With shift
    @Query("SELECT s FROM Z3PullchordT2Entity s WHERE s.dateTime BETWEEN :from AND :to AND s.shift = :shift")
    List<Z3PullchordT2Entity> findFilteredWithShift(@Param("from") String from, @Param("to") String to, @Param("shift") String shiftName);
    
    @Query(value = "SELECT * FROM Z3_Pullchord_T2 WHERE station = :station", 
    	       countQuery = "SELECT count(*) FROM Z3_Pullchord_T2 WHERE station = :station", 
    	       nativeQuery = true)
    Page<Z3PullchordT2Entity> findByStation(@Param("station") String station,Pageable pageable);
    
    
    @Query(value = "SELECT * FROM Z3_Pullchord_T2 WHERE station = :station AND shift = :shift", 
    	       countQuery = "SELECT count(*) FROM Z3_Pullchord_T2 WHERE station = :station AND shift = :shift", 
    	       nativeQuery = true)
    	Page<Z3PullchordT2Entity> findByStationAndShift(@Param("station") String station,
    	                                                @Param("shift") String shiftName,
    	                                                Pageable pageable);

    
    @Query("SELECT s FROM Z3PullchordT2Entity s WHERE s.station = :station" )
    List<Z3PullchordT2Entity> findByStation(@Param("station") String station);

    
                        
    


    // Analytics Queries
    @Query(value = "SELECT TOP 5 station, COUNT(*) as count FROM Z3_Pullchord_T2 GROUP BY station ORDER BY count DESC", nativeQuery = true)
    List<Object[]> findTopStations();

    @Query(value = "SELECT line, COUNT(*) as count FROM Z3_Pullchord_T2 GROUP BY line", nativeQuery = true)
    List<Object[]> findLineCounts();
    
    @Query(value = "SELECT shift, COUNT(*) as count FROM Z3_Pullchord_T2 GROUP BY shift", nativeQuery = true)
    List<Object[]> findShiftCounts();

    @Query("SELECT e FROM Z3PullchordT2Entity e WHERE " +
           "(:station IS NULL OR :station = '' OR e.station = :station) AND " +
           "(:shift IS NULL OR :shift = '' OR e.shift = :shift) AND " +
           "(:from IS NULL OR :from = '' OR e.dateTime >= :from) AND " +
           "(:to IS NULL OR :to = '' OR e.dateTime <= :to)")
    Page<Z3PullchordT2Entity> searchReports(
           @Param("station") String station,
           @Param("shift") String shift,
           @Param("from") String from,
           @Param("to") String to,
           Pageable pageable);

}