# MyBatisGeneratorPlugin  
#### *MaiHaoChe* 
![](https://avatars2.githubusercontent.com/u/1483254?v=4&s=200)

### 需求
现在团队内部有一套代码，能够根据数据库表结构直接生成一系列对象，实现基本的增删改查API，便于直接在业务逻辑中使用。这套代码目前处于游离状态（哪里需要复制粘贴到哪里），又不便于组织成jar包，作为各个项目的依赖（因为这是一套工具代码，与项目无关），这样非常不便于维护升级。
Intellij Idea 插件是最适合实现这种工具需求。既便于团队内共享，又便于统一管理并维护升级。

### 使用
* 安装：在IntelliJ 的插件安装页面点击"Browse reposities..."，在弹窗中搜索"mybatis generator":
![enter image description here](http://of8cu1h2w.bkt.clouddn.com/searchmybatisgenerator.png?imageView/2/w/500)
* 入口：安装并重启后，在菜单栏Tools下可以看到插件的功能入口：
![enter image description here](http://of8cu1h2w.bkt.clouddn.com/entry.png?imageView/2/w/500)
* 配置：需要配置用于连接到目标数据库的链接，用户名和密码以及生成的各个java类和xml文件的路径。
* 生成mybatis代码：输入表名，点击确定，生成该表的mybatis代码。

### 实现
##### 配置流程

1. 配置数据库连接链接，用户名，密码，配置生成代码的目录路径；

2. 插件连接到数据库，获取表结构信息(不获取表内容信息)；

3. 通过Velocity ，将预先定义好的模板于数据库信息结合，生成类文件，写入目标路径中。

##### API 
* 实体
DO，Query，Mapper，Manager,  Mapper.xml。前缀为：表名（驼峰），比如表table_user，生成的实体分别为：TableUserDO，TableUserQuery,TableUserMapper,TableUserManager 和 TableUserMapper.xml。	
> * DO 表对象，每个字段代表一个列
> * Query 查询对象，用于添加各类查询条件
> * Mapper mybatis 需要的mapper的接口
> * Manager 对外输出的API，包含了基本的增删改查
> * Mapper.xml mybatis的mapper文件，sql的具体实现

* 拓展
每个对象都可以创建一个子对象，包含在同级目录的ext子目录下。具体的实现可以自定义。


