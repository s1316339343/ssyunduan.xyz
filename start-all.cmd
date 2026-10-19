@echo off
chcp 65001 >nul
setlocal
cd /d "%~dp0"

echo.
echo ==========================================
echo   云端の小窝 · 启动前后端
echo ==========================================
echo.

if not defined JAVA_HOME if exist "D:\codeTool\jdk-21\bin\java.exe" set "JAVA_HOME=D:\codeTool\jdk-21"
if defined JAVA_HOME set "PATH=%JAVA_HOME%\bin;%PATH%"

where java >nul 2>nul
if errorlevel 1 if "%JAVA_HOME%"=="" (
  echo [错误] 没有找到 Java。
  echo 请先安装 JDK 17 或更高版本，并把 java 加入 PATH，
  echo 或者设置 JAVA_HOME 指向 JDK 目录。
  echo 下载地址：https://adoptium.net/
  echo.
  pause
  exit /b 1
)

where node >nul 2>nul
if errorlevel 1 (
  echo [错误] 没有找到 Node.js。
  echo 请先安装 Node.js 18 或更高版本。
  echo 下载地址：https://nodejs.org/
  echo.
  pause
  exit /b 1
)

echo 正在启动后端窗口...
start "云端小窝 · 后端 8080" "%~dp0start-backend.cmd"

echo 等待后端初始化...
timeout /t 6 /nobreak >nul

echo 正在启动前端窗口...
start "云端小窝 · 前端 5173" "%~dp0start-frontend.cmd"

echo 等待前端启动...
timeout /t 6 /nobreak >nul

start "" "http://127.0.0.1:5173"

echo.
echo 已打开浏览器：http://127.0.0.1:5173
echo 如果页面暂时没有数据，等后端启动完成后再刷新一次。
echo.
pause
