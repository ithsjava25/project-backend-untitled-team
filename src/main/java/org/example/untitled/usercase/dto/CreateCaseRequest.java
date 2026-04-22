package org.example.untitled.usercase.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class CreateCaseRequest {

    @NotBlank private String title;

    @NotBlank private String description;
    private List<String> fileNames;

    public CreateCaseRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getFileNames() {
        return fileNames;
    }

    public void setFileNames(List<String> fileNames) {
        this.fileNames = fileNames;
    }
}
