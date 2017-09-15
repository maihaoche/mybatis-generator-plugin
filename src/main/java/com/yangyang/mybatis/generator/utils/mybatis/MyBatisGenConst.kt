package main.java.com.yangyang.mybatis.generator.utils.mybatis

import com.intellij.openapi.project.Project
import main.java.com.yangyang.mybatis.generator.MyConstant
import main.java.com.yangyang.mybatis.generator.utils.PlatformUtil
import java.io.File


/**
 * 类简介：
 * 作者：  yang
 * 时间：  17/7/24
 * 邮箱：  yangyang1@maihaoche.com
 */
class MyBatisGenConst {

    /**
     * 伴生对象。companion中的内容，可以通过类直接访问。
     */
    companion object {
        var sConfigured = false
        // 包名
        var DAO_MODULE = ""
        // 工作目录
        var DO_PACKAGE = ""
        var QUERY_PACKAGE = ""
        var MAPPER_PACKAGE = ""
        var MAPPER_EXT_PACKAGE = ""

        var MANAGER_MODULE = ""
        var MANAGER_PACKAGE = ""
        var MANAGER_IMPL_PACKAGE = ""
        // mapper xml 输出目录
        var MAPPER_XML_DIR = ""
        // mapper-ext xml 输出目录
        var MAPPER_EXT_XML_DIR = ""
        // do/model 输出目录
        var MAPPER_DO_DIR = ""
        // query 输出目录
        var MAPPER_QUERY_DIR = ""
        // mapper java 输出目录
        var MAPPER_JAVA_DIR = ""
        // mapper-ext java 输出目录
        var MAPPER_EXT_JAVA_DIR = ""
        //manager输出目录
        var MANAGER_JAVA_DIR = ""
        var MANAGER_IMPL_JAVA_DIR = ""

        //模板路径
        val DO_TEMPLATE = "/template/do.txt"
        val QUERY_TEMPLATE = "/template/query.txt"
        val SQLMAP_TEMPLATE = "/template/sqlmap.txt"
        val MAPPER_TEMPLATE = "/template/mapper.txt"
        val MANAGER_TEMPLATE = "/template/manager.txt"
        val MANAGER_IMPL_TEMPLATE = "/template/managerImpl.txt"
        val MAPPER_EXT_TEMPLATE = "/template/mapper-ext.txt"
        val SQLMAP_EXT_TEMPLATE = "/template/sqlmap-ext.txt"

        val QUERY_PREFIX = "Query"
        val MAPPER_SUFFIX = "Mapper"
        val MANAGER_SUFFIX = "Manager"
        val MANAGER_IMPL_SUFFIX = "ManagerImpl"
        val MAPPER_EXT_SUFFIX = "ExtMapper"
        val DO_SUFFIX = "DO"

        val COMMON_COLUMN_STR = "gmt_create,gmt_modified,"

        // jdbc result set metadata collumn name
        val RSMD_COLUMN_NAME = "rsmdColumnName"
        val RSMD_COLUMN_CLASS_NAME = "columnClassName"
        val RSMD_COLUMN_TYPE_NAME = "columnTypeName"
        val RSMD_COLUMN_PRECISION = "Precision"
        val RSMD_COLUMN_SCALE = "Scale"
        val RSMD_COLUMN_PRIMARY_KEY = "PrimaryKey"
        val RSMD_COLUMN_REMARKS = "Remarks"

        val COLUMN_NAME = "COLUMN_NAME"

        // velocity param
        val VP_LIST = "list"
        val VP_QUERY_PREFIX = "queryPrefix"
        val VP_DO_SUFFIX = "doSuffix"
        val VP_MAPPER_SUFFIX = "mapperSuffix"
        val VP_MANAGER_SUFFIX = "managerSuffix"

        val VP_MANAGER_IMPL_SUFFIX = "managerImplSuffix"
        val VP_MAPPER_EXT_SUFFIX = "extMapperSuffix"
        val VP_CLASS_NAME = "className"
        val VP_MAPPER_PROPERTY_NAME = "mapperPropertyName"
        val VP_DO_PACKAGE = "doPackage"
        val VP_QUERY_PACKAGE = "queryPackage"
        val VP_MAPPER_PACKAGE = "mapperPackage"
        val VP_MANAGER_PACKAGE = "managerPackage"
        val VP_MANAGER_IMPL_PACKAGE = "managerImplPackage"
        val VP_MAPPER_EXT_PACKAGE = "mapperExtPackage"
        val VP_JAVA_TYPE = "javaType"
        val VP_PROP_NAME = "propName"
        val VP_GET_METHOD = "getMethod"

        val VP_SET_METHOD = "setMethod"
        val VP_COLUMN_NAME = "columnName"
        val VP_COLUMN_REMARKS = "columnRemarks"
        val VP_TABLE_NAME = "tableName"
        val VP_JDBC_TYPE = "jdbcType"
        val VP_COLS = "cols"
        val VP_COLS_WITHOUT_COMMON_COLUMNS = "colsWithoutCommColumns"
        val VP_SERIAL_VERSION_UID = "serialVersionUID"

        val VP_SERIAL_VERSION_UID2 = "serialVersionUID2"
        val VP_PRIMARY_KEY = "primaryKey"
        val VP_PROP_PRIMARY_KEY = "propPrimaryKey"

        //分库分表 表后缀regex
        val SHARDING_SUFFIX_REG = "_[\\d]{4}"


        fun initConfig(project: Project?): Boolean {
            val onNullReturn = {
                clearConfig()
                sConfigured = false
            }

            PlatformUtil.getData(MyConstant.DAO_MODULE, "").let {
                if (it.isNullOrBlank()) {
                    DAO_MODULE = ""
                    onNullReturn()
                } else {
                    DAO_MODULE = it
                }
            }
            PlatformUtil.getData(MyConstant.DO_PACKAGE, "").let {
                if (it.isNullOrBlank()) {
                    DO_PACKAGE = ""
                    onNullReturn()
                } else {
                    DO_PACKAGE = it
                }
            }
            PlatformUtil.getData(MyConstant.QUERY_PACKAGE, "").let {
                if (it.isNullOrBlank()) {
                    QUERY_PACKAGE = ""
                    onNullReturn()
                } else {
                    QUERY_PACKAGE = it
                }
            }
            PlatformUtil.getData(MyConstant.MAPPER_PACKAGE, "").let {
                if (it.isNullOrBlank()) {
                    MAPPER_PACKAGE = ""
                    onNullReturn()
                } else {
                    MAPPER_PACKAGE = it
                }
            }
            PlatformUtil.getData(MyConstant.MANAGER_MODULE, "").let {
                if (it.isNullOrBlank()) {
                    MANAGER_MODULE = ""
                    onNullReturn()
                } else {
                    MANAGER_MODULE = it
                }
            }
            PlatformUtil.getData(MyConstant.MANAGER_PACKAGE, "").let {
                if (it.isNullOrBlank()) {
                    MANAGER_PACKAGE = ""
                    onNullReturn()
                } else {
                    MANAGER_PACKAGE = it
                }
            }
            setConfig(DAO_MODULE, DO_PACKAGE, QUERY_PACKAGE, MAPPER_PACKAGE, MANAGER_MODULE, MANAGER_PACKAGE, project)
            return sConfigured
        }

        fun setConfig(
                daoModule: String,
                doPackage: String,
                queryPackage: String,
                mapperPackage: String,
                managerModule: String,
                managerPackage: String,
                project: Project?
        ) {
            if (null == project) {
                return
            }
            DAO_MODULE = daoModule
            DO_PACKAGE = doPackage
            QUERY_PACKAGE = queryPackage
            MAPPER_PACKAGE = mapperPackage
            MANAGER_MODULE = managerModule
            MANAGER_PACKAGE = managerPackage
            MAPPER_EXT_PACKAGE = MAPPER_PACKAGE + ".ext"
            MANAGER_IMPL_PACKAGE = MANAGER_PACKAGE + ".impl"
            var daoPath = project.baseDir.path + File.separator + DAO_MODULE
            var managerPath = project.baseDir.path + File.separator + MANAGER_MODULE
            //资源文件路径
            MAPPER_XML_DIR = daoPath + "/src/main/resources/mapper/"
            MAPPER_EXT_XML_DIR = daoPath + "/src/main/resources/mapper/ext/"
            //代码文件路径
            MAPPER_DO_DIR = daoPath + "/src/main/java/" + getFilePathFromPackage(DO_PACKAGE)
            MAPPER_QUERY_DIR = daoPath + "/src/main/java/" + getFilePathFromPackage(QUERY_PACKAGE)
            MAPPER_JAVA_DIR = daoPath + "/src/main/java/" + getFilePathFromPackage(MAPPER_PACKAGE)
            MAPPER_EXT_JAVA_DIR = daoPath + "/src/main/java/" + getFilePathFromPackage(MAPPER_EXT_PACKAGE)
            MANAGER_JAVA_DIR = managerPath + "/src/main/java/" + getFilePathFromPackage(MANAGER_PACKAGE)
            MANAGER_IMPL_JAVA_DIR = MANAGER_JAVA_DIR + "/impl"
            sConfigured = true
        }

        fun getFilePathFromPackage(packageName: String): String {
            return packageName.replace(".", "/")
        }

        fun clearConfig() {
            DAO_MODULE = ""
            DO_PACKAGE = ""
            QUERY_PACKAGE = ""
            MAPPER_PACKAGE = ""
            MAPPER_EXT_PACKAGE = ""
            MANAGER_MODULE = ""
            MANAGER_PACKAGE = ""
            MANAGER_IMPL_PACKAGE = ""
            MAPPER_XML_DIR = ""
            MAPPER_EXT_XML_DIR = ""
            MAPPER_DO_DIR = ""
            MAPPER_QUERY_DIR = ""
            MAPPER_JAVA_DIR = ""
            MANAGER_JAVA_DIR = ""
            MANAGER_IMPL_JAVA_DIR = ""
            MAPPER_EXT_JAVA_DIR = ""
            sConfigured = false
        }

    }
}