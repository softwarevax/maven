package com.github.softwarevax.dict.mybatis.domain;

/**
 * @author ctw
 * @Project： plugin-parent
 * @Package: com.github.softwarevax.dict.mybatis
 * @Description:
 * @date 2020/11/21 13:35
 */

import com.github.softwarevax.dict.core.Dictionary;
import lombok.Data;

/**
 * habit实体
 * 2020/11/19 16:38
 */
@Data
public class Habit {

    /**
     * 直接将value替换key
     */
    @Dictionary(table ="app_user", column = "name", value = "id")
    private String createUserId;

    /**
     * table默认值，默认取加载的第一张表，property默认替换当前属性，column默认字典表的column为label, value默认字典表的value为value， conditions默认没有查询条件
     * table：字典所在的表，可不配置，但性能会较低
     * name：字典值value所在的列名
     * value：字典键key所在的列名，字典没办法通过一个列确定时，可使用conditions再进行筛选，type为列名，habit_state为列的值, conditions内字符串格式为 key = value, 可含多个条件
     * property：需要将替换后的字典，放到当前类的哪个属性中。默认是当前属性
     */
    @Dictionary(table ="app_user", column = "name", value = "id", property = "updateUserName")
    private String updateUserId;

    private String updateUserName;

    /**
     保留当前属性的key， 将value放到另外一个属性stateLabel中
     @Dictionary(table ="sys_config", property = "stateLabel", column = "label", value = "value", conditions = {"type = habit_state"})
     */
    @Dictionary
    private String state;
}
