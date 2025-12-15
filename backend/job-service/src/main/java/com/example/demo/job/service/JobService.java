package com.example.demo.job.service;

import com.example.demo.job.domain.Employer;
import com.example.demo.job.domain.Job;
import com.example.demo.job.repository.EmployerRepository;
import com.example.demo.job.repository.JobRepository;
import com.example.demo.job.dto.JobRequest;
import com.example.demo.job.dto.JobResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final EmployerRepository employerRepository;

    public JobService(JobRepository jobRepository, EmployerRepository employerRepository) {
        this.jobRepository = jobRepository;
        this.employerRepository = employerRepository;
    }

    @Transactional
    public JobResponse createJob(JobRequest request) {
        Employer employer = employerRepository.findById(request.getEmployerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employer not found"));

        Job job = new Job(
                employer,
                request.getTitle(),
                request.getDescription(),
                request.getLocation(),
                request.getWorkType(),
                request.getSeniority(),
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