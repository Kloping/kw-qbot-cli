package top.kloping.api.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 宠物表
 * </p>
 *
 * @author kloping
 * @since 2025-04-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Pet implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 宠物ID，主键自增
     */
    private Integer id;
    /**
     * 是否置顶
     */
    private Integer top;
    /**
     * 所属玩家ID，外键关联player表
     */
    private Long playerId;
    /**
     * 宠物名称
     */
    private String name;
    /**
     * 宠物种类ID，用于区分不同种类的宠物
     */
    private Integer speciesId;
    /**
     * 宠物等级，默认1级
     */
    private Integer level;
    /**
     * 宠物经验值，默认0
     */
    private Long experience;
    /**
     * 当前血量，默认等于hp
     */
    private Integer currentHp;
    /**
     * 宠物类型
     */
    private String type;
    /**
     * 宠物最大生命值，默认100
     */
    private Integer hp;
    /**
     * 宠物攻击力，默认10
     */
    private Integer attack;
    /**
     * 宠物防御力，默认5
     */
    private Integer defense;
    /**
     * 宠物速度，默认5
     */
    private Integer speed;
    /**
     * 宠物暴击率，默认0
     */
    private Integer critRate = 0;
    /**
     * 宠物暴击伤害，默认50%
     */
    private Integer critDamage = 50;
    /**
     * 升级所需经验值
     */
    private Long requiredExp;
}
