package ua.edu.nupp.kpp.client.Model;

public class StudyTask {
    private Long id;
    private Long enrollmentId;
    private String title;
    private int maxScore;
    private String dueDate;

    public StudyTask() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(Long enrollmentId) { this.enrollmentId = enrollmentId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public int getMaxScore() { return maxScore; }
    public void setMaxScore(int maxScore) { this.maxScore = maxScore; }
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
}