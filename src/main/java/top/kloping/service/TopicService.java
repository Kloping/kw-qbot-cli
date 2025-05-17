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
import top.kloping.api.dto.TopicData;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author github kloping
 * @date 2025/4/20-23:54
 */
@Entity
public class TopicService implements StompFrameHandler {

    @AutoStandAfter
    public void r0(PetWebSocketClient client) {
        client.addRunnable(() -> {
            StompHeaders headers = new StompHeaders();
            headers.setDestination("/topic/topic");
            headers.setId("topic");
            headers.setHeartbeat(new long[]{10000L, 10000L});
            client.stompSession.subscribe(headers, TopicService.this);
            logger.info("topic subscribe");
        });
    }

    @Override
    public @NotNull Type getPayloadType(StompHeaders headers) {
        return TopicData.class;
    }

    @AutoStand(id = "records")
    Map<Long, MessageEvent> records;

    @AutoStand
    Logger logger;

    public void handleFrame(StompHeaders headers, Object payload) {
        TopicData topicData = (TopicData) payload;
        Long pid = topicData.getPid();
        MessageEvent messageEvent = records.get(pid);
        if (messageEvent != null) CliMain.trySendTo(topicData.getTips(), messageEvent);
        else logger.error("当接收广播时未找到消息事件 " + JSON.toJSON(payload));
    }
}
