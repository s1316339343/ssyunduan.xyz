@echo off
chcp 65001 >nul
cd /d "%~dp0backend"

echo.
echo ==========================================
echo   云端の小窝 · Spring Boot 后端
echo ==========================================
echo.

if not exist "mvnw.cmd" (
  echo [错误] 没有找到 mvnw.cmd，项目文件可能不完整。
  pause
  exit /b 1
)

if not defined JAVA_HOME if exist "D:\codeTool\jdk-21\bin\java.exe" set "JAVA_HOME=D:\codeTool\jdk-21"
if defined JAVA_HOME set "PATH=%JAVA_HOME%\bin;%PATH%"

where java >nul 2>nul
if errorlevel 1 (
  if "%JAVA_HOME%"=="" (
    echo [错误] 没有找到 Java，也没有设置 JAVA_HOME。
    echo 请安装 JDK 17+，或设置 JAVA_HOME 指向 JDK 目录。
    pause
    exit /b 1
  )
)

echo 正在启动 Spring Boot...
echo 首次运行会下载 Maven 和依赖，请耐心等待。
echo 后端地址：http://127.0.0.1:8080
echo.

call mvnw.cmd spring-boot:run

echo.
echo 后端已退出。
pause
