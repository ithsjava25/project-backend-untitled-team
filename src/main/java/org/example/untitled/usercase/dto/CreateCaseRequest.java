package org.example.untitled.usercase.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateCaseRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    public CreateCaseRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
