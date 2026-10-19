package ink.yunduan.nest.web;

import ink.yunduan.nest.web.dto.SiteInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/site")
public class SiteController {

    @GetMapping
    public SiteInfo site() {
        return new SiteInfo(
                "云端の小窝",
                "把日子过成诗，把代码写成歌",
                "YUNDUAN NEST",
                "宋爽",
                "云端",
                "",
                "在云端，记录人间的烟火。",
                "宋爽，网名云端。一个在代码与文字之间来回散步的人。",
                "yunduan.xyz",
                "hello@yunduan.xyz",
                "https://github.com/yunduan",
                "中国",
                "2026-08-01",
                "备案号待填写",
                "公网安备待填写",
                List.of("Vue 3", "TypeScript", "Spring Boot", "H2"),
                List.of(
                        new SiteInfo.Social("GitHub", "https://github.com/yunduan", "github"),
                        new SiteInfo.Social("邮箱", "mailto:hello@yunduan.xyz", "mail"),
                        new SiteInfo.Social("留言板", "/guestbook", "message")
                )
        );
    }
}
