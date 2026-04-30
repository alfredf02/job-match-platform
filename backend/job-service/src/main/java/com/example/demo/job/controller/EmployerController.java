package com.example.demo.job.controller;
import com.example.demo.job.dto.employer.CreateEmployerRequest;
import com.example.demo.job.dto.employer.EmployerResponse;
import com.example.demo.job.dto.job.JobResponse;
import com.example.demo.job.service.EmployerService;
import com.example.demo.job.service.JobService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employers")
public class EmployerController {

    private final EmployerService employerService;
    private final JobService jobService;

    public EmployerController(EmployerService employerService, JobService jobService) {
        this.employerService = employerService;
        this.jobService = jobService;
    }

    @PostMapping
    public ResponseEntity<EmployerResponse> createEmployer(@Valid @RequestBody CreateEmployerRequest request) {
        EmployerResponse employer = employerService.createEmployer(request);
        URI location = URI.create("/api/employers/" + employer.getId());
        return ResponseEntity.created(location).body(employer);
    }

    @GetMapping("/{employerId}")
    public EmployerResponse getEmployer(@PathVariable long employerId) {
        return employerService.getEmployer(employerId);
    }

    @GetMapping("/{employerId}/jobs")
    public List<JobResponse> listEmployerJobs(@PathVariable long employerId) {
        return jobService.listJobsByEmployer(employerId);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationError(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        String message = fieldError != null ? fieldError.getDefaultMessage() : "Validation failed";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", message));
    }
}