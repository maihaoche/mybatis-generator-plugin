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
        var sDaoModule = ""
        var sDaoPackage = ""//do,query,mapper,mapper_ext所在的package名
        var sManagerModule = ""
        var sManagerPackage = ""
        var sManagerImplPackage = ""

        var sTablePrefix = ""//表名的前缀

        //  输出目录
        var sDaoPackageDir = ""
        var sBaseDir = ""//BaseCriteria,BaseQuery,PageResult所在的文件目录
        var sMapperXmlDir = ""
        var sMapperExtXmlDir = ""
        var sDoJavaDir = ""
        var sQueryJavaDir = ""
        var sMapperJavaDir = ""
        var sMapperExtJavaDir = ""
        var sManagerJavaDir = ""
        var sManagerImplJavaDir = ""

        //是否是mhc内部使用的代码
        var sIsMHCStaff = false

        //模板路径
        val DO_TEMPLATE = "/template/do.txt"
        val QUERY_TEMPLATE = "/template/query.txt"
        val SQLMAP_TEMPLATE = "/template/sqlmap.txt"
        val MAPPER_TEMPLATE = "/template/mapper.txt"
        val MANAGER_TEMPLATE = "/template/manager.txt"
        val MANAGER_IMPL_TEMPLATE = "/template/managerImpl.txt"
        val MAPPER_EXT_TEMPLATE = "/template/mapper-ext.txt"
        val SQLMAP_EXT_TEMPLATE = "/template/sqlmap-ext.txt"

        val PAGERESULT_TEMPLATE = "/template/base/pageResult.txt"
        val BASE_CRITERIA_TEMPLATE = "/template/base/baseCriteria.txt"
        val CRITERIA_TEMPLATE = "/template/base/criteria.txt"
        val BASE_QUERY_TEMPLATE = "/template/base/baseQuery.txt"


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
        val VP_DAO_PACKAGE = "daoPackage"
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
                    sDaoModule = ""
                    onNullReturn()
                } else {
                    sDaoModule = it
                }
            }
            PlatformUtil.getData(MyConstant.DAO_PACKAGE, "").let {
                if (it.isNullOrBlank()) {
                    sDaoPackage = ""
                    onNullReturn()
                } else {
                    sDaoPackage = it
                }
            }
            PlatformUtil.getData(MyConstant.MANAGER_MODULE, "").let {
                if (it.isNullOrBlank()) {
                    sManagerModule = ""
                    onNullReturn()
                } else {
                    sManagerModule = it
                }
            }
            PlatformUtil.getData(MyConstant.MANAGER_PACKAGE, "").let {
                if (it.isNullOrBlank()) {
                    sManagerPackage = ""
                    onNullReturn()
                } else {
                    sManagerPackage = it
                }
            }
            PlatformUtil.getData(MyConstant.TABLE_PREFIX, "").let {
                if (it.isNullOrBlank()) {
                    sTablePrefix = ""
                    onNullReturn()
                } else {
                    sTablePrefix = it
                }
            }


            PlatformUtil.getData(MyConstant.MHC_STAFF, false).let {
                sIsMHCStaff = it
            }
            setConfig(sDaoModule,
                    sDaoPackage,
                    sManagerModule,
                    sManagerPackage,
                    sTablePrefix,
                    sIsMHCStaff,
                    project)
            return sConfigured
        }

        fun setConfig(
                daoModule: String,
                daoPackage: String,
                managerModule: String,
                managerPackage: String,
                tablePrefix:String,
                isMHCStaff: Boolean,
                project: Project?
        ) {
            if (null == project) {
                return
            }
            sIsMHCStaff = isMHCStaff
            sDaoModule = daoModule
            sDaoPackage = daoPackage
            sManagerModule = managerModule
            sManagerPackage = managerPackage
            sTablePrefix = tablePrefix
            sManagerImplPackage = sManagerPackage + ".impl"
            sDaoPackageDir = project.baseDir.path + File.separator + sDaoModule
            var managerPath = project.baseDir.path + File.separator + sManagerModule
            //资源文件路径
            sMapperXmlDir = sDaoPackageDir + "/src/main/resources/mapper/"
            sMapperExtXmlDir = sDaoPackageDir + "/src/main/resources/mapper/ext/"
            //代码文件路径
            sBaseDir = sDaoPackageDir + "/src/main/java/" + getFilePathFromPackage(sDaoPackage)
            sDoJavaDir = sDaoPackageDir + "/src/main/java/" + getFilePathFromPackage(sDaoPackage) + "/model"
            sQueryJavaDir = sDaoPackageDir + "/src/main/java/" + getFilePathFromPackage(sDaoPackage) + "/query"
            sMapperJavaDir = sDaoPackageDir + "/src/main/java/" + getFilePathFromPackage(sDaoPackage) + "/mapper"
            sMapperExtJavaDir = sDaoPackageDir + "/src/main/java/" + getFilePathFromPackage(sDaoPackage) + "/mapper/ext"
            sManagerJavaDir = managerPath + "/src/main/java/" + getFilePathFromPackage(sManagerPackage) + "/manager"
            sManagerImplJavaDir = sManagerJavaDir + "/impl"
            //

            sConfigured = true
        }

        fun getFilePathFromPackage(packageName: String): String {
            return packageName.replace(".", "/")
        }

        fun clearConfig() {
            sIsMHCStaff = false
            sDaoModule = ""
            sDaoPackage = ""
            sManagerModule = ""
            sManagerPackage = ""
            sManagerImplPackage = ""
            sTablePrefix = ""
            sMapperXmlDir = ""
            sMapperExtXmlDir = ""
            sDoJavaDir = ""
            sQueryJavaDir = ""
            sMapperJavaDir = ""
            sManagerJavaDir = ""
            sManagerImplJavaDir = ""
            sMapperExtJavaDir = ""
            sConfigured = false
        }

    }
}