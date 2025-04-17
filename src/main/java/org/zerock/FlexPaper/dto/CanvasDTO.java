package org.zerock.FlexPaper.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CanvasDTO {
    private Long bundleId; // 번들 ID
    private List<CanvasData> canvases; // 여러 개의 Canvas 데이터

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CanvasData {
        private String id; // 개별 Canvas ID
        private JsonNode canvasData; // Fabric.js JSON 데이터

    }
}
