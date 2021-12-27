package com.tanhua.test;

import com.easemob.im.server.EMProperties;
import com.easemob.im.server.EMService;
import com.easemob.im.server.model.EMTextMessage;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class HuanXinTest {

    private EMService emService;

    @Before
    public void init() {
        EMProperties properties = EMProperties.builder()
                .setAppkey("1152190714098252#tanhua")
                .setClientId("YXA6Vlbh5fdgQtmfyvtPlxD6gg")
                .setClientSecret("YXA6vHEF17HLeSQnqvciBE5f5n7xQjs")
                .build();

        emService = new EMService(properties);
    }

    @Test
    public void test() {
        //emService.user().create("user02", "123456").block();
        //emService.contact().add("user01", "user02").block();

        Set<String> tos = new HashSet<>();
        tos.add("user02");

        emService.message().send(
                "user01",
                "users",
                tos,
                new EMTextMessage().text("测试消息"),
                null
        ).block();
    }
}
