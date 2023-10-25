package cn.mercury.xcode.window;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.jcef.JBCefApp;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class WebSideWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentManager contentManager = toolWindow.getContentManager();
        JPanel webPanel = new JPanel(new BorderLayout());
        SideWindowService service = project.getService(SideWindowService.class);
        if (Objects.nonNull(service.getCodeShellSideWindow()) && Objects.nonNull(service.getCodeShellSideWindow().getComponent())) {
            webPanel.add(service.getCodeShellSideWindow().getComponent());
            Content labelContent = contentManager.getFactory().createContent(webPanel, "", false);
            contentManager.addContent(labelContent);
        }
    }

    static {
        JBCefApp.getInstance();
    }

}
