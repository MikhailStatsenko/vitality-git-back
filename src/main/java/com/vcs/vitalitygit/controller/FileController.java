package com.vcs.vitalitygit.controller;

import com.vcs.vitalitygit.domain.dto.RepositoryDetails;
import com.vcs.vitalitygit.domain.dto.file.DeleteFileResponse;
import com.vcs.vitalitygit.domain.dto.file.UploadFilesResponse;
import com.vcs.vitalitygit.domain.dto.repository.RepositoryContentsResponse;
import com.vcs.vitalitygit.service.FileService;
import com.vcs.vitalitygit.util.PathExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/file")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getRepositoryContents(
            @PathVariable String userId
    ) throws IOException {
        Map<String, String> dirContents = fileService.getDirectoryContents(userId);
        var response = new RepositoryContentsResponse(dirContents.keySet());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/{repositoryName}/**")
    public ResponseEntity<?> getFileOrDirectoryContents(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            HttpServletRequest request
    ) throws IOException {
        String path = PathExtractor.extractPathFromRequest(request);
        var repositoryContext = new RepositoryDetails(userId, repositoryName);
        return ResponseEntity.ok(fileService.getFileOrDirectoryContents(repositoryContext, path));
    }

    @PostMapping("/upload/{userId}/{repositoryName}/**")
    public ResponseEntity<UploadFilesResponse> uploadFilesToWorkingDirectory(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            @RequestParam(value = "files", required = false) MultipartFile[] files,
            HttpServletRequest httpServletRequest
    ) throws IOException {
        String requestPath = PathExtractor.extractPathFromRequest(httpServletRequest);
        var repositoryContext = new RepositoryDetails(userId, repositoryName);
        UploadFilesResponse response = fileService.uploadFiles(repositoryContext, requestPath, files);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add-directory/{userId}/{repositoryName}/**")
    public ResponseEntity<String> createNewDirectory(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            @RequestParam String name,
            HttpServletRequest httpServletRequest
    ) throws IOException {
        String requestPath = PathExtractor.extractPathFromRequest(httpServletRequest);
        var repositoryContext = new RepositoryDetails(userId, repositoryName);
        fileService.createNewDirectory(repositoryContext, requestPath, name);
        return ResponseEntity.ok("Directory created successfully");
    }


    @GetMapping("/download/{userId}/{repositoryName}")
    public void downloadRepositoryArchive(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            HttpServletResponse response
    ) throws IOException {
        var repositoryContext = new RepositoryDetails(userId, repositoryName);
        fileService.getRepositoryArchive(repositoryContext, response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @DeleteMapping("/delete/{userId}/{repositoryName}/**")
    public ResponseEntity<DeleteFileResponse> deleteFile(
            @PathVariable String userId,
            @PathVariable String repositoryName,
            HttpServletRequest request
    ) throws IOException {
        String path = PathExtractor.extractPathFromRequest(request);
        var repositoryContext = new RepositoryDetails(userId, repositoryName);
        List<String> deletedFiles = fileService.deleteFile(repositoryContext, path);
        return ResponseEntity.ok(new DeleteFileResponse(deletedFiles));
    }
}
