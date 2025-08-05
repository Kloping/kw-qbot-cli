package top.kloping;

import io.github.kloping.judge.Judge;
import io.github.kloping.spt.StarterObjectApplication;
import io.github.kloping.spt.annotations.AutoStand;
import io.github.kloping.spt.annotations.Bean;
import io.github.kloping.spt.annotations.ComponentScan;
import io.github.kloping.spt.entity.interfaces.Runner;
import io.github.kloping.spt.exceptions.NoRunException;
import io.github.kloping.url.UrlUtils;
import net.mamoe.mirai.console.terminal.MiraiConsoleImplementationTerminal;
import net.mamoe.mirai.console.terminal.MiraiConsoleTerminalLoader;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import top.kloping.api.KwGameApi;
import top.kloping.chatai.ChatQuestion;
import top.kloping.config.LoggerImpl;
import top.kloping.config.OpenConf;
import top.kloping.controller.SelectController;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ComponentScan("top.kloping")
public class CliMain implements ListenerHost, Runner {
    public static final MiraiConsoleImplementationTerminal TERMINAL = new MiraiConsoleImplementationTerminal(Paths.get("qbot"));
    public static final CliMain INSTANCE = new CliMain();
    private static boolean online = false;

    public static void main(String[] args) {
        io.github.kloping.common.Public.EXECUTOR_SERVICE.submit(() -> MiraiConsoleTerminalLoader.INSTANCE.startAsDaemon(TERMINAL));
        net.mamoe.mirai.event.GlobalEventChannel.INSTANCE.registerListenerHost(INSTANCE);
        //ai
        net.mamoe.mirai.event.GlobalEventChannel.INSTANCE.registerListenerHost(ChatQuestion.INSTANCE);
    }

    @AutoStand
    private static SelectController selectController;

    @AutoStand
    public static PetWebSocketClient client;

    @AutoStand
    public static OpenConf openConf;

    @net.mamoe.mirai.event.EventHandler
    public void on(net.mamoe.mirai.event.events.GroupMessageEvent event) {
        String text = event.getMessage().contentToString().trim();
        if (!text.equalsIgnoreCase("开") && !text.equalsIgnoreCase("关"))
            if (!openConf.opened(event.getGroup().getId())) return;
        Long sid = event.getSender().getId();
        APPLICATION.executeMethod(sid, text, event, sid);
        if (text.matches("\\d+")) {
            int i = Integer.parseInt(text);
            if (i >= 0 && i <= 10) {
                Object t = selectController.select(sid, i);
                run(selectM, t, new Object[]{sid, text, event});
            }
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
            APPLICATION.setWaitTime(20000);
            APPLICATION.setAllAfter(INSTANCE);
            APPLICATION.addConfFile("./conf/conf.txt");
            APPLICATION.logger = LoggerImpl.INSTANCE;
            APPLICATION.logger.setPrefix("[kw-cli]");
            APPLICATION.logger.setLogLevel(1);
            APPLICATION.logger.setFormat(new SimpleDateFormat("yyy/MM/dd HH:mm:ss:SSS"));
            APPLICATION.run0(CliMain.class);
            APPLICATION.INSTANCE.getContextManager().append(event.getBot());
        }
    }

    @Override
    public void run(Method method, Object t, Object[] objects) throws NoRunException {
        if (t != null && Judge.isNotEmpty(t.toString())) {
            MessageEvent event = (MessageEvent) objects[2];
            trySendTo(t, event);
        }
    }

    public static void trySendTo(Object t, MessageEvent event) {
        if (t instanceof String || t instanceof StringBuilder) sendToText(t.toString(), event);
        else if (t instanceof List) sendToList((List) t, event);
        else System.err.println("unsupport type: " + t.getClass().getName());
    }

    private static void sendToList(List t, MessageEvent m) {
        if (t == null) return;
        MessageChainBuilder builder = new MessageChainBuilder();
        builder.append(new QuoteReply(m.getMessage()));
        for (Object o : t) {
            if (o == null) {
                System.err.println("Null element in list");
                continue;
            }
            try {
                if (o instanceof CharSequence) {
                    builder.append(o.toString());
                } else if (o instanceof byte[]) {
                    builder.append(Contact.uploadImage(m.getSubject(), new ByteArrayInputStream((byte[]) o)));
                } else if (o == Icon.class) {
                    byte[] bytes = UrlUtils.getBytesFromHttpUrl(m.getSender().getAvatarUrl());
                    builder.append(Contact.uploadImage(m.getSubject(), new ByteArrayInputStream(bytes)));
                } else if (o instanceof Map) {
                    Map<Integer, String> map = (Map<Integer, String>) o;
                    tryInsertSelect(map, m);
                    StringBuilder sb = new StringBuilder();
                    int[] index = {1};
                    map.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .forEach(entry -> {
                                Object key = entry.getKey();
                                Object value = entry.getValue();
                                sb.append(key).append(".").append(value);
                                sb.append(index[0]++ % 2 == 0 ? "\n" : "  ");
                            });

                    builder.append("\n\n").append(sb.toString().trim());
                } else if (o instanceof Message) {
                    builder.append((Message) o);
                } else {
                    System.err.println("Unsupported type: " + o.getClass().getName());
                }
            } catch (Exception e) {
                System.err.println("Processing error: " + e.getMessage());
            }
        }
        m.getSubject().sendMessage(builder.build());
    }


    public static void sendToText(String t, MessageEvent event) {
        MessageChainBuilder builder = new MessageChainBuilder();
        builder.append(new QuoteReply(event.getMessage()));
        builder.append(t.trim());
        event.getSubject().sendMessage(builder.build());
    }

    private static void tryInsertSelect(Map<Integer, String> st2ac, MessageEvent event) {
        selectController.register(event.getSender().getId(), i -> {
            String content = st2ac.get(i);
            if (content == null) return null;
            Long sid = event.getSender().getId();
            APPLICATION.executeMethod(sid, content, event, sid);
            return null;
        });
    }

    static Method selectM;

    @AutoStand(id = "auth.key")
    private String key;

    @Bean
    public RestTemplate restTemplate() {
        try {
            selectM = SelectController.class.getDeclaredMethod("select", Long.class, Integer.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        RestTemplate template = new RestTemplate();
        // 添加请求头拦截器
        template.setInterceptors(Collections.singletonList(
                (request, body, execution) -> {
                    HttpHeaders headers = request.getHeaders();
                    headers.set("kpet-auth", key);
                    return execution.execute(request, body);
                }
        ));
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

    @Bean
    public OpenConf opens() {
        return new OpenConf();
    }
}