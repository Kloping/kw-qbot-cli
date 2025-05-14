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
import top.kloping.api.KwGameConvertApi;
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
    KwGameConvertApi api;

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        RewardItem rewardItem = (RewardItem) payload;
        MessageEvent messageEvent = records.get(rewardItem.getPid());
        StringBuilder sb = new StringBuilder();
        sb.append(rewardItem.isWin() ? "对局获胜\n" : "对局未成功\n");
        if (messageEvent != null) {
            sb.append(rewardItem.getTips());
            CliMain.trySendTo(sb.toString(), messageEvent);
        } else log.error("当接收广播时未找到消息事件 {}", JSON.toJSON(payload));
    }
}
