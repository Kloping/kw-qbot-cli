package top.kloping.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

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
    private List<Integer> pets = new LinkedList<>();
    private List<Integer> monsters = new LinkedList<>();
    //lo2av
    private Map<Integer, Integer> actionQueue;
    private Map<Integer, Character> locationMap;

    private List<String> mayopts = new LinkedList<>();

    //按照value升序重新排
    public Map<Integer, Integer> getSortedActionQueue() {
        if (actionQueue == null || actionQueue.isEmpty()) {
            return new LinkedHashMap<>();
        }

        List<Map.Entry<Integer, Integer>> entries = new ArrayList<>(actionQueue.entrySet());
        Collections.sort(entries, (e1, e2) -> e1.getValue().compareTo(e2.getValue()));

        Map<Integer, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Integer, Integer> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    @Data
    public static class Character {
        private Integer id;
        private String name;
        /**
         * 剩余血量百分比
         */
        private Integer hpb;
        /**
         * 护盾百分比
         */
        private Integer shieldb;
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
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BattleRecord {
        private String aname;
        private String opt;
        private String bname;
    }

}
