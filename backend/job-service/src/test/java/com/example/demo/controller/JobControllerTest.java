package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.demo.job.controller.JobController;
import com.example.demo.job.domain.Employer;
import com.example.demo.job.domain.Job;
import com.example.demo.job.domain.Seniority;
import com.example.demo.job.domain.WorkType;
import com.example.demo.job.dto.job.CreateJobRequest;
import com.example.demo.job.dto.job.JobResponse;
import com.example.demo.job.dto.job.UpdateJobRequest;
import com.example.demo.job.service.JobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
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
class JobControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private JobService jobService;

    @InjectMocks
    private JobController jobController;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(jobController)
                .build();
    }

    @Test
    void createJob_validationError_returns400() throws Exception {
        CreateJobRequest request = new CreateJobRequest();
        request.setEmployerId(1L);

        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getJob_returns200() throws Exception {
        JobResponse response = buildJobResponse(5L, "Platform Engineer", WorkType.REMOTE, Seniority.SENIOR);
        when(jobService.getJob(5L)).thenReturn(response);

        mockMvc.perform(get("/api/jobs/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.title").value("Platform Engineer"));
    }

    @Test
    void getJob_notFound_returns404() throws Exception {
        when(jobService.getJob(404L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found"));

        mockMvc.perform(get("/api/jobs/404"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateJob_returns200() throws Exception {
        UpdateJobRequest request = new UpdateJobRequest();
        request.setTitle("Updated Title");
        request.setSkills(List.of("java", "spring"));

        JobResponse response = buildJobResponse(8L, "Updated Title", WorkType.HYBRID, Seniority.MID);
        when(jobService.updateJob(eq(8L), any(UpdateJobRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/jobs/8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(8L))
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void searchJobs_returnsList() throws Exception {
        JobResponse response = buildJobResponse(11L, "Backend Engineer", WorkType.REMOTE, Seniority.MID);

        when(jobService.searchJobs(
                eq(Optional.of("Remote")),
                eq(Optional.of(WorkType.REMOTE)),
                eq(Optional.of(Seniority.MID)),
                eq(Optional.of(100000)),
                eq(Optional.of(150000)),
                eq(Optional.of("java")),
                eq(20)
        )).thenReturn(List.of(response));

        mockMvc.perform(get("/api/jobs")
                        .param("location", "Remote")
                        .param("workType", "REMOTE")
                        .param("seniority", "MID")
                        .param("salaryMin", "100000")
                        .param("salaryMax", "150000")
                        .param("skill", "java")
                        .param("limit", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(11L))
                .andExpect(jsonPath("$[0].title").value("Backend Engineer"));
    }

    private static JobResponse buildJobResponse(Long id, String title, WorkType workType, Seniority seniority) {
        Employer employer = new Employer("Acme", null, null, "Remote");
        employer.onCreate();
        setField(employer, "id", 1L);

        Job job = new Job(employer, title, "desc", "Remote", workType, seniority, 100000, 150000);
        job.onCreate();
        setField(job, "id", id);

        return new JobResponse(job);
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