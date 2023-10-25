package cn.mercury.xcode.window;

import com.google.gson.JsonObject;
import com.intellij.openapi.project.Project;
import org.cef.browser.CefBrowser;
import org.jetbrains.annotations.NotNull;

public class SideWindowService {

    private final Project project;

    private final WebSideWindow sideWindow;

    public Project getProject() {
        return this.project;
    }

    public WebSideWindow getCodeShellSideWindow() {
        return this.sideWindow;
    }

    public SideWindowService(Project project) {
        this.project = project;
        this.sideWindow = new WebSideWindow(project);
    }


    public void notifyIdeAppInstance(@NotNull JsonObject result) {
        CefBrowser browser = this.getCodeShellSideWindow().jbCefBrowser().getCefBrowser();
        browser.executeJavaScript("window.postMessage(" + result + ",'*');", browser.getURL(), 0);
    }
}
