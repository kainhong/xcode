package cn.mercury.xcode.utils;

import cn.mercury.xcode.code.setting.Template;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

/**
 * Velocity工具类，主要用于代码生成
 *

 * @version 1.0.0
 * @since 2018/07/17 13:10
 */
public class VelocityUtils {
    /**
     * velocity配置
     */
    private static final Properties INIT_PROP;

    static {
        // 设置初始化配置
        INIT_PROP = new Properties();
        // 修复部分用户的velocity日志记录无权访问velocity.log文件问题
        INIT_PROP.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.Log4JLogChute");
        INIT_PROP.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
    }

    /**
     * 禁止创建实例对象
     */
    private VelocityUtils() {
        throw new UnsupportedOperationException();
    }


    public static String generate(Template template, Map<String, Object> map) {

        // 处理模板，注入全局变量
        String content = TemplateUtils.parseTemplate(template.getContent());

        return generate(content, map);
    }


    public static String getTemplateContent(Template template){
        if(StringUtils.isNotEmpty(template.getFile()))
            return "";
        return  ResourcesUtils.readText(template.getFile());
    }

    /**
      * 生成代码
      *
      * @param template 模板
      * @param map      参数
      * @return 生成的代码
      */

    public static String generate(String template, Map<String, Object> map) {
        // 每次创建一个新实例，防止velocity缓存宏定义
        VelocityEngine velocityEngine = new VelocityEngine(INIT_PROP);
        // 创建上下文对象
        VelocityContext velocityContext = new VelocityContext();
        if (map != null) {
            map.forEach(velocityContext::put);
        }
        StringWriter stringWriter = new StringWriter();
        try {
            // 生成代码
            velocityEngine.evaluate(velocityContext, stringWriter, "Velocity Code Generate", template);
        } catch (Exception e) {
            // 将异常全部捕获，直接返回，用于写入模板
            StringBuilder builder = new StringBuilder("在生成代码时，模板发生了如下语法错误：\n");
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            builder.append(writer.toString());
            return builder.toString().replace("\r", "");
        }
        String code = stringWriter.toString();
        // 清除前面空格
        StringBuilder sb = new StringBuilder(code);
        while (sb.length() > 0 && Character.isWhitespace(sb.charAt(0))) {
            sb.deleteCharAt(0);
        }
        // 返回结果
        return sb.toString();
    }
}
