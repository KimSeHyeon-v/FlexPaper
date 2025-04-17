package org.zerock.FlexPaper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.FlexPaper.domain.Bundle;
import org.zerock.FlexPaper.domain.Canvas;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CanvasRepository extends JpaRepository<Canvas, Long> {
    List<Canvas> findByBundleId(Long bundleId);
    Optional<Canvas> findByCanvasIDAndBundle(String canvasID, Bundle bundle);
    // 특정 번들에 속한 Canvas 중 요청된 canvasID가 아닌 것 삭제
    void deleteByBundleAndCanvasIDNotIn(Bundle bundle, Set<String> canvasIDs);
    // 특정 번들에 속한 모든 Canvas 삭제 (요청된 canvasID가 없을 때)
    void deleteByBundle(Bundle bundle);
}
