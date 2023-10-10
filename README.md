deploy发布到远程仓库:

半小时到https://repo1.maven.org/maven2/

4个小时到https://search.maven.org

一天到https://mvnrepository.com
## 数据字典插件(当前版本0.0.3.RELEASE)
[github地址](https://github.com/softwarevax/maven.git)
>版本列表(已提交到中央仓库)
* 0.0.1-RELEASE
* 0.0.2.RELEASE
* 0.0.3.RELEASE
* 0.0.4.RELEASE

maven依赖:
```
<dependency>
  <groupId>com.github.softwarevax</groupId>
  <artifactId>dictionary-core</artifactId>
  <version>${lastVersion}</version>
</dependency>
<dependency>
  <groupId>com.github.softwarevax</groupId>
  <artifactId>starter-dictionary-mybatis</artifactId>
  <version>${lastVersion}</version>
</dependency>
```
### 1、插件功能描述:
```
1、当表中含有的字典项较多时，需要进行多次连字典表进行查询，从而影响查询和开发速度。此插件的目的在于改善此过程
2、使用注解，将字典项中的编码替换为文本，如表中返回的字典编码"MAN",需要连接字典表，将"MAN"查出"男"。使用此插件，一个注解可以解决问题
3、当字典表数据较多时，不建议使用，如用户表，不适合全部加载到内存中
4、字典支持嵌套，包括对象嵌套，集合嵌套，数组嵌套
```
#### 1.1、插件注解说明
```
@Inherited
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Dict {
    String table() default "";
    String property() default "";
    String column() default "";
    String value() default "";
    String[] conditions() default {""};
}
```
##### 1.1.2、table
```
此字典项所在的表名，若为空，则从所有的缓存中查找，若找不到，则不替换。若有多个，找到第一个替换(由加载的顺序决定)
```
##### 1.1.3、property
```
要进行替换的属性，如性别sex=MAN属性，若为空，则替换当前属性，替换后sex=男。有时需要保留编码，则可以将文本放到另外一个自定义的字段中，
如property=sexLabel,替换后sex=MAN,sexLabel=男,若sexLabel属性是实体中不存在的属性，则替换失败
```
##### 1.1.4、column、value
```
即文本和编码对应字典表的列名
```
##### 1.1.5、conditions
```
条件，即字典项的唯一性，有时需要很多字段才可以确定，如一个字典表存放多个项目模块的，此时需要加上模块名，才能确定唯一的字典项。
用户状态: ON===>在线 (type=user_status)
好友状态: ON===>在线 (type=friend_status)
此时没有办法区分，需要多一个字段来确定字典项的唯一性，此时可以设置conditions={"type = user_status"}, conditions为数组，支持有多个条件
```
### 2、集成字典
#### 2.1、普通java项目集成字典插件
##### 2.1.1、pom引入依赖包(非maven项目可从中央仓库下载后引入)
```
<dependency>
  <groupId>com.github.softwarevax</groupId>
  <artifactId>dictionary-core</artifactId>
  <version>${lastVersion}</version>
</dependency>
```
##### 2.1.2、配置字典表
```
DatabaseTable config = new DatabaseTable();
String configTableName = "sys_config"; //配置表名
String[] configColumn = new String[]{"label", "value", "type"}; //配置表中的列
String[] configCondition = new String[]{"1 = 1"}; //筛选字典需要的条件
config.setColumn(configColumn);
config.setConditions(configCondition);
config.setTableName(configTableName);
DatabaseLoader dbLoader = new DatabaseLoader(dataSource);
dbLoader.addDictionaryTable(config);
DictionaryHelper.addLoader(dbLoader);
```
说明：DatabaseTable中的column为在注解中使用到的所有列，一般字典表只需要key，value，当需要多个字段确定唯一字典项时，就需要设置多个column
##### 2.1.3、调用
```
将需要转换的实体通过集合传入DictionaryHelper.resultWrapper()，返回的结果中，被Dictionary注解的字段，会被转换
```
#### 2.2、mybatis项目集成字典插件
> (非spring环境，通过mybatis的xml配置文件集成), 参照demo-dictionary-mybatis

##### 2.2.1、pom引入依赖包
同2.1.1
##### 2.2.2、将字典插件和mybatis拦截器注册到spring容器中
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
#### 2.3、springboot集成字典插件
> 参照demo-starter-dictionary-mybatis
##### 2.2.1、pom引入字典插件starter依赖包，并使用@EnableDictionary注解开启字典插件功能
```
<dependency>
  <groupId>com.github.softwarevax</groupId>
  <artifactId>starter-dictionary-mybatis</artifactId>
  <version>${lastVersion}</version>
</dependency>
```
##### 2.2.2、配置文件配置字典表
```
# sys_config表
dictionary.db-table[0].table-name=sys_config
dictionary.db-table[0].column[0]=label
dictionary.db-table[0].column[1]=value
dictionary.db-table[0].column[2]=type
dictionary.db-table[0].conditions[0]=1=1
# app_user表
dictionary.db-table[1].table-name=app_user
dictionary.db-table[1].column[0]=id
dictionary.db-table[1].column[1]=name

# 5秒刷新一次缓存，默认1小时
dictionary.configure.refresh-interval=5
```
### 3、字典插件使用
#### 3.1、注解标记属性
```
使用注解后
1、createUserId、updateUserId由原来的用户id改为用户姓名
2、state由原来的状态"ON"改为"进行中"
```
```
public class Habit {

   /**
    * 直接将value替换key
    */
   @Dict(table ="app_user", column = "name", value = "id")
   private String createUserId;

   /**
    * table默认值，默认取加载的第一张表，property默认替换当前属性，column默认字典表的column为label, value默认字典表的value为value， conditions默认没有查询条件
    * table：字典所在的表，可不配置，但性能会较低
    * name：字典值value所在的列名
    * value：字典键key所在的列名，字典没办法通过一个列确定时，可使用conditions再进行筛选，type为列名，habit_state为列的值, conditions内字符串格式为 key = value, 可含多个条件
    * property：需要将替换后的字典，放到当前类的哪个属性中。默认是当前属性
    */
   @Dict(table ="app_user", column = "name", value = "id", property = "updateUserName")
   private String updateUserId;

   private String updateUserName;

   /**
    保留当前属性的key， 将value放到另外一个属性stateLabel中
    @Dict(table ="sys_config", property = "stateLabel", column = "label", value = "value", conditions = {"type = habit_state"})
    */
   @Dict
   private String state;
}
```
#### 3.2、插件使用效果
使用插件前
```
[
    {
        "createUserId": "1",
        "updateUserId": "1",
        "updateUserName": null,
        "state": "ON"
    }
]
```
使用插件后
```
注解
/**
 * 直接将value替换key
 */
@Dict(table ="app_user", column = "name", value = "id")
private String createUserId;

/**
 * table默认值，默认取加载的第一张表，property默认替换当前属性，column默认字典表的column为label, value默认字典表的value为value， conditions默认没有查询条件
 * table：字典所在的表，可不配置，但性能会较低
 * name：字典值value所在的列名
 * value：字典键key所在的列名，字典没办法通过一个列确定时，可使用conditions再进行筛选，type为列名，habit_state为列的值, conditions内字符串格式为 key = value, 可含多个条件
 * property：需要将替换后的字典，放到当前类的哪个属性中。默认是当前属性
 */
@Dict(table ="app_user", column = "name", value = "id", property = "updateUserName")
private String updateUserId;

private String updateUserName;

/**
 保留当前属性的key， 将value放到另外一个属性stateLabel中
 @Dict(table ="sys_config", property = "stateLabel", column = "label", value = "value", conditions = {"type = habit_state"})
 */
@Dict
private String state;

[
    {
        "createUserId": "vax",
        "updateUserId": "1",
        "updateUserName": "vax",
        "state": "进行中"
    }
]
```
Version 0.0.1.RELEASE Finished at 2020-11-21

Version 0.0.2.RELEASE Finished at 2020-11-22
```
新增功能:
1、字典可以嵌套(详见demo-starter-dictionary-mybatis)
public class Habit {

    private String id;

    private String habitName;

    /**
     * 对象: 字典嵌套
     */
    @Dict
    private User createUser;

    /**
     * 集合: 字典嵌套
     */
    @Dict
    private List<User> createUsers;

    @Dict(table ="app_user", column = "name", value = "id", property = "updateUserName")
    private String updateUserId;

    private String updateUserName;

    @Dict
    private String state;
}

public class User {
    private String id;

    private String name;

    /**
     * 状态
     */
    @Dict(table = "sys_config", property = "stateLabel", column = "label", value = "value", conditions = {"type = user_state"})
    private String state;

    private String stateLabel;

    @Dict
    private String sex;
}
2、提供刷新字典缓存的接口
ip:port/dict/refresh
```

Version 0.0.3.RELEASE Finished at 2022-06-12
```
issue1:
1、修复问题：字典的类型和业务字段的类型不一致时，无法适配，如：字典的值为"1",而业务字段的值为1,
解决方案：默认转为字符串匹配，可实现接口DictionaryValueComparator，自定义实现两值的比较，可通过dictionary.configure.comparator配置实现类
```
```
issue2:
2、修复问题：子类继承父类，在父类中添加字典注解不生效
解决方案：获取从父类继承的属性，设置从父类继承的属性
```
```
issue3:
3、修复问题：字典类型是Integer类型，反显后的值是String,那么类型转化失败
解决方案：目前只实现两个类型的转换，基础类型转字符串和布尔类型
3.1、基础类型转字符串，调用的是String.valueOf();
3.2、基础类型转布尔，先将基础类型转为int类型，若大于0，则为true，反之false
注：DictionaryValueParser.parse(Object val, Class<T> clazz)
val为从字典中查询出的值，clazz为Dictionary注解中property的类型，即要设置属性值的类型，也就是目标类型
可实现DictionaryValueParser接口，完成其他类型的转换,可通过dictionary.configure.value-parser配置实现类
```

Version 0.0.4.RELEASE Finished at 2023-10-10
```
1、缓存支持内存和redis
```


github tag:
github 创建Tag: git tag 名字 –m "注释"
推送到远端: git push origin tag名
clone 指定的tag: git clone --branch [tag] [git地址]
