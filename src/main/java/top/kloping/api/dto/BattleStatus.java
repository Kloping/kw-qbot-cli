package top.kloping.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * @author github kloping
 * @date 2025/4/24-12:29
 */
@Data
@NoArgsConstructor
public class BattleStatus {
    private Long pid;
    private Boolean started = false;
    private Integer currentRound;
    private String tips;
    /**
     * 技能点
     */
    private Integer skp;

    public BattleStatus(Long pid, String tips) {
        this.tips = tips;
        this.pid = pid;
    }

    private Queue<BattleRecord> records;
    private List<Integer> pets = new ArrayList<>();
    private List<Integer> monsters = new ArrayList<>();

    private Map<Integer, Integer> actionQueue;
    private Map<Integer, Character> locationMap;

    private List<String> mayopts = new ArrayList<>();

    @Data
    public static class Character {
        private Integer id;
        private String name;
        /**
         * 剩余血量百分比
         */
        private Integer hpb;
        /**
         * 几条命
         */
        private Integer lifes;
        /**
         * 剩余行动条
         */
        private Integer cv;
        /**
         * 等级
         */
        private Integer level;
        /**
         * buff效果
         */
        private String bufftips;
        /**
         * 血量变化
         */
        private Integer hpc;
        /**
         * 受控
         */
        private Integer controlled;
    }

    @Data
    public static class BattleRecord {
        private String aname;
        private String opt;
        private String bname;
    }

}
