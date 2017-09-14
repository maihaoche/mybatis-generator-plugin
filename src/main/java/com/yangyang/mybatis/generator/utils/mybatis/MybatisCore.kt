package main.java.com.yangyang.mybatis.generator.utils.mybatis

import com.intellij.openapi.project.Project
import main.java.com.yangyang.mybatis.generator.MyConstant
import main.java.com.yangyang.mybatis.generator.utils.NotificationUtil
import main.java.com.yangyang.mybatis.generator.utils.PlatformUtil
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.math.NumberUtils
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.Velocity
import java.io.*
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.*
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import org.apache.velocity.runtime.RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.runtime.RuntimeConstants


/**
 * 类简介：mybatis 生成文件的核心工具类
 * 作者：  yang
 * 时间：  17/7/24
 * 邮箱：  yangyang1@maihaoche.com
 */
class MybatisCore {


    companion object {

        /**
         * 批量生成java数据对象类文件和sqlmap文件

         * @param tables 表 多个表用逗号分隔 table1,table2,table3
         * *
         * @throws Exception 抛出异常
         */
        fun batchGen(tables: List<String>, project: Project): Exception? {
            if (!MyBatisGenConst.sConfigured) {
                return Exception("还没有配置参数，请先配置")
            }
            try {
                for (table in tables) {
                    gen(table.trim { it <= ' ' }, project)
                }
            } catch (e: Exception) {
                return e
            }
            return null
        }


        /**
         * 根据表名获取字段信息

         * @param cn    数据库连接
         * *
         * @param table 数据库表名
         * *
         * @return 列infoList
         * *
         * @throws Exception 抛出错误
         */
        @Throws(Exception::class)
        private fun getColInfoList(cn: Connection, table: String): ArrayList<Map<String, String>> {
            val sql = "select * from $table where 1>2"
            var stmt: Statement? = null
            var rs: ResultSet? = null
            try {
                //获取主键串
                val dbmd = cn.metaData
                val primaryKeys = dbmd.getPrimaryKeys(null, null, table)
                val pks = getstrPimaryKeys(primaryKeys)

                stmt = cn.createStatement()
                rs = stmt!!.executeQuery(sql)
                // 获取结果集元数据信息
                val rsmd = rs!!.metaData

                val num = rsmd.columnCount
                var map: MutableMap<String, String>
                val list = ArrayList<Map<String, String>>()
                for (i in 1..num) {
                    map = HashMap<String, String>()
                    map.put(MyBatisGenConst.RSMD_COLUMN_NAME, rsmd.getColumnName(i))
                    map.put(MyBatisGenConst.RSMD_COLUMN_CLASS_NAME, rsmd.getColumnClassName(i))
                    map.put(MyBatisGenConst.RSMD_COLUMN_TYPE_NAME, rsmd.getColumnTypeName(i))
                    map.put(MyBatisGenConst.RSMD_COLUMN_PRECISION, rsmd.getPrecision(i).toString() + "")
                    map.put(MyBatisGenConst.RSMD_COLUMN_SCALE, rsmd.getScale(i).toString() + "")
                    val remarks = getColRemarks(cn, table, rsmd.getColumnName(i))
                    map.put(MyBatisGenConst.RSMD_COLUMN_REMARKS, remarks)
                    list.add(map)
                }
                //主键串放入list
                map = HashMap<String, String>()
                map.put(MyBatisGenConst.RSMD_COLUMN_PRIMARY_KEY, pks)
                list.add(map)
                return list
            } catch (e: Exception) {
                throw Exception(e.toString() + ",table=" + table, e)
            } finally {
                try {
                    if (stmt != null) {
                        stmt.close()
                    }
                } catch (e2: Exception) {
                    e2.printStackTrace()
                }

                try {
                    if (rs != null) {
                        rs.close()
                    }
                } catch (e2: Exception) {
                    e2.printStackTrace()
                }

            }
        }

        /**
         * 根据表列名获取字段注释

         * @param cn    数据库连接
         * *
         * @param table 数据库表名
         * *
         * @return 字段注释
         * *
         * @throws Exception 抛出异常
         */
        @Throws(Exception::class)
        private fun getColRemarks(cn: Connection, table: String, columnName: String): String {
            //获取主键串
            val dbmd = cn.metaData
            val resultSet = dbmd.getTables(null, "%", "%", arrayOf("TABLE"))
            while (resultSet.next()) {
                val tableName = resultSet.getString("TABLE_NAME")
                if (table == tableName) {
                    val rs = dbmd.getColumns(null, "%", tableName, "%")
                    while (rs.next()) {
                        if (rs.getString("COLUMN_NAME") == columnName) {
                            return rs.getString("REMARKS")
                        }
                    }
                }
            }
            return ""
        }

        /**
         * 获取列信息

         * @param table 表名
         * *
         * @return 列信息
         * *
         * @throws Exception 抛出错误
         */
        @Throws(Exception::class)
        private fun getColInfoList(table: String): ArrayList<Map<String, String>> {
            val cn = getConnection()
            try {
                return getColInfoList(cn, table)
            } finally {
                try {
                    cn.close()
                } catch (e2: Exception) {
                    e2.printStackTrace()
                }

            }
        }

        /**
         * 获取参数列表

         * @param colInfoList 列信息列表
         * *
         * @return 参数列表
         * *
         * @throws Exception 抛出异常
         */
        @Throws(Exception::class)
        private fun makeParamList(colInfoList: List<Map<String, String>>): ArrayList<Map<String, String?>> {
            val list = ArrayList<Map<String, String?>>()
            var map: Map<String, String>
            var mapNew: MutableMap<String, String?>
            for (aColInfoList in colInfoList) {
                map = aColInfoList
                mapNew = HashMap<String, String?>()
                val columnName = map[MyBatisGenConst.RSMD_COLUMN_NAME]
                val columnClassName = map[MyBatisGenConst.RSMD_COLUMN_CLASS_NAME]
                val columnTypeName = map[MyBatisGenConst.RSMD_COLUMN_TYPE_NAME]
                val scaleStr = map[MyBatisGenConst.RSMD_COLUMN_SCALE]
                val scale = NumberUtils.toInt(scaleStr)
                val precisionStr = map[MyBatisGenConst.RSMD_COLUMN_PRECISION]
                val remarks = map[MyBatisGenConst.RSMD_COLUMN_REMARKS]
                val precision = NumberUtils.toInt(precisionStr)
                val javaType = getJavaType(columnClassName, columnTypeName, scale, precision)
                val jdbcType = getJdbcType(columnClassName, columnTypeName)
                val propName = getPropName(columnName)
                val setMethod = getSetMethod(propName)
                val getMethod = getGetMethod(propName)
                mapNew.put(MyBatisGenConst.VP_COLUMN_NAME, columnName?.toLowerCase())
                mapNew.put(MyBatisGenConst.VP_COLUMN_REMARKS, remarks)
                mapNew.put(MyBatisGenConst.VP_PROP_NAME, propName)
                mapNew.put(MyBatisGenConst.VP_JAVA_TYPE, javaType)
                mapNew.put(MyBatisGenConst.VP_JDBC_TYPE, jdbcType)
                mapNew.put(MyBatisGenConst.VP_GET_METHOD, getMethod)
                mapNew.put(MyBatisGenConst.VP_SET_METHOD, setMethod)
                list.add(mapNew)
            }
            return list
        }

        /**
         * 获取字段的java类型

         * @param columnClassName 字段类名
         * *
         * @param columnTypeName  字段类型名称
         * *
         * @param scale           精度 小数位数
         * *
         * @param precision       精度
         * *
         * @return java类型
         */
        private fun getJavaType(columnClassName: String?, columnTypeName: String?, scale: Int, precision: Int): String {
            if (columnClassName == null || columnTypeName == null) return ""

            if (columnClassName == "java.sql.Timestamp") {
                return "LocalDateTime"
            }
            if (columnClassName == "java.lang.String") {
                return "String"
            }
            if (columnTypeName == "DECIMAL" && scale < 1) {
                return "Long"
            }
            if (columnTypeName == "DECIMAL" && scale > 0) {
                return "java.math.BigDecimal"
            }
            if (columnTypeName.startsWith("BIGINT")) {
                return "Long"
            }
            if (columnTypeName.startsWith("INT")) {
                return "Integer"
            }
            if (columnTypeName.startsWith("TINYINT") && precision == 1) {
                return "Boolean"
            }
            if (columnTypeName.startsWith("TINYINT") && precision != 1) {
                return "Integer"
            }
            if (columnTypeName.startsWith("BIT") && precision > 1) {
                return "Integer"
            }
            if (columnTypeName.startsWith("BIT") && precision == 1) {
                return "Boolean"
            }
            if (columnTypeName.startsWith("SMALLINT")) {
                return "Integer"
            }
            return columnClassName
        }

        /**
         * 获取jdbc类型

         * @param columnClassName 字段类名
         * *
         * @param columnTypeName  字段类型名称
         * *
         * @return jdbc类型
         */
        private fun getJdbcType(columnClassName: String?, columnTypeName: String?): String? {
            if (columnClassName == null || columnTypeName == null) return ""
            if (columnClassName == "java.lang.String") {
                return "VARCHAR"
            }
            if (columnClassName.startsWith("java.sql.")) {
                return "TIMESTAMP"
            }
            if (columnTypeName.startsWith("NUMBER")) {
                return "DECIMAL"
            }
            if (columnTypeName.startsWith("INT")) {
                return "INTEGER"
            }
            return columnTypeName
        }

        /**
         * 根据表名获取java类型

         * @param tableName 表名
         * *
         * @return java类型
         */
        private fun getClassName(tableName: String): String {
            var t = tableName.toLowerCase()
            val arr = t.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val s = StringBuilder()
            for (anArr in arr) {
                s.append(StringUtils.capitalize(anArr))
            }
            return s.toString()
        }

        /**
         * 根据字段名获取java数据对象属性名

         * @param columnName 字段名
         * *
         * @return 属性名
         */
        private fun getPropName(columnName: String?): String {
            if (columnName == null) return ""
            val t = columnName.toLowerCase()
            val arr = t.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val num = arr.size
            val s = StringBuilder()
            for (i in 0..num - 1) {
                if (i > 0) {
                    s.append(StringUtils.capitalize(arr[i]))
                } else {
                    s.append(arr[i])
                }
            }
            return s.toString()
        }

        private fun getSetMethod(propName: String): String {
            return "set" + StringUtils.capitalize(propName)
        }

        private fun getGetMethod(propName: String): String {
            return "get" + StringUtils.capitalize(propName)
        }

        private fun getColsStr(list: List<Map<String, String?>>): String {
            val num = list.size
            var map: Map<String, String?>
            var colName: String?
            val stringBuilder = StringBuilder()
            for (i in 0..num - 1) {
                map = list[i]
                colName = map[MyBatisGenConst.VP_COLUMN_NAME]
                if (i > 0) {
                    stringBuilder.append(",")
                }
                stringBuilder.append(colName)
            }
            return stringBuilder.toString()
        }

        @Throws(SQLException::class)
        private fun getstrPimaryKeys(primaryKeys: ResultSet): String {
            val sb = StringBuilder()
            val pks: String
            while (primaryKeys.next()) {
                sb.append(primaryKeys.getString(MyBatisGenConst.COLUMN_NAME)).append(",")
            }
            pks = sb.toString()
            return if (StringUtils.isBlank(pks)) "" else pks.substring(0, pks.length - 1)
        }

        private var sVelocityInited = false;
        /**
         * Velocity 模板合并

         * @param template 模板字符串 如 hello,${name}
         * *
         * @param paramMap 参数
         * *
         * @return 合并结果
         * *
         * @throws Exception 抛出错误
         */
        @Throws(Exception::class)
        private fun merge(template: String, paramMap: Map<String, Any>): String {
            val vc = VelocityContext(paramMap)
            val writer = StringWriter()
            Velocity.evaluate(vc, writer, "mybatis_code_gen", template)
            return writer.buffer.toString()
        }

        /**
         * 获取sqlmap 参数列表 去掉 主键 GMT_CREATE GMT_MODIFIED 字段

         * @param paramList 参数列表 Map
         * *
         * @param pks 主键
         * *
         * @return sql 参数列表
         * *
         * @throws Exception 抛出错误
         */
        @Throws(Exception::class)
        private fun getSqlmapParamList(paramList: ArrayList<Map<String, String?>>, pks: String): List<Map<String, String?>> {
            val list = ArrayList<Map<String, String?>>()
            var tmp: Map<String, String?>
            var map: MutableMap<String, String?>
            for (aParamList in paramList) {
                tmp = aParamList
                val columnName = tmp[MyBatisGenConst.VP_COLUMN_NAME]
                if (columnName.equals(pks, ignoreCase = true)) {
                    continue
                }
                if (columnName.equals("GMT_CREATE", ignoreCase = true)) {
                    continue
                }
                if (columnName.equals("GMT_MODIFIED", ignoreCase = true)) {
                    continue
                }
                map = HashMap<String, String?>()
                map.putAll(tmp)
                list.add(map)
            }
            return list
        }

        /**
         * 根据表名生成java数据对象类文件和sqlmap文件

         * @param table 表名
         * *
         * @throws Exception 抛出错误
         */
        @Throws(Exception::class)
        private fun gen(table: String, project: Project) {
            var tableLocal = table
            val colInfoList = getColInfoList(tableLocal)
            var pks: String? = ""
            if (colInfoList.size > 0) {
                pks = colInfoList.removeAt(colInfoList.size - 1)[MyBatisGenConst.RSMD_COLUMN_PRIMARY_KEY]
            }
            val paramList = makeParamList(colInfoList)
            val isSharding = Pattern.compile(MyBatisGenConst.SHARDING_SUFFIX_REG).matcher(tableLocal).find()

            if (isSharding) {
                // 去掉分库分表后面的表后缀，如_0001
                tableLocal = tableLocal.replace(MyBatisGenConst.SHARDING_SUFFIX_REG.toRegex(), "")
            }

            val className = getClassName(tableLocal)

            val doTemplate = readFileToString(MyBatisGenConst.DO_TEMPLATE)

            val queryTemplate = readFileToString(MyBatisGenConst.QUERY_TEMPLATE)

            val sqlmapTemplate = readFileToString(MyBatisGenConst.SQLMAP_TEMPLATE)
            val mapperTemplate = readFileToString(MyBatisGenConst.MAPPER_TEMPLATE)
            val managerTemplate = readFileToString(MyBatisGenConst.MANAGER_TEMPLATE)
            val managerImplTemplate = readFileToString(MyBatisGenConst.MANAGER_IMPL_TEMPLATE)
            val sqlmapExtTemplate = readFileToString(MyBatisGenConst.SQLMAP_EXT_TEMPLATE)
            val mapperExtTemplate = readFileToString(MyBatisGenConst.MAPPER_EXT_TEMPLATE)


            val param = HashMap<String, Any>()

            param.put(MyBatisGenConst.VP_DO_PACKAGE, MyBatisGenConst.DO_PACKAGE)
            param.put(MyBatisGenConst.VP_QUERY_PACKAGE, MyBatisGenConst.QUERY_PACKAGE)
            param.put(MyBatisGenConst.VP_MAPPER_PACKAGE, MyBatisGenConst.MAPPER_PACKAGE)
            param.put(MyBatisGenConst.VP_MANAGER_PACKAGE, MyBatisGenConst.MANAGER_PACKAGE)
            param.put(MyBatisGenConst.VP_MANAGER_IMPL_PACKAGE, MyBatisGenConst.MANAGER_IMPL_PACKAGE)
            param.put(MyBatisGenConst.VP_MAPPER_EXT_PACKAGE, MyBatisGenConst.MAPPER_EXT_PACKAGE)
            param.put(MyBatisGenConst.VP_CLASS_NAME, className)
            param.put(MyBatisGenConst.VP_MAPPER_PROPERTY_NAME, className.substring(0, 1).toLowerCase() + className.substring(1) + MyBatisGenConst.MAPPER_EXT_SUFFIX)

            param.put(MyBatisGenConst.VP_LIST, paramList)
            param.put(MyBatisGenConst.VP_QUERY_PREFIX, MyBatisGenConst.QUERY_PREFIX)
            param.put(MyBatisGenConst.VP_DO_SUFFIX, MyBatisGenConst.DO_SUFFIX)
            param.put(MyBatisGenConst.VP_MAPPER_SUFFIX, MyBatisGenConst.MAPPER_SUFFIX)
            param.put(MyBatisGenConst.VP_MANAGER_SUFFIX, MyBatisGenConst.MANAGER_SUFFIX)
            param.put(MyBatisGenConst.VP_MANAGER_IMPL_SUFFIX, MyBatisGenConst.MANAGER_IMPL_SUFFIX)


            param.put(MyBatisGenConst.VP_MAPPER_EXT_SUFFIX, MyBatisGenConst.MAPPER_EXT_SUFFIX)

            var vpTableName = tableLocal.toLowerCase()
            if (isSharding) {
                vpTableName += "_\$tabNum$"
            }

            param.put(MyBatisGenConst.VP_TABLE_NAME, vpTableName)
            param.put(MyBatisGenConst.VP_SERIAL_VERSION_UID, "" + (Math.random() * 1000000000000000000L).toLong())

            param.put(MyBatisGenConst.VP_SERIAL_VERSION_UID2, "" + (Math.random() * 1000000000000000000L).toLong())


            val doResult = merge(doTemplate, param)

            // 获取字段名不包含 id gmt_create gmt_modified TODO 去掉主键
            val sqlmapParamList = getSqlmapParamList(paramList, pks.toString())
            param.put(MyBatisGenConst.VP_LIST, sqlmapParamList)

            val colsWithoutCommColumns = getColsStr(sqlmapParamList)
            param.put(MyBatisGenConst.VP_COLS_WITHOUT_COMMON_COLUMNS, colsWithoutCommColumns)
            val cols = pks + "," + MyBatisGenConst.COMMON_COLUMN_STR + colsWithoutCommColumns
            param.put(MyBatisGenConst.VP_COLS, cols)
            param.put(MyBatisGenConst.VP_PRIMARY_KEY, pks.toString())
            param.put(MyBatisGenConst.VP_PROP_PRIMARY_KEY, getPropName(pks))
            val sqlmapResult = merge(sqlmapTemplate, param)
            val mapperResult = merge(mapperTemplate, param)
            val managerResult = merge(managerTemplate, param)
            val managerImplResult = merge(managerImplTemplate, param)
            val queryResult = merge(queryTemplate, param)
            val mapperExtResult = merge(mapperExtTemplate, param)
            val sqlmapExtResult = merge(sqlmapExtTemplate, param)

            val doOutFilePath = MyBatisGenConst.MAPPER_DO_DIR + "/" + className + MyBatisGenConst.DO_SUFFIX + ".java"
            val queryOutFilePath = MyBatisGenConst.MAPPER_QUERY_DIR + "/" + className + MyBatisGenConst.QUERY_PREFIX + ".java"
            val sqlmapOutFilePath = MyBatisGenConst.MAPPER_XML_DIR + "/" + className + MyBatisGenConst.MAPPER_SUFFIX + ".xml"
            val mapperOutFilePath = MyBatisGenConst.MAPPER_JAVA_DIR + "/" + className + MyBatisGenConst.MAPPER_SUFFIX + ".java"
            val managerOutFilePath = MyBatisGenConst.MANAGER_JAVA_DIR + "/" + className + MyBatisGenConst.MANAGER_SUFFIX + ".java"
            val managerImplOutFilePath = MyBatisGenConst.MANAGER_IMPL_JAVA_DIR + "/" + className + MyBatisGenConst.MANAGER_IMPL_SUFFIX + ".java"
            val sqlmapExtOutFilePath = MyBatisGenConst.MAPPER_EXT_XML_DIR + "/" + className + MyBatisGenConst.MAPPER_EXT_SUFFIX + ".xml"
            val mapperExtOutFilePath = MyBatisGenConst.MAPPER_EXT_JAVA_DIR + "/" + className + MyBatisGenConst.MAPPER_EXT_SUFFIX + ".java"

            var success = File(MyBatisGenConst.MAPPER_DO_DIR).mkdirs()
            if (success) {
                NotificationUtil.info(String.format("目录 %s 创建成功", MyBatisGenConst.MAPPER_DO_DIR), project)
            }
            success = File(MyBatisGenConst.MAPPER_QUERY_DIR).mkdirs()
            if (success) {
                NotificationUtil.info(String.format("目录 %s 创建成功", MyBatisGenConst.MAPPER_QUERY_DIR), project)
            }
            success = File(MyBatisGenConst.MANAGER_JAVA_DIR).mkdirs()
            if (success) {
                NotificationUtil.info(String.format("目录 %s 创建成功", MyBatisGenConst.MANAGER_JAVA_DIR), project)
            }
            success = File(MyBatisGenConst.MANAGER_IMPL_JAVA_DIR).mkdirs()
            if (success) {
                NotificationUtil.info(String.format("目录 %s 创建成功", MyBatisGenConst.MANAGER_IMPL_JAVA_DIR), project)
            }

            success = File(MyBatisGenConst.MAPPER_XML_DIR).mkdirs()
            if (success) {
                NotificationUtil.info(String.format("目录 %s 创建成功", MyBatisGenConst.MAPPER_XML_DIR), project)
            }
            success = File(MyBatisGenConst.MAPPER_JAVA_DIR).mkdirs()
            if (success) {
                NotificationUtil.info(String.format("目录 %s 创建成功", MyBatisGenConst.MAPPER_JAVA_DIR), project)
            }
            success = File(MyBatisGenConst.MAPPER_EXT_XML_DIR).mkdirs()
            if (success) {
                NotificationUtil.info(String.format("目录 %s 创建成功", MyBatisGenConst.MAPPER_EXT_XML_DIR), project)
            }
            success = File(MyBatisGenConst.MAPPER_EXT_JAVA_DIR).mkdirs()
            if (success) {
                NotificationUtil.info(String.format("目录 %s 创建成功", MyBatisGenConst.MAPPER_EXT_JAVA_DIR), project)
            }

            val sqlmapOutFile = File(sqlmapOutFilePath)
            if (!sqlmapOutFile.exists()) {
                sqlmapOutFile.createNewFile()
            }
            val doOutFile = File(doOutFilePath)
            if (!doOutFile.exists()) {
                doOutFile.createNewFile()
            }
            val mapperOutFile = File(mapperOutFilePath)
            if (!mapperOutFile.exists()) {
                mapperOutFile.createNewFile()
            }


            val queryOutFile = File(queryOutFilePath)
            if (!queryOutFile.exists()) {
                queryOutFile.createNewFile()
            }

            //强行覆盖的文件
            writeStringToFile(sqlmapOutFile, sqlmapResult, project)
            writeStringToFile(doOutFile, doResult, project)
            writeStringToFile(queryOutFile, queryResult, project)
            writeStringToFile(mapperOutFile, mapperResult, project)


            //如果存在则不会重新产生的文件。
            val mapperExtOutFile = File(mapperExtOutFilePath)
            if (!mapperExtOutFile.exists()) {
                try {
                    if (mapperExtOutFile.createNewFile()) {
                        writeStringToFile(mapperExtOutFile, mapperExtResult, project)
                    }
                } catch (e: IOException) {
                    print("create file error："+e)
                }

            }

            val sqlmapExtOutFile = File(sqlmapExtOutFilePath)
            if (!sqlmapExtOutFile.exists()) {

                if (sqlmapExtOutFile.createNewFile()) {
                    writeStringToFile(sqlmapExtOutFile, sqlmapExtResult, project)
                }
            }


            val managerOutFile = File(managerOutFilePath)
            if (!managerOutFile.exists()) {
                createAndWriteFile(managerResult, managerOutFilePath, managerOutFile, project)
            }

            val managerImplOutFile = File(managerImplOutFilePath)
            if (!managerImplOutFile.exists()) {
                createAndWriteFile(managerImplResult, managerImplOutFilePath, managerImplOutFile, project)
            }
        }

        @Throws(Exception::class)
        private fun createAndWriteFile(managerResult: String, managerOutFilePath: String, managerOutFile: File, project: Project) {
            val success: Boolean
            success = managerOutFile.createNewFile()
            if (!success) {
                NotificationUtil.info(String.format("创建文件 %s 失败", managerOutFilePath), project)
            } else {
                writeStringToFile(managerOutFile, managerResult, project)
            }
        }

        @Throws(Exception::class)
        private fun writeStringToFile(file: File, data: String, project: Project) {
            val path = Paths.get(file.absolutePath)
            var writer: BufferedWriter? = null
            try {
                writer = Files.newBufferedWriter(path)
                writer.write(data)
                NotificationUtil.info("文件:" + path + "生成成功", project)
            } catch (e: Exception) {
                throw e
            } finally {
                writer?.close()
            }
        }

        @Throws(Exception::class)
        private fun readFileToString(fileRelativePath: String): String {
            var bufferReader = BufferedReader(InputStreamReader(javaClass.getResourceAsStream(fileRelativePath)))
            var fileStr = ""
            var lineStr: String?
            while (true) {
                lineStr = bufferReader.readLine()
                if (lineStr == null) {
                    break
                }
                fileStr += "\n" + lineStr
            }
            return fileStr
        }

        /**
         * 获取数据库连接

         * @return 数据库连接
         * *
         * @throws ClassNotFoundException 类找不到异常
         * *
         * @throws SQLException SQL 异常
         * *
         * @throws IOException IO 异常
         */
        @Throws(ClassNotFoundException::class, SQLException::class, IOException::class)
        private fun getConnection(): Connection {
            Class.forName("com.mysql.jdbc.Driver")
            val url = PlatformUtil.getData(MyConstant.DB_URL, "")
            val user = PlatformUtil.getData(MyConstant.DB_USER, "")
            val psw = PlatformUtil.getData(MyConstant.DB_PW, "")
            var connection = DriverManager.getConnection(url, user, psw)
            if (connection.isValid(0)) {
                return connection
            } else {
                throw SQLException("数据库链接不上")
            }
        }


    }
}