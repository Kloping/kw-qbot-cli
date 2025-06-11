package top.kloping.controller;

import io.github.kloping.spt.annotations.Action;
import io.github.kloping.spt.annotations.AutoStand;
import io.github.kloping.spt.annotations.Controller;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.ForwardMessageBuilder;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.springframework.http.ResponseEntity;
import top.kloping.api.KwGameEmailsApi;
import top.kloping.api.dto.Emails;

import java.util.List;

/**
 * @author github kloping
 * @date 2025/5/15-18:07
 */
@Controller
public class EmailsController {

    @AutoStand
    KwGameEmailsApi api;

    @Action("查看邮箱")
    public String list(MessageEvent event, Long id) {
        ResponseEntity<String> data = api.list(id);
        List<Emails> emails = api.convertTs(data, Emails.class);
        if (emails.isEmpty()) return "邮箱为空";
        ForwardMessageBuilder builder = new ForwardMessageBuilder(event.getBot().getAsFriend());
        for (Emails email : emails) {
            builder.add(email.getPid(), "邮箱助手", new MessageChainBuilder()
                    .append(new PlainText(email.getContent())).append("\n[可领]").build());
        }
        builder.add(id, "邮箱助手", new PlainText("TIPS:通过'领取全部'命令领取"));
        event.getSubject().sendMessage(builder.build());
        return null;
    }

    @Action("领取全部")
    public String gets(MessageEvent event, Long id) {
        api.gets(id);
        return "领取邮箱已执行";
    }

}
