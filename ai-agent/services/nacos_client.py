"""
Nacos 客户端 - 服务注册与发现

职责: 将 AI Agent 服务注册到 Nacos，实现服务发现
"""
import socket
import logging
from typing import Optional

logger = logging.getLogger(__name__)


class NacosClient:
    """
    Nacos 客户端

    功能:
    - 注册服务到 Nacos
    - 发现其他服务
    - 心跳保活
    """

    def __init__(self, server_addr: str, namespace: str = "public"):
        """
        初始化 Nacos 客户端

        Args:
            server_addr: Nacos 服务器地址 (ip:port)
            namespace: 命名空间
        """
        self.server_addr = server_addr
        self.namespace = namespace
        self.service_name = None
        self.service_ip = None
        self.service_port = None

    def get_local_ip(self) -> str:
        """获取本机 IP 地址"""
        try:
            # 创建一个 UDP 连接来获取本机 IP
            s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
            s.connect(("8.8.8.8", 80))
            ip = s.getsockname()[0]
            s.close()
            return ip
        except Exception:
            return "127.0.0.1"

    def register_service(self, service_name: str, port: int, group_name: str = "DEFAULT_GROUP") -> bool:
        """
        注册服务到 Nacos

        Args:
            service_name: 服务名称
            port: 服务端口
            group_name: 分组名称

        Returns:
            是否注册成功
        """
        import requests

        self.service_name = service_name
        self.service_port = port
        self.service_ip = self.get_local_ip()

        # 构建注册请求
        url = f"http://{self.server_addr}/nacos/v1/ns/instance"
        params = {
            "serviceName": service_name,
            "ip": self.service_ip,
            "port": port,
            "groupName": group_name,
            "namespaceId": self.namespace,
            "weight": 1.0,
            "enabled": "true",
            "healthy": "true",
            "ephemeral": "true"  # 临时实例，服务下线自动删除
        }

        try:
            response = requests.post(url, params=params, timeout=5)
            if response.status_code == 200 and response.text == "ok":
                logger.info(f"✅ 服务注册成功: {service_name} -> {self.service_ip}:{port}")
                return True
            else:
                logger.error(f"❌ 服务注册失败: {response.text}")
                return False
        except Exception as e:
            logger.error(f"❌ 服务注册异常: {e}")
            return False

    def deregister_service(self, service_name: str, group_name: str = "DEFAULT_GROUP") -> bool:
        """
        注销服务

        Args:
            service_name: 服务名称
            group_name: 分组名称

        Returns:
            是否注销成功
        """
        import requests

        if not self.service_ip or not self.service_port:
            logger.warning("服务未注册，无需注销")
            return True

        url = f"http://{self.server_addr}/nacos/v1/ns/instance"
        params = {
            "serviceName": service_name,
            "ip": self.service_ip,
            "port": self.service_port,
            "groupName": group_name,
            "namespaceId": self.namespace
        }

        try:
            response = requests.delete(url, params=params, timeout=5)
            if response.status_code == 200 and response.text == "ok":
                logger.info(f"✅ 服务注销成功: {service_name}")
                return True
            else:
                logger.error(f"❌ 服务注销失败: {response.text}")
                return False
        except Exception as e:
            logger.error(f"❌ 服务注销异常: {e}")
            return False

    def get_service_instances(self, service_name: str, group_name: str = "DEFAULT_GROUP") -> list:
        """
        获取服务实例列表

        Args:
            service_name: 服务名称
            group_name: 分组名称

        Returns:
            实例列表 [{"ip": "xxx", "port": 8080}, ...]
        """
        import requests

        url = f"http://{self.server_addr}/nacos/v1/ns/instance/list"
        params = {
            "serviceName": service_name,
            "groupName": group_name,
            "namespaceId": self.namespace
        }

        try:
            response = requests.get(url, params=params, timeout=5)
            if response.status_code == 200:
                data = response.json()
                hosts = data.get("hosts", [])
                return [{"ip": h["ip"], "port": h["port"]} for h in hosts if h.get("healthy")]
            return []
        except Exception as e:
            logger.error(f"❌ 获取服务实例失败: {e}")
            return []


# 全局客户端实例
_nacos_client: Optional[NacosClient] = None


def get_nacos_client() -> Optional[NacosClient]:
    """获取 Nacos 客户端实例"""
    return _nacos_client


def init_nacos(server_addr: str, namespace: str = "public") -> NacosClient:
    """
    初始化 Nacos 客户端

    Args:
        server_addr: Nacos 服务器地址
        namespace: 命名空间

    Returns:
        Nacos 客户端实例
    """
    global _nacos_client
    _nacos_client = NacosClient(server_addr, namespace)
    return _nacos_client
