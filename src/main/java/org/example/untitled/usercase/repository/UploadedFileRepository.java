package org.example.untitled.usercase.repository;

import org.example.untitled.user.User;
import org.example.untitled.usercase.CaseEntity;
import org.example.untitled.usercase.UploadedFile;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UploadedFileRepository extends ListCrudRepository<UploadedFile, Long> {

    List<UploadedFile> associatedCaseEntity(CaseEntity caseEntity);
    UploadedFile getUploadedFilesByFilename(String filename);

    List<UploadedFile> getUploadedFilesByUploadedBy(User uploadedBy);
}
