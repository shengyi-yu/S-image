"""
API 路由
"""
from fastapi import APIRouter
from pydantic import BaseModel
from typing import Optional, List
from services.agents.router import RouterAgent
from services.agents.image_agent import ImageRecognitionAgent

router = APIRouter()

# 创建 Agent 单例
_router = None
_image_agent = None


def get_router() -> RouterAgent:
    """获取 RouterAgent 单例"""
    global _router
    if _router is None:
        _router = RouterAgent()
    return _router


def get_image_agent() -> ImageRecognitionAgent:
    """获取图片识别 Agent 单例"""
    global _image_agent
    if _image_agent is None:
        _image_agent = ImageRecognitionAgent()
    return _image_agent


# ========== 请求/响应模型 ==========

class ChatRequest(BaseModel):
    message: str
    thread_id: Optional[str] = "default"


class ChatResponse(BaseModel):
    code: int = 0
    message: str = "success"
    data: Optional[str] = None


class AnalyzeRequest(BaseModel):
    """图片分析请求（供 Java 端调用）"""
    image_url: str
    question: Optional[str] = None


class AnalyzeData(BaseModel):
    """图片分析结果"""
    description: str
    tags: List[str]
    objects: List[str]
    style: str
    scene: str


class AnalyzeResponse(BaseModel):
    code: int = 0
    message: str = "success"
    data: Optional[AnalyzeData] = None


# ========== 路由 ==========

@router.get("/health")
def health():
    """健康检查"""
    return {"status": "ok", "service": "ai-agent-service"}


@router.post("/chat", response_model=ChatResponse)
async def chat(request: ChatRequest):
    """
    AI 对话接口

    直接调用 RouterAgent 处理
    """
    router = get_router()
    response = await router.route(
        message=request.message,
        thread_id=request.thread_id
    )
    return ChatResponse(
        code=0,
        data=response
    )


@router.post("/analyze", response_model=AnalyzeResponse)
async def analyze(request: AnalyzeRequest):
    """
    图片分析接口（结构化 JSON，供 Java 端上传后调用）

    入参: image_url 图片地址, question 可选的分析问题
    出参: description/tags/objects/style/scene
    """
    image_agent = get_image_agent()
    try:
        result = await image_agent.analyze_image(
            image_url=request.image_url,
            question=request.question
        )
        return AnalyzeResponse(
            code=0,
            data=AnalyzeData(
                description=result.description,
                tags=result.tags,
                objects=result.objects,
                style=result.style,
                scene=result.scene
            )
        )
    except Exception as e:
        return AnalyzeResponse(
            code=1,
            message=f"图片分析失败: {str(e)}",
            data=None
        )
