package com.pedalbuildpipeline.pbp.project.service;

import com.pedalbuildpipeline.pbp.persistence.LobHelper;
import com.pedalbuildpipeline.pbp.project.exception.MediaStorageException;
import com.pedalbuildpipeline.pbp.project.repo.ProjectRepo;
import com.pedalbuildpipeline.pbp.project.repo.entity.Project;
import com.pedalbuildpipeline.pbp.project.repo.entity.ProjectMedia;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.sql.Blob;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectService {
  private final ProjectRepo projectRepo;
  private final LobHelper lobHelper;

  public Project createProject(Project project, MultipartFile[] media) {
    for (MultipartFile mediaEntry : media) {
      Blob blob;
      try {
        blob = lobHelper.createBlob(mediaEntry.getInputStream(), mediaEntry.getSize());
      } catch (IOException e) {
        throw new MediaStorageException(mediaEntry.getOriginalFilename());
      }

      ProjectMedia projectMedia =
          ProjectMedia.builder()
              .project(project)
              .name(FilenameUtils.removeExtension(mediaEntry.getOriginalFilename()))
              .contentType(mediaEntry.getContentType())
              .size(mediaEntry.getSize())
              .data(blob)
              .build();
    }
    return projectRepo.save(project);
  }

  public Optional<Project> findProject(UUID id) {
    return projectRepo.findById(id);
  }
}
