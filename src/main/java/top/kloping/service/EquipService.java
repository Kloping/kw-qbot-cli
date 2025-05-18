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
import top.kloping.api.dto.EquipSkill;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author github kloping
 * @date 2025/4/20-23:54
 */
@Entity
public class EquipService implements StompFrameHandler {

    @AutoStandAfter
    public void r0(PetWebSocketClient client) {
        client.addRunnable(() -> {
            StompHeaders headers = new StompHeaders();
            headers.setDestination("/topic/equip");
            headers.setId("equip");
            headers.setHeartbeat(new long[]{10000L, 10000L});
            client.stompSession.subscribe(headers, EquipService.this);
            logger.info("equip subscribe");
        });
    }

    @AutoStand
    Logger logger;

    @Override
    public @NotNull Type getPayloadType(StompHeaders headers) {
        return EquipSkill.class;
    }

    @AutoStand(id = "records")
    Map<Long, MessageEvent> records;

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        EquipSkill equipSkill = (EquipSkill) payload;
        MessageEvent messageEvent = records.get(equipSkill.getPid());
        if (messageEvent != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(equipSkill.getPetName()).append("习得技能[")
                    .append(equipSkill.getName()).append("]")
                    .append("已装备在").append(equipSkill.getOpt()).append("号技能位");
            CliMain.trySendTo(List.of(sb, Map.of(1, "宠物技能")), messageEvent);
        } else logger.error("当接收广播时未找到消息事件 " + JSON.toJSON(payload));
    }
}
