package main.java.com.yangyang.mybatis.generator.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.externalSystem.util.ExternalSystemUiUtil
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.progress.util.ProgressIndicatorUtils
import com.intellij.ui.CheckBoxList
import com.intellij.ui.IdeBorderFactory
import com.intellij.util.containers.isNullOrEmpty
import com.yangyang.mybatis.generator.actions.ActionConfigureDB
import main.java.com.yangyang.mybatis.generator.MyConstant
import main.java.com.yangyang.mybatis.generator.utils.NotificationUtil
import main.java.com.yangyang.mybatis.generator.utils.PlatformUtil
import main.java.com.yangyang.mybatis.generator.utils.UIUtil
import main.java.com.yangyang.mybatis.generator.utils.mybatis.MyBatisGenConst
import main.java.com.yangyang.mybatis.generator.utils.mybatis.MybatisCore
import java.awt.GridBagLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.ListSelectionModel

/**
 * 类简介：产生模板代码的动作
 * 作者：  yang
 * 时间：  17/7/24
 * 邮箱：  yangyang1@maihaoche.com
 */
class ActionGenerate : AnAction() {


    override fun actionPerformed(event: AnActionEvent?) {
        if (event == null) {
            return
        }
        val project = event.project
        if (project == null) {
            return
        }

        if (!MyBatisGenConst.initConfig(project)) {
            ActionConfigPath.showConfigureDialog("还没有设置生成代码路径，是否现在去设置?", event)
            return
        }
        if (!PlatformUtil.getData(MyConstant.DB_CONF_INITED, false)) {
            ActionConfigureDB.showConfigureDialog("还没有配置数据库连接，是否现在去设置?", event)
            return
        }

        val content = JPanel(GridBagLayout())
        content.add(JLabel("请输入需要产生代码的表名，以\",\"隔开:        "), ExternalSystemUiUtil.getFillLineConstraints(0))

        val talbeNameInput = UIUtil.getTextField("")
        content.add(talbeNameInput, ExternalSystemUiUtil.getFillLineConstraints(0))

        var tableHistory = PlatformUtil.getDatas(MyConstant.TABLE_HISTORY)?.toList()
        val orphanModulesList = CheckBoxList<String>()
        if (tableHistory != null && tableHistory.size > 0) {
            orphanModulesList.selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
            orphanModulesList.setItems(tableHistory) { moduleName -> moduleName }
            orphanModulesList.border = IdeBorderFactory.createEmptyBorder(8)
            content.add(orphanModulesList, ExternalSystemUiUtil.getFillLineConstraints(0))

            //清空缓存
            val clearCacheBtn = JButton("清除缓存")
            clearCacheBtn.addActionListener {
                content.remove(orphanModulesList)
                content.updateUI()
            }
            clearCacheBtn.border = IdeBorderFactory.createEmptyBorder(14, 4, 4, 4)
            content.add(clearCacheBtn, ExternalSystemUiUtil.getLabelConstraints(0))
        }
        content.border = IdeBorderFactory.createEmptyBorder(0, 0, 8, 0)

        UIUtil.showDialog("请选择表名", project, content,
                {
                    if (it) {
                        var tableNames = mutableSetOf<String>()
                        talbeNameInput.text?.split(",")?.let { tableNames.addAll(it.filter { !it.isNullOrBlank() }) }
                        if (tableHistory != null) {
                            for (table in tableHistory) {
                                if (table.isNullOrBlank()) {
                                    continue
                                }
                                if (orphanModulesList.isItemSelected(table)) {
                                    tableNames.add(table)
                                }
                            }
                        }
                        if (tableNames.size == 0) {
                            return@showDialog
                        }
                        PlatformUtil.setDatas(MyConstant.TABLE_HISTORY, tableNames.toTypedArray())
                        PlatformUtil.executeBackgroundTask(project
                                , "正在生成代码"
                                , Runnable {
                            var exception = MybatisCore.batchGen(tableNames.toList(), project)
                            if (exception == null) {
                                NotificationUtil.popInfo("生成代码成功,详情见EventLog控制台输出", event)
                            } else {
                                NotificationUtil.popError("生成代码出错,详情见EventLog控制台输出", event)
                                NotificationUtil.error("生成代码出错exception:" + exception )
                            }
                            PlatformUtil.notifyFileChanged(project)
                        })

                    }
                })
    }
}