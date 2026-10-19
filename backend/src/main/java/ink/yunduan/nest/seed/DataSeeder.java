package ink.yunduan.nest.seed;

import ink.yunduan.nest.domain.SiteUser;
import ink.yunduan.nest.repo.SiteUserRepository;
import ink.yunduan.nest.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 首次启动只创建一个站长账号，不写入任何演示文章、留言或相册数据。
 *
 * 默认账号：yunduan / yunduan123
 * 首次登录后请立刻在「内容管理 > 账号安全」里修改密码。
 * 也可以通过环境变量 YUNDUAN_ADMIN_PASSWORD 指定首次初始化的密码。
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final SiteUserRepository users;
    private final AuthService authService;

    public DataSeeder(SiteUserRepository users, AuthService authService) {
        this.users = users;
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
            return;
        }

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

    private String initialPassword() {
        String fromEnv = System.getenv("YUNDUAN_ADMIN_PASSWORD");
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv.trim();
        }
        return "yunduan123";
    }
}
