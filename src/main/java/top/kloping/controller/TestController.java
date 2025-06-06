package top.kloping.controller;

import io.github.kloping.spt.annotations.Action;
import io.github.kloping.spt.annotations.AutoStand;
import io.github.kloping.spt.annotations.Controller;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import top.kloping.api.KwGameSocialApi;
import top.kloping.config.OpenConf;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author github kloping
 * @date 2025/5/15-18:07
 */
@Controller
public class TestController {

    @AutoStand
    OpenConf openConf;

    @Action("开")
    public String open(MessageEvent event) {
        if (event instanceof GroupMessageEvent) {
            GroupMessageEvent gme = (GroupMessageEvent) event;
            if (gme.getSender().getPermission().getLevel() >= 1) {
                openConf.toOpen(gme.getGroup().getId());
                return "OK";
            }
        }
        return null;
    }

    @Action("关")
    public String close(MessageEvent event) {
        if (event instanceof GroupMessageEvent) {
            GroupMessageEvent gme = (GroupMessageEvent) event;
            if (gme.getSender().getPermission().getLevel() >= 1) {
                openConf.toClose(gme.getGroup().getId());
                return "OK";
            }
        }
        return null;
    }

    @AutoStand
    KwGameSocialApi api;

    int max = 10;

    @Action("更新日志")
    public String updateLogs(MessageEvent event) {
        StringBuilder sb = new StringBuilder();
        Map<Integer, String> map = api.logs();
        int i = 1;
        List<Integer> integers = new LinkedList<>(map.keySet());
        integers.sort((o1, o2) -> o2 - o1);
        for (Integer a : integers) {
            sb.append("V").append(a).append(":").append(map.get(a)).append("\n");
            if (i >= max) break;
        }
        return sb.toString();
    }
}
