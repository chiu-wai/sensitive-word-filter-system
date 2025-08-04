@echo off
echo ========================================
echo 敏感词过滤系统启动脚本
echo ========================================

echo.
echo 正在启动后端服务...
cd backend
start "后端服务" cmd /k "mvn spring-boot:run"

echo.
echo 等待后端服务启动...
timeout /t 10 /nobreak > nul

echo.
echo 正在启动前端服务...
cd ..\frontend
start "前端服务" cmd /k "npm run serve"

echo.
echo 系统启动完成！
echo 后端地址: http://localhost:8080
echo 前端地址: http://localhost:3000
echo.
echo 按任意键退出...
pause > nul 