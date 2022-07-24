# hrpc-java

## 1. 项目背景

为了兼容更多语言类型，提升框架的健壮性，在原有hrpc-go项目的基础上，特设立本项目作为使用Java语言的开发框架。本项目支持与hrpc-go的ctx传递 (即gRPC中的metadata传递)，能够做到协同开发。

## 2. 项目结构

| 模块名称                                 | 模块描述                                                     |
| ---------------------------------------- | ------------------------------------------------------------ |
| hirpc-api                                | gRPC接口集合，proto文件存放在该目录下                        |
| hirpc-business                           | 业务服务集合 (consumers)                                     |
| hirpc-common                             | 公共组件集合，包括公用工具类、公用异常等                     |
| hirpc-plugins                            | 插件模块集合，该项目所有插件都在此目录下                     |
| &nbsp;&nbsp;&brvbar;- hirpc-plugin-dbConfig    | &nbsp;&nbsp;&brvbar;- 公共数据库插件，包括DB插件以及MybatisPlus插件 |
| &nbsp;&nbsp;&brvbar;- hirpc-plugin-kafka | &nbsp;&nbsp;&brvbar;- 公共Kafka插件                          |
| &nbsp;&nbsp;&brvbar;- hirpc-plugin-mongo | &nbsp;&nbsp;&brvbar;- 公共MongoDB插件                        |
| &nbsp;&nbsp;&brvbar;- hirpc-plugin-redis | &nbsp;&nbsp;&brvbar;- 公共Redis插件                          |
| hirpc-providers                          | 数据服务集合 (providers)                                     |

## 3. 项目规范

### 1) 实体类

* **PO: 持久对象**

表示数据库中的一条记录映射成的Java对象。

* **BO: 业务对象**

把业务逻辑封装为一个对象。这个对象可以包括一个或多个其它的对象。

* **VO: 表现层对象**

主要对应界面显示的数据对象。前端传输到后台的数据，可以是VO封装。

* **DTO: 数据传输对象**

主要用于封装返回Web Service的JSON数据。

* **POJO: 简单Java对象**

一个POJO持久化以后就是PO；直接用它传递，传递过程中就是DTO；直接用来对应表示层就是VO。

### 2) 依赖引用

**[依赖引用步骤]:**

+ 在hrpc-java项目根目录的pom.xml文件中的<dependencyManagement>标签内添加需要引用的依赖，例如：

  ```xml
  <dependencyManagement>
      <dependency>
          <groupId>org.projectlombok</groupId>
          <artifactId>lombok</artifactId>
          <version>${lombok.version}</version>
          <scope>provided</scope>
      </dependency>
  </dependencyManagement>
  ```

+ 将引用的依赖版本提取到<properties>标签中， 例如：

  ```xml
  <properties>
      <lombok.version>1.18.24</lombok.version>
  </properties>
  ```


+ 进入到需要引用该依赖的模块目录下，在该模块的pom.xml的标签<dependencies>中添加需要引用的依赖。**<u>注意：去除版本号</u>**。例如：

  ```xml
  <dependencies>
      <dependency>
          <groupId>org.projectlombok</groupId>
          <artifactId>lombok</artifactId>
          <scope>provided</scope>
      </dependency>
  </dependencies>
  ```

### 3) 插件使用

本项目通过**<u>自定义注解</u>**的方式使用插件。下面详细介绍各个插件的注解使用方法以及相关的配置需求。

#### (i) hirpc-plugin-dbConfig

**[插件依赖引用]:**

进入到hirpc-providers模块下的对应服务子模块，打开pom.xml文件。将插件信息加入到<dependencies>标签下，如下所示：

```xml
<dependencies>
    <dependency>
        <groupId>dev.hirpc</groupId>
        <artifactId>hirpc-plugin-db</artifactId>
        <scope>compile</scope>
    </dependency>
</dependencies>
```

**[可用自定义注解]:** @EnableDB, @EnableMybatisPlus

+ 以上两个注解必须同时启用，才能通过MybatisPlus连接到MySQL数据源。

**[@EnableDB使用步骤]:**

+ 进入到hirpc-plugin-dbConfig/src/main/resources目录下，打开db-dev.yaml文件，将MySQL数据库的相关信息填在`datasource`一栏下。本项目支持多数据源，可在数据源之间进行随意切换，因此可以在此处填入多个数据库的信息。**<u>注意：每个数据源的信息都必须以 `-` 符号开头，否则无法正常读取。另外，如无特别提醒，README中展示的所有配置属性都是必需的</u>**。例如：

  ```yaml
  hirpc:
    datasource:
      - name: datasource_demo
        url: jdbc:mysql://localhost:3306/datasource_demo?useSSL=false&serverTimezone=GMT%2b8&autoReconnect=true
        username: root
        password: xxxxxx
        driverClassName: com.mysql.cj.jdbc.Driver
      - name: datasource_other
        url: jdbc:mysql://localhost:3306/datasource_other?useSSL=false&serverTimezone=GMT%2b8&autoReconnect=true
        username: root
        password: xxxxxx
        driverClassName: com.mysql.cj.jdbc.Driver
  ```

  在此我们定义了两个MySQL数据源，它们的名字分别是datasource_demo和datasource_other。

+ 进入到hirpc-plugin-dbConfig/src/main/java/dev/hirpc/dbConfig/domain目录下，打开DBName.java文件，将上面的数据源名称记录进去。例如：

  ```java
  public class DBName {
  
      private DBName() {}
  
      public static final String DB_DEMO = "datasource_demo";
  
      public static final String DB_OTHER = "datasource_other";
  
  }
  ```

+ 最后，进入到hirpc-providers模块下的对应服务子模块，在应用启动文件的类名上方加入自定义注解。例如：

  ```java
  @EnableDB(defaultDB = DBName.DB_DEMO, dbConfig = {DBName.DB_DEMO, DBName.DB_OTHER})
  ```

  该注解表示我们将DB_DEMO (datasource_demo)作为默认MySQL数据源，DB_OTHER (datasource_other)作为备用数据源。两者可以随意切换，后续将详细讲解。

**[@EnableMybatisPlus使用步骤]:**

+ 进入到hirpc-providers模块下的对应服务子模块，此处以一个User服务做例子，建立mapper类 (此处假设包路径为com.yumontime.provider.mapper)，在其中设计实现通过MybatisPlus的方式与数据源直接进行交互的函数。例如：

  ```java
  @Mapper
  public interface UserMapper extends BaseMapper<User> {
  
      @Select("SELECT * FROM user")
      List<User> getUserList();
  
      @Select("SELECT * FROM user WHERE id = #{id}")
      User getUserById(@Param("id") Integer id);
  
  }
  ```

  可以看到MybatisPlus可以通过注解直接执行sql语句与MySQL数据库进行交互。更多MybatisPlus的使用方法可以到官网学习。

+ 在同一provider子模块下，建立service类，并在其中通过`@Resource`获取mapper类。例如：

  ```java
  @Service
  public class UserServiceImpl implements UserService {
  
      @Resource
      private UserMapper userMapper;
  
      @Override
      public List<User> getUserList() {
          return userMapper.getUserList();
      }
  
      @Override
      public User getUserById(int id) {
          return userMapper.getUserById(id);
      }
  
  }
  ```

  该例子并未使用gRPC，但即便使用gRPC，过程也是完全一致的。它们都需要调用mapper类与数据源交互，只是gRPC有独特的参数交互模式而已。

+ 依旧在在同一provider子模块下，在应用启动文件的类名上方加入自定义注解。例如：

  ```java
  @EnableMybatisPlus(basePackages = "com.yumontime.provider.mapper")
  ```

  basePackages的值设置为mapper包的包路径即可，例如在上面的例子中，即为"com.yumontime.provider.mapper"。

+ 一个启用DB插件和MybatisPlus插件的服务的应用启动文件，应该如下所示：

  ```java
  @EnableDB(defaultDB = DBName.DB_DEMO, dbConfig = {DBName.DB_DEMO, DBName.DB_OTHER})
  @EnableMybatisPlus(basePackages = "com.yumontime.provider.mapper")
  @SpringBootApplication
  public class DataSourceDemoApp {
  
      public static void main(String[] args) {
          SpringApplication.run(DataSourceDemoApp.class, args);
      }
  
  }
  ```

**[切换数据源步骤]:**

+ 我们继续用上面的User服务做例子。进入到provider子模块的service类，我们可以使用hirpc-plugin-db模块封装好的**DBContextHolder**类进行数据源切换。例如：

  ```java
  @Service
  public class UserServiceImpl implements UserService {
  
      @Resource
      private UserMapper userMapper;
  
      @Override
      public List<User> getUserList() {
      		// 更换MySQL数据源
          DBContextHolder.setDbType(DBName.DB_OTHER);
          return userMapper.getUserList();
      }
  
      @Override
      public User getUserById(int id) {
          return userMapper.getUserById(id);
      }
  
  }
  ```

  在这个例子中，getUserList()函数将数据源从默认的DB_DEMO切换为DB_OTHER。而getUserById()函数依旧使用默认的数据源DB_DEMO。

#### (ii) hirpc-plugin-redis

**[插件依赖引用]:**

进入到hirpc-providers模块下的对应服务子模块，打开pom.xml文件。将插件信息加入到<dependencies>标签下，如下所示：

```xml
<dependencies>
    <dependency>
        <groupId>dev.hirpc</groupId>
        <artifactId>hirpc-plugin-redis</artifactId>
        <scope>compile</scope>
    </dependency>
</dependencies>
```

**[可用自定义注解]:** @EnableRedis

+ 本项目的Redis插件实现了单例模式 (standalone)、集群模式 (cluster)、哨兵模式 (sentinel)三种模式，只需要在配置信息中做些区分即可。同时，本项目使用Redisson实现分布式锁，通过工具类**RedissonUtil**的形式进行使用，后续会讲解使用步骤。

**[@EnableRedis使用步骤]:**

+ 进入到hirpc-plugin-redis/src/main/resources目录下，打开redis-dev.yaml文件，将Redis数据库的相关信息填在`redis`一栏下。单例模式、集群模式、哨兵模式的配置方法各不相同，下面会一一讲解。

    + 单例模式 (standalone)

      ```yaml
      hirpc:
        redis:
          - name: hirpc-redis # 单例模式
            isCluster: false
            isSentinel: false
            nodes:
              - 127.0.0.1:6379
            max-redirects: 3
            database: 0
            password:
            pool:
              max-active: 1000 # 连接池最大连接数（使用负值表示没有限制）
              max-idle: 10 # 连接池中的最大空闲连接
              min-idle: 5  # 连接池中的最小空闲连接
              max-wait: -1 # 连接池最大阻塞等待时间（使用负值表示没有限制）
      ```

        + 将`isCluster`和`isSentinel`属性都设置为false。
        + `nodes`属性内有且只能有一个redis节点的url。
        + 其他内容按照需求设置即可。

    + 集群模式 (cluster)

      ```yaml
      hirpc:
        redis:
          - name: hirpc-redis-cluster # 集群模式
            isCluster: true
            isSentinel: false
            nodes:
              - 127.0.0.1:8000
              - 127.0.0.1:8001
              - 127.0.0.1:8002
              - 127.0.0.1:8003
              - 127.0.0.1:8004
              - 127.0.0.1:8005
            max-redirects: 3
            database: 0
            password:
            pool:
              max-active: 1000 # 连接池最大连接数（使用负值表示没有限制）
              max-idle: 10 # 连接池中的最大空闲连接
              min-idle: 5  # 连接池中的最小空闲连接
              max-wait: -1 # 连接池最大阻塞等待时间（使用负值表示没有限制）
      ```

        + 将`isCluster`属性设置为true，`isSentinel`属性设置为false。
        + `nodes`属性内至少需要3个redis节点的url，推荐使用6个及以上节点。
        + 其他内容按照需求设置即可。

    + 哨兵模式 (sentinel)

      ```yaml
      hirpc:
        redis:
          - name: hirpc-redis-sentinel # 哨兵模式
            isCluster: false
            isSentinel: true
            master: mymaster
            sentinels:
              - 127.0.0.1:26380
              - 127.0.0.1:26381
              - 127.0.0.1:26382
            max-redirects: 3
            database: 0
            password: 123456
            pool:
              max-active: 1000 # 连接池最大连接数（使用负值表示没有限制）
              max-idle: 10 # 连接池中的最大空闲连接
              min-idle: 5  # 连接池中的最小空闲连接
              max-wait: -1 # 连接池最大阻塞等待时间（使用负值表示没有限制）
      ```

        + 将`isCluster`属性设置为false，`isSentinel`属性设置为true。
        + 将主节点的名称写入`master`属性。
        + 将一个或一个以上的哨兵节点的url写入`sentinels`属性。
        + 其他内容按照需求设置即可。

+ 进入到hirpc-plugin-redis/src/main/java/dev/hirpc/redis/domain目录下，打开RedisSourceName.java文件，将上面的数据源名称记录进去。例如：

  ```java
  public class RedisSourceName {
  
      private RedisSourceName() {}
  
      public static final String HIRPC_REDIS = "hirpc-redis";
  
      public static final String HIRPC_REDIS_CLUSTER = "hirpc-redis-cluster";
  
      public static final String HIRPC_REDIS_SENTINEL = "hirpc-redis-sentinel";
  
  }
  ```

+ 进入到hirpc-providers模块下的对应服务子模块，建立service类。我们此处以下面的StoreService为例。使用`@Resource`获取**RedisTemplate**，并凭借其对Redis数据库进行操作。例如：

  ```java
  @Service
  public class StoreServiceImpl implements StoreService {
  
      @Resource
      private RedisTemplate redisTemplate;
  
      @Override
      public String getStoreById(int id) {
          JSONObject store = (JSONObject) redisTemplate.opsForValue().get(String.valueOf(id));
          return store.toString();
      }
  
  }
  ```

  更多RedisTemplate的操作请自行学习。

