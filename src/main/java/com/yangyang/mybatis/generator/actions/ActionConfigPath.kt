package main.java.com.yangyang.mybatis.generator.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.externalSystem.util.ExternalSystemUiUtil
import com.intellij.ui.IdeBorderFactory
import main.java.com.yangyang.mybatis.generator.MyConstant
import main.java.com.yangyang.mybatis.generator.utils.NotificationUtil
import main.java.com.yangyang.mybatis.generator.utils.PlatformUtil
import main.java.com.yangyang.mybatis.generator.utils.UIUtil
import main.java.com.yangyang.mybatis.generator.utils.mybatis.MyBatisGenConst
import java.awt.GridBagLayout
import javax.swing.*


/**
 * 类简介：配置的动作
 * 作者：  yang
 * 时间：  17/7/24
 * 邮箱：  yangyang1@maihaoche.com
 */
class ActionConfigPath : AnAction() {
    /**
     * 执行相关的动作
     */
    override fun actionPerformed(event: AnActionEvent?) {
        if (event == null) {
            return
        }
        val project = event.getProject()
        //总的内容
        val content = JPanel(GridBagLayout())

        val daoModule = UIUtil.addTextInputToPanel("Dao的module名:        ", MyConstant.DAO_MODULE, content)
        val doPackagePath = UIUtil.addTextInputToPanel("DO目录的package全名:        ", MyConstant.DO_PACKAGE, content)
        val queryPackagePath = UIUtil.addTextInputToPanel("Query目录的package全名:        ", MyConstant.QUERY_PACKAGE, content)
        val mapperPackagePath = UIUtil.addTextInputToPanel("Mapper目录的package全名:        ", MyConstant.MAPPER_PACKAGE, content)
        val managerModule = UIUtil.addTextInputToPanel("Manager所在的module名:        ", MyConstant.MANAGER_MODULE, content)
        val managerPackagePath = UIUtil.addTextInputToPanel("Manager目录的package全名:        ", MyConstant.MANAGER_PACKAGE, content)


        //清空缓存
        val clearCacheBtn = JButton("清除缓存")
        clearCacheBtn.addActionListener {
            PlatformUtil.setData(MyConstant.DAO_MODULE, "")
            PlatformUtil.setData(MyConstant.DO_PACKAGE, "")
            PlatformUtil.setData(MyConstant.QUERY_PACKAGE, "")
            PlatformUtil.setData(MyConstant.MAPPER_PACKAGE, "")
            PlatformUtil.setData(MyConstant.MANAGER_MODULE, "")
            PlatformUtil.setData(MyConstant.MANAGER_PACKAGE, "")
            MyBatisGenConst.clearConfig()
            NotificationUtil.info("清除缓存成功", project)
            daoModule.text = ""
            doPackagePath.text = ""
            queryPackagePath.text = ""
            mapperPackagePath.text = ""
            managerModule.text = ""
            managerPackagePath.text = ""
        }
        clearCacheBtn.border = IdeBorderFactory.createEmptyBorder(14, 4, 4, 4)
        content.add(clearCacheBtn, ExternalSystemUiUtil.getLabelConstraints(0))
        //显示dialog
        content.border = IdeBorderFactory.createEmptyBorder(0, 0, 8, 0)

        UIUtil.showDialog("路径配置参数", project, content, {
            if (it) {
                if (doPackagePath.text.trim().isNullOrBlank()
                        || queryPackagePath.text.trim().isNullOrBlank()
                        || managerModule.text.trim().isNullOrBlank()
                        || managerPackagePath.text.trim().isNullOrBlank()
                        || mapperPackagePath.text.trim().isNullOrBlank()
                        ) {
                    NotificationUtil.popInfo("配置不能为空", event)
                    return@showDialog
                }
                PlatformUtil.setData(MyConstant.DAO_MODULE, daoModule.text)
                PlatformUtil.setData(MyConstant.DO_PACKAGE, doPackagePath.text)
                PlatformUtil.setData(MyConstant.QUERY_PACKAGE, queryPackagePath.text)
                PlatformUtil.setData(MyConstant.MAPPER_PACKAGE, mapperPackagePath.text)
                PlatformUtil.setData(MyConstant.MANAGER_MODULE, managerModule.text)
                PlatformUtil.setData(MyConstant.MANAGER_PACKAGE, managerPackagePath.text)
                MyBatisGenConst.setConfig(
                        daoModule.text,
                        doPackagePath.text,
                        queryPackagePath.text,
                        mapperPackagePath.text,
                        managerModule.text,
                        managerPackagePath.text,
                        project)
                NotificationUtil.popInfo("路径配置修改成功", event)
            }
        })
    }

    companion object {
        /**
         * 显示配置窗口
         */
        fun showConfigureDialog(msg: String, event: AnActionEvent) {
            //总的内容
            val content = JPanel(GridBagLayout())
            //设置切换为全module任务名
            content.add(JLabel(msg), ExternalSystemUiUtil.getFillLineConstraints(0))

            UIUtil.showDialog("错误提示", event.project, content, {
                if (it) {
                    PlatformUtil.performActionWithPath(event, MyConstant.ACTION_TOOLS_MENU, MyConstant.ACTION_MYBATIS, MyConstant.ACTION_MYBATIS_PATH_CONFIGURE)
                }
            })
        }
    }
}