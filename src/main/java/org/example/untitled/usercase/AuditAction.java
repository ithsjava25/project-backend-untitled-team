package org.example.untitled.usercase;

public enum AuditAction {
    CASE_CREATED,
    CASE_ASSIGNED,
    CASE_STATUS_CHANGED,
    COMMENT_ADDED,
    FILE_UPLOADED,
    USER_ROLE_CHANGED,
    CASE_UPDATED;

    public String getDisplayName() {
        String name = this.name().toLowerCase().replace('_', ' ');
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
