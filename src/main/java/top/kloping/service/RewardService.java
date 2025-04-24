package top.kloping.service;

import com.alibaba.fastjson.JSON;
import io.github.kloping.spt.annotations.AutoStand;
import io.github.kloping.spt.annotations.Entity;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import top.kloping.CliMain;
import top.kloping.PetWebSocketClient;
import top.kloping.api.KwGameApi;
import top.kloping.api.dto.RewardItem;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author github kloping
 * @date 2025/4/20-23:54
 */
@Entity
@Slf4j
public class RewardService implements StompFrameHandler {

    public RewardService(PetWebSocketClient client) {
        client.runnables.add(() -> {
            StompHeaders headers = new StompHeaders();
            headers.setDestination("/topic/reward");
            headers.setId("reward");
            headers.setHeartbeat(new long[]{10000L, 10000L});
            client.stompSession.subscribe(headers, RewardService.this);
        });
    }

    @Override
    public @NotNull Type getPayloadType(StompHeaders headers) {
        return RewardItem.class;
    }

    @AutoStand(id = "records")
    Map<Long, MessageEvent> records;

    @AutoStand
    KwGameApi api;

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        RewardItem rewardItem = (RewardItem) payload;
        MessageEvent messageEvent = records.get(rewardItem.getPid());
        if (messageEvent != null) {
            StringBuilder sb = new StringBuilder("获得奖励:\n");
            for (int[] ints : rewardItem.getIdcount()) {
                sb.append("> ").append(api.toName(ints[0])).append("x").append(ints[1]).append("\n");
            }
            CliMain.trySendTo(sb.toString(), messageEvent);
        } else log.error("当接收广播时未找到消息事件 {}", JSON.toJSON(payload));
    }
}
