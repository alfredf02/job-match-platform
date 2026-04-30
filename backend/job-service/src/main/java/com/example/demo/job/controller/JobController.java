package com.example.demo.job.controller;
import com.example.demo.job.dto.job.CreateJobRequest;
import com.example.demo.job.dto.job.JobResponse;
import com.example.demo.job.service.JobService;
import com.example.demo.job.domain.Seniority;
import com.example.demo.job.domain.WorkType;
import com.example.demo.job.dto.job.CreateJobRequest;
import com.example.demo.job.dto.job.JobResponse;
import com.example.demo.job.dto.job.UpdateJobRequest;
import com.example.demo.job.service.JobService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 100;

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    public ResponseEntity<JobResponse> createJob(@Valid @RequestBody CreateJobRequest request) {
        JobResponse createdJob = jobService.createJob(request);
        URI location = URI.create("/api/jobs/" + createdJob.getId());
        return ResponseEntity.created(location).body(createdJob);
    }

    @GetMapping("/{jobId}")
    public JobResponse getJob(@PathVariable long jobId) {
        return jobService.getJob(jobId);
    }

    @PutMapping("/{jobId}")
    public JobResponse updateJob(@PathVariable long jobId, @Valid @RequestBody UpdateJobRequest request) {
        return jobService.updateJob(jobId, request);
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<Void> deleteJob(@PathVariable long jobId) {
        jobService.deleteJob(jobId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<JobResponse> searchJobs(@RequestParam(required = false) String location,
                                        @RequestParam(required = false) WorkType workType,
                                        @RequestParam(required = false) Seniority seniority,
                                        @RequestParam(required = false) Integer salaryMin,
                                        @RequestParam(required = false) Integer salaryMax,
                                        @RequestParam(required = false) String skill,
                                        @RequestParam(defaultValue = "" + DEFAULT_LIMIT) int limit) {

        int effectiveLimit = validateAndNormalizeLimit(limit);

        return jobService.searchJobs(
                Optional.ofNullable(location),
                Optional.ofNullable(workType),
                Optional.ofNullable(seniority),
                Optional.ofNullable(salaryMin),
                Optional.ofNullable(salaryMax),
                Optional.ofNullable(skill),
                effectiveLimit
        );
    }

    private int validateAndNormalizeLimit(int limit) {
        if (limit <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "limit must be greater than zero");
        }
        if (limit > MAX_LIMIT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "limit cannot exceed " + MAX_LIMIT);
        }
        return limit;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationError(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        String message = fieldError != null ? fieldError.getDefaultMessage() : "Validation failed";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", message));
    }
}