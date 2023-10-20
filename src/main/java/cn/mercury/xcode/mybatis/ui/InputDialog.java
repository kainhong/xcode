package cn.mercury.xcode.mybatis.ui;

import cn.mercury.xcode.GlobalDict;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.ui.JBUI;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class InputDialog extends DialogWrapper {

    private JPanel mainPanel;
    private String title;

    private JTextField txtValue;

    private String defaultValue;

    private  String returnVal;

    public String getValue() {
        return returnVal;
    }

    public InputDialog(@Nullable Project project, String title,String defaultValue) {
        super(project);

        this.title = title;

        this.defaultValue = defaultValue;

        initPanel();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return this.mainPanel;
    }

    private void initPanel() {
        setTitle(GlobalDict.TITLE_INFO);
        var label = new JLabel(title);
        this.mainPanel = new JPanel(new BorderLayout());
        this.mainPanel.setBorder(JBUI.Borders.empty(5, 10, 7, 10));
        mainPanel.add(label, BorderLayout.NORTH);
        txtValue = new JTextField();

        if(StringUtils.isNotEmpty(defaultValue))
            txtValue.setText(defaultValue);

        this.mainPanel.add(this.txtValue, BorderLayout.CENTER);
        init();
    }

    @Override
    protected void doOKAction() {
        if(txtValue.getText().isEmpty()){
            return;
        }
        returnVal = txtValue.getText();

        super.doOKAction();
    }

    @Override
    public void doCancelAction() {
        returnVal = null;
        super.doCancelAction();
    }
}
