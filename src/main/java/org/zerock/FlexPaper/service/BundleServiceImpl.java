package org.zerock.FlexPaper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.FlexPaper.domain.Bundle;
import org.zerock.FlexPaper.domain.FPUser;
import org.zerock.FlexPaper.dto.BundleDTO;
import org.zerock.FlexPaper.dto.CanvasDTO;
import org.zerock.FlexPaper.repository.BundleRepository;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BundleServiceImpl implements BundleService {

    private final BundleRepository bundleRepository;
    @Value("${thumbnail.dir}")
    private String thumbnailDir;

    // Bundle 생성 로직
    public void createBundle(BundleDTO bundleDTO, FPUser user) {
        Bundle newBundle = Bundle.builder()
                .title(bundleDTO.getTitle())
                .description(bundleDTO.getDescription())
                .FPUser(user) // 사용자를 연결
                .build();

        bundleRepository.save(newBundle); // DB에 저장
    }

    public BundleDTO getBundleWithCanvas(Long bno) {
        Bundle bundle = bundleRepository.findById(bno)
                .orElseThrow(() -> new IllegalArgumentException("Bundle not found"));

        // Canvas 리스트를 CanvasDTO 리스트로 변환
        List<CanvasDTO.CanvasData> canvasDataList = bundle.getCanvases().stream()
        .map(canvas -> {
            CanvasDTO.CanvasData canvasData = new CanvasDTO.CanvasData();
            canvasData.setId(canvas.getCanvasID()); // Canvas ID 설정
            canvasData.setCanvasData(canvas.getCanvasData()); // JSON 데이터 설정
            return canvasData;
            }
        ).collect(Collectors.toList());

        // CanvasDTO 생성
        CanvasDTO canvasDTO = new CanvasDTO();
        canvasDTO.setBundleId(bundle.getId()); // 번들 ID 설정
        canvasDTO.setCanvases(canvasDataList);  // 변환된 Canvas 리스트 설정

        return BundleDTO.builder()
                .bno(bundle.getId())
                .title(bundle.getTitle())
                .description(bundle.getDescription())
                .fpUser(bundle.getFPUser())
                .canvasDTO(canvasDTO)
                .build();
    }

    @Transactional //@Transactional을 사용하여 영속성 컨텍스트가 자동으로 변경 사항을 감지하고 저장
    public void modifyBundle(BundleDTO bundleDTO) {
        Bundle bundle = bundleRepository.findById(bundleDTO.getBno())
                .orElseThrow(() -> new IllegalArgumentException("Bundle not found"));

        bundle.setTitle(bundleDTO.getTitle());
        bundle.setDescription(bundleDTO.getDescription());

        bundleRepository.save(bundle); // 변경된 내용 저장
    }

    @Transactional
    public void removeBundle(Long bno) {
        Bundle bundle = bundleRepository.findById(bno)
                .orElseThrow(() -> new IllegalArgumentException("Bundle not found"));

        bundleRepository.delete(bundle);

        Path bundleThumbnailPath = Paths.get(thumbnailDir, String.valueOf(bno));

        try {
            if (Files.exists(bundleThumbnailPath)) {
                Files.walkFileTree(bundleThumbnailPath, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete thumbnail directory for bundle " + bno, e);
        }
    }

    @Override
    public Page<Bundle> searchUserBundles(String username, String keyword, Pageable pageable) {
        return bundleRepository.searchByUserAndKeyword(username, keyword, pageable);
    }
}
