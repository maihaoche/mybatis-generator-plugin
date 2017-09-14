package com.yangyang.mybatis.generator.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.externalSystem.util.ExternalSystemUiUtil
import com.intellij.ui.IdeBorderFactory
import main.java.com.yangyang.mybatis.generator.MyConstant
import main.java.com.yangyang.mybatis.generator.utils.NotificationUtil
import main.java.com.yangyang.mybatis.generator.utils.PlatformUtil
import main.java.com.yangyang.mybatis.generator.utils.UIUtil
import java.awt.GridBagLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

/**
 * 类简介：配置mysql的action
 * 作者：  yang
 * 时间：  17/7/26
 * 邮箱：  yangyang1@maihaoche.com
 */
class ActionConfigureDB : AnAction()  {
    override fun actionPerformed(event: AnActionEvent?) {
        if (event == null) {
            return
        }
        val project = event.getProject()
        //总的内容
        val content = JPanel(GridBagLayout())
        content.add(JLabel("该版本仅支持mysql的驱动(com.mysql.jdbc.Driver):        "), ExternalSystemUiUtil.getFillLineConstraints(0))

        val urlInput = UIUtil.addTextInputToPanel("请输入数据库链接的URL:        ", MyConstant.DB_URL, content)
        val userNameInput = UIUtil.addTextInputToPanel("请输入用户名:        ", MyConstant.DB_USER, content)
        val passwordInput = UIUtil.addTextInputToPanel("请输入密码:        ", MyConstant.DB_PW, content)

        //清空缓存
        val clearCacheBtn = JButton("清除缓存")
        clearCacheBtn.addActionListener {
            PlatformUtil.setData(MyConstant.DB_URL, "")
            PlatformUtil.setData(MyConstant.DB_USER, "")
            PlatformUtil.setData(MyConstant.DB_PW, "")
            PlatformUtil.setData(MyConstant.DB_CONF_INITED, false)
            NotificationUtil.info("清除缓存成功", project)
            urlInput.text = ""
            userNameInput.text = ""
            passwordInput.text = ""
        }
        clearCacheBtn.border = IdeBorderFactory.createEmptyBorder(14, 4, 4, 4)
        content.add(clearCacheBtn, ExternalSystemUiUtil.getLabelConstraints(0))
        //显示dialog
        content.border = IdeBorderFactory.createEmptyBorder(0, 0, 8, 0)

        UIUtil.showDialog("数据库配置参数", project, content, {
            if (it) {
                if (urlInput.text.trim().isNullOrBlank()
                        || userNameInput.text.trim().isNullOrBlank()
                        || passwordInput.text.trim().isNullOrBlank()
                        ) {
                    NotificationUtil.popInfo("配置不能为空", event)
                    return@showDialog
                }
                PlatformUtil.setData(MyConstant.DB_URL, urlInput.text)
                PlatformUtil.setData(MyConstant.DB_USER, userNameInput.text)
                PlatformUtil.setData(MyConstant.DB_PW, passwordInput.text)
                PlatformUtil.setData(MyConstant.DB_CONF_INITED, true)
                NotificationUtil.popInfo("数据库配置修改成功", event)
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
                    PlatformUtil.performActionWithPath(event, MyConstant.ACTION_TOOLS_MENU, MyConstant.ACTION_MYBATIS, MyConstant.ACTION_MYBATIS_DB_CONFIGURE)
                }
            })
        }
    }
}