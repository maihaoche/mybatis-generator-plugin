package main.java.com.yangyang.mybatis.generator.utils

import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataKeys
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.wm.WindowManager
import com.intellij.ui.awt.RelativePoint

/**
 * 类简介：
 * 作者：  yang
 * 时间：  17/7/24
 * 邮箱：  yangyang1@maihaoche.com
 */
class NotificationUtil private constructor(){

    companion object {
        private val sPLUGIN_LOGGER = Logger.getInstance(NotificationUtil::class.java)
        private val GROUP_DISPLAY_ID_INFO_BALLOON = NotificationGroup("main.java.com.yangyang.mybatis.generator",
                NotificationDisplayType.BALLOON, true)

        private val LOGGING_NOTIFICATION = NotificationGroup("Gradle sync", NotificationDisplayType.NONE, true)

        /**
         * IntelliJ自带的API输出的info，android studio 会显示在底部statusbar下面。

         * @param info
         */
        fun infoToStatusBar(info: String) {
            sPLUGIN_LOGGER.info(info)
        }

        /**
         * 显示在gradle 的consol中的info

         * @param infoMsg
         * *
         * @param project
         */
        fun info(infoMsg: String?, project: Project?) {
            if (infoMsg == null || infoMsg.trim { it <= ' ' } == "" || project == null) {
                return
            }
            LOGGING_NOTIFICATION.createNotification(infoMsg, MessageType.INFO).notify(project)
        }
        /**
         * 控制台，显示错误信息
         */
        fun error(errorMsg: String?) {
            if (errorMsg == null || errorMsg.trim { it <= ' ' } == "") {
                return
            }
            val notificationX = GROUP_DISPLAY_ID_INFO_BALLOON.createNotification(errorMsg, NotificationType.ERROR)
            Notifications.Bus.notify(notificationX)
        }

        /**
         * 严重的错误，需要弹窗提醒。
         */
        fun popError(errorMsg: String?, event: AnActionEvent) {
            pop(errorMsg,event,MessageType.ERROR)
        }

        /**
         * 弹窗提醒。
         */
        fun popInfo(info: String?, event: AnActionEvent) {
            pop(info,event,MessageType.INFO)
        }

        /**
         * 弹出信息。
         */
        private fun pop(msg:String?,event:AnActionEvent,msgType:MessageType){
            if (msg == null || msg.trim { it <= ' ' } == "") {
                return
            }
            val statusBar = WindowManager.getInstance()
                    .getStatusBar(DataKeys.PROJECT.getData(event.dataContext))
            JBPopupFactory.getInstance()
                    .createHtmlTextBalloonBuilder(msg, msgType, null)
                    .setFadeoutTime(3500)
                    .createBalloon()
                    .show(RelativePoint.getCenterOf(statusBar.component),
                            Balloon.Position.atRight)
        }


    }

}