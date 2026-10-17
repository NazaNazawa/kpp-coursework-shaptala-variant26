package ua.edu.nupp.kpp.server.model;

public class Enrollment {
    private Long id;
    private Long courseId;
    private Long listenerId;
    private String state; // REQUESTED -> APPROVED -> ACTIVE -> COMPLETED
    private double f1;
    private double f2;
    private double f3;
    private double integralScore;

    public Enrollment() {}

    public Enrollment(Long id, Long courseId, Long listenerId, String state, double f1, double f2, double f3, double integralScore) {
        this.id = id;
        this.courseId = courseId;
        this.listenerId = listenerId;
        this.state = state;
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = f3;
        this.integralScore = integralScore;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public Long getListenerId() { return listenerId; }
    public void setListenerId(Long listenerId) { this.listenerId = listenerId; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public double getF1() { return f1; }
    public void setF1(double f1) { this.f1 = f1; }

    public double getF2() { return f2; }
    public void setF2(double f2) { this.f2 = f2; }

    public double getF3() { return f3; }
    public void setF3(double f3) { this.f3 = f3; }

    public double getIntegralScore() { return integralScore; }
    public void setIntegralScore(double integralScore) { this.integralScore = integralScore; }
}