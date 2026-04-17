package org.example.untitled.usercase.controller;

import java.util.List;

import org.example.untitled.s3.S3Service;
import org.example.untitled.usercase.CaseStatus;
import org.example.untitled.usercase.dto.CaseEntityDto;
import org.example.untitled.usercase.dto.CreateCaseRequest;
import org.example.untitled.usercase.service.CaseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/tickets")
public class CaseController {

    private final CaseService caseService;


    public CaseController(CaseService caseService, S3Service s3Service) {
        this.caseService = caseService;
    }

    @PostMapping
    public ResponseEntity<CaseEntityDto> createTicket(
            @Valid @RequestBody CreateCaseRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(caseService.createTicket(request, userDetails.getUsername(), request.getFileName()));
    }

    @GetMapping("/my")
    public ResponseEntity<List<CaseEntityDto>> getMyTickets(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(caseService.getMyTickets(userDetails.getUsername()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CaseEntityDto> updateTicket(
            @PathVariable Long id,
            @Valid @RequestBody CreateCaseRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(caseService.updateTicket(id, request, userDetails.getUsername()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('HANDLER', 'ADMIN')")
    public ResponseEntity<List<CaseEntityDto>> getAllTickets() {
        return ResponseEntity.ok(caseService.getAllTickets());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('HANDLER', 'ADMIN')")
    public ResponseEntity<CaseEntityDto> updateStatus(
            @PathVariable Long id, @RequestParam CaseStatus status) {
        return ResponseEntity.ok(caseService.updateStatus(id, status));
    }

    @PutMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('HANDLER', 'ADMIN')")
    public ResponseEntity<CaseEntityDto> assignToSelf(
            @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(caseService.assignTicket(id, userDetails.getUsername()));
    }
}
