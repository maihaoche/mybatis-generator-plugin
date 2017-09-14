package main.java.com.yangyang.mybatis.generator.utils

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.TransactionGuard
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.progress.util.ProgressIndicatorUtils
import com.intellij.openapi.progress.util.ReadTask
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ex.ProjectRootManagerEx
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.RefreshQueue
import com.intellij.util.ui.UIUtil
import javax.swing.SwingUtilities

/**
 * 类简介：
 * 作者：  yang
 * 时间：  17/7/24
 * 邮箱：  yangyang1@maihaoche.com
 */
class PlatformUtil {
    companion object {

        /**
         * 在异步线程池中执行任务，真正的异步任务。不会阻塞主线程
         */
        fun executeBackgroundTask(project: Project,title: String, runnable: Runnable) {
            ApplicationManager.getApplication().invokeLater({
                ApplicationManager.getApplication().runReadAction({
                    object : Task.Backgroundable(project,title) {
                        override fun run(progressIndicator: ProgressIndicator) {
                            ProgressIndicatorUtils.runWithWriteActionPriority({
                                runnable.run()
                                if (!progressIndicator.isCanceled) {
                                    progressIndicator.stop()
                                }
                            }, progressIndicator)
                        }
                        override fun shouldStartInBackground(): Boolean {
                            return true
                        }
                    }.queue()
                })
            })
        }

        /**
         * 通知文件发生变化
         */
        fun notifyFileChanged(project: Project) {
            project?.getBaseDir()?.refresh(true, true);
        }

        /**
         * 涉及到Project修改的操作。在主线程执行某个耗时任务，需要调用invokeAndWaitIfNeeded
         * 注意，该方法执行的任务会阻塞主线程！
         */
        fun executeProjectChanges(project: Project, changes: Runnable) {
            UIUtil.invokeAndWaitIfNeeded(Runnable {
                //在UI主线程执行任务。执行耗时的任务
                //官方文档：http://www.jetbrains.org/intellij/sdk/docs/basics/architectural_overview/general_threading_rules.html
                //总结：文件修改，涉及project module的修改，等操作都需要用该API来run.
                ApplicationManager.getApplication().runWriteAction() {
                    if (!project.isDisposed) {
                        ProjectRootManagerEx.getInstanceEx(project).mergeRootsChangesDuring(changes)
                    }
                }
            })
        }


        /**
         * 获取本地存储的key value ，string类型
         */
        fun getData(key: String, defaultValue: String): String {
            return PropertiesComponent.getInstance().getValue(key, defaultValue)
        }

        /**
         * 获取本地存储的key value ，string类型
         */
        fun getData(key: String, defaultValue: Boolean): Boolean {
            return PropertiesComponent.getInstance().getBoolean(key, defaultValue)
        }


        /**
         * 数组数据
         */
        fun getDatas(key: String): Array<String>? {
            return PropertiesComponent.getInstance().getValues(key)
        }

        /**
         * 存储key value数据，string[]类型
         */
        fun setDatas(key: String, setValue: Array<String>?) {
            PropertiesComponent.getInstance().setValues(key, setValue)
        }

        /**
         * 存储key value数据，string类型
         */
        fun setData(key: String, stringValue: String) {
            PropertiesComponent.getInstance().setValue(key, stringValue)
        }

        /**
         * 存储key value数据，boolean类型
         */
        fun setData(key: String, booleanValue: Boolean) {
            PropertiesComponent.getInstance().setValue(key, booleanValue)
        }

        /**
         * 根据路径，执行某个命令。最多三个层级。既：ToolsMenu-->Mybatis-->xxx
         */
        fun performActionWithPath(event: AnActionEvent, menuName: String?, vararg paths: String) {
            if (menuName == null || menuName.isEmpty()) {
                return
            }
            var firstLevelPath: String?
            var secondLevelPath = ""
            if (paths.size > 0) {
                firstLevelPath = paths[0]
                //至少有1级深度，切不为空
                if (firstLevelPath.isEmpty()) {
                    NotificationUtil.error("在id为" + menuName + "的菜单栏,paths的第一级路径为空。")
                    return
                }
                if (paths.size > 1) {
                    secondLevelPath = paths[1]
                }
            } else {
                NotificationUtil.error("在id为" + menuName + "的菜单栏,paths为空。")
                return
            }
            val actionManager = ActionManager.getInstance()
            val actionGroup = actionManager.getAction(menuName) as DefaultActionGroup
            var found = false
            val anActions = actionGroup.childActionsOrStubs
            if (anActions.size > 0) {
                for (i in anActions.indices) {
                    if (firstLevelPath == anActions[i].templatePresentation.text) {
                        if (paths.size == 1) {
                            anActions[i].actionPerformed(event)
                            return
                        } else if (secondLevelPath.isEmpty()) {
                            return
                        }
                        var subActions: Array<AnAction>? = null
                        if (anActions[i] != null && anActions[i] is DefaultActionGroup && anActions[i].templatePresentation.isEnabledAndVisible) {
                            subActions = (anActions[i] as DefaultActionGroup).childActionsOrStubs
                        }
                        if (subActions != null && subActions.size > 0) {
                            for (j in subActions.indices) {
                                if (secondLevelPath == subActions[j].templatePresentation.text) {
                                    found = true
                                    subActions[j].actionPerformed(event)
                                }
                            }
                        }
                    }
                }
            }
            if (!found) {
                NotificationUtil.error("在id为" + menuName + "的菜单栏下没有找到Action：" + firstLevelPath + if (secondLevelPath.isEmpty()) "" else "->" + secondLevelPath)
            }
        }

    }

}