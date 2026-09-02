"""
Router Agent - 路由智能体

职责: 识别用户意图，派发给对应的 Agent
"""
from typing import Literal
from langchain_openai import ChatOpenAI
from langchain_core.messages import SystemMessage, HumanMessage
from langchain.agents import create_agent
from langchain.tools import tool
from langgraph.checkpoint.memory import InMemorySaver
from config.settings import get_settings
from services.agents.chat_agent import ChatAgent
from services.agents.search_agent import SearchAgent
from services.agents.image_agent import ImageRecognitionAgent


# ========== 全局工具定义 ==========

# 创建子Agent实例（模块级单例）
_chat_agent = ChatAgent()
_search_agent = SearchAgent()
_image_agent = ImageRecognitionAgent()


@tool
def chat(message: str) -> str:
    """普通对话、问答、聊天"""
    return _chat_agent.chat_sync(message)


@tool
def search_image(query: str) -> str:
    """搜索图片、找图片，输入搜索关键词如：猫、风景、美食"""
    try:
        images = _search_agent.search_images_sync(query)
        if images:
            result = f"找到 {len(images)} 张图片：\n"
            for i, url in enumerate(images, 1):
                result += f"{i}. {url}\n"
            return result
        return f"没有找到 '{query}' 相关的图片"
    except Exception as e:
        return f"搜索图片失败：{str(e)}"


@tool
def analyze_image(image_url: str) -> str:
    """分析图片内容、识别图片，输入图片的URL地址"""
    try:
        result = _image_agent.analyze_image_sync(image_url)
        return f"图片分析结果：\n- 描述：{result.description}\n- 标签：{', '.join(result.tags)}\n- 场景：{result.scene}\n- 风格：{result.style}"
    except Exception as e:
        return f"图片分析失败：{str(e)}"


# 工具列表（全局定义，只创建一次）
TOOLS = [chat, search_image, analyze_image]


# ========== Router Agent ==========

class RouterAgent:
    """
    主智能体

    职责：协调子 Agent，管理对话记忆
    """

    def __init__(self):
        self.settings = get_settings()

        self.llm = ChatOpenAI(
            model=self.settings.deepseek_model,
            api_key=self.settings.deepseek_api_key,
            base_url=self.settings.deepseek_base_url
        )

        self.system_prompt = """你是一个智能助手，可以帮用户：
- 普通对话（使用 chat 工具）
- 搜索图片（使用 search_image 工具）
- 分析图片（使用 analyze_image 工具）

不论对方使用什么语言，请用中文回答问题。
根据用户需求，调用合适的工具完成任务。"""

        # 创建 Agent（只创建一次）
        self.agent = create_agent(
            self.llm,
            TOOLS,
            system_prompt=self.system_prompt,
            checkpointer=InMemorySaver(),
        )
        

    async def route(self, message: str, thread_id: str = "default") -> str:
        """
        处理用户请求

        Args:
            message: 用户消息
            thread_id: 会话ID（用于区分不同用户的对话）

        Returns:
            回复内容
        """
        config = {"configurable": {"thread_id": thread_id}}
        response = await self.agent.ainvoke(
            {"messages": [HumanMessage(content=message)]},
            config=config
        )
        return response["messages"][-1].content
