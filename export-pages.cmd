@echo off
chcp 65001 >nul
setlocal
cd /d "%~dp0"

echo.
echo ==========================================
echo   云端小窝 · 导出 GitHub Pages 静态版
echo ==========================================
echo.

where node >nul 2>nul
if errorlevel 1 (
  echo [错误] 没有找到 Node.js，请先安装 Node.js 18+。
  pause
  exit /b 1
)

echo 检查本地后端 http://127.0.0.1:8080 ...
powershell -NoProfile -Command "try { $r = Invoke-WebRequest -Uri 'http://127.0.0.1:8080/api/site' -UseBasicParsing -TimeoutSec 4; if ($r.StatusCode -eq 200) { exit 0 } } catch {}; exit 1"
if errorlevel 1 (
  echo.
  echo [错误] 后端没有启动。
  echo 请先在另一个窗口双击 start-onejar.cmd，等它启动完成后再运行本脚本。
  pause
  exit /b 1
)

echo 正在导出文章、照片和站点数据...
node scripts\export-static.mjs
if errorlevel 1 (
  pause
  exit /b 1
)

set "PAGES_BASE=%~1"
if not defined PAGES_BASE set "PAGES_BASE=/"
if not "%PAGES_BASE:~0,1%"=="/" set "PAGES_BASE=/%PAGES_BASE%"
if not "%PAGES_BASE:~-1%"=="/" set "PAGES_BASE=%PAGES_BASE%/"

echo.
echo 构建静态前端，base=%PAGES_BASE%
cd /d "%~dp0frontend"
if not exist "node_modules" (
  echo 首次构建，正在安装前端依赖...
  call npm install
  if errorlevel 1 (
    echo [错误] npm install 失败。
    pause
    exit /b 1
  )
)
set "VITE_BASE=%PAGES_BASE%"
call npm run build
if errorlevel 1 (
  echo [错误] 前端构建失败。
  pause
  exit /b 1
)

copy /Y "dist\index.html" "dist\404.html" >nul
type nul > "dist\.nojekyll"

echo.
echo ==========================================
echo   完成
echo ==========================================
echo.
echo 静态站点目录：%~dp0frontend\dist
echo GitHub Pages base：%PAGES_BASE%
echo.
echo 下一步：
echo   1. git add frontend/public/data frontend/public/uploads frontend/src
echo   2. git add .github scripts README.md
echo   3. git commit -m "update static site"
echo   4. git push
echo.
echo 如果仓库地址是 https://用户名.github.io/仓库名/
echo 请使用：export-pages.cmd /仓库名/
echo 如果使用自定义域名或 用户名.github.io 仓库，直接运行 export-pages.cmd。
echo.
pause
