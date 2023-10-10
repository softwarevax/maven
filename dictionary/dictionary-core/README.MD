1、字典表(DictionaryTable)
```agsl
1、DictionaryTable：数据字典表，不同来源(database，redis)的表，可以有相同的名字，dictType决定不同的来源
2、AbstractDbTable： 数据库类型的字典表的抽象类
3、DatabaseTable：数据库类型的字典表
```

2、字典加载器(DictionaryLoader)
```agsl
1、DictionaryLoader： 字典加载器，不同的来源，使用的加载器不同
2、DatabaseLoader：数据库类型的加载器
```

3、缓存(ICache)
```agsl
1、ICache：添加缓存，利用缓存处理数据，清理缓存
2、AbstractCache：缓存抽象类
3、LinkedHashMapCache: 基于LinkedHashMap实现的内存缓存
4、RedisCache: 基于redis实现的内存缓存
dictionary.configure.cache.type=redis
dictionary.configure.cache.redis.database=1
dictionary.configure.cache.redis.password=H3yuncom
dictionary.configure.cache.redis.prefix=smart # 默认dict前缀
dictionary.configure.cache.redis.expired=3600 #缓存过期时间，默认无限制，单位秒
```

4、事件(event)
```agsl
1、BeforeInvokeEvent：处理结果前调用
2、AfterInvokeEvent：处理结果后调用
3、BeforeRefreshEvent：字典刷新前调用
4、AfterRefreshEvent：字典刷新后调用
```






