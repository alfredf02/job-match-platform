package com.example.demo.job.service;

import com.example.demo.job.domain.Employer;
import com.example.demo.job.dto.employer.CreateEmployerRequest;
import com.example.demo.job.dto.employer.EmployerResponse;
import com.example.demo.job.repository.EmployerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EmployerService {

    private final EmployerRepository employerRepository;

    public EmployerService(EmployerRepository employerRepository) {
        this.employerRepository = employerRepository;
    }

    @Transactional
    public EmployerResponse createEmployer(CreateEmployerRequest request) {
        Employer employer = new Employer(
                request.getName(),
                request.getWebsite(),
                request.getDescription(),
                request.getLocation()
        );

        Employer savedEmployer = employerRepository.save(employer);
        return new EmployerResponse(savedEmployer);
    }

    @Transactional(readOnly = true)
    public EmployerResponse getEmployer(long employerId) {
        Employer employer = employerRepository.findById(employerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employer not found"));
        return new EmployerResponse(employer);
    }
}