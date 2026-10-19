package ink.yunduan.nest.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "changelog_entry")
@Getter
@Setter
@NoArgsConstructor
public class ChangelogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(length = 20)
    private String version;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "changelog_item", joinColumns = @JoinColumn(name = "entry_id"))
    @Column(name = "item", length = 300)
    private List<String> items = new ArrayList<>();
}
