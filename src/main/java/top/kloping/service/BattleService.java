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
import top.kloping.api.SrcRegistry;
import top.kloping.api.dto.BattleStatus;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author github kloping
 * @date 2025/4/20-23:54
 */
@Entity
@Slf4j
public class BattleService implements StompFrameHandler {

    public BattleService(PetWebSocketClient client) {
        client.runnables.add(() -> {
            StompHeaders headers = new StompHeaders();
            headers.setDestination("/topic/battle");
            headers.setId("battle");
            headers.setHeartbeat(new long[]{10000L, 10000L});
            client.stompSession.subscribe(headers, BattleService.this);
        });
    }

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
        List<Object> list = new ArrayList();
        if (messageEvent != null) {
            list.add(battleStatus.getTips() + "\n位置 名字 血量\n");
            for (Integer loc : battleStatus.getMonsters()) {
                BattleStatus.Character v = battleStatus.getLocationMap().get(loc);
                list.add(registry.getImage(v.getId()));
                StringBuilder sb = new StringBuilder();
                sb.append(getN(loc)).append(".").append(v.getName()).append(" Lv.").append(v.getLevel())
                        .append(v.getHpc() >= 0 ? " \t+" : " \t").append(v.getHpc()).append("\n")
                        .append(KwGameApi.getProgressBar(v.getHpb(), 100, 9, "⬜", "\uD83D\uDFE6"))
                        .append("\n");
                String bufftips = v.getBufftips();
                if (bufftips != null && !bufftips.isEmpty()) {
                    sb.append(bufftips).append("\n");
                }
                list.add(sb.toString());
            }
            list.add("------VS-------\n");
            for (Integer pet : battleStatus.getPets()) {
                BattleStatus.Character v = battleStatus.getLocationMap().get(pet);
                list.add(registry.getImage(v.getId() + "_" + v.getLevel()));
                StringBuilder sb = new StringBuilder();
                sb.append(getN(pet)).append(".").append(v.getName()).append(" Lv.").append(v.getLevel())
                        .append(v.getHpc() >= 0 ? " \t+" : " \t").append(v.getHpc()).append("\n")
                        .append(KwGameApi.getProgressBar(v.getHpb(), 100, 9, "⬜", "\uD83D\uDFE9")).append("\n");
                String bufftips = v.getBufftips();
                if (bufftips != null && !bufftips.isEmpty()) {
                    sb.append(bufftips).append("\n");
                }
                list.add(sb.toString());
            }
            StringBuilder sb = new StringBuilder();
            sb.append("------行动值------");
            battleStatus.getActionQueue().forEach((k, loc) -> {
                BattleStatus.Character v = battleStatus.getLocationMap().get(loc);
                sb.append("\n> ").append(k).append(" ").append(v.getName()).append(v.getControlled() > 0 ? "(被控)" : "");
            });
            list.add(sb.toString());
            sb.delete(0, sb.length());
            sb.append("\n------行动记录------");
            battleStatus.getRecords().forEach(v -> {
                sb.append("\n").append(v.getBname()).append(" ").append(v.getOpt()).append(" <").append(v.getAname());
            });
            list.add(sb.toString());
            Map<Integer, String> opts = new HashMap<>();
            int i = 1;
            for (String mayopt : battleStatus.getMayopts()) {
                opts.put(i++, mayopt);
            }
            list.add(opts);
            CliMain.trySendTo(list, messageEvent);
        } else log.error("当接收广播时未找到消息事件 {}", JSON.toJSON(payload));
    }

    private String getN(Integer k) {
        return String.valueOf((char) ((char) 9311 + k));
    }
}
