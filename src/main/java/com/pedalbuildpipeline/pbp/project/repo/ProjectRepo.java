package com.pedalbuildpipeline.pbp.project.repo;

import com.pedalbuildpipeline.pbp.project.repo.entity.Project;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectRepo extends PagingAndSortingRepository<Project, UUID> {
}
