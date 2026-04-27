package org.example.untitled.usercase;

public enum CaseStatus {
    OPEN,
    IN_PROGRESS,
    CLOSED,
    SOLVED;

    public String getDisplayName() {
        String name = this.name().toLowerCase().replace('_', ' ');
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
}
