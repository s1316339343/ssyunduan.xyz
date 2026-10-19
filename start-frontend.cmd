@echo off
chcp 65001 >nul
cd /d "%~dp0frontend"

echo.
echo ==========================================
echo   云端の小窝 · Vue 前端
echo ==========================================
echo.

where node >nul 2>nul
if errorlevel 1 (
  echo [错误] 没有找到 Node.js，请先安装 Node.js 18+。
  pause
  exit /b 1
)

if not exist "node_modules" (
  echo 首次运行，正在安装前端依赖...
  call npm install
  if errorlevel 1 (
    echo.
    echo [错误] npm install 失败。
    echo 可以尝试设置国内镜像：
    echo   npm config set registry https://registry.npmmirror.com
    pause
    exit /b 1
  )
)

echo 正在启动 Vite 开发服务器...
echo 前端地址：http://127.0.0.1:5173
echo.

call npm run dev

echo.
echo 前端已退出。
pause
