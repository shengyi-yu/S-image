"""
AI Agent 服务入口

启动: python main.py
文档: http://localhost:8000/docs
"""
import logging
import uvicorn
from contextlib import asynccontextmanager
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import FileResponse
from config.settings import get_settings
from api.routes import router
from services.nacos_client import init_nacos, get_nacos_client

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)


@asynccontextmanager
async def lifespan(app: FastAPI):
    """应用生命周期管理"""
    # 启动时：注册到 Nacos
    settings = get_settings()
    nacos_client = init_nacos(settings.nacos_server_addr, settings.nacos_namespace)

    try:
        nacos_client.register_service(
            service_name=settings.nacos_service_name,
            port=settings.nacos_service_port
        )
        logger.info(f"✅ AI Agent 服务已注册到 Nacos: {settings.nacos_service_name}")
    except Exception as e:
        logger.warning(f"⚠️ Nacos 注册失败（服务仍可独立运行）: {e}")

    yield

    # 关闭时：注销服务
    try:
        nacos_client.deregister_service(settings.nacos_service_name)
        logger.info("✅ 服务已从 Nacos 注销")
    except Exception as e:
        logger.warning(f"⚠️ Nacos 注销失败: {e}")


app = FastAPI(
    title="AI Agent 服务",
    version="1.0.0",
    lifespan=lifespan
)

# CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

# 注册路由
app.include_router(router, prefix="/api/ai")


@app.get("/")
async def root():
    return FileResponse("static/index.html")


@app.get("/health")
async def health():
    """健康检查"""
    return {"status": "ok", "service": "ai-agent"}


if __name__ == "__main__":
    settings = get_settings()
    uvicorn.run("main:app", host=settings.app_host, port=settings.app_port, reload=settings.debug)
