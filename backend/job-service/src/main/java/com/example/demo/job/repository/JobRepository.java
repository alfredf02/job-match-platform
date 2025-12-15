package com.example.demo.job.repository;

import com.example.demo.job.domain.Job;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByEmployerId(Long employerId);

    @Query("SELECT j FROM Job j WHERE (:location IS NULL OR j.location = :location) " +
            "AND (:workType IS NULL OR j.workType = :workType) " +
            "AND (:seniority IS NULL OR j.seniority = :seniority)")
    List<Job> search(@Param("location") String location,
                     @Param("workType") String workType,
                     @Param("seniority") String seniority);
}