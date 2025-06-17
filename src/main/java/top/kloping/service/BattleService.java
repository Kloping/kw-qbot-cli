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
import top.kloping.api.SrcRegistry;
import top.kloping.api.dto.BattleStatus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author github kloping
 * @date 2025/4/20-23:54
 */
@Entity
public class BattleService implements StompFrameHandler {

    @AutoStandAfter
    public void r0(PetWebSocketClient client) {
        client.addRunnable(() -> {
            StompHeaders headers = new StompHeaders();
            headers.setDestination("/topic/battle");
            headers.setId("battle");
            headers.setHeartbeat(new long[]{10000L, 10000L});
            client.stompSession.subscribe(headers, BattleService.this);
            logger.info("battle subscribe");
        });
    }

    @AutoStand
    Logger logger;

    @Override
    public @NotNull Type getPayloadType(StompHeaders headers) {
        return BattleStatus.class;
    }

    @AutoStand(id = "records")
    Map<Long, MessageEvent> records;

    @AutoStand
    SrcRegistry registry;

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        BattleStatus battleStatus = (BattleStatus) payload;
        MessageEvent messageEvent = records.get(battleStatus.getPid());
        List<Object> list = new ArrayList<>();
        if (messageEvent != null) {
            list.add(battleStatus.getTips() + "\n位置 名字 血量\n");
            int x = 1;
            for (Integer loc : battleStatus.getMonsters()) {
                BattleStatus.Character v = battleStatus.getLocationMap().get(loc);
                if (x == 1) {
                    x = 2;
                    list.add(registry.getImage(v.getId()));
                }
                loc2view(loc, v, "\uD83D\uDFE6", list);
            }
            list.add("------VS-------\n");
            for (Integer pet : battleStatus.getPets()) {
                BattleStatus.Character v = battleStatus.getLocationMap().get(pet);
                if (x == 2) {
                    x = 3;
                    list.add(registry.getImage(v.getId() + "_" + v.getLevel()));
                }
                loc2view(pet, v, "\uD83D\uDFE9", list);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("------行动值------");
            battleStatus.getSortedActionQueue().forEach((loc, k) -> {
                BattleStatus.Character v = battleStatus.getLocationMap().get(loc);
                sb.append("\n> ").append(k).append(" ").append(v.getName()).append(v.getControlled() > 0 ? "(被控)" : "");
            });
            list.add(sb.toString());
            sb.delete(0, sb.length());
            sb.append("\n------行动记录------");
            AtomicInteger size = new AtomicInteger(1);
            final int max = 7;
            battleStatus.getRecords().forEach(v -> {
                if (size.getAndIncrement() > max) return;
                sb.append("\n").append(v.getBname()).append(" ").append(v.getOpt()).append(" <").append(v.getAname());
            });
            list.add(sb.toString());
            //添加选项
            Map<Integer, String> opts = new HashMap<>();
            int index = 1;
            for (String mayopt : battleStatus.getMayopts()) {
                opts.put(index++, mayopt);
            }
            list.add(opts);
            CliMain.trySendTo(list, messageEvent);
        } else logger.error("当接收广播时未找到消息事件 " + JSON.toJSON(payload));
    }

    private void loc2view(Integer loc, BattleStatus.Character v, String filledChar, List<Object> list) {
        StringBuilder sb = new StringBuilder();
        sb.append(getN(loc)).append(".").append(v.getName()).append(" Lv.").append(v.getLevel())
                .append(" ").append("\uD83D\uDC9A".repeat(v.getLifes()))
                .append(v.getHpc() >= 0 ? " \t+" : " \t").append(v.getHpc()).append("hp\n")
                .append(KwGameApi.getProgressBar(v.getHpb(), 100, 9, "⬜", filledChar));

        if (v.getShieldb() > 0)
            sb.append("\uD83D\uDEE1\uFE0F: ").append(v.getShieldb()).append("%");
        sb.append("\n");

        String bufftips = v.getBufftips();
        if (bufftips != null && !bufftips.isEmpty()) {
            sb.append(bufftips).append("\n");
        }
        list.add(sb.toString());
    }

    private String getN(Integer k) {
        return String.valueOf((char) ((char) 9311 + k));
    }
}