+ 最后，依旧在服务子模块下，在应用启动文件的类名上方加入自定义注解。例如：

  ```java
  @EnableRedis(defaultSource = RedisSourceName.HIRPC_REDIS, source = {RedisSourceName.HIRPC_REDIS, RedisSourceName.HIRPC_REDIS_CLUSTER, RedisSourceName.HIRPC_REDIS_SENTINEL})
  @SpringBootApplication
  public class DataSourceDemoApp {
  
      public static void main(String[] args) {
          SpringApplication.run(DataSourceDemoApp.class, args);
      }
  
  }
  ```

  该注解表明我们将连接HIRPC_REDIS (hirpc-redis)作为默认Redis数据源。

**[Redis分布式锁使用步骤]:**

+ 进入到hirpc-providers模块下的对应服务子模块，打开service类，这里我们仍用上面的StoreService做例子。使用`RedissonUtil.lock()`上锁，使用`RedissonUtil.unLock()`解锁。**<u>注意：请严格参照try-catch-finally的形式使用分布式锁</u>**。例如：

  ```java
  @Service
  public class StoreServiceImpl implements StoreService {
  
      @Resource
      private RedisTemplate redisTemplate;
  
      @Override
      public String getStoreById(int id) {
        	try {
              RedissonUtil.lock("test"); // 参数为锁名称
            	JSONObject store = (JSONObject) redisTemplate.opsForValue().get(String.valueOf(id));
          		return store.toString();
          } catch (InterruptedException e) {
              e.printStackTrace();
          } finally {
              RedissonUtil.unLock("test");
          }
      }
  
  }
  ```

#### (iii) hirpc-plugin-mongo

**[插件依赖引用]:**

进入到hirpc-providers模块下的对应服务子模块，打开pom.xml文件。将插件信息加入到<dependencies>标签下，如下所示：

```xml
<dependencies>
    <dependency>
        <groupId>dev.hirpc</groupId>
        <artifactId>hirpc-plugin-mongo</artifactId>
        <scope>compile</scope>
    </dependency>
</dependencies>
```

**[可用自定义注解]:** @EnableMongoDB

**[@EnableMongoDB使用步骤]:**

+ 进入到hirpc-plugin-mongo/src/main/resources目录下，打开mongo-dev.yaml文件，将MongoDB数据库的相关信息填在`mongodb`一栏下。例如：

  ```java
  hirpc:
    mongodb:
      - name: hirpc_mongo_demo
        uri: mongodb://hirpc:hirpc2022@ac-npm1ok8-shard-00-00.rh32juh.mongodb.net:27017,ac-npm1ok8-shard-00-01.rh32juh.mongodb.net:27017,ac-npm1ok8-shard-00-02.rh32juh.mongodb.net:27017/yumontime?ssl=true&authSource=admin&retryWrites=true&maxIdleTimeMS=5000
  ```

  建议如上述例子中的uri所示设置`ssl`, `authSource`, `retryWrites`, `maxIdleTimeMS`等属性，不然可能报错。

+ 进入到hirpc-plugin-mongo/src/main/java/dev/hirpc/mongo/config目录下，打开MongoSourceName.java文件，将上面的数据源名称记录进去。例如：

  ```java
  public class MongoSourceName {
  
      private MongoSourceName() {}
  
      public static final String HIRPC_MONGO_DEMO = "hirpc_mongo_demo";
  
  }
  ```

+ 进入到hirpc-providers模块下的对应服务子模块，建立service类。我们此处以下面的DriverService为例。使用`@Resource`获取**MongoTemplate**，并凭借其对MongoDB数据库进行操作。例如：

  ```java
  @Service
  public class DriverServiceImpl implements DriverService {
  
      @Resource
      private MongoTemplate mongoTemplate;
  
      @Override
      public String getDriverById(int id) {
          Query query = new Query(Criteria.where("_id").is(id));
          Driver driver = mongoTemplate.findOne(query, Driver.class);
  
          return driver.toString();
      }
  
  }
  ```

  更多MongoTemplate的操作请自行学习。

+ 最后，依旧在服务子模块下，在应用启动文件的类名上方加入自定义注解。**<u>注意：需要在@SpringBootApplication里exclude掉MongoDB自动配置到类，防止配置冲突</u>**。例如：

  ```java
  @EnableMongoDB(defaultSource = MongoSourceName.HIRPC_MONGO_DEMO, sources = {MongoSourceName.HIRPC_MONGO_DEMO})
  @SpringBootApplication(exclude = {MongoAutoConfiguration.class})
  public class DataSourceDemoApp {
  
      public static void main(String[] args) {
          SpringApplication.run(DataSourceDemoApp.class, args);
      }
  
  }
  ```

  该注解表明我们将HIRPC_MONGO_DEMO (hirpc_mongo_demo)作为默认MongoDB数据源。
