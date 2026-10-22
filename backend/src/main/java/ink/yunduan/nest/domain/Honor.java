package ink.yunduan.nest.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "honor")
@Getter
@Setter
@NoArgsConstructor
public class Honor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 160)
    private String issuer;

    @Column(length = 40)
    private String awardedAt;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private int priority = 0;

    @Column(nullable = false)
    private boolean visible = true;
}
