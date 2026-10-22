package ink.yunduan.nest.seed;

import ink.yunduan.nest.domain.AboutProfile;
import ink.yunduan.nest.domain.Honor;
import ink.yunduan.nest.domain.SiteUser;
import ink.yunduan.nest.repo.AboutProfileRepository;
import ink.yunduan.nest.repo.HonorRepository;
import ink.yunduan.nest.repo.SiteUserRepository;
import ink.yunduan.nest.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 首次启动创建站长账号、关于我简历和荣誉，不写入演示文章或留言。
 *
 * 默认账号：yunduan / yunduan123
 * 首次登录后请立刻在「内容管理 > 账号安全」里修改密码。
 * 也可以通过环境变量 YUNDUAN_ADMIN_PASSWORD 指定首次初始化的密码。
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final SiteUserRepository users;
    private final AboutProfileRepository profiles;
    private final HonorRepository honors;
    private final AuthService authService;

    public DataSeeder(
            SiteUserRepository users,
            AboutProfileRepository profiles,
            HonorRepository honors,
            AuthService authService
    ) {
        this.users = users;
        this.profiles = profiles;
        this.honors = honors;
        this.authService = authService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        SiteUser admin = users.findByUsername("yunduan").orElse(null);

        if (admin == null) {
            admin = new SiteUser();
            admin.setUsername("yunduan");
            admin.setPasswordHash(authService.encode(initialPassword()));
            admin.setNickname("云端");
            admin.setEmail("hello@yunduan.xyz");
            admin.setRole("ADMIN");
            users.save(admin);
        } else {
            // 如果数据库里已经有这个账号，确保它始终是站长，不会被普通注册覆盖。
            boolean changed = false;
            if (!"ADMIN".equals(admin.getRole())) {
                admin.setRole("ADMIN");
                changed = true;
            }

            // 旧版本的站长邮箱是 hello@yunduan.dev，启动时平滑迁移到新域名。
            String email = admin.getEmail();
            if (email == null || email.isBlank() || email.endsWith("@yunduan.dev")) {
                admin.setEmail("hello@yunduan.xyz");
                changed = true;
            }

            if (changed) {
                users.save(admin);
            }
        }

        seedAbout();
    }

    private void seedAbout() {
        if (profiles.findFirstByOrderByIdAsc().isPresent()) {
            return;
        }

        AboutProfile profile = new AboutProfile();
        profile.setName("宋爽");
        profile.setNickname("云端");
        profile.setBirthDate("2003-10-14");
        profile.setHometown("河北唐山");
        profile.setPhone("18731513039");
        profile.setEmail("1316339343@qq.com");
        profile.setStatus("沧州交通学院 · 物联网工程 · 本科在读");
        profile.setIntro("本人为沧州交通学院物联网工程专业本科在读生，具备专升本学习背景，学习目标清晰、执行力强，长期规划以人工智能方向深造为核心，对智能体、大模型上层应用领域抱有浓厚热情。在校期间主动夯实计算机基础，备考计算机二级、四级以及 408 相关课程；积极参与学科竞赛，曾获蓝桥杯省赛二等奖，同时自主开展项目实践，先后独立开发鸿蒙美食推荐应用、校园论坛、个人静态博客，尝试本地大模型部署，擅长结合 AI 能力落地实用应用，偏向智能应用开发。学习上擅长复盘总结，有较强应试能力与自主钻研习惯，能够主动跟进 AI 相关新技术；未来希望深耕人工智能、智能体应用赛道，持续把想法转化为可运行的产品。");
        profile.setGoal("深耕人工智能与智能体应用，把想法持续转化为可运行的产品。");
        profile.setCollegeSchool("河北工业职业技术大学");
        profile.setCollegeMajor("移动应用开发");
        profile.setCollegePeriod("2023.09 - 2026.06");
        profile.setCollegeCourses("Vue、HTML、CSS、JavaScript、Web 前端开发、小程序开发");
        profile.setCollegeExperience("参加校园内部前端实战班，重点学习前端知识，提升项目实战经验；在班级里学习成绩优异，实训项目完成度优秀。");
        profile.setBachelorSchool("沧州交通学院");
        profile.setBachelorMajor("物联网工程");
        profile.setBachelorPeriod("2026.09 - 至今");
        profile.setBachelorCourses("主修课程与校内经历暂无");
        profile.setBachelorExperience("本科在读，正在继续补充物联网工程与人工智能相关基础。");
        profile.setSkills("Vue 3、TypeScript、Spring Boot、鸿蒙应用开发、AI 应用实践、项目复盘");
        profiles.save(profile);

        saveHonor("华为开发者学堂专业证书", "华为开发者联盟", "2024-02-27", "/honors/huawei.jpg", "HarmonyOS 应用开发者基础认证。", 100);
        saveHonor("阿里云 Apsara Clouder 专项技能认证", "阿里云", "2024-06-17", "/honors/aliyun.jpg", "云数据库 Clouder 认证：云数据库 RDS 快速入门。", 90);
        saveHonor("AI 驱动的 TDSQL-C Serverless 数据库技术实战营结业证书", "腾讯云开发者社区 / 腾讯云数据库 / CSDN", "2026-10", "/honors/tdsql.jpg", "完成 AI 驱动的 TDSQL-C Serverless 数据库技术实战营。", 80);
        saveHonor("第 16 届蓝桥杯 Web 应用开发河北省赛二等奖", "工业和信息化部人才交流中心 / 蓝桥杯大赛组委会", "2025-05-26", "/honors/lanqiao.jpg", "全国软件和信息技术专业人才大赛河北赛区 Web 应用开发职业院校组二等奖。", 70);
        saveHonor("2024 年百度商业 AI 技术创新大赛北部赛区二等奖", "中国人工智能学会 / 百度商业 / NVIDIA / 飞桨", "2024-08", "/honors/baidu.jpg", "行业智能体搭建赛道，所在团队「慢走不宋先生队」获北部赛区二等奖。", 60);
        saveHonor("文心智能体 DeepSeek 挑战赛一等奖", "文心智能体", "2025", "", "DeepSeek 挑战赛一等奖（暂无证书图片）。", 50);
    }

    private void saveHonor(String title, String issuer, String awardedAt, String imageUrl, String description, int priority) {
        Honor honor = new Honor();
        honor.setTitle(title);
        honor.setIssuer(issuer);
        honor.setAwardedAt(awardedAt);
        honor.setImageUrl(imageUrl);
        honor.setDescription(description);
        honor.setPriority(priority);
        honor.setVisible(true);
        honors.save(honor);
    }

    private String initialPassword() {
        String fromEnv = System.getenv("YUNDUAN_ADMIN_PASSWORD");
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv.trim();
        }
        return "yunduan123";
    }
}
