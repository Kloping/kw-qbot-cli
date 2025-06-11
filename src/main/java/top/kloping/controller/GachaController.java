package top.kloping.controller;

import io.github.kloping.spt.annotations.Action;
import io.github.kloping.spt.annotations.AutoStand;
import io.github.kloping.spt.annotations.Controller;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.ForwardMessageBuilder;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.springframework.http.ResponseEntity;
import top.kloping.api.KwGameGachaApi;
import top.kloping.api.SrcRegistry;
import top.kloping.api.dto.GachaPoolPre;

import java.util.List;

/**
 * @author github kloping
 * @date 2025/5/15-18:07
 */
@Controller
public class GachaController {

    @AutoStand
    KwGameGachaApi api;

    @AutoStand
    SrcRegistry registry;

    @Action(value = "祈愿列表", otherName = {"抽卡", "抽卡列表"})
    public Object list(MessageEvent event) {
        ResponseEntity<String> data = api.list();

        ForwardMessageBuilder builder = new ForwardMessageBuilder(event.getBot().getAsFriend());

        if (data.getStatusCode().is2xxSuccessful()) {
            builder.add(event.getBot().getAsFriend(), new PlainText("常驻卡池\n单抽命令: 常驻祈愿/常驻单抽\n十连命令: 十连常驻/常驻十连"));
            builder.add(event.getBot().getAsFriend(), new PlainText("限定卡池\n单抽命令: 限定祈愿/限定单抽\n十连命令: 十连限定/限定十连"));

            List<GachaPoolPre> gachaPoolPres = api.convertTs(data, GachaPoolPre.class);
            for (GachaPoolPre gachaPoolPre : gachaPoolPres) {
                MessageChainBuilder b0 = new MessageChainBuilder();

                b0.append(gachaPoolPre.getName()).append("\n");
                b0.append(registry.getImageByPath(gachaPoolPre.getIconPath()));
                b0.append(gachaPoolPre.getDesc()).append("\n");
                b0.append("卡池时间: ").append(gachaPoolPre.getTime()).append("\n");
                b0.append("---------------\n");
                b0.append("单抽: ").append(String.valueOf(gachaPoolPre.getOne())).append(gachaPoolPre.getDw())
                        .append("\t\t").append("十连: ").append(String.valueOf(gachaPoolPre.getTen())).append(gachaPoolPre.getDw());

                builder.add(event.getBot().getAsFriend(), b0.build());
            }

            event.getSubject().sendMessage(builder.build());

            return null;
        }
        return "暂无信息";
    }

    @Action(value = "常驻祈愿", otherName = {"常驻单抽"})
    public Object one0(Long pid) {
        ResponseEntity<String> data = api.one(pid, "NORMAL");
        return data.getBody();
    }

    @Action(value = "十连常驻", otherName = {"常驻十连"})
    public Object ten0(Long pid) {
        ResponseEntity<String> data = api.ten(pid, "NORMAL");
        return data.getBody();
    }

    @Action(value = "限定祈愿", otherName = {"限定单抽"})
    public Object one1(Long pid) {
        ResponseEntity<String> data = api.one(pid, "XIAN");
        return data.getBody();
    }

    @Action(value = "限定常驻", otherName = {"限定十连"})
    public Object ten1(Long pid) {
        ResponseEntity<String> data = api.ten(pid, "XIAN");
        return data.getBody();
    }
}
