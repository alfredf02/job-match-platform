package com.example.demo.job.repository;

import com.example.demo.job.domain.JobSkill;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobSkillRepository extends JpaRepository<JobSkill, Long> {

    List<JobSkill> findByJobId(Long jobId);
}