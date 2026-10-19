package ua.edu.nupp.kpp.server.service;

import ua.edu.nupp.kpp.server.model.Enrollment;
import ua.edu.nupp.kpp.server.model.EnrollmentState;

import java.util.logging.Logger;

public class EnrollmentService {
    private static final Logger LOGGER = Logger.getLogger(EnrollmentService.class.getName());

    public double calculateIntegralScore(double f1, double f2, double f3) {
        validateFactors(f1, f2, f3);
        return 0.40 * f1 + 0.35 * f2 + 0.25 * f3;
    }

    public void validateFactors(double f1, double f2, double f3) {
        if (f1 < 0 || f1 > 100 || f2 < 0 || f2 > 100 || f3 < 0 || f3 > 100) {
            LOGGER.warning("Помилка валідації: фактори мають бути в діапазоні 0-100.");
            throw new IllegalArgumentException("Фактори F1, F2, F3 повинні знаходитися в межах від 0 до 100.");
        }
    }

    public boolean isValidTransition(EnrollmentState current, EnrollmentState target) {
        if (current == null || target == null) return false;
        switch (current) {
            case REQUESTED:
                return target == EnrollmentState.APPROVED;
            case APPROVED:
                return target == EnrollmentState.ACTIVE;
            case ACTIVE:
                return target == EnrollmentState.COMPLETED;
            case COMPLETED:
                return false; // З завершеного стану переходи заборонені
            default:
                return false;
        }
    }

    public void changeState(Enrollment enrollment, EnrollmentState targetState) {
        EnrollmentState currentState;
        try {
            currentState = EnrollmentState.valueOf(enrollment.getState());
        } catch (Exception e) {
            throw new IllegalArgumentException("Невідомий поточний стан зарахування: " + enrollment.getState());
        }

        if (!isValidTransition(currentState, targetState)) {
            LOGGER.warning("Заборонений перехід стану з " + currentState + " у " + targetState);
            throw new IllegalArgumentException("Недопустимий перехід життєвого циклу з " + currentState + " у " + targetState);
        }

        enrollment.setState(targetState.name());
        LOGGER.info("Успішно змінено стан зарахування ID " + enrollment.getId() + " на " + targetState);
    }
}