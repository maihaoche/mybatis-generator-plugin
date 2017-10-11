package main.java.com.yangyang.mybatis.generator.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.externalSystem.util.ExternalSystemUiUtil
import com.intellij.ui.CheckBoxList
import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.components.CheckBox
import main.java.com.yangyang.mybatis.generator.MyConstant
import main.java.com.yangyang.mybatis.generator.utils.NotificationUtil
import main.java.com.yangyang.mybatis.generator.utils.PlatformUtil
import main.java.com.yangyang.mybatis.generator.utils.UIUtil
import main.java.com.yangyang.mybatis.generator.utils.mybatis.MyBatisGenConst
import java.awt.GridBagLayout
import java.util.*
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

        val content = JPanel(GridBagLayout())

        val daoModule = UIUtil.addTextInputToPanel("DO,Query,Mapper所在的module名:        ", MyConstant.DAO_MODULE, content)
        val daoPackagePath = UIUtil.addTextInputToPanel("DO,Query,Mapper所在的package名:        ", MyConstant.DAO_PACKAGE, content)
        val managerModule = UIUtil.addTextInputToPanel("Manager所在的module名:        ", MyConstant.MANAGER_MODULE, content)
        val managerPackagePath = UIUtil.addTextInputToPanel("Manager所在的package:        ", MyConstant.MANAGER_PACKAGE, content)
        val tablePrefix = UIUtil.addTextInputToPanel("表名的前缀:        ", MyConstant.TABLE_PREFIX, content)
        content.add(JLabel("是否用于卖好车内部代码:        "), ExternalSystemUiUtil.getFillLineConstraints(0))
        // 卖好车内部圆通的选择
        val isMHCStaffCheck = CheckBoxList<String>()
        isMHCStaffCheck.setItems(Arrays.asList("是"), null)
        isMHCStaffCheck.border = IdeBorderFactory.createEmptyBorder(8)
        content.add(isMHCStaffCheck, ExternalSystemUiUtil.getFillLineConstraints(0))
        if (PlatformUtil.getData(MyConstant.MHC_STAFF, false)) {
            isMHCStaffCheck.setItemSelected("是", true)
        }
        //清空缓存
        val clearCacheBtn = JButton("清除缓存")
        clearCacheBtn.addActionListener {
            PlatformUtil.setData(MyConstant.DAO_MODULE, "")
            PlatformUtil.setData(MyConstant.DAO_PACKAGE, "")
            PlatformUtil.setData(MyConstant.MANAGER_MODULE, "")
            PlatformUtil.setData(MyConstant.MANAGER_PACKAGE, "")
            isMHCStaffCheck.clearSelection()
            MyBatisGenConst.clearConfig()
            NotificationUtil.info("清除缓存成功", project)
            daoModule.text = ""
            daoPackagePath.text = ""
            managerModule.text = ""
            managerPackagePath.text = ""
            tablePrefix.text = ""
        }
        clearCacheBtn.border = IdeBorderFactory.createEmptyBorder(14, 4, 4, 4)
        content.add(clearCacheBtn, ExternalSystemUiUtil.getLabelConstraints(0))
        //显示dialog
        content.border = IdeBorderFactory.createEmptyBorder(0, 0, 8, 0)

        UIUtil.showDialog("路径配置参数", project, content, {
            if (it) {
                if (daoPackagePath.text.trim().isNullOrBlank()
                        || managerModule.text.trim().isNullOrBlank()
                        || managerPackagePath.text.trim().isNullOrBlank()
                        ) {
                    NotificationUtil.popError("路径配置不能为空", event)
                    return@showDialog
                }
                PlatformUtil.setData(MyConstant.DAO_MODULE, daoModule.text)
                PlatformUtil.setData(MyConstant.DAO_PACKAGE, daoPackagePath.text)
                PlatformUtil.setData(MyConstant.MANAGER_MODULE, managerModule.text)
                PlatformUtil.setData(MyConstant.MANAGER_PACKAGE, managerPackagePath.text)
                PlatformUtil.setData(MyConstant.TABLE_PREFIX, tablePrefix.text)
                val isMHCStaff = isMHCStaffCheck.isItemSelected("是")
                PlatformUtil.setData(MyConstant.MHC_STAFF, isMHCStaff)

                MyBatisGenConst.setConfig(
                        daoModule.text,
                        daoPackagePath.text,
                        managerModule.text,
                        managerPackagePath.text,
                        tablePrefix.text,
                        isMHCStaff,
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