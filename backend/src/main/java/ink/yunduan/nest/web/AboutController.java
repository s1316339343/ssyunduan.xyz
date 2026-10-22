package ink.yunduan.nest.web;

import ink.yunduan.nest.domain.AboutProfile;
import ink.yunduan.nest.domain.Honor;
import ink.yunduan.nest.repo.AboutProfileRepository;
import ink.yunduan.nest.repo.HonorRepository;
import ink.yunduan.nest.service.AuthService;
import ink.yunduan.nest.web.dto.AboutDtos.AboutResponse;
import ink.yunduan.nest.web.dto.AboutDtos.HonorRequest;
import ink.yunduan.nest.web.dto.AboutDtos.ProfileRequest;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
@Transactional
public class AboutController {

    private final AboutProfileRepository profiles;
    private final HonorRepository honors;
    private final AuthService authService;

    public AboutController(AboutProfileRepository profiles, HonorRepository honors, AuthService authService) {
        this.profiles = profiles;
        this.honors = honors;
        this.authService = authService;
    }

    @GetMapping("/about")
    public AboutResponse publicAbout() {
        return new AboutResponse(profileOrDefault(), honors.findAllByVisibleTrueOrderByPriorityDescIdAsc());
    }

    @GetMapping("/admin/about")
    public AboutResponse adminAbout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.requireAdmin(authorization);
        return new AboutResponse(profileOrDefault(), honors.findAllByOrderByPriorityDescIdAsc());
    }

    @PutMapping("/admin/about")
    public AboutProfile updateProfile(
            @Valid @RequestBody ProfileRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        authService.requireAdmin(authorization);
        AboutProfile profile = profiles.findFirstByOrderByIdAsc().orElseGet(AboutProfile::new);
        applyProfile(profile, request);
        return profiles.save(profile);
    }

    @PostMapping("/admin/about/honors")
    public Honor createHonor(
            @Valid @RequestBody HonorRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        authService.requireAdmin(authorization);
        Honor honor = new Honor();
        applyHonor(honor, request);
        return honors.save(honor);
    }

    @PutMapping("/admin/about/honors/{id}")
    public Honor updateHonor(
            @PathVariable Long id,
            @Valid @RequestBody HonorRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        authService.requireAdmin(authorization);
        Honor honor = honors.findById(id).orElseThrow(() -> new NoSuchElementException("荣誉不存在。"));
        applyHonor(honor, request);
        return honors.save(honor);
    }

    @DeleteMapping("/admin/about/honors/{id}")
    public Map<String, Object> deleteHonor(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        authService.requireAdmin(authorization);
        if (!honors.existsById(id)) {
            throw new NoSuchElementException("荣誉不存在。");
        }
        honors.deleteById(id);
        return Map.of("ok", true);
    }

    private AboutProfile profileOrDefault() {
        return profiles.findFirstByOrderByIdAsc().orElseGet(() -> {
            AboutProfile profile = new AboutProfile();
            profile.setName("宋爽");
            profile.setNickname("云端");
            profile.setStatus("本科在读");
            return profile;
        });
    }

    private void applyProfile(AboutProfile profile, ProfileRequest request) {
        profile.setName(request.name().trim());
        profile.setNickname(value(request.nickname()));
        profile.setBirthDate(value(request.birthDate()));
        profile.setHometown(value(request.hometown()));
        profile.setPhone(value(request.phone()));
        profile.setEmail(value(request.email()));
        profile.setStatus(value(request.status()));
        profile.setIntro(value(request.intro()));
        profile.setGoal(value(request.goal()));
        profile.setCollegeSchool(value(request.collegeSchool()));
        profile.setCollegeMajor(value(request.collegeMajor()));
        profile.setCollegePeriod(value(request.collegePeriod()));
        profile.setCollegeCourses(value(request.collegeCourses()));
        profile.setCollegeExperience(value(request.collegeExperience()));
        profile.setBachelorSchool(value(request.bachelorSchool()));
        profile.setBachelorMajor(value(request.bachelorMajor()));
        profile.setBachelorPeriod(value(request.bachelorPeriod()));
        profile.setBachelorCourses(value(request.bachelorCourses()));
        profile.setBachelorExperience(value(request.bachelorExperience()));
        profile.setSkills(value(request.skills()));
    }

    private void applyHonor(Honor honor, HonorRequest request) {
        honor.setTitle(request.title().trim());
        honor.setIssuer(value(request.issuer()));
        honor.setAwardedAt(value(request.awardedAt()));
        honor.setImageUrl(value(request.imageUrl()));
        honor.setDescription(value(request.description()));
        honor.setPriority(request.priority() == null ? 0 : request.priority());
        honor.setVisible(request.visible() == null || request.visible());
    }

    private String value(String text) {
        return text == null ? "" : text.trim();
    }
}
