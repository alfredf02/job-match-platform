package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.job.service.JobService;
import com.example.demo.job.domain.Employer;
import com.example.demo.job.domain.Job;
import com.example.demo.job.domain.Seniority;
import com.example.demo.job.domain.WorkType;
import com.example.demo.job.dto.job.CreateJobRequest;
import com.example.demo.job.dto.job.JobResponse;
import com.example.demo.job.dto.job.UpdateJobRequest;
import com.example.demo.job.repository.EmployerRepository;
import com.example.demo.job.repository.JobRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private EmployerRepository employerRepository;

    @InjectMocks
    private JobService jobService;

    @Test
    void createJob_persistsNormalizedSkills() {
        Employer employer = employer(7L, "Employer A");
        when(employerRepository.findById(7L)).thenReturn(Optional.of(employer));
        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> {
            Job job = invocation.getArgument(0);
            setJobId(job, 100L);
            job.onCreate();
            return job;
        });

        CreateJobRequest request = new CreateJobRequest();
        request.setEmployerId(7L);
        request.setTitle("Backend Engineer");
        request.setDescription("Build APIs");
        request.setLocation("Remote");
        request.setWorkType(WorkType.REMOTE);
        request.setSeniority(Seniority.MID);
        request.setSalaryMin(100000);
        request.setSalaryMax(150000);
        request.setSkills(List.of(" Java ", "SPRING", "java"));

        JobResponse response = jobService.createJob(request);

        assertEquals(100L, response.getId());
        assertEquals(List.of("java", "spring"), response.getSkills());
        verify(jobRepository).save(any(Job.class));
    }

    @Test
    void getJob_notFound_throws404() {
        when(jobRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> jobService.getJob(999L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void updateJob_updatesFieldsAndSkills() {
        Employer employer = employer(1L, "Emp");
        Job existing = new Job(employer, "Old title", "Old desc", "Old loc", WorkType.ONSITE, Seniority.JUNIOR, 50000, 70000);
        existing.addJobSkill(new com.example.demo.job.domain.JobSkill("java"));
        setJobId(existing, 50L);
        existing.onCreate();

        when(jobRepository.findById(50L)).thenReturn(Optional.of(existing));
        when(jobRepository.save(any(Job.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateJobRequest request = new UpdateJobRequest();
        request.setTitle("New title");
        request.setLocation("Remote");
        request.setWorkType(WorkType.HYBRID);
        request.setSeniority(Seniority.SENIOR);
        request.setSalaryMin(120000);
        request.setSalaryMax(180000);
        request.setSkills(List.of(" Kotlin ", "SPRING"));

        JobResponse response = jobService.updateJob(50L, request);

        assertEquals("New title", response.getTitle());
        assertEquals("Remote", response.getLocation());
        assertEquals("HYBRID", response.getWorkType());
        assertEquals("SENIOR", response.getSeniority());
        assertEquals(120000, response.getSalaryMin());
        assertEquals(180000, response.getSalaryMax());
        assertEquals(List.of("kotlin", "spring"), response.getSkills());
    }

    @Test
    void deleteJob_removesExistingJob() {
        Job existing = new Job(employer(2L, "Emp"), "Title", "Desc", "Loc", WorkType.REMOTE, Seniority.MID, null, null);
        when(jobRepository.findById(8L)).thenReturn(Optional.of(existing));

        jobService.deleteJob(8L);

        verify(jobRepository).delete(existing);
    }

    @Test
    void listJobsByEmployer_returnsMappedResults() {
        Employer employer = employer(3L, "Emp");
        Job job = new Job(employer, "Title", "Desc", "NYC", WorkType.ONSITE, Seniority.MID, 90000, 120000);
        setJobId(job, 11L);
        job.addJobSkill(new com.example.demo.job.domain.JobSkill("sql"));
        job.onCreate();

        when(employerRepository.existsById(3L)).thenReturn(true);
        when(jobRepository.findByEmployerId(3L)).thenReturn(List.of(job));

        List<JobResponse> results = jobService.listJobsByEmployer(3L);

        assertEquals(1, results.size());
        assertEquals(11L, results.get(0).getId());
        assertEquals("Title", results.get(0).getTitle());
    }

    @Test
    void searchJobs_appliesFiltersAndNormalizesSkill() {
        Employer employer = employer(4L, "Emp");
        Job job = new Job(employer, "Platform Engineer", "Desc", "Remote", WorkType.REMOTE, Seniority.SENIOR, 140000, 190000);
        setJobId(job, 12L);
        job.addJobSkill(new com.example.demo.job.domain.JobSkill("java"));
        job.onCreate();

        when(jobRepository.search(eq("Remote"), eq(WorkType.REMOTE), eq(Seniority.SENIOR), eq(130000), eq(200000), eq("java"),
                any(Pageable.class))).thenReturn(List.of(job));

        List<JobResponse> results = jobService.searchJobs(
                Optional.of("Remote"),
                Optional.of(WorkType.REMOTE),
                Optional.of(Seniority.SENIOR),
                Optional.of(130000),
                Optional.of(200000),
                Optional.of(" Java "),
                20
        );

        assertEquals(1, results.size());
        assertEquals("Platform Engineer", results.get(0).getTitle());
    }

    @Test
    void searchJobs_invalidLimit_throws400() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> jobService.searchJobs(Optional.empty(), Optional.empty(), Optional.empty(),
                        Optional.empty(), Optional.empty(), Optional.empty(), 0)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Limit"));
    }

    private static Employer employer(Long id, String name) {
        Employer employer = new Employer(name, null, null, null);
        employer.onCreate();
        setEmployerId(employer, id);
        return employer;
    }

    private static void setEmployerId(Employer employer, Long id) {
        try {
            java.lang.reflect.Field field = Employer.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(employer, id);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void setJobId(Job job, Long id) {
        try {
            java.lang.reflect.Field field = Job.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(job, id);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }
}