"""
项目配置
"""
from pydantic_settings import BaseSettings
from functools import lru_cache


class Settings(BaseSettings):
    """应用配置"""

    # DeepSeek 配置 (对话用)
    deepseek_api_key: str = ""
    deepseek_base_url: str = "https://api.deepseek.com"
    deepseek_model: str = "deepseek-v4-flash"

    # 多模态模型配置 (图片识别用 mimo-v2.5)
    vision_api_key: str = ""
    vision_base_url: str = "https://token-plan-cn.xiaomimimo.com/v1"
    vision_model: str = "mimo-v2.5"

    # Tavily 搜索配置
    tavily_api_key: str = ""

    # 服务配置
    app_host: str = "0.0.0.0"
    app_port: int = 8000
    debug: bool = True

    # Nacos 配置 (可选)
    nacos_server_addr: str = "localhost:8848"
    nacos_namespace: str = "public"
    nacos_service_name: str = "ai-agent-service"
    nacos_service_port: int = 8000

    class Config:
        env_file = ".env"
        env_file_encoding = "utf-8"


@lru_cache()
def get_settings() -> Settings:
    return Settings()
