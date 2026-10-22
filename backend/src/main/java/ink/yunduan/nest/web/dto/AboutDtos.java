package ink.yunduan.nest.web.dto;

import ink.yunduan.nest.domain.AboutProfile;
import ink.yunduan.nest.domain.Honor;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public final class AboutDtos {

    private AboutDtos() {
    }

    public record ProfileRequest(
            @NotBlank(message = "姓名不能为空") String name,
            String nickname,
            String birthDate,
            String hometown,
            String phone,
            String email,
            String status,
            String intro,
            String goal,
            String collegeSchool,
            String collegeMajor,
            String collegePeriod,
            String collegeCourses,
            String collegeExperience,
            String bachelorSchool,
            String bachelorMajor,
            String bachelorPeriod,
            String bachelorCourses,
            String bachelorExperience,
            String skills
    ) {
    }

    public record HonorRequest(
            @NotBlank(message = "荣誉名称不能为空") String title,
            String issuer,
            String awardedAt,
            String imageUrl,
            String description,
            Integer priority,
            Boolean visible
    ) {
    }

    public record AboutResponse(AboutProfile profile, List<Honor> honors) {
    }
}
