package top.kloping.api.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 玩家表
 * </p>
 *
 * @author kloping
 * @since 2025-04-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Player implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 玩家等级上限，默认10级
     */
    private final Integer maxLevel = 10;
    /**
     * 玩家ID，主键自增
     */
    private Long id;
    /**
     * 玩家名称
     */
    private String name;
    /**
     * 玩家等级，默认1级
     */
    private Integer level = 1;
    /**
     * 玩家经验值，默认0
     */
    private Long experience = 0L;
    /**
     * 玩家金币，默认1000
     */
    private Integer gold = 1000;
    /**
     * 钻石
     */
    private Integer diamond = 0;
    /**
     * 最终活跃时间
     */
    private Long lastActive = 0L;
    /**
     * 上次修改名字的时间
     */
    private Long lastRenameTime = 0L;
    /**
     * 体力
     */
    private Integer stamina = 100;
    /**
     * 最后一次体力恢复时间
     */
    private Long ltr = 0L;
    /**
     * 升级所需经验值
     */
    private Long requiredExp;
}