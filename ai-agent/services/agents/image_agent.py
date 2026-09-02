"""
Image Recognition Agent - 图片识别智能体

职责: 图片内容分析、标签生成、物体识别
使用多模态模型 (mimo-v2.5)
"""
from typing import List
from pydantic import BaseModel, Field
from langchain_openai import ChatOpenAI
from langchain_core.messages import HumanMessage, SystemMessage
from config.settings import get_settings


class ImageAnalysisResult(BaseModel):
    """图片分析结果"""
    description: str = Field(description="图片描述")
    tags: List[str] = Field(description="标签列表")
    objects: List[str] = Field(description="识别到的物体")
    style: str = Field(description="图片风格")
    scene: str = Field(description="场景描述")


class ImageRecognitionAgent:
    """
    图片识别智能体

    使用 mimo-v2.5 多模态模型分析图片
    """

    def __init__(self):
        self.settings = get_settings()
        self.llm = ChatOpenAI(
            model=self.settings.vision_model,
            api_key=self.settings.vision_api_key,
            base_url=self.settings.vision_base_url,
            max_completion_tokens=1024,
            default_headers={"Authorization": f"Bearer {self.settings.vision_api_key}"}
        )
        self.system_prompt = """你是一名图片分析员。
请分析图片内容，并以JSON格式返回结果：
{
    "description": "图片描述",
    "tags": ["标签1", "标签2"],
    "objects": ["物体1", "物体2"],
    "style": "图片风格",
    "scene": "场景描述"
}
只返回JSON，不要返回其他内容。"""

    def analyze_image_sync(self, image_url: str, question: str = None) -> ImageAnalysisResult:
        """
        同步分析图片内容（用于工具调用）

        Args:
            image_url: 图片URL
            question: 可选的特定问题

        Returns:
            分析结果
        """
        import json

        # 构建多模态消息
        message = HumanMessage(content=[
            {"type": "text", "text": question or "请分析这张图片"},
            {"type": "image_url", "image_url": {"url": image_url}}
        ])

        # 调用 LLM（同步）
        response = self.llm.invoke([
            SystemMessage(content=self.system_prompt),
            message
        ])

        # 解析 JSON结果（带错误处理）
        try:
            # 清理响应内容，可能包含 markdown 代码块
            content = response.content.strip()
            if content.startswith("```"):
                # 去掉 ```json 和 ```
                content = content.split("\n", 1)[1] if "\n" in content else content[3:]
                if content.endswith("```"):
                    content = content[:-3]
                content = content.strip()

            result = json.loads(content)
            return ImageAnalysisResult(**result)
        except json.JSONDecodeError:
            # JSON 解析失败，返回默认结果
            return ImageAnalysisResult(
                description=response.content,
                tags=["解析失败"],
                objects=[],
                style="未知",
                scene="未知"
            )

    async def analyze_image(self, image_url: str, question: str = None) -> ImageAnalysisResult:
        """
        异步分析图片内容

        Args:
            image_url: 图片URL
            question: 可选的特定问题

        Returns:
            分析结果
        """
        # 用同步方法包装成异步
        import asyncio
        loop = asyncio.get_event_loop()
        return await loop.run_in_executor(
            None,
            lambda: self.analyze_image_sync(image_url, question)
        )

    def generate_tags_sync(self, image_url: str) -> List[str]:
        """
        同步生成标签

        Args:
            image_url: 图片URL

        Returns:
            标签列表
        """
        result = self.analyze_image_sync(image_url)
        return result.tags

    async def generate_tags(self, image_url: str) -> List[str]:
        """
        异步生成标签

        Args:
            image_url: 图片URL

        Returns:
            标签列表
        """
        result = await self.analyze_image(image_url)
        return result.tags
