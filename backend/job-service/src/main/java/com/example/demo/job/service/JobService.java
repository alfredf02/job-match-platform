package com.example.demo.job.service;

import com.example.demo.job.domain.Employer;
import com.example.demo.job.domain.Job;
import com.example.demo.job.domain.JobSkill;
import com.example.demo.job.dto.job.CreateJobRequest;
import com.example.demo.job.dto.job.JobResponse;
import com.example.demo.job.repository.EmployerRepository;
import com.example.demo.job.repository.JobRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final EmployerRepository employerRepository;

    public JobService(JobRepository jobRepository, EmployerRepository employerRepository) {
        this.jobRepository = jobRepository;
        this.employerRepository = employerRepository;
    }

    @Transactional
    public JobResponse createJob(CreateJobRequest request) {
        Employer employer = employerRepository.findById(request.getEmployerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employer not found"));

        validateSalaryRange(request.getSalaryMin(), request.getSalaryMax());

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

        request.getSkills().forEach(skill -> job.addJobSkill(new JobSkill(skill)));

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

    private void validateSalaryRange(Integer salaryMin, Integer salaryMax) {
        if (salaryMin != null && salaryMax != null && salaryMin > salaryMax) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "salaryMin cannot be greater than salaryMax");
        }
    }
}