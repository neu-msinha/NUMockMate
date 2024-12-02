package task;

public enum TaskCategory {
    COMPANY_RESEARCH("Company Research"),
    ROLE_PREPARATION("Role Preparation"),
    TECHNICAL_PRACTICE("Technical Practice"),
    BEHAVIORAL_PRACTICE("Behavioral Practice"),
    DOCUMENTATION("Documentation");

    private final String displayName;

    TaskCategory(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}