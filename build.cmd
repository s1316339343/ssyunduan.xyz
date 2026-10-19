@echo off
chcp 65001 >nul
setlocal
cd /d "%~dp0"

echo.
echo ==========================================
echo   云端の小窝 · 生产构建
echo ==========================================
echo.

where node >nul 2>nul
if errorlevel 1 (
  echo [错误] 没有找到 Node.js。
  pause
  exit /b 1
)

if not defined JAVA_HOME if exist "D:\codeTool\jdk-21\bin\java.exe" set "JAVA_HOME=D:\codeTool\jdk-21"
if defined JAVA_HOME set "PATH=%JAVA_HOME%\bin;%PATH%"

where java >nul 2>nul
if errorlevel 1 if "%JAVA_HOME%"=="" (
  echo [错误] 没有找到 Java，也没有设置 JAVA_HOME。
  pause
  exit /b 1
)

echo [1/3] 构建前端...
cd /d "%~dp0frontend"
if not exist "node_modules" call npm install
call npm run build
if errorlevel 1 (
  echo [错误] 前端构建失败。
  pause
  exit /b 1
)

echo.
echo [2/3] 把前端复制进后端 static，让一个 JAR 也能打开网页...
if exist "%~dp0backend\src\main\resources\static" rmdir /s /q "%~dp0backend\src\main\resources\static"
xcopy "%~dp0frontend\dist" "%~dp0backend\src\main\resources\static\" /E /I /Y >nul
if errorlevel 1 (
  echo [错误] 复制前端产物失败。
  pause
  exit /b 1
)

cd /d "%~dp0backend"
call mvnw.cmd clean package -DskipTests
if errorlevel 1 (
  echo [错误] 后端构建失败。
  pause
  exit /b 1
)

echo.
echo [3/3] 完成。
echo 前端产物：%~dp0frontend\dist
echo 单 JAR 产物：%~dp0backend\target\yunduan-nest-1.0.0.jar
echo.
echo 现在可以直接双击 start-onejar.cmd，只启动一个窗口。
echo.
pause
