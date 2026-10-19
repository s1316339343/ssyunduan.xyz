package ink.yunduan.nest.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "photo")
@Getter
@Setter
@NoArgsConstructor
public class Photo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String title;

    @Column(nullable = false, length = 40)
    private String category;

    @Column(length = 80)
    private String location;

    private LocalDate takenAt;

    @Column(length = 40)
    private String art;

    /** 上传后的图片地址，例如 /uploads/xxxx.jpg；为空时回退到 art 插画。 */
    @Column(length = 500)
    private String imageUrl;
}
