package org.zerock.FlexPaper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.FlexPaper.domain.FPUser;

import java.util.Optional;

public interface FPUserRepository extends JpaRepository<FPUser, Long> {
    Optional<FPUser> findByUsername(String username);
}

