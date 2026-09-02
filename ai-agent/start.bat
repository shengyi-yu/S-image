@echo off
echo ================================
echo   AI Agent 服务启动脚本
echo ================================
echo.

REM 激活虚拟环境
call venv\Scripts\activate

echo [1/2] 虚拟环境已激活
echo [2/2] 启动服务...
echo.

echo 服务地址: http://localhost:8000
echo API文档: http://localhost:8000/docs
echo.

python main.py
pause
