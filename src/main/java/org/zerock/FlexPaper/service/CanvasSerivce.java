package org.zerock.FlexPaper.service;

import org.zerock.FlexPaper.dto.CanvasDTO;
import org.zerock.FlexPaper.dto.ThumbnailRequestDTO;

import java.io.IOException;
import java.util.List;

public interface CanvasSerivce {
    public void save(CanvasDTO canvasDTO);
    CanvasDTO getCanvasDTOByBundleId(Long bundleId);
    void saveCanvasThumbnails(String bundleId, List<ThumbnailRequestDTO> imageList) throws IOException;
}
