package com.ssafy.goose.domain.factcheck.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "factcheck")  // ✅ MongoDB 컬렉션 이름
public class FactCheck {
    @Id
    private String id;
    private String title;         // 팩트체크 제목
    private String description;   // 팩트체크 설명
    private String url;           // 팩트체크 원본 링크
    private String source;        // 출처 (예: 오마이뉴스)
    private String timestamp;     // 업로드 시간
}
