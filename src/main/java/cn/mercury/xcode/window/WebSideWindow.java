package cn.mercury.xcode.window;


import cn.mercury.xcode.window.handle.CustomSchemeHandlerFactory;
import com.google.gson.JsonObject;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefBrowserBase;
import com.intellij.ui.jcef.JBCefJSQuery;
import org.cef.CefApp;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLifeSpanHandlerAdapter;
import org.cef.handler.CefLoadHandler;
import org.cef.network.CefRequest;

import javax.swing.*;
import java.util.Objects;

public class WebSideWindow {
    private JBCefBrowser jbCefBrowser;
    private final Project project;
    private boolean webLoaded;

    public WebSideWindow(Project project) {
        super();
        this.webLoaded = false;
        this.project = project;
    }

    public synchronized JBCefBrowser jbCefBrowser() {
        return !this.webLoaded ? lazyLoad() : this.jbCefBrowser;
    }

    private JBCefBrowser lazyLoad() {
        try {
            if (!this.webLoaded) {
                boolean isOffScreenRendering = false;
                JBCefBrowser browser;
                try {
                    browser = JBCefBrowser.createBuilder().setOffScreenRendering(isOffScreenRendering).build();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("JBCefBrowser# build Browser not supported");
                    browser = new JBCefBrowser();
                }

                registerLifeSpanHandler(browser);
                registerJsCallJavaHandler(browser);
                browser.loadURL(ShellSettings.current.getCompleteURL());
                this.jbCefBrowser = browser;
                this.webLoaded = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.jbCefBrowser;
    }

    private void registerJsCallJavaHandler(JBCefBrowser browser) {
        JBCefJSQuery query = JBCefJSQuery.create((JBCefBrowserBase) browser);
        query.addHandler((String arg) -> {
            try {
                return new JBCefJSQuery.Response("success");
            } catch (Exception e) {
                return new JBCefJSQuery.Response(null, 0, "errorMsg");
            }
        });
        browser.getJBCefClient().addLoadHandler(new CefLoadHandler() {
            @Override
            public void onLoadingStateChange(CefBrowser browser, boolean isLoading, boolean canGoBack, boolean canGoForward) {

            }

            @Override
            public void onLoadStart(CefBrowser browser, CefFrame frame, CefRequest.TransitionType transitionType) {

            }

            @Override
            public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("sendUrl", ShellSettings.getInstance().getCompleteURL());
                jsonObject.addProperty("maxToken", ShellSettings.getInstance().getMaxTokenSize());

                JsonObject result = new JsonObject();
                result.addProperty("data", jsonObject.toString());
                (project.getService(SideWindowService.class)).notifyIdeAppInstance(result);

                browser.executeJavaScript(
                        "window.callJava = function(arg) {" +
                                query.inject(
                                        "arg",
                                        "response => console.log(response)",
                                        "(error_code, error_message) => console.log('callJava 失败', error_code, error_message)"
                                ) +  "};",
                        null, 0);

                //jbCefBrowser.openDevtools();
            }

            @Override
            public void onLoadError(CefBrowser browser, CefFrame frame, ErrorCode errorCode, String errorText, String failedUrl) {
                System.out.println("JBCefBrowser# onLoadError: " + failedUrl + " " + errorText);
            }
        }, browser.getCefBrowser());
    }

    private void registerLifeSpanHandler(JBCefBrowser browser) {
        final CefLifeSpanHandlerAdapter lifeSpanHandlerAdapter = new CefLifeSpanHandlerAdapter() {
            @Override
            public void onAfterCreated(CefBrowser browse) {
                CefApp.getInstance().registerSchemeHandlerFactory("http", "xCode", new CustomSchemeHandlerFactory(WebSideWindow.this.project));
            }
        };
        browser.getJBCefClient().addLifeSpanHandler(lifeSpanHandlerAdapter, browser.getCefBrowser());
        final JBCefBrowser tempBrowser = browser;
        Disposer.register(this.project, () -> tempBrowser.getJBCefClient().removeLifeSpanHandler(lifeSpanHandlerAdapter, tempBrowser.getCefBrowser()));
    }

    public JComponent getComponent() {
        if (Objects.nonNull(jbCefBrowser())) {
            return jbCefBrowser().getComponent();
        }
        return null;
    }


}





