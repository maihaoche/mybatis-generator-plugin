package main.java.com.yangyang.mybatis.generator

/**
 * 类简介：项目的常量。存储一些配置数据的信息
 * 作者：  yang
 * 时间：  17/7/24
 * 邮箱：  yangyang1@maihaoche.com
 */
class MyConstant {

    companion object {
        // -------------------------配置数据库连接的key-------------------------
        val DB_CONF_INITED = "db_conf_inited"//mysql是否初始化
        val DB_URL = "db_url"//数据库的url
        val DB_USER = "db_user"//数据库的用户名
        val DB_PW = "db_pw"//数据库的密码
        // -------------------------生成数据的一些路径的配置key-------------------------
        val TARGET_MODULE = "target_module"//目标module
        val DO_PACKAGE = "dao_package"//do生成目录
        val QUERY_PACKAGE = "query_package"//查询语句生成目录
        val MANAGER_PACKAGE = "manager_package"//manager生成目录
        val MAPPER_PACKAGE = "mapper_package"//mapper生成目录
        val TABLE_PREFIX = "table_prefix"//数据库表名字的前缀

        //-------------------------内部字段-------------------------
        val TABLE_HISTORY = "table_history"//保存历史选中的表名。

        //-------------------------action的一些路径-------------------

        val ACTION_TOOLS_MENU = "ToolsMenu"
        val ACTION_MYBATIS = "Mybatis"
        val ACTION_MYBATIS_PATH_CONFIGURE = "配置路径"
        val ACTION_MYBATIS_DB_CONFIGURE = "配置数据库"


    }
}