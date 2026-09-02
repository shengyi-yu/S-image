"""
Search Agent - 搜索智能体

职责: 搜索图片、搜索信息
使用 Tavily 搜索 API
"""
from typing import List, Dict
from tavily import TavilyClient
from config.settings import get_settings


class SearchResult:
    """搜索结果"""
    def __init__(self, title: str, url: str, content: str, score: float):
        self.title = title
        self.url = url
        self.content = content
        self.score = score


class SearchAgent:
    """
    搜索智能体

    使用 Tavily API 搜索图片和信息
    """

    def __init__(self):
        self.settings = get_settings()
        self.client = TavilyClient(api_key=self.settings.tavily_api_key)

    def search_sync(self, query: str, include_images: bool = False) -> Dict:
        """
        同步搜索（用于工具调用）

        Args:
            query: 搜索关键词
            include_images: 是否包含图片

        Returns:
            搜索结果
        """
        # 搜索
        results = self.client.search(
            query=query,
            include_images=include_images,
            max_results=5
        )

        return results

    def search_images_sync(self, query: str) -> List[str]:
        """
        同步搜索图片（用于工具调用）

        Args:
            query: 搜索关键词

        Returns:
            图片URL列表
        """
        results = self.search_sync(query, include_images=True)

        # 提取图片URL
        images = results.get("images", [])
        return images

    async def search(self, query: str, include_images: bool = False) -> Dict:
        """
        异步搜索

        Args:
            query: 搜索关键词
            include_images: 是否包含图片

        Returns:
            搜索结果
        """
        import asyncio
        loop = asyncio.get_event_loop()
        return await loop.run_in_executor(
            None,
            lambda: self.search_sync(query, include_images)
        )

    async def search_images(self, query: str) -> List[str]:
        """
        异步搜索图片

        Args:
            query: 搜索关键词

        Returns:
            图片URL列表
        """
        import asyncio
        loop = asyncio.get_event_loop()
        return await loop.run_in_executor(
            None,
            lambda: self.search_images_sync(query)
        )
