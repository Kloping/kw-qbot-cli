package top.kloping;

import io.github.kloping.judge.Judge;
import io.github.kloping.spt.StarterObjectApplication;
import io.github.kloping.spt.annotations.AutoStand;
import io.github.kloping.spt.annotations.Bean;
import io.github.kloping.spt.annotations.ComponentScan;
import io.github.kloping.spt.entity.interfaces.Runner;
import io.github.kloping.spt.exceptions.NoRunException;
import net.mamoe.mirai.console.terminal.MiraiConsoleImplementationTerminal;
import net.mamoe.mirai.console.terminal.MiraiConsoleTerminalLoader;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import top.kloping.api.KwGameApi;
import top.kloping.config.LoggerImpl;
import top.kloping.controller.SelectController;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;

@ComponentScan("top.kloping")
public class CliMain implements ListenerHost, Runner {
    public static final MiraiConsoleImplementationTerminal TERMINAL = new MiraiConsoleImplementationTerminal(Paths.get("qbot"));
    public static final CliMain INSTANCE = new CliMain();
    private static boolean online = false;

    public static void main(String[] args) {
        io.github.kloping.common.Public.EXECUTOR_SERVICE.submit(() -> MiraiConsoleTerminalLoader.INSTANCE.startAsDaemon(TERMINAL));
        net.mamoe.mirai.event.GlobalEventChannel.INSTANCE.registerListenerHost(INSTANCE);
    }

    @AutoStand
    private static SelectController selectController;

    @net.mamoe.mirai.event.EventHandler
    public void on(net.mamoe.mirai.event.events.GroupMessageEvent event) {
        String text = event.getMessage().contentToString();
        Long sid = event.getSender().getId();
        APPLICATION.executeMethod(sid, text, event, sid);
        if (text.matches("\\d+")) {
            Object t = selectController.select(sid, Integer.valueOf(text));
            run(selectM, t, new Object[]{sid, text, event});
        }
        records.put(sid, event);
    }

    public static StarterObjectApplication APPLICATION;

    @net.mamoe.mirai.event.EventHandler
    public void on(net.mamoe.mirai.event.events.BotOnlineEvent event) {
        if (!online) {
            online = true;
            APPLICATION = new StarterObjectApplication(CliMain.class);
            APPLICATION.setAccessTypes(MessageEvent.class, Long.class);
            APPLICATION.setMainKey(Long.class);
            APPLICATION.setWaitTime(5000);
            APPLICATION.setAllAfter(INSTANCE);
            APPLICATION.addConfFile("./conf/conf.txt");
            APPLICATION.logger = LoggerImpl.INSTANCE;
            APPLICATION.logger.setPrefix("[kw-cli]");
            APPLICATION.logger.setLogLevel(1);
            APPLICATION.logger.setFormat(new SimpleDateFormat("yyy/MM/dd HH:mm:ss:SSS"));
            APPLICATION.run0(CliMain.class);
        }
    }

    @Override
    public void run(Method method, Object t, Object[] objects) throws NoRunException {
        if (t != null && Judge.isNotEmpty(t.toString())) {
            MessageEvent event = (MessageEvent) objects[2];
            sendToText(t, event);
        }
    }

    public static void sendToText(Object t, MessageEvent event) {
        MessageChainBuilder builder = new MessageChainBuilder();
        builder.append(new QuoteReply(event.getMessage()));
        builder.append(t.toString().trim());
        event.getSubject().sendMessage(builder.build());
    }

    static Method selectM;

    @Bean
    public RestTemplate restTemplate() {
        try {
            selectM = SelectController.class.getDeclaredMethod("select", Long.class, Integer.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        RestTemplate template = new RestTemplate();
        template.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(@NotNull ClientHttpResponse response) throws IOException {
                if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
                    return false;
                }
                return super.hasError(response);
            }
        });
        return (KwGameApi.TEMPLATE = template);
    }

    private static Map<Long, MessageEvent> records = null;

    @Bean("records")
    public Map<Long, MessageEvent> records() {
        return (records = new LinkedHashMap<>());
    }
}