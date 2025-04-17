package org.zerock.FlexPaper.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.FlexPaper.domain.Bundle;
import org.zerock.FlexPaper.domain.Canvas;
import org.zerock.FlexPaper.dto.CanvasDTO;
import org.zerock.FlexPaper.dto.ThumbnailRequestDTO;
import org.zerock.FlexPaper.repository.BundleRepository;
import org.zerock.FlexPaper.repository.CanvasRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CanvasServiceImpl implements CanvasSerivce {

    private final BundleRepository bundleRepository;
    private final CanvasRepository canvasRepository; // Canvas 저장을 위한 Repository 추가
    private final String UPLOAD_DIR = "C:/thumbnails/";

    @Override
    @Transactional // 명확한 트랜잭션 범위를 보장하여 save() 내에서 원자적인 작업을 유지
    public void save(CanvasDTO canvasDTO) {
        // 1. bundleId로 Bundle 조회 (없으면 예외 발생)
        Bundle bundle = bundleRepository.findById(canvasDTO.getBundleId())
                .orElseThrow(() -> new IllegalArgumentException("Bundle not found"));

        // 2. 기존 bundle 내 모든 Canvas 삭제
        canvasRepository.deleteByBundle(bundle);

/*        // 3. canvasDTO의 데이터를 ID 순서대로 정렬하여 새로운 ID 부여
        List<CanvasDTO.CanvasData> sortedCanvasList = canvasDTO.getCanvases()
                .stream()
                .sorted(Comparator.comparing(CanvasDTO.CanvasData::getId)) // 기존 ID 순서대로 정렬
                .collect(Collectors.toList());*/

        // 4. 정렬된 순서대로 새 canvasID 부여 (1부터 시작)
        for (CanvasDTO.CanvasData canvasData : canvasDTO.getCanvases()) {

            Canvas canvas = Canvas.builder()
                    .canvasID(canvasData.getId()) // 새로운 canvasID 설정
                    .canvasData(canvasData.getCanvasData())
                    .bundle(bundle)
                    .build();

            canvasRepository.save(canvas);
        }
    }




    @Override
    public CanvasDTO getCanvasDTOByBundleId(Long bundleId) {
        List<Canvas> canvases = canvasRepository.findByBundleId(bundleId);

/*        List<CanvasDTO.CanvasData> canvasDataList = new ArrayList<>();
        if(canvases.size() > 0){
            for(Canvas canvas : canvases){
                CanvasDTO.CanvasData data = new CanvasDTO.CanvasData();
                data.setId(canvas.getCanvasID());
                data.setCanvasData(canvas.getCanvasData());
                canvasDataList.add(data);
            }
        }*/

        // CanvasDTO 생성
        List<CanvasDTO.CanvasData> canvasDataList = canvases.stream()
                .map(canvas -> new CanvasDTO.CanvasData(canvas.getCanvasID(), canvas.getCanvasData())) // canvas -> CanvasDTO.CanvasData 변환
                .collect(Collectors.toList()); // 리스트로 수집

        CanvasDTO canvasDTO = new CanvasDTO();
        canvasDTO.setBundleId(bundleId);
        canvasDTO.setCanvases(canvasDataList);

        return canvasDTO;
    }

    @Override
    public void saveCanvasThumbnails(String bundleId, List<ThumbnailRequestDTO> imageList) throws IOException {
        String bundleFolderPath = UPLOAD_DIR + bundleId + "/";
        File bundleFolder = new File(bundleFolderPath);

        // 1️⃣ 저장 디렉토리 확인 및 생성
        if (!bundleFolder.exists()) {
            bundleFolder.mkdirs();
        }

        // 2️⃣ 기존 파일 모두 삭제
        if (bundleFolder.exists() && bundleFolder.isDirectory()) {
            File[] files = bundleFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!file.isDirectory()) {
                        file.delete();
                    }
                }
            }
        }

        // 3️⃣ 이미지 리스트 저장
        for (ThumbnailRequestDTO thumbnail : imageList) {
            String canvasId = thumbnail.getCanvasId();
            String base64Image = thumbnail.getImageData();

            // 유효성 검사
            if (base64Image == null || !base64Image.startsWith("data:image/png;base64,")) {
                throw new IllegalArgumentException("Invalid image data for canvasId: " + canvasId);
            }

            // 디코딩
            byte[] imageBytes = decodeBase64Image(base64Image);

            // 파일 저장
            String filePath = bundleFolderPath + canvasId + ".png";
            Path path = Paths.get(filePath);
            Files.write(path, imageBytes);
        }
    }

    // Base64 문자열을 디코딩하여 바이트 배열로 변환하는 메서드
    private byte[] decodeBase64Image(String base64Image) {
        String base64Data = base64Image.replace("data:image/png;base64,", ""); // 헤더 제거
        return Base64.getDecoder().decode(base64Data); // Base64 디코딩
    }
}
