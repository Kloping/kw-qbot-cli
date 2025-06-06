package top.kloping.service;

import com.alibaba.fastjson.JSON;
import io.github.kloping.spt.annotations.AutoStand;
import io.github.kloping.spt.annotations.AutoStandAfter;
import io.github.kloping.spt.annotations.Entity;
import io.github.kloping.spt.interfaces.Logger;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import top.kloping.CliMain;
import top.kloping.PetWebSocketClient;
import top.kloping.api.KwGameApi;
import top.kloping.api.dto.ChooseOptions;
import top.kloping.controller.SelectController;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author github kloping
 * @date 2025/4/20-23:54
 */
@Entity
public class ChooseService implements StompFrameHandler {

    @AutoStandAfter
    public void r0(PetWebSocketClient client) {
        client.addRunnable(() -> {
            StompHeaders headers = new StompHeaders();
            headers.setDestination("/topic/choose");
            headers.setId("choose");
            headers.setHeartbeat(new long[]{10000L, 10000L});
            client.stompSession.subscribe(headers, ChooseService.this);
            logger.info("choose subscribe");
        });
    }

    @AutoStand
    Logger logger;

    @Override
    public @NotNull Type getPayloadType(StompHeaders headers) {
        return ChooseOptions.class;
    }

    @AutoStand(id = "records")
    Map<Long, MessageEvent> records;

    @AutoStand
    SelectController selectController;

    @AutoStand
    KwGameApi api;

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        ChooseOptions chooseOptions = (ChooseOptions) payload;
        MessageEvent messageEvent = records.get(chooseOptions.getPid());
        if (messageEvent != null) {
            StringBuilder sb = new StringBuilder("必选!!! 数字选择").append(chooseOptions.getTips()).append("\n");
            int i = 1;
            Map<Integer, String> st2url = new HashMap<>();
            for (ChooseOptions.Option option : chooseOptions.getList()) {
                if (i % 2 == 1) sb.append("\n");
                else sb.append("  ");
                st2url.put(i, option.getUrl());
                sb.append(i++).append(".").append(option.getName());
            }
            selectController.register(false, chooseOptions.getPid()
                    , n -> api.doGetAbs(st2url.get(n) + "&pid=" + chooseOptions.getPid()).getBody());
            CliMain.trySendTo(sb, messageEvent);
        } else logger.error("当接收广播时未找到消息事件 " + JSON.toJSON(payload));
    }
}
