@echo off
chcp 65001 >nul
cd /d "%~dp0backend"

echo.
echo ==========================================
echo   云端の小窝 · 单 JAR 启动
echo ==========================================
echo.

if not defined JAVA_HOME if exist "D:\codeTool\jdk-21\bin\java.exe" set "JAVA_HOME=D:\codeTool\jdk-21"
if defined JAVA_HOME set "PATH=%JAVA_HOME%\bin;%PATH%"

where java >nul 2>nul
if errorlevel 1 (
  echo [错误] 没有找到 Java，请安装 JDK 17+。
  pause
  exit /b 1
)

if not exist "target\yunduan-nest-1.0.0.jar" (
  echo [提示] 还没有构建过项目。
  echo 请先回到项目根目录，双击 build.cmd。
  pause
  exit /b 1
)

echo 正在启动：http://127.0.0.1:8080
echo 前端和接口在同一个进程里，不需要再开 5173。
echo.

start "" "http://127.0.0.1:8080"
java -jar "target\yunduan-nest-1.0.0.jar" %*

echo.
echo 已停止。
pause
