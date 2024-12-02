package application;

import java.time.LocalDate;

public class PrepTask {
    private String company;
    private String name;
    private String description;
    private LocalDate deadline;
    private TaskCategory category;
    private boolean completed;

    public PrepTask(String company, String name, String description, LocalDate deadline, TaskCategory category, boolean completed) {
        this.company = company;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.category = category;
        this.completed = completed;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public TaskCategory getCategory() {
        return category;
    }

    public void setCategory(TaskCategory category) {
        this.category = category;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
