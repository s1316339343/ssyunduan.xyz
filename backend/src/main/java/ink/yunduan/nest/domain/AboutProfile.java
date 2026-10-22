package ink.yunduan.nest.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "about_profile")
@Getter
@Setter
@NoArgsConstructor
public class AboutProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(length = 80)
    private String nickname;

    @Column(length = 40)
    private String birthDate;

    @Column(length = 80)
    private String hometown;

    @Column(length = 40)
    private String phone;

    @Column(length = 120)
    private String email;

    @Column(length = 80)
    private String status;

    @Lob
    private String intro;

    @Lob
    private String goal;

    @Column(length = 120)
    private String collegeSchool;

    @Column(length = 120)
    private String collegeMajor;

    @Column(length = 60)
    private String collegePeriod;

    @Lob
    private String collegeCourses;

    @Lob
    private String collegeExperience;

    @Column(length = 120)
    private String bachelorSchool;

    @Column(length = 120)
    private String bachelorMajor;

    @Column(length = 60)
    private String bachelorPeriod;

    @Lob
    private String bachelorCourses;

    @Lob
    private String bachelorExperience;

    @Lob
    private String skills;
}
