package com.pedalbuildpipeline.pbp.user.repo;

import com.pedalbuildpipeline.pbp.user.repo.entity.User;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, UUID> {
  Optional<User> findByUsernameEquals(String username);

  Page<User> findByUsernameContaining(String username, Pageable pageable);
}
