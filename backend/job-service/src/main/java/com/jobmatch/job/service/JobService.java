package com.jobmatch.job.service;

import com.jobmatch.job.domain.Job;
import com.jobmatch.job.dto.JobRequest;
import com.jobmatch.job.dto.JobResponse;
import com.jobmatch.job.repository.JobRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Transactional
    public JobResponse createJob(JobRequest request) {
        Job job = new Job(
                request.getTitle(),
                request.getDescription(),
                request.getLocation(),
                request.getEmploymentType(),
                request.getSalaryMin(),
                request.getSalaryMax()
        );

        Job savedJob = jobRepository.save(job);
        return new JobResponse(savedJob);
    }

    @Transactional(readOnly = true)
    public List<JobResponse> listAll() {
        return jobRepository.findAll()
                .stream()
                .map(JobResponse::new)
                .toList();
    }
}