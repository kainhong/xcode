package cn.mercury.xcode.mybatis.utils;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

@Slf4j
public class IntellijServiceUtil {

    private IntellijServiceUtil() {
    }

    private static final String METHOD_GET_SERVICE = "getService";

    public static <T> T getService(Project project, @NotNull Class<T> serviceClass) {
        T instance;
        Application application = ApplicationManager.getApplication();
        Method getService;
        Object invoke;
        try {
            Class<? extends Application> aClass = application.getClass();
            if (project != null) {
                //尝试获取project的getService方法
                Class<? extends Project> projectClass = project.getClass();
                getService = projectClass.getMethod(METHOD_GET_SERVICE, Class.class);
                invoke = getService.invoke(project, serviceClass);
            } else {
                getService = aClass.getMethod(METHOD_GET_SERVICE, Class.class);
                invoke = getService.invoke(application, serviceClass);
            }
            return (T) invoke;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        try {
            Class<ServiceManager> serviceManagerClass = ServiceManager.class;
            if (project != null) {
                getService = serviceManagerClass.getDeclaredMethod(METHOD_GET_SERVICE, Project.class, Class.class);
                invoke = getService.invoke(null, project, serviceClass);
            } else {
                getService = serviceManagerClass.getDeclaredMethod(METHOD_GET_SERVICE, Class.class);
                invoke = getService.invoke(null, serviceClass);
            }
            return (T) invoke;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        if (project == null) {
            instance = application.getComponent(serviceClass);
        } else {
            instance = project.getComponent(serviceClass);
        }
        return instance;
    }

}
