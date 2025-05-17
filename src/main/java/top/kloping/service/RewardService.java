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
import top.kloping.api.dto.RewardItem;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author github kloping
 * @date 2025/4/20-23:54
 */
@Entity
public class RewardService implements StompFrameHandler {

    @AutoStandAfter
    public void r0(PetWebSocketClient client) {
        client.addRunnable(() -> {
            StompHeaders headers = new StompHeaders();
            headers.setDestination("/topic/reward");
            headers.setId("reward");
            headers.setHeartbeat(new long[]{10000L, 10000L});
            client.stompSession.subscribe(headers, RewardService.this);
            logger.info("reward subscribe");
        });
    }
    @Override
    public @NotNull Type getPayloadType(StompHeaders headers) {
        return RewardItem.class;
    }

    @AutoStand(id = "records")
    Map<Long, MessageEvent> records;

    @AutoStand
    Logger logger;

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        RewardItem rewardItem = (RewardItem) payload;
        MessageEvent messageEvent = records.get(rewardItem.getPid());
        StringBuilder sb = new StringBuilder();
        sb.append(rewardItem.isWin() ? "✔\uFE0F" : "❌");
        if (messageEvent != null) {
            sb.append(rewardItem.getTips());
            CliMain.trySendTo(sb.toString(), messageEvent);
        } else logger.error("当接收广播时未找到消息事件 " + JSON.toJSON(payload));
    }
}
