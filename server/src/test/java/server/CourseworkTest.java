package server;

import org.junit.jupiter.api.Test;
import ua.edu.nupp.kpp.server.model.EnrollmentState;
import ua.edu.nupp.kpp.server.service.EnrollmentService;

import static org.junit.jupiter.api.Assertions.*;

public class CourseworkTest {

    private final EnrollmentService service = new EnrollmentService();

    @Test
    void testIntegralScoreCalculation() {
        double score = service.calculateIntegralScore(80.0, 85.0, 90.0);
        // 0.40*80 + 0.35*85 + 0.25*90 = 32 + 29.75 + 22.5 = 84.25
        assertEquals(84.25, score, 0.001);
    }

    @Test
    void testIntegralScoreMinValues() {
        double score = service.calculateIntegralScore(0.0, 0.0, 0.0);
        assertEquals(0.0, score, 0.001);
    }

    @Test
    void testIntegralScoreMaxValues() {
        double score = service.calculateIntegralScore(100.0, 100.0, 100.0);
        assertEquals(100.0, score, 0.001);
    }

    @Test
    void testFactorValidationThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.calculateIntegralScore(105.0, 50.0, 50.0);
        });
    }

    @Test
    void testFactorValidationNegativeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.calculateIntegralScore(50.0, -5.0, 50.0);
        });
    }

    @Test
    void testTransitionRequestedToApproved() {
        assertTrue(service.isValidTransition(EnrollmentState.REQUESTED, EnrollmentState.APPROVED));
    }

    @Test
    void testTransitionApprovedToActive() {
        assertTrue(service.isValidTransition(EnrollmentState.APPROVED, EnrollmentState.ACTIVE));
    }

    @Test
    void testTransitionActiveToCompleted() {
        assertTrue(service.isValidTransition(EnrollmentState.ACTIVE, EnrollmentState.COMPLETED));
    }

    @Test
    void testForbiddenTransitionRequestedToCompleted() {
        assertFalse(service.isValidTransition(EnrollmentState.REQUESTED, EnrollmentState.COMPLETED));
    }

    @Test
    void testForbiddenTransitionCompletedToRequested() {
        assertFalse(service.isValidTransition(EnrollmentState.COMPLETED, EnrollmentState.REQUESTED));
    }
}