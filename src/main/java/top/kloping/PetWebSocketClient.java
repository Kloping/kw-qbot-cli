package top.kloping;

import com.alibaba.fastjson.support.spring.messaging.MappingFastJsonMessageConverter;
import io.github.kloping.common.Public;
import io.github.kloping.spt.PartUtils;
import io.github.kloping.spt.annotations.AutoStand;
import io.github.kloping.spt.annotations.AutoStandAfter;
import io.github.kloping.spt.annotations.Entity;
import io.github.kloping.spt.interfaces.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import top.kloping.api.KwGameApi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Entity
public class PetWebSocketClient extends StompSessionHandlerAdapter implements Runnable {

    @AutoStand(id = "server.ip")
    String server_ip;

    @AutoStand(id = "server.port")
    Integer server_port;

    @AutoStand(id = "auth.key")
    String key;

    @AutoStandAfter
    public void after() {
        KwGameApi.URL = "http://" + server_ip + ":" + server_port;
    }

    public StompSession stompSession;
    public StandardWebSocketClient webSocketClient;
    public WebSocketStompClient stompClient;
    public List<Runnable> runnables = new ArrayList<>();

    public void addRunnable(Runnable runnable) {
        runnables.add(runnable);
        if (stompSession != null && stompSession.isConnected()) {
            runnable.run();
        }
    }

    @Override
    public void run() {
        webSocketClient = new StandardWebSocketClient();
        stompClient = new WebSocketStompClient(webSocketClient);
        stompClient.setMessageConverter(new MappingFastJsonMessageConverter());
        try {
            tryConnect();
        } catch (Exception e) {
            logger.error("链接失败:" + PartUtils.getExceptionLine(e));
        }
    }

    public void tryConnect() throws InterruptedException, ExecutionException {
        if (stompSession == null || !stompSession.isConnected()) {
            String url = String.format("ws://%s:%s/ws", server_ip, server_port);
            WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
            headers.add("auth", key);
            StompHeaders headers1 = new StompHeaders();
            headers1.add("auth", key);
            ListenableFuture<StompSession> future = stompClient.connect(url, headers, headers1, this);
            stompSession = future.get();
            logger.log("首次已链接!");
            stompSession.send("/app/hello", "成功链接!");
            runnables.forEach(Runnable::run);
        }
    }

    public PetWebSocketClient() {
        CliMain.APPLICATION.INSTANCE.getSTARTED_RUNNABLE().add(() -> Public.EXECUTOR_SERVICE.submit(this));
    }

    @Override
    public void afterConnected(StompSession stompSession, @NotNull StompHeaders connectedHeaders) {
        logger.info("Connected to WebSocket Server!");
    }

    @AutoStand
    Logger logger;

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        logger.error("ws error for \n" + PartUtils.getExceptionLine(exception));
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        logger.error("WebSocket 远程关闭 20秒后尝试重连");
        Public.EXECUTOR_SERVICE.submit(() -> {
            try {
                TimeUnit.SECONDS.sleep(20);
                tryConnect();
            } catch (Exception e) {
                logger.error("链接失败:" + e.getLocalizedMessage());
            }
        });
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        logger.waring("main websocket handler recv: " + payload);
    }
}