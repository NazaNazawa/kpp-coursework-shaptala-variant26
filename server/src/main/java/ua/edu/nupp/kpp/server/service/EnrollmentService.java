package ua.edu.nupp.kpp.server.service;

import ua.edu.nupp.kpp.server.model.Enrollment;

public class EnrollmentService {

    public double calculateIntegralScore(double f1, double f2, double f3) {
        return 0.40 * f1 + 0.35 * f2 + 0.25 * f3;
    }

    public String getNextState(String currentState) {
        switch (currentState) {
            case "REQUESTED":
                return "APPROVED";
            case "APPROVED":
                return "ACTIVE";
            case "ACTIVE":
                return "COMPLETED";
            default:
                return currentState;
        }
    }
}