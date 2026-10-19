package ink.yunduan.nest.web.dto;

import java.util.List;

public record SiteInfo(
        String name,
        String title,
        String englishTitle,
        String author,
        String nickname,
        String avatar,
        String tagline,
        String bio,
        String domain,
        String email,
        String github,
        String location,
        String startDate,
        String icp,
        String police,
        List<String> techStack,
        List<Social> socials
) {
    public record Social(String label, String url, String icon) {
    }
}
