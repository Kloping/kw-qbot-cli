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
import top.kloping.api.dto.TaskStatus;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author github kloping
 * @date 2025/4/20-23:54
 */
@Entity
public class TaskService implements StompFrameHandler {

    @AutoStandAfter
    public void r0(PetWebSocketClient client) {
        client.addRunnable(() -> {
            StompHeaders headers = new StompHeaders();
            headers.setDestination("/topic/tasks");
            headers.setId("task");
            headers.setHeartbeat(new long[]{10000L, 10000L});
            client.stompSession.subscribe(headers, TaskService.this);
            logger.info("task subscribe");
        });
    }
    @Override
    public @NotNull Type getPayloadType(StompHeaders headers) {
        return TaskStatus.class;
    }

    @AutoStand(id = "records")
    Map<Long, MessageEvent> records;

    @AutoStand
    Logger logger;

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        TaskStatus taskStatus = (TaskStatus) payload;
        MessageEvent messageEvent = records.get(taskStatus.getPid());
        if (messageEvent != null) {
            StringBuilder sb = new StringBuilder("任务\"").append(taskStatus.getName()).append("\"完成")
                    .append("\n").append(taskStatus.getDesc())
                    .append("\n当前进度:").append(taskStatus.getNum()).append("/").append(taskStatus.getCnum())
                    .append("\n奖励:").append(taskStatus.getReward()).append("\n已解锁下一任务");
            CliMain.trySendTo(List.of(sb, Map.of(1, "当前任务")), messageEvent);
        } else logger.error("当接收广播时未找到消息事件 " + JSON.toJSON(payload));
    }
}
