package top.kloping.controller;

import io.github.kloping.judge.Judge;
import io.github.kloping.spt.annotations.Action;
import io.github.kloping.spt.annotations.AutoStand;
import io.github.kloping.spt.annotations.Controller;
import io.github.kloping.spt.annotations.Param;
import io.github.kloping.url.UrlUtils;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;
import org.springframework.http.ResponseEntity;
import top.kloping.api.KwGamePlayerApi;
import top.kloping.api.dto.DataWithTips;
import top.kloping.api.entity.Player;

import java.io.ByteArrayInputStream;

/**
 * @author github kloping
 * @date 2025/4/20-13:56
 */
@Controller
public class PlayerController {
    @AutoStand
    KwGamePlayerApi api;

    @AutoStand
    SelectController selectController;

    @Action("注册<.*?=>x>")
    public String register(Long id, @Param("x") String text) {
        if (Judge.isEmpty(text)) return "使用‘注册{昵称}’来注册\n名字不可空或超过8个字符";
        selectController.register(id, i -> {
            if (i == 1) {
                ResponseEntity<String> data = api.register(id, text);
                if (data.getStatusCode().value() == 200) {
                    return "注册成功,请使用`信息`查看";
                } else return data.getBody();
            } else return null;
        });
        return "确定注册名为:'" + text + "'吗? \n后续可使用'改名{新名}'修改\n回复 1 确认 0 取消";
    }

    @Action("信息")
    public Object show(Long id, MessageEvent event) {
        ResponseEntity<String> data = api.show(id);
        if (data.getStatusCode().value() == 200) {
            return showInfo(event, data);
        } else return data.getBody();
    }


    @Action("改名<.*?=>x>")
    public String rename(Long id, @Param("x") String text, MessageEvent event) {
        if (Judge.isEmpty(text)) return "使用‘改名{昵称}’来修改\n名字不可空或超过8个字符";
        selectController.register(id, i -> {
            if (i == 1) {
                ResponseEntity<String> data = api.rename(id, text);
                if (data.getStatusCode().value() == 200) {
                    return showInfo(event, data);
                } else {
                    return data.getBody();
                }
            } else return null;
        });
        return "确定修改名为:'" + text + "'吗?\n下次修改时间将是14天后\n回复 1 确认 0 取消";
    }

    @AutoStand
    PetController petController;

    @Action("打工")
    public String work(Long id,MessageEvent event) {
        ResponseEntity<String> data = api.work(id);
        if (data.getStatusCode().value() == 200) {
            DataWithTips dw = api.convertT(data, DataWithTips.class);
            selectController.register(id, (i) -> {
                switch (i){
                    case 1:
                        return show(id, event);
                    case 2:
                        return petController.list(id);
                    default:
                        return null;
                }
            });
            return dw.toString() + "\n\n1.信息  2.我的宠物";
        } else return data.getBody();
    }


    private Object showInfo(MessageEvent m, ResponseEntity<String> data) {
        Player player = api.convertT(data, Player.class);
        StringBuilder sb = new StringBuilder();
        int exp = Math.toIntExact(player.getExperience());
        int maxexp = Math.toIntExact(player.getRequiredExp());
        final int len = 10;
        int progress = (int) ((double) exp / maxexp * len);
        sb.append("👤 名字: ").append(player.getName()).append("\n")
                .append("📊 等级: ").append(player.getLevel()).append("\n")
                .append("🌟 经验: ").append(exp)
                .append("/").append(maxexp).append("\n")
                .append("📈 进度: [");
        for (int i = 0; i < len; i++) {
            if (i < progress) sb.append("█");
            else sb.append("░");
        }
        sb.append("]\n").append("💰 金币: ").append(player.getGold());
        sb.append("\n⚡ 体力: ").append(player.getStamina());
        MessageChainBuilder builder = new MessageChainBuilder();
        builder.append(new QuoteReply(m.getMessage()));
        builder.append(Contact.uploadImage(m.getSubject(), new ByteArrayInputStream(UrlUtils.getBytesFromHttpUrl(m.getSender().getAvatarUrl()))));
        builder.append(sb.toString());
        m.getSubject().sendMessage(builder.build());
        return null;
    }
}
