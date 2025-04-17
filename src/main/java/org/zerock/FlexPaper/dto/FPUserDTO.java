package org.zerock.FlexPaper.dto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FPUserDTO {
    private String username;
    private List<BundleDTO> bundles; // FPUser와 관련된 Bundle 목록
}
