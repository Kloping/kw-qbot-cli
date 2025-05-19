package top.kloping.controller;

import io.github.kloping.judge.Judge;
import io.github.kloping.spt.annotations.Action;
import io.github.kloping.spt.annotations.AutoStand;
import io.github.kloping.spt.annotations.Controller;
import io.github.kloping.spt.annotations.Param;
import org.springframework.http.ResponseEntity;
import top.kloping.api.KwGamePlayerApi;
import top.kloping.api.KwGameTaskApi;
import top.kloping.api.dto.DataWithTips;
import top.kloping.api.dto.TaskStatus;
import top.kloping.api.dto.TeamDto;
import top.kloping.api.entity.Player;

import javax.swing.*;
import java.util.List;
import java.util.Map;

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

    @AutoStand
    KwGameTaskApi taskApi;

    @Action("注册<.*?=>x>")
    public String register(Long id, @Param("x") String text) {
        if (Judge.isEmpty(text)) return "使用‘注册{昵称}’来注册\n名字不可空或超过8个字符";
        selectController.register(id, i -> {
            if (i == 1) {
                ResponseEntity<String> data = api.register(id, text);
                if (data.getStatusCode().value() == 200) {
                    return "注册成功,请使用`信息`查看\ntips:优先关注'当前任务'~";
                } else return data.getBody();
            } else return null;
        });
        return "确定注册名为:'" + text + "'吗? \n后续可使用'改名{新名}'修改\n回复 1 确认 0 取消";
    }

    @Action("信息")
    public Object show(Long id) {
        ResponseEntity<String> data = api.show(id);
        if (data.getStatusCode().value() == 200) {
            return showInfo(data);
        } else return data.getBody();
    }


    @Action("改名<.*?=>x>")
    public String rename(Long id, @Param("x") String text) {
        if (Judge.isEmpty(text)) return "使用‘改名{昵称}’来修改\n名字不可空或超过8个字符";
        selectController.register(id, i -> {
            if (i == 1) {
                ResponseEntity<String> data = api.rename(id, text);
                if (data.getStatusCode().value() == 200) {
                    return showInfo(data);
                } else {
                    return data.getBody();
                }
            } else return null;
        });
        return "确定修改名为:'" + text + "'吗?\n下次修改时间将是14天后\n回复 1 确认 0 取消";
    }

    @Action("打工")
    public Object work(Long id) {
        ResponseEntity<String> data = api.work(id);
        if (data.getStatusCode().value() == 200) {
            DataWithTips dw = api.convertT(data, DataWithTips.class);
            return List.of(dw.toString(), Map.of(1, "信息", 2, "我的宠物"));
        } else return data.getBody();
    }

    @Action("兑换金币<.*?=>x>")
    public Object exchangeDiamondToGold(Long id, @Param("x") String text) {
        int count = 1;
        if (!Judge.isEmpty(text)) {
            count = api.getIntegerOrDefault(text, null);
        }
        ResponseEntity<String> data = api.exchangeDiamondToGold(id, count);
        if (data.getStatusCode().value() == 200) {
            return data.getBody() + "\n使用钻石按照1:100兑换金币";
        } else return data.getBody() + "\n使用钻石按照1:100兑换金币";
    }

    private Object showInfo(ResponseEntity<String> data) {
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
        sb.append("\n").append("\uD83D\uDC8E 钻石: ").append(player.getDiamond());
        sb.append("\n⚡ 体力: ").append(player.getStamina());
        return List.of(Icon.class, sb.toString()
                , Map.of(1, "打工", 2, "领取宠物", 3, "当前任务", 4, "宠物信息"));
    }

    @Action("当前任务")
    public Object task(Long id) {
        ResponseEntity<String> data = taskApi.list(id);
        if (data.getStatusCode().value() == 200) {
            List<TaskStatus> list = taskApi.convertTs(data, TaskStatus.class);
            StringBuilder sb = new StringBuilder();
            for (TaskStatus taskStatus : list) {
                sb.append("『").append(taskStatus.getName()).append("』")
                        .append("\n").append("描述: ").append(taskStatus.getDesc())
                        .append("\n").append("类型: ").append(taskStatus.getType())
                        .append("\n").append("奖励: ").append(taskStatus.getReward())
                        .append("\n\n");
            }
            return sb.length() == 0 ? "暂无更多任务\n请等待下次更新" : sb.toString();
        } else return data.getBody();
    }

    //组队
    @Action("组队<.*?=>x>")
    public String team(Long id, @Param("x") String text) {
        if (!Judge.isEmpty(text)) {
            int i = text.indexOf("@");
            if (i >= 0) {
                String stid = text.substring(i + 1).trim();
                Long tid = Long.valueOf(stid);
                ResponseEntity<String> en = api.team(id, tid);
                if (en.getStatusCode().value() == 200) {
                    return en.getBody();
                } else return en.getBody();
            }
        }
        ResponseEntity<String> en = api.team(id);
        if (en.getStatusCode().value() == 200) {
            TeamDto teamDto = api.convertT(en, TeamDto.class);
            return teamDto.toString();
        } else return "组队用法: '组队@某人'\n组队后可给予物品(2人)\n组队后可一起游历打怪\n当前:" + en.getBody();
    }

    @Action("退出组队")
    public String quitTeam(Long id) {
        ResponseEntity<String> en = api.quitTeam(id);
        if (en.getStatusCode().value() == 200) {
            return en.getBody();
        } else return en.getBody();
    }
}
