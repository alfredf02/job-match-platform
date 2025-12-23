package com.example.demo.job.service;

import com.example.demo.job.domain.Employer;
import com.example.demo.job.domain.Job;
import com.example.demo.job.domain.JobSkill;
import com.example.demo.job.domain.Seniority;
import com.example.demo.job.domain.WorkType;
import com.example.demo.job.dto.job.CreateJobRequest;
import com.example.demo.job.dto.job.JobResponse;
import com.example.demo.job.dto.job.UpdateJobRequest;
import com.example.demo.job.repository.EmployerRepository;
import com.example.demo.job.repository.JobRepository;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

        List<String> normalizedSkills = normalizeSkills(request.getSkills(), true);

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

        normalizedSkills.forEach(skill -> job.addJobSkill(new JobSkill(skill)));

        Job savedJob = jobRepository.save(job);
        return new JobResponse(savedJob);
    }

    @Transactional(readOnly = true)
    public JobResponse getJob(long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
        return new JobResponse(job);
    }

    @Transactional(readOnly = true)
    public List<JobResponse> listAll() {
        return jobRepository.findAll().stream()
                .map(JobResponse::new)
                .toList();
    }

    @Transactional
    public JobResponse updateJob(long jobId, UpdateJobRequest request) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));

        if (request.getTitle() != null) {
            job.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            job.setDescription(request.getDescription());
        }
        if (request.getLocation() != null) {
            job.setLocation(request.getLocation());
        }
        if (request.getWorkType() != null) {
            job.setWorkType(request.getWorkType());
        }
        if (request.getSeniority() != null) {
            job.setSeniority(request.getSeniority());
        }

        Integer updatedSalaryMin = request.getSalaryMin() != null ? request.getSalaryMin() : job.getSalaryMin();
        Integer updatedSalaryMax = request.getSalaryMax() != null ? request.getSalaryMax() : job.getSalaryMax();
        validateSalaryRange(updatedSalaryMin, updatedSalaryMax);
        job.setSalaryMin(request.getSalaryMin() != null ? request.getSalaryMin() : job.getSalaryMin());
        job.setSalaryMax(request.getSalaryMax() != null ? request.getSalaryMax() : job.getSalaryMax());

        if (request.getSkills() != null) {
            List<String> normalizedSkills = normalizeSkills(request.getSkills(), false);
            job.getJobSkills().clear();
            normalizedSkills.forEach(skill -> job.addJobSkill(new JobSkill(skill)));
        }

        Job savedJob = jobRepository.save(job);
        return new JobResponse(savedJob);
    }

    @Transactional
    public void deleteJob(long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));
        jobRepository.delete(job);
    }

    @Transactional(readOnly = true)
    public List<JobResponse> listJobsByEmployer(long employerId) {
        if (!employerRepository.existsById(employerId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employer not found");
        }

        return jobRepository.findByEmployerId(employerId).stream()
                .map(JobResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<JobResponse> searchJobs(Optional<String> location,
                                        Optional<WorkType> workType,
                                        Optional<Seniority> seniority,
                                        Optional<Integer> salaryMin,
                                        Optional<Integer> salaryMax,
                                        Optional<String> skill,
                                        int limit) {
        if (limit <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Limit must be greater than zero");
        }

        Pageable pageable = PageRequest.of(0, limit);
        String normalizedSkill = skill.map(this::normalizeSkillValue).orElse(null);

        return jobRepository.search(
                        location.map(String::trim).filter(s -> !s.isEmpty()).orElse(null),
                        workType.orElse(null),
                        seniority.orElse(null),
                        salaryMin.orElse(null),
                        salaryMax.orElse(null),
                        normalizedSkill,
                        pageable)
                .stream()
                .map(JobResponse::new)
                .toList();
    }

    private void validateSalaryRange(Integer salaryMin, Integer salaryMax) {
        if (salaryMin != null && salaryMax != null && salaryMin > salaryMax) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "salaryMin cannot be greater than salaryMax");
        }
    }

    private List<String> normalizeSkills(List<String> skills, boolean required) {
        if (skills == null) {
            return List.of();
        }

        Set<String> normalized = new LinkedHashSet<>();
        for (String skill : skills) {
            String value = normalizeSkillValue(skill);
            if (value != null) {
                normalized.add(value);
            }
        }

        if (required && normalized.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one skill is required");
        }

        return new ArrayList<>(normalized);
    }

    private String normalizeSkillValue(String skill) {
        if (skill == null) {
            return null;
        }
        String value = skill.trim().toLowerCase();
        return value.isEmpty() ? null : value;
    }
}