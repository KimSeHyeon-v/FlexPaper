package org.zerock.FlexPaper.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.FlexPaper.domain.Bundle;
import org.zerock.FlexPaper.dto.BundleDTO;

public interface BundleRepository extends JpaRepository<Bundle, Long> {

    // 사용자의 번들 중 title 또는 description에 검색어가 포함된 것만 페이징하여 조회
    @Query("SELECT b FROM Bundle b WHERE b.FPUser.username = :username " +
            "AND (LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Bundle> searchByUserAndKeyword(@Param("username") String username,
                                        @Param("keyword") String keyword,
                                        Pageable pageable);
}
