package org.zerock.FlexPaper.dto;

import lombok.*;
import org.zerock.FlexPaper.domain.FPUser;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BundleDTO {
    private Long bno;
    private String title;
    private String description;

    private FPUser fpUser;  // 추가: Bundle에 속한 사용자 정보
    private CanvasDTO canvasDTO;
}
