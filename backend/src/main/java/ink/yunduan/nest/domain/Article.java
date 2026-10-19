package ink.yunduan.nest.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "article")
@Getter
@Setter
@NoArgsConstructor
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String slug;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 500)
    private String excerpt;

    @Lob
    @Column(nullable = false, columnDefinition = "CLOB")
    private String content;

    @Column(nullable = false, length = 40)
    private String category;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "article_tag", joinColumns = @JoinColumn(name = "article_id"))
    @Column(name = "tag", length = 40)
    private List<String> tags = new ArrayList<>();

    @Column(length = 40)
    private String cover;

    @Column(nullable = false)
    private LocalDate publishedAt;

    private LocalDate updatedAt;

    @Column(nullable = false)
    private int views;

    @Column(nullable = false)
    private int words;

    @Column(nullable = false)
    private boolean featured;

    @Column(nullable = false)
    private boolean published = true;
}
