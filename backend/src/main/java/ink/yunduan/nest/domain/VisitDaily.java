package ink.yunduan.nest.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "visit_daily", uniqueConstraints = @UniqueConstraint(columnNames = "visit_date"))
@Getter
@Setter
@NoArgsConstructor
public class VisitDaily {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "visit_date", nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int pv;

    @Column(nullable = false)
    private int uv;
}
