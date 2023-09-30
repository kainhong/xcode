package cn.mercury.xcode.mybatis.actions;


import cn.mercury.xcode.mybatis.utils.LogUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static cn.mercury.xcode.mybatis.utils.LogUtil.copySqlToClipboard;

public class CopySqlAction extends AnAction {

    /**
     * Creates a new action with its text, description and icon set to {@code null}.
     */
    @SuppressWarnings("unused")
    public CopySqlAction() {
    }

    /**
     * Constructs a new action with the specified text, description and icon.
     *
     * @param text        Serves as a tooltip when the presentation is a button and the name of the
     *                    menu item when the presentation is a menu item
     * @param description Describes current action, this description will appear on
     *                    the status bar when presentation has focus
     * @param icon        Action's icon
     */
    public CopySqlAction(@Nls(capitalization = Nls.Capitalization.Title) @Nullable String text,
                         @Nls(capitalization = Nls.Capitalization.Sentence) @Nullable String description,
                         @Nullable Icon icon) {
        super(text, description, icon);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String selectedText = LogUtil.getSelectText(e);
        copySqlToClipboard(e, selectedText);
    }

}
