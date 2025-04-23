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
import top.kloping.api.dto.EquipSkill;
import top.kloping.api.dto.TaskStatus;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author github kloping
 * @date 2025/4/20-23:54
 */
@Entity
@Slf4j
public class EquipService implements StompFrameHandler {

    public EquipService(PetWebSocketClient client) {
        client.runnables.add(() -> {
            StompHeaders headers = new StompHeaders();
            headers.setDestination("/topic/equip");
            headers.setId("equip");
            headers.setHeartbeat(new long[]{10000L, 10000L});
            client.stompSession.subscribe(headers, EquipService.this);
        });
    }

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
            CliMain.trySendTo(List.of(sb, Map.of(1, "宠物装备")), messageEvent);
        } else log.error("当接收广播时未找到消息事件 {}", JSON.toJSON(payload));
    }
}
