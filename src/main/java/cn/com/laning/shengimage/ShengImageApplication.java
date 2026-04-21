package cn.com.laning.shengimage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("cn.com.laning.shengimage.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class ShengImageApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShengImageApplication.class, args);
    }

}
