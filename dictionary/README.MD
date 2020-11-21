## 数据字典
### 1、插件功能描述:
```
1、当表中含有的字典项较多时，需要进行多次联字典表进行查询，从而影响查询速度和开发速度。此插件的目的在于改善此过程。
2、使用注解，将字典项中的编码替换为文本，如表中返回的字典编码"MAN",需要连接字典表，将“MAN”查出“男”。使用此插件，一个注解解决问题。实体中添加了注解
3、当字典表数据较多时，不建议使用，如用户表，不适合全部加载到内存中
```
### 2、集成步骤
#### 2.1、注册mybatis插件
##### 2.1.1、pom引入依赖包
将此项目本地打包，在要使用的项目中引入
```
<dependency>
    <groupId>org.wit.ctw.plugin</groupId>
    <artifactId>spring-dictionary</artifactId>
    <version>1.0.0-RELEASE</version>
</dependency>
```
##### 2.1.2、注解说明
```
@Inherited
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Dictionary {
    String table() default "";
    String property() default "";
    String column() default "";
    String value() default "";
    String[] conditions() default {""};
}
```
##### 2.1.2.1、table
```
此字典项所在的表名，若为空，则从所有的缓存中查找，若找不到，则不替换。若有多个，找到第一个替换(有加载的顺序决定)
```
##### 2.1.2.2、property
```
要进行替换的属性，如性别sex=MAN属性，若为空，则替换当前属性，替换后sex=男，有时需要保留编码，则可以将文本放到另外一个新增的字段中，
如property=sexLabel,替换后sex=MAN,sexLabel=男,若sexLabel属性是实体中不存在的属性，则替换失败
```
##### 2.1.2.3、column、value
```
即文本和编码对应字典表的列名
```
##### 2.1.2.4、conditions
```
条件，即字典项的唯一性，有时需要很多字段才可以确定，如一个字典表存放多个项目模块的，此时需要加上模块名，才能确定唯一的字典项。
用户状态: ON===>在线 (type=user_status)
好友状态: ON===>在线 (type=friend_status)
此时没有办法区分，一般会多一个字段来确定字典项的唯一性，此时可以设置condition={"type = user_status"},可以有多个条件，多个条件and组合
```
##### 2.1.3、注册interceptor插件
###### 2.1.3.1、配置表sys_config结构说明
```
CREATE TABLE `sys_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `label` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `value` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `type` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `description` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `sort` int(11) DEFAULT NULL,
  `parent_id` int(11) DEFAULT '0',
  `app_id` varchar(255) COLLATE utf8_bin DEFAULT '',
  `create_user_id` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `update_user_id` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `remarks` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `del_flag` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
)
```
###### 2.1.3.2、用户表app_user结构说明
```
CREATE TABLE `app_user` (
  `id` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL
)
```
###### 2.1.3.3、将插件注册到spring容器中
column:
DatabaseTable中的column为在注解中使用到的所有列，一般字典表只需要key，value，当需要多个字段确定唯一字典项时，就需要设置多个column
```
@Bean
public DictionaryInterceptor dictionaryInterceptor(DataSource dataSource) {
    // config 配置表
    DatabaseTable config = new DatabaseTable();
    String[] configColumn = new String[]{"label", "value", "type"};
    String[] configCondition = new String[]{"1 = 1"};
    String configTableName = "sys_config";
    config.setColumn(configColumn);
    config.setConditions(configCondition);
    config.setTableName(configTableName);
    // user 用户表
    DatabaseTable user = new DatabaseTable();
    String[] userColumn = new String[]{"id", "name"};
    String userTableName = "app_user";
    user.setColumn(userColumn);
    user.setTableName(userTableName);
    DatabaseLoader dbLoader = new DatabaseLoader(dataSource);
    dbLoader.addDictionaryTable(config);
    dbLoader.addDictionaryTable(user);
    DictionaryInterceptor interceptor = new DictionaryInterceptor(dbLoader);
    return interceptor;
}
```
### 3、字典插件使用
#### 3.1、注解标记属性
使用注解后
1、createUserId、updateUserId由原来的用户id改为用户姓名
2、state由原来的状态"ON"改为"进行中"
```
public class Habit {

    @Dictionary(table ="app_user", column = "name", value = "id")
    private String createUserId;
    
    /**
     * 当表没有加载时, 从全部缓存中查询, 效率较低
     */
    @Dictionary(table ="app_user", column = "name", value = "id")
    private String updateUserId;

    private String stateLabel;

    // table默认值，默认取加载的第一张表，property默认替换当前属性，column默认字典表的column为label, value默认字典表的value为value， conditions默认没有查询条件
    //@Dictionary(table ="sys_config", property = "stateLabel", column = "label", value = "value", conditions = {"type = habit_state"})
    @Dictionary(property = "stateLabel")
    private String state;
}
```
#### 3.2、插件使用效果
使用插件前
```
{
	"flag": true,
	"data": {
		"id": "d99839ee821511ea94d354ee75dd4fca",
		"habitName": "",
		"startDate": "2020-04-19",
		"endDate": "2020-05-10",
		"clockStartTime": "22:00",
		"clockEndTime": "23:30",
		"publishTime": "2020-04-19 16:14:33",
		"createTime": "2020-04-19 16:14:33",
		"createUserId": "1",
		"updateTime": "2020-04-19 16:14:33",
		"updateUserId": "1",
		"stateLabel": null,
		"state": "ON",
		"records": []
	},
	"message": "接口调用成功"
}
```
使用插件后1
```
注解
@Dictionary(table ="app_user", column = "name", value = "id")
private String createUserId;

@Dictionary(table ="app_user", column = "name", value = "id")
private String updateUserId;

// 默认值，默认取加载的第一张表，默认替换当前属性，默认字典表的column为label, 默认字典表的value为value， 默认没有查询条件
//@Dictionary(table ="sys_config", property = "stateLabel", column = "label", value = "value", conditions = {"type = habit_state"})
@Dictionary(property = "stateLabel")
private String state;
{
	"flag": true,
	"data": {
		"id": "d99839ee821511ea94d354ee75dd4fca",
		"habitName": "",
		"startDate": "2020-04-19",
		"endDate": "2020-05-10",
		"clockStartTime": "22:00",
		"clockEndTime": "23:30",
		"publishTime": "2020-04-19 16:14:33",
		"createTime": "2020-04-19 16:14:33",
		"createUserId": "vax",
		"updateTime": "2020-04-19 16:14:33",
		"updateUserId": "vax",
		"stateLabel": "进行中",
		"state": "ON",
		"records": []
	},
	"message": "接口调用成功"
}
```
使用插件后2
```
@Dictionary(table ="app_user", column = "name", value = "id")
private String createUserId;

@Dictionary(table ="app_user", column = "name", value = "id")
private String updateUserId;

@Dictionary
private String state;
{
	"flag": true,
	"data": {
		"id": "d99839ee821511ea94d354ee75dd4fca",
		"habitName": "睡前刷牙",
		"startDate": "2020-04-19",
		"endDate": "2020-05-10",
		"clockStartTime": "22:00",
		"clockEndTime": "23:30",
		"publishTime": "2020-04-19 16:14:33",
		"createTime": "2020-04-19 16:14:33",
		"createUserId": "vax",
		"updateTime": "2020-04-19 16:14:33",
		"updateUserId": "vax",
		"stateLabel": null,
		"state": "进行中",
		"records": []
	},
	"message": "接口调用成功"
}
```

