package org.zerock.FlexPaper.domain;

import jakarta.persistence.*;
import lombok.*;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bundle extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;  // 추가: Bundle 설명

    @ManyToOne
    @JoinColumn(name = "FPUser_id")
    private FPUser FPUser;

    @OneToMany(mappedBy = "bundle", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Canvas> canvases = new ArrayList<>();
}
