package org.example.untitled.usercase.dto;

public class CreateCommentRequest {

    private String text;
    private Long caseId;

    public CreateCommentRequest() {}

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Long getCaseId() { return caseId; }
    public void setCaseId(Long caseId) { this.caseId = caseId; }
}
