# 域名说明 · 云端 / 宋爽

当前项目统一使用：

```text
yunduan.xyz
www.yunduan.xyz
```

站点邮箱也同步改为：

```text
hello@yunduan.xyz
```

> 邮箱地址目前只是网站上的展示地址。要真正收发邮件，还需要在域名服务商或邮箱服务商处开通邮箱、邮件转发，例如 Cloudflare Email Routing。

## 为什么用 `yunduan.xyz`

- `yunduan` 对应“云端”，和站名「云端の小窝」一致；
- `.xyz` 简短、好记，价格通常比 `.com` 更容易接受；
- 不像 `.dev` 那样被浏览器强制要求 HTTPS，部署门槛更低；
- 仍然建议上线后开启 HTTPS，Caddy 可以自动申请证书。

## 域名解析

在域名服务商处添加：

| 类型 | 主机记录 | 记录值 |
| --- | --- | --- |
| A | `@` | 服务器公网 IP |
| A | `www` | 服务器公网 IP |

如果以后换服务器，只改 A 记录即可。

## 备案说明

- 使用**中国大陆服务器**：需要 ICP 备案，且域名后缀、注册商、实名信息都要满足工信部和接入商要求；`.xyz` 是否能在当前省份备案，以阿里云/腾讯云备案系统实际校验为准。
- 使用**香港或海外服务器**：不需要 ICP 备案，`yunduan.xyz` 可以直接解析使用。
- 个人站在大陆上线前，建议先确认备案要求，再购买服务器。

## 项目中的配置位置

域名和邮箱已经统一在以下位置：

- `backend/src/main/java/ink/yunduan/nest/web/SiteController.java`
- `backend/src/main/java/ink/yunduan/nest/seed/DataSeeder.java`
- `frontend/src/stores/site.ts`
- `deploy/Caddyfile`
- `deploy/nginx.conf`
- `deploy/README.md`
- `README.md`

前端所有展示位置都从 `/api/site` 读取，所以修改后端后，页脚、关于页、文章封面、友链信息会一起更新。

## 旧域名迁移

旧版本使用的是 `yunduan.dev`。现在：

- 新访问统一使用 `yunduan.xyz`；
- 如果数据库里已有站长账号，启动时会自动把旧的 `hello@yunduan.dev` 邮箱迁移为 `hello@yunduan.xyz`；
- 其他文章、照片、留言和用户数据不受影响；
- 旧域名如果还解析着，可以在 DNS 里保留 301 跳转到新域名，或直接停止解析。

## 上线 HTTPS

项目自带 `deploy/Caddyfile`：

```caddyfile
yunduan.xyz, www.yunduan.xyz {
    encode zstd gzip
    reverse_proxy 127.0.0.1:8080
}
```

把 DNS 指向服务器后，Caddy 会自动申请 Let's Encrypt 证书。也可以用 `deploy/nginx.conf` + Certbot。

不要把后端 `8080` 直接暴露到公网，只让 Caddy / Nginx 访问 `127.0.0.1:8080`。
