"""
Chat Agent - 对话智能体

最简单的版本，先跑通
"""
from langchain_openai import ChatOpenAI
from langchain_core.messages import HumanMessage, SystemMessage
from config.settings import get_settings


class ChatAgent:
    """对话智能体"""

    def __init__(self):
        self.settings = get_settings()

        # TODO 1: 初始化 LLM
        # self.llm = ...
        self.llm = ChatOpenAI(
            model=self.settings.deepseek_model,
            api_key=self.settings.deepseek_api_key,
            base_url=self.settings.deepseek_base_url
        )
        # TODO 2: 系统提示词
        # self.system_prompt = ...
        self.system_prompt = """你是一个智能助手根据他人的情感进行夸奖和温暖"""

    def chat_sync(self, message: str) -> str:
        """
        同步处理对话（用于工具调用）

        Args:
            message: 用户消息

        Returns:
            回复内容
        """
        messages = [
            SystemMessage(content=self.system_prompt),
            HumanMessage(content=message)
        ]
        response = self.llm.invoke(messages)
        return response.content

    async def chat(self, message: str) -> str:
        """
        处理对话

        Args:
            message: 用户消息

        Returns:
            回复内容
        """
        # TODO 4: 构建消息列表
        # messages = [...]
        messages = []
        messages.append(SystemMessage(content=self.system_prompt))
        messages.append(HumanMessage(content=message))
        # TODO 5: 调用 LLM
        # response = ...
        response = await self.llm.ainvoke(messages)

        # TODO 7: 返回回复
        # return ...
        return response.content
