# 云端の小窝 · Linux 服务器部署示例
#
# 目标结构：
#   Caddy（HTTPS 443） -> Spring Boot JAR（127.0.0.1:8080） -> H2 文件数据库
#
# 下面以 Ubuntu 22.04/24.04、域名 yunduan.xyz 为例。

## 1. 本地构建

在 Windows 项目根目录执行：

```bat
build.cmd
```

产物：

```text
backend\target\yunduan-nest-1.0.0.jar
```

这个 JAR 已经包含前端页面和 API，服务器上不需要 Node.js、不需要再开 Vite。

## 2. 服务器准备

```bash
sudo apt update
sudo apt install -y openjdk-21-jre-headless || sudo apt install -y openjdk-17-jre-headless
sudo adduser --system --group --home /opt/yunduan --shell /usr/sbin/nologin yunduan
sudo mkdir -p /opt/yunduan
sudo chown -R yunduan:yunduan /opt/yunduan
```

上传 JAR：

```bash
scp backend/target/yunduan-nest-1.0.0.jar root@服务器IP:/opt/yunduan/
sudo chown yunduan:yunduan /opt/yunduan/yunduan-nest-1.0.0.jar
```

## 3. 配置环境变量

复制示例：

```bash
sudo cp deploy/yunduan.env.example /opt/yunduan/yunduan.env
sudo nano /opt/yunduan/yunduan.env
```

第一次启动前，把 `YUNDUAN_ADMIN_PASSWORD` 改成强密码。它只在数据库里还没有 `yunduan` 账号时使用；如果已经存在，会保留原密码。

```bash
sudo chown yunduan:yunduan /opt/yunduan/yunduan.env
sudo chmod 600 /opt/yunduan/yunduan.env
```

## 4. systemd

复制服务文件：

```bash
sudo cp deploy/yunduan.service /etc/systemd/system/yunduan.service
sudo systemctl daemon-reload
sudo systemctl enable --now yunduan
sudo systemctl status yunduan
```

查看日志：

```bash
sudo journalctl -u yunduan -f
```

正常会看到：

```text
Tomcat started on port 8080
Started YunduanNestApplication
```

此时后端只在服务器本机 `127.0.0.1:8080` 监听，外网访问不到 8080。

## 5. Caddy 自动 HTTPS（推荐）

安装 Caddy（官方仓库）：

```bash
sudo apt install -y debian-keyring debian-archive-keyring apt-transport-https curl
curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/gpg.key' \
  | sudo gpg --dearmor -o /usr/share/keyrings/caddy-stable-archive-keyring.gpg
curl -1sLf 'https://dl.cloudsmith.io/public/caddy/stable/debian.deb.txt' \
  | sudo tee /etc/apt/sources.list.d/caddy-stable.list
sudo apt update
sudo apt install caddy
```

复制配置：

```bash
sudo cp deploy/Caddyfile /etc/caddy/Caddyfile
sudo nano /etc/caddy/Caddyfile   # 改域名
sudo systemctl reload caddy
```

Caddy 会自动申请和续期 Let's Encrypt 证书。确保：

- 域名 A 记录指向服务器 IP；
- 防火墙开放 80 和 443；
- 80 端口没有被其他程序占用。

## 6. 防火墙

```bash
sudo ufw allow 22/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable
```

**不要**开放 8080。

## 7. 验证

```bash
curl -I https://yunduan.xyz
curl https://yunduan.xyz/api/site
```

浏览器打开：

```text
https://yunduan.xyz
```

用 `yunduan` 登录，进入「内容管理」发布第一篇文章。

## 8. 更新版本

本地重新 `build.cmd`，上传新 JAR，然后：

```bash
sudo systemctl stop yunduan
sudo cp yunduan-nest-1.0.0.jar /opt/yunduan/
sudo chown yunduan:yunduan /opt/yunduan/yunduan-nest-1.0.0.jar
sudo systemctl start yunduan
```

数据库在 `/opt/yunduan/data/yunduan.mv.db`，上传的照片在 `/opt/yunduan/data/uploads/`，更新 JAR 不会清空内容。

## 9. 备份

建议停服后复制整个 `data` 目录（数据库 + 上传的相册照片）：

```bash
sudo systemctl stop yunduan
sudo cp -r /opt/yunduan/data /root/yunduan-backup-$(date +%F)
sudo systemctl start yunduan
```

## 10. 注意

- `.xyz` 不强制 HTTPS，但仍建议开启证书并只开放 80/443。
- 中国大陆服务器需要 ICP 备案。
- 生产环境已通过 `SPRING_PROFILES_ACTIVE=prod` 关闭 H2 控制台，并只监听 127.0.0.1。
- 当前登录 Token 存在内存里，重启服务后需要重新登录。
- 普通用户注册后只能浏览和留言；管理接口由后端强制校验站长身份。
