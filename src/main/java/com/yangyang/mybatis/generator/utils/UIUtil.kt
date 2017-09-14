package main.java.com.yangyang.mybatis.generator.utils

import com.intellij.openapi.externalSystem.util.ExternalSystemUiUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.CheckBoxList
import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.components.JBScrollPane
import java.awt.GridBagLayout
import java.awt.event.ItemEvent
import java.util.ArrayList
import javax.swing.*

/**
 * 类简介：
 * 作者：  yang
 * 时间：  17/7/24
 * 邮箱：  yangyang1@maihaoche.com
 */
class UIUtil {
    companion object {
        /**
         * 显示一个dialog的对话框
         */
        fun showDialog(titleText:String,project:Project?,content:JComponent, onOkClick:(ok:Boolean)->Unit){
            val dialog = object : DialogWrapper(project) {
                init {
                    title = titleText
                    init()
                }

                override fun createCenterPanel(): JComponent? {
                    return JBScrollPane(content)
                }

                override fun createActions(): Array<Action> {
                    return arrayOf(okAction)
                }
            }
            onOkClick.let {
                onOkClick(dialog.showAndGet())
            }
        }
        /**在对话框面中
         * 添加一个数据框的内容
         */
         fun addTextInputToPanel(label: String, dataKey: String, panel: JPanel): JTextField {
            panel.add(JLabel(label), ExternalSystemUiUtil.getFillLineConstraints(0))
            val textField = getTextField(PlatformUtil.getData(dataKey, ""))
            panel.add(textField, ExternalSystemUiUtil.getFillLineConstraints(0))
            return textField
        }

        /**
         * 创建一个文本输入框
         */
         fun getTextField(defaultStr: String): JTextField {
            val jTextField = JTextField(defaultStr)
            jTextField.border = IdeBorderFactory.createEmptyBorder(4, 4, 4, 4)
            return jTextField
        }

        /**
         * 弹窗选择,单选，则是radiogroup组件。复选，则是checkbox组件

         * @param project
         * @param title
         * @param datas
         * @param singleChoose
         * @return
         */
        private fun chooseFromData(project: Project, title: String, datas: ArrayList<String>, singleChoose: Boolean): ArrayList<String>? {
            val mChoosedModuleNames = ArrayList<String>()
            //布局
            val content = JPanel(GridBagLayout())
            content.add(JLabel(title), ExternalSystemUiUtil.getFillLineConstraints(0))
            val radioButtons = ArrayList<JRadioButton>()
            val orphanModulesList = CheckBoxList<String>()
            if (singleChoose) {
                val box = Box.createVerticalBox()
                for (i in datas.indices) {
                    val radioButton = JRadioButton(datas[i])
                    radioButtons.add(radioButton)
                    radioButton.addItemListener { e ->
                        //选中这个，取消其他的选中
                        if (e.stateChange == ItemEvent.SELECTED) {
                            for (j in radioButtons.indices) {
                                if (radioButtons[j] !== radioButton) {
                                    radioButtons[j].isSelected = false
                                }
                            }
                        }
                    }
                    box.add(radioButton)
                }
                box.border = IdeBorderFactory.createEmptyBorder(8)
                content.add(box, ExternalSystemUiUtil.getFillLineConstraints(0))
            } else {
                orphanModulesList.selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
                orphanModulesList.setItems(datas) { moduleName -> moduleName }
                orphanModulesList.border = IdeBorderFactory.createEmptyBorder(8)
                content.add(orphanModulesList, ExternalSystemUiUtil.getFillLineConstraints(0))
            }
            content.border = IdeBorderFactory.createEmptyBorder(0, 0, 8, 0)

            showDialog("请选择",project,content,{
                if (!it) return@showDialog
                if (singleChoose) {
                    for (i in radioButtons.indices) {
                        if (radioButtons[i].isSelected) {
                            mChoosedModuleNames.add(radioButtons[i].text)
                        }
                    }
                } else {
                    for (i in datas.indices) {
                        val module = datas[i]
                        if (orphanModulesList.isItemSelected(i)) {
                            mChoosedModuleNames.add(module)
                        }
                    }
                }
            })
            return mChoosedModuleNames
        }
    }
}