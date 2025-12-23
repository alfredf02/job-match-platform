package com.example.demo.job.repository;

import com.example.demo.job.domain.Job;
import com.example.demo.job.domain.Seniority;
import com.example.demo.job.domain.WorkType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByEmployerId(Long employerId);

    @Query("SELECT DISTINCT j FROM Job j LEFT JOIN j.jobSkills js WHERE (:location IS NULL OR j.location = :location) " +
            "AND (:workType IS NULL OR j.workType = :workType) " +
            "AND (:seniority IS NULL OR j.seniority = :seniority) " +
            "AND (:salaryMin IS NULL OR (j.salaryMin IS NOT NULL AND j.salaryMin >= :salaryMin)) " +
            "AND (:salaryMax IS NULL OR (j.salaryMax IS NOT NULL AND j.salaryMax <= :salaryMax)) " +
            "AND (:skill IS NULL OR LOWER(js.skill) = :skill)")
    List<Job> search(@Param("location") String location,
                     @Param("workType") WorkType workType,
                     @Param("seniority") Seniority seniority,
                     @Param("salaryMin") Integer salaryMin,
                     @Param("salaryMax") Integer salaryMax,
                     @Param("skill") String skill,
                     Pageable pageable);
}