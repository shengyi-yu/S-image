"""
测试 ImageAgent
"""
import asyncio
from services.agents.image_agent import ImageRecognitionAgent


async def test():
    # 创建 ImageAgent
    agent = ImageRecognitionAgent()

    # 测试图片URL
    image_url = "https://picsum.photos/id/237/200/300"

    print("=== 测试图片分析 ===")
    print(f"图片URL: {image_url}")
    print()

    # 分析图片
    result = await agent.analyze_image(image_url)
    print("分析结果:")
    print(f"  描述: {result.description}")
    print(f"  标签: {result.tags}")
    print(f"  物体: {result.objects}")
    print(f"  风格: {result.style}")
    print(f"  场景: {result.scene}")
    print()

    # 生成标签
    print("=== 测试标签生成 ===")
    tags = await agent.generate_tags(image_url)
    print(f"标签: {tags}")


if __name__ == "__main__":
    asyncio.run(test())
