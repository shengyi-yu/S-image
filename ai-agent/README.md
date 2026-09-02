# AI Agent 服务 - 多智能体架构

基于 **FastAPI + LangChain + Nacos** 的图片智能助手服务，采用多智能体架构。

## 架构设计

```
用户消息
    ↓
┌─────────────────┐
│  Router Agent   │  ← 意图识别 (DeepSeek)
└────────┬────────┘
         │
    ┌────┴────┐
    ↓         ↓
┌────────┐ ┌────────────┐
│ Chat   │ │ Image      │
│ Agent  │ │ Agent      │
│(DeepSeek)│ │(多模态模型)│
└────────┘ └────────────┘
```

## 项目结构

```
ai-agent/
├── main.py              # 服务入口
├── requirements.txt     # Python 依赖
├── .env.example         # 环境变量示例
├── config/
│   ├── __init__.py
│   └── settings.py      # 配置管理 (支持多模型配置)
├── services/
│   ├── __init__.py
│   ├── nacos_client.py  # Nacos 服务注册
│   └── agents/          # 多智能体模块
│       ├── __init__.py
│       ├── router.py    # Router Agent - 意图识别
│       ├── chat_agent.py # Chat Agent - 普通对话
│       ├── image_agent.py # Image Agent - 图片识别
│       └── orchestrator.py # 编排器 - 协调多个Agent
├── api/
│   ├── __init__.py
│   ├── routes.py        # API 路由
│   └── schemas.py       # 请求/响应模型
└── test_api.py          # API 测试脚本
```

## 快速开始

### 1. 安装依赖

```bash
cd ai-agent
pip install -r requirements.txt
```

### 2. 配置环境变量

复制 `.env.example` 为 `.env`，填入你的配置:

```bash
cp .env.example .env
# 编辑 .env 文件，填入 OPENAI_API_KEY 等
```

### 3. 启动服务

```bash
# 开发模式 (自动热重载)
python main.py

# 或者用 uvicorn
uvicorn main:app --reload --port 8000
```

### 4. 访问 API 文档

启动后打开浏览器:
- Swagger UI: http://localhost:8000/docs
- ReDoc: http://localhost:8000/redoc

### 5. 测试 API

```bash
python test_api.py
```

## API 接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/ai/health` | GET | 健康检查 |
| `/api/ai/chat` | POST | AI 对话 |
| `/api/ai/analyze` | POST | 图片分析 |
| `/api/ai/services` | GET | 查看注册服务 |

## 与 Java 服务集成

### Nacos 注册

启动后会自动注册到 Nacos，Java 端可通过服务名 `ai-agent-service` 发现本服务。

### Java 端调用示例

```java
// 使用 OpenFeign 调用
@FeignClient(name = "ai-agent-service")
public interface AiAgentClient {

    @PostMapping("/api/ai/chat")
    BaseResponse<String> chat(@RequestBody ChatRequest request);
}
```

## 多智能体设计亮点

### 1. Router Agent (意图路由)
- 使用轻量模型快速判断用户意图
- 支持结构化输出 (Pydantic)
- 低置信度时的降级策略

### 2. Chat Agent (对话处理)
- 集成 Tool 调用能力
- 支持对话历史
- 系统信息查询

### 3. Image Agent (图片识别)
- 多模态模型支持
- 结构化分析结果
- 标签自动生成

### 4. Orchestrator (编排器)
- 统一入口，自动路由
- 调用统计
- 错误降级处理

## 后续学习路线

### 第一阶段: 基础 (当前)
- [x] FastAPI 框架搭建
- [x] 多智能体架构设计
- [x] LangChain Agent 实现
- [x] 结构化输出
- [ ] Nacos 注册调通

### 第二阶段: LangChain 进阶
- [ ] Prompt Engineering (提示词工程)
- [ ] Chain 组合 (多步骤处理)
- [ ] Memory 管理 (对话记忆)
- [ ] Output Parser (输出解析)

### 第三阶段: LangGraph 工作流
- [ ] 状态图定义
- [ ] 条件分支
- [ ] 人机协作 (Human-in-the-loop)
- [ ] 并行处理

### 第四阶段: 实际功能
- [ ] 图片多模态分析 (GPT-4 Vision)
- [ ] 向量搜索 (Embedding + FAISS)
- [ ] 自然语言转 SQL
- [ ] 批量处理任务

## 常见问题

### Q: 没有 OpenAI Key 怎么办?
A: 可以用其他 LLM 替代:
- 通义千问: `base_url` 改为阿里云地址
- 智谱 GLM: 用 `langchain-community` 的集成
- 本地模型: 用 Ollama 部署

### Q: Nacos 连不上?
A: 本地开发可以忽略，服务正常运行。生产环境确保 Nacos 地址正确。

### Q: 如何调试 Agent?
A: 查看日志输出，或者用 `/docs` 页面在线测试。
