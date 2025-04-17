package org.zerock.FlexPaper.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ThumbnailRequestDTO {

    private String canvasId;
    private String imageData;
}
