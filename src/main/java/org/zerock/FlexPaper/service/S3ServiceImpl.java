package org.zerock.FlexPaper.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service{

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    // ✅ S3에 이미지 업로드
    public Map<String, String> uploadFile(MultipartFile file) {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            String fileUrl = amazonS3.getUrl(bucketName, fileName).toString(); // 업로드된 파일의 URL 반환
            // JSON 응답 생성
            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", fileUrl);
            return response; // JSON 응답 반환; // 업로드된 파일의 URL 반환
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }

    // ✅ S3에 저장된 이미지 목록 가져오기
    public List<String> getImages() {
        return amazonS3.listObjectsV2(bucketName).getObjectSummaries().stream()
                .map(summary -> "https://" + bucketName + ".s3.amazonaws.com/" + summary.getKey())
                .collect(Collectors.toList());
    }
}
