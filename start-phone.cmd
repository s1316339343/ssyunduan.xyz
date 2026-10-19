@echo off
chcp 65001 >nul
setlocal
cd /d "%~dp0"

set "LANIP="
for /f "delims=" %%i in ('powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\lan-ip.ps1"') do set "LANIP=%%i"
if not defined LANIP set "LANIP=你的电脑局域网IP"

echo.
echo ==========================================
echo   云端の小窝 · 手机访问模式
echo ==========================================
echo.
echo 请确保：
echo   1. 手机和这台电脑连接同一个 Wi-Fi / 路由器；
echo   2. 电脑上的这个窗口保持打开；
echo   3. Windows 防火墙已放行 8080 端口。
echo.
echo 手机浏览器打开：
echo.
echo     http://%LANIP%:8080
echo.
echo 电脑本地访问：
echo     http://127.0.0.1:8080
echo.
echo 如果手机打不开：
echo   - 右键管理员运行 allow-firewall.cmd（只需一次）
echo   - 关闭电脑和手机上的 VPN / 代理
echo   - 确认路由器没有开启“AP 隔离 / 访客网络”
echo   - 在电脑 cmd 输入 ipconfig，确认 IPv4 地址
echo.
echo 注意：这是局域网访问，离开这个 Wi-Fi 后手机就打不开了。
echo 手机上传的照片会保存在电脑的 backend\data\uploads 目录。
echo.

call "%~dp0start-onejar.cmd" --server.address=0.0.0.0 --spring.h2.console.enabled=false
