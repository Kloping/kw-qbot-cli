package top.kloping.chatai;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.github.kloping.file.FileUtils;
import io.github.kloping.judge.Judge;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.mamoe.mirai.event.ListenerHost;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author github kloping
 * @date 2025/4/3-18:10
 */
public class ChatQuestion implements ListenerHost {

    public static final ChatQuestion INSTANCE = new ChatQuestion();

    public static String getAllStr(MessageChain message) {
        StringBuilder sb = new StringBuilder();
        for (SingleMessage singleMessage : message) {
            if (singleMessage instanceof net.mamoe.mirai.message.data.PlainText) {
                sb.append(singleMessage.contentToString().trim());
            }
        }
        return sb.toString();
    }

    public static boolean isAt(MessageEvent event) {
        for (SingleMessage singleMessage : event.getMessage()) {
            if (singleMessage instanceof At) {
                At at = (At) singleMessage;
                if (at.getTarget() == event.getBot().getId()) return true;
            } else if (singleMessage instanceof QuoteReply) {
                QuoteReply quoteReply = (QuoteReply) singleMessage;
                if (quoteReply.getSource().getFromId() == event.getBot().getId()) return true;
            }
        }
        return false;
    }

    public static String getSystemInfo() {
        return FileUtils.getStringFromFile("./conf/sysinfo.txt");
    }

    public static String getKey() {
        return FileUtils.getStringFromFile("./conf/ai.key");
    }

    public static String getModel() {
        return FileUtils.getStringFromFile("./conf/model.txt");
    }

    @Data
    public static class AiData {
        private String model;
        private List<Message> messages;
        private double temperature;
        private Boolean stream = false;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Message {
        private String role;
        private Object content;
    }

    public String chat(String question) {
        ChatQuestion.AiData aiData = new ChatQuestion.AiData();
        aiData.setModel(getModel());
        aiData.setTemperature(1.0);

        List<Message> messages = new ArrayList<>();

        messages.add(new Message("system", getSystemInfo()));

        messages.add(new Message("user", question));

        aiData.setMessages(new ArrayList<>(messages));
        try {
            System.out.println("开始调用AI for " + question);
            Document doc0 = Jsoup.connect("https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions")
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + getKey())
                    .ignoreContentType(true)
                    .timeout(120 * 1000)
                    .ignoreHttpErrors(true)
                    .requestBody(JSON.toJSONString(aiData))
                    .post();
            String data = doc0.body().text();
            System.out.println(data);
            Assistant assistant = JSONObject.parseObject(data).toJavaObject(Assistant.class);
            return assistant.getChoices().get(0).getMessage().getContent();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @net.mamoe.mirai.event.EventHandler
    public void on(net.mamoe.mirai.event.events.GroupMessageEvent event) {
        if (isAt(event)) {
            String all = getAllStr(event.getMessage());
            if (Judge.isNotEmpty(all)) {
                String out = chat(all);
                MessageChainBuilder builder = new MessageChainBuilder();
                builder.append(new QuoteReply(event.getMessage()));
                builder.append(new At(event.getSender().getId()));
                builder.append(out);
                event.getSubject().sendMessage(builder.build());
            }
        }
    }
}
