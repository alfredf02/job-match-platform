package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.demo.job.service.EmployerService;
import com.example.demo.job.domain.Employer;
import com.example.demo.job.dto.employer.CreateEmployerRequest;
import com.example.demo.job.dto.employer.EmployerResponse;
import com.example.demo.job.repository.EmployerRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class EmployerServiceTest {

    @Mock
    private EmployerRepository employerRepository;

    @InjectMocks
    private EmployerService employerService;

    @Test
    void createEmployer_persistsAndReturnsResponse() {
        CreateEmployerRequest request = new CreateEmployerRequest();
        request.setName("Acme Corp");
        request.setWebsite("https://acme.test");
        request.setDescription("Great company");
        request.setLocation("Remote");

        Employer saved = new Employer("Acme Corp", "https://acme.test", "Great company", "Remote");
        saved.setName("Acme Corp");
        saved.onCreate();
        setEmployerId(saved, 1L);

        when(employerRepository.save(any(Employer.class))).thenReturn(saved);

        EmployerResponse response = employerService.createEmployer(request);

        assertEquals(1L, response.getId());
        assertEquals("Acme Corp", response.getName());
        assertEquals("https://acme.test", response.getWebsite());
        assertEquals("Great company", response.getDescription());
        assertEquals("Remote", response.getLocation());
        verify(employerRepository).save(any(Employer.class));
    }

    @Test
    void getEmployer_notFound_throws404() {
        when(employerRepository.findById(42L)).thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> employerService.getEmployer(42L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
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
}