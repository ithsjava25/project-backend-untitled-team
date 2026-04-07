package org.example.untitled.usercase.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateCommentRequest {

    @NotBlank private String text;

    @NotNull @Positive private Long caseId;

    public CreateCommentRequest() {}

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Long getCaseId() { return caseId; }
    public void setCaseId(Long caseId) { this.caseId = caseId; }
}
