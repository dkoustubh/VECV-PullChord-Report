package com.example.PullChord_Report.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.PullChord_Report.entity.Z5PullchordTEntity;
import com.example.PullChord_Report.entity.Z9PullchordTEntity;

public interface Z9PullchordTRepository extends JpaRepository<Z9PullchordTEntity, Integer> {

	

	@Query("SELECT s FROM Z9PullchordTEntity s WHERE s.dateTime BETWEEN :from AND :to")
    List<Z9PullchordTEntity> findFiltered(@Param("from") String from, @Param("to") String to);

    // With shift
    @Query("SELECT s FROM Z9PullchordTEntity s WHERE s.dateTime BETWEEN :from AND :to AND s.shift = :shift AND s.station = :station")
    List<Z9PullchordTEntity> findFilteredWithShiftAndStation(
        @Param("from") String from,
        @Param("to") String to,
        @Param("shift") String shift,
        @Param("station") String station
    );

//    @Query("SELECT t FROM Z9PullchordTEntity t WHERE t.dateTime BETWEEN :fromDateTime AND :toDateTime")
//    Page<Z9PullchordTEntity> findFiltered(@Param("fromDateTime") String fromDateTime,
//                                          @Param("toDateTime") String toDateTime,
//                                          Pageable pageable);
    
    @Query("""
    	      SELECT t FROM Z9PullchordTEntity t
    	       WHERE t.dateTime BETWEEN :from AND :to
    	         AND (:shift   IS NULL OR t.shift   = :shift)
    	         AND (:station IS NULL OR t.station = :station)
    	      """)
    	    Page<Z9PullchordTEntity> findFiltered(
    	        @Param("from")    String from,
    	        @Param("to")      String to,
    	        @Param("shift")   String shift,
    	        @Param("station") String station,
    	        Pageable pageable
    	    );

    @Query("SELECT t FROM Z9PullchordTEntity t WHERE t.dateTime BETWEEN :fromDateTime AND :toDateTime AND t.shift = :shift AND t.station = :station")
    Page<Z9PullchordTEntity> findByFromDateTimeBetweenAndShiftAndStation(
    	    String from, String to, String shift, String station, Pageable pageable);

    @Query("SELECT e FROM Z9PullchordTEntity e WHERE " +
           "(:station IS NULL OR :station = '' OR e.station = :station) AND " +
           "(:shift IS NULL OR :shift = '' OR e.shift = :shift) AND " +
           "(:from IS NULL OR :from = '' OR e.dateTime >= :from) AND " +
           "(:to IS NULL OR :to = '' OR e.dateTime <= :to)")
    Page<Z9PullchordTEntity> searchReports(
           @Param("station") String station,
           @Param("shift") String shift,
           @Param("from") String from,
           @Param("to") String to,
           Pageable pageable);

}