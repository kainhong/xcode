package cn.mercury.xcode.setting.ui.component;

import cn.mercury.xcode.code.setting.Template;
import cn.mercury.xcode.code.setting.TemplateConfiguration;
import cn.mercury.xcode.code.setting.TemplateGroup;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBCheckBox;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 模板选择组件
 *
 * @version 1.0.0
 * @date 2021/08/16 16:18
 */
public class TemplateSelectComponent {
    @Getter
    private JPanel mainPanel;

    /**
     * 分组
     */
    private ComboBox<String> groupComboBox;

    /**
     * 选中所有复选框
     */
    private JBCheckBox allCheckbox;

    /**
     * 所有复选框
     */
    private List<JBCheckBox> checkBoxList;

    /**
     * 模板面板
     */
    private JPanel templatePanel;

    public TemplateSelectComponent() {
        this.init();
    }

    private void init() {
        this.mainPanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new BorderLayout());
        this.groupComboBox = new ComboBox<>();
        this.groupComboBox.setSwingPopup(false);
        this.groupComboBox.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String groupName = (String) groupComboBox.getSelectedItem();
                if (StringUtils.isEmpty(groupName)) {
                    return;
                }
                refreshTemplatePanel(groupName);
            }
        });
        this.allCheckbox = new JBCheckBox("All");
        this.allCheckbox.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkBoxList == null) {
                    return;
                }
                for (JBCheckBox checkBox : checkBoxList) {
                    checkBox.setSelected(allCheckbox.isSelected());
                }
            }
        });
        topPanel.add(this.groupComboBox, BorderLayout.WEST);
        topPanel.add(this.allCheckbox, BorderLayout.EAST);
        this.mainPanel.add(topPanel, BorderLayout.NORTH);
        this.templatePanel = new JPanel(new GridLayout(-1, 2));
        this.mainPanel.add(templatePanel, BorderLayout.CENTER);
        this.refreshData();
    }

    public void refreshData() {
        this.groupComboBox.removeAllItems();

        for (var group : TemplateConfiguration.instance().getTemplateGrops()) {
            this.groupComboBox.addItem(group.getName());
        }
    }

    private void refreshTemplatePanel(String groupName) {
        this.allCheckbox.setSelected(false);
        this.templatePanel.removeAll();
        this.checkBoxList = new ArrayList<>();
        TemplateGroup templateGroup = TemplateConfiguration.instance().getTemplateGroup(groupName);//   SettingsStorageService.getSettingsStorage().getTemplateGroupMap().get(groupName);
        for (var template : templateGroup.getItems()) {
            JBCheckBox checkBox = new JBCheckBox(template.getName());
            this.checkBoxList.add(checkBox);
            this.templatePanel.add(checkBox);
        }
        this.mainPanel.updateUI();
    }

    public String getselectedGroupName() {
        return (String) this.groupComboBox.getSelectedItem();
    }

    public void setSelectedGroupName(String groupName) {
        this.groupComboBox.setSelectedItem(groupName);
    }

    public List<Template> getAllSelectedTemplate() {
        String groupName = (String) this.groupComboBox.getSelectedItem();
        if (StringUtils.isEmpty(groupName)) {
            return Collections.emptyList();
        }
        TemplateGroup templateGroup = TemplateConfiguration.instance().getTemplateGroup(groupName);
        Map<String, Template> map = templateGroup.getItems().stream().collect(Collectors.toMap(Template::getName, val -> val));
        List<Template> result = new ArrayList<>();
        for (JBCheckBox checkBox : this.checkBoxList) {
            if (checkBox.isSelected()) {
                Template template = map.get(checkBox.getText());
                if (template != null) {
                    result.add(template);
                }
            }
        }
        return result;
    }
}
