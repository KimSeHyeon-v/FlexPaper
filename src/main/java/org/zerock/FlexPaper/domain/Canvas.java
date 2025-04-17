package org.zerock.FlexPaper.domain;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.*;
import org.zerock.FlexPaper.common.JsonNodeConverter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Canvas extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bundle_id")
    private Bundle bundle;

    private String canvasID;

    @Convert(converter = JsonNodeConverter.class) // JSON 변환기 적용
    @Column(columnDefinition = "LONGTEXT")
    private JsonNode canvasData;
}
