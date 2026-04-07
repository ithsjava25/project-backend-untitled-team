package org.example.untitled.usercase.dto;

public class CreateCaseRequest {

    private String title;
    private String description;

    public CreateCaseRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
