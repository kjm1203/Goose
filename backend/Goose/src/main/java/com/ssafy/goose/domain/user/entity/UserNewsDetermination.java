// UserNewsDetermination.java
package com.ssafy.goose.domain.user.entity;

import lombok.*;
import jakarta.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_news_determinations")
public class UserNewsDetermination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "news_id", nullable = false)
    private String newsId;

    @Column(name = "search_type", nullable = false)
    private String searchType;  // URL, TEXT, IMAGE

    @Column(name = "determined_at", nullable = false)
    private int determinedAt;
}