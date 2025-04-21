package top.kloping.service;

import io.github.kloping.spt.annotations.AutoStand;
import io.github.kloping.spt.annotations.Entity;
import net.mamoe.mirai.event.events.MessageEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import top.kloping.CliMain;
import top.kloping.PetWebSocketClient;
import top.kloping.api.dto.PetChangeData;
import top.kloping.controller.ItemController;
import top.kloping.controller.PetController;
import top.kloping.controller.SelectController;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author github kloping
 * @date 2025/4/20-23:54
 */
@Entity
public class PetService implements StompFrameHandler {

    public PetService(PetWebSocketClient client) {
        client.runnables.add(() -> client.stompSession.subscribe("/topic/pet", PetService.this));
    }

    @Override
    public @NotNull Type getPayloadType(StompHeaders headers) {
        return PetChangeData.class;
    }

    @AutoStand(id = "records")
    Map<Long, MessageEvent> records;

    @AutoStand
    SelectController selectController;

    @AutoStand
    PetController petController;

    @AutoStand
    ItemController itemController;

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        PetChangeData data = (PetChangeData) payload;
        MessageEvent messageEvent = records.get(data.getId());
        if (messageEvent != null) {
            String sb = getRes(data);
            CliMain.sendToText(sb, messageEvent);
            selectController.register(data.getId(), i -> {
                switch (i) {
                    case 1:
                        return petController.info(data.getId(), "", messageEvent);
                    case 2:
                        return itemController.list(data.getId());
                    case 3:
                        return itemController.shop(data.getId());
                    case 4:
                        return petController.list(data.getId());
                }
                return null;
            });
        }
    }

    private static @NotNull String getRes(PetChangeData data) {
        String sb;
        if (data.getIsChange()) {
            sb = data.getName() + "升级了\n" +
                    "等级: +" + data.getLevel() + "\n" +
                    "经验: +" + data.getExp() + "\n" +
                    "生命: +" + data.getHp() + "\n" +
                    "攻击: +" + data.getAttack() + "\n" +
                    "防御: +" + data.getDefense() + "\n" +
                    "速度: +" + data.getSpeed() + "\n";
        } else {
            sb = data.getName() + "获得了" + data.getExp() + "点经验";
        }
        sb = sb + "\n\n1.宠物信息  2.背包\n3.商城  4.我的宠物";
        return sb;
    }
}
