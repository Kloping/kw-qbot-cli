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
import top.kloping.api.dto.PetChangeData;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author github kloping
 * @date 2025/4/20-23:54
 */
@Entity
public class PetService implements StompFrameHandler {

    @AutoStandAfter
    public void r0(PetWebSocketClient client) {
        client.addRunnable(() -> {
            StompHeaders headers = new StompHeaders();
            headers.setDestination("/topic/pet");
            headers.setId("pet");
            headers.setHeartbeat(new long[]{10000L, 10000L});
            client.stompSession.subscribe(headers, PetService.this);
            logger.info("pet subscribe");
        });
    }

    @AutoStand
    Logger logger;

    @Override
    public @NotNull Type getPayloadType(StompHeaders headers) {
        return PetChangeData.class;
    }

    @AutoStand(id = "records")
    Map<Long, MessageEvent> records;

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        PetChangeData data = (PetChangeData) payload;
        MessageEvent messageEvent = records.get(data.getPid());
        if (messageEvent != null) {
            String sb;
            if (data.getIsChange()) {
                sb = data.getName() + "升级了\n" + "等级: +" + data.getLevel() + "\n"
                        + "经验: +" + data.getExp() + "\n" + "生命: +" + data.getHp() + "\n"
                        + "攻击: +" + data.getAttack() + "\n" + "防御: +"
                        + data.getDefense() + "\n" + "速度: +" + data.getSpeed();
            } else {
                sb = data.getName() + "获得了" + data.getExp() + "点经验";
            }
            CliMain.trySendTo(List.of(sb, Map.of(1, "宠物信息", 2, "背包", 3, "商城", 4, "我的宠物")), messageEvent);
        } else logger.error("当接收广播时未找到消息事件 " + JSON.toJSON(data));
    }
}
