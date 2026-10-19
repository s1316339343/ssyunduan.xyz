package ink.yunduan.nest.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "moment")
@Getter
@Setter
@NoArgsConstructor
public class Moment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false, columnDefinition = "CLOB")
    private String content;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "moment_image", joinColumns = @JoinColumn(name = "moment_id"))
    @Column(name = "art", length = 40)
    private List<String> images = new ArrayList<>();

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private int likes;

    @Column(nullable = false)
    private int comments;
}
