@echo off
chcp 65001 >nul
setlocal

net session >nul 2>&1
if %errorlevel% neq 0 (
    echo 需要管理员权限，正在请求提升...
    powershell -NoProfile -Command "Start-Process -FilePath '%~f0' -Verb RunAs"
    exit /b
)

echo.
echo ==========================================
echo   放行云端小窝的局域网端口
echo ==========================================
echo.

netsh advfirewall firewall delete rule name="Yunduan Nest 8080" >nul 2>&1
netsh advfirewall firewall delete rule name="Yunduan Nest 5173" >nul 2>&1

netsh advfirewall firewall add rule name="Yunduan Nest 8080" dir=in action=allow protocol=TCP localport=8080 profile=any
netsh advfirewall firewall add rule name="Yunduan Nest 5173" dir=in action=allow protocol=TCP localport=5173 profile=any

echo.
echo 已放行 TCP 8080 和 5173。
echo 现在可以运行 start-phone.cmd，让手机访问。
echo.
echo 安全提示：
echo   这个规则允许同一局域网内的设备访问。
echo   不要在公共 Wi-Fi 上长期开启，使用完可以运行：
echo   netsh advfirewall firewall delete rule name="Yunduan Nest 8080"
echo   netsh advfirewall firewall delete rule name="Yunduan Nest 5173"
echo.
pause
