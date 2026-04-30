package com.example.demo.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.job.controller.EmployerController;
import com.example.demo.job.domain.Employer;
import com.example.demo.job.domain.Job;
import com.example.demo.job.domain.Seniority;
import com.example.demo.job.domain.WorkType;
import com.example.demo.job.dto.employer.CreateEmployerRequest;
import com.example.demo.job.dto.employer.EmployerResponse;
import com.example.demo.job.dto.job.JobResponse;
import com.example.demo.job.service.EmployerService;
import com.example.demo.job.service.JobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class EmployerControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private EmployerService employerService;

    @Mock
    private JobService jobService;

    @InjectMocks
    private EmployerController employerController;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(employerController)
                .build();
    }

    @Test
    void createEmployer_validation_returns400() throws Exception {
        CreateEmployerRequest request = new CreateEmployerRequest();
        request.setName(" ");

        mockMvc.perform(post("/api/employers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getEmployer_returns200() throws Exception {
        Employer employer = new Employer("Acme", "https://acme.test", "desc", "Remote");
        employer.onCreate();
        setField(employer, "id", 1L);

        EmployerResponse response = new EmployerResponse(employer);
        when(employerService.getEmployer(1L)).thenReturn(response);

        mockMvc.perform(get("/api/employers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Acme"));
    }

    @Test
    void getEmployer_notFound_returns404() throws Exception {
        when(employerService.getEmployer(99L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Employer not found"));

        mockMvc.perform(get("/api/employers/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listEmployerJobs_returns200() throws Exception {
        Employer employer = new Employer("Acme", null, null, null);
        employer.onCreate();
        setField(employer, "id", 1L);

        Job job = new Job(
                employer,
                "Backend Engineer",
                "desc",
                "Remote",
                WorkType.REMOTE,
                Seniority.MID,
                null,
                null
        );
        job.onCreate();
        setField(job, "id", 10L);

        JobResponse response = new JobResponse(job);

        when(jobService.listJobsByEmployer(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/employers/1/jobs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10L))
                .andExpect(jsonPath("$[0].title").value("Backend Engineer"));
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }
}