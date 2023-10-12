package cn.mercury.xcode.sql.generate;

import cn.mercury.xcode.sql.setting.Template;
import lombok.Data;

import java.util.List;

/**
 * 回调实体类
 *

 * @version 1.0.0
 * @since 2018/07/17 13:10
 */
@Data
public class GenerateContext {
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 保存路径
     */
    private String savePath;
    /**
     * 是否重新格式化代码
     */
    private Boolean reformat;
    /**
     * 是否写入文件，部分模块不需要写入文件。例如debug.json模板
     */
    private Boolean writeFile;

    private List<Template> templates;

    public String getPackageName(String module){
        return templates.stream().filter(t->t.getName().equalsIgnoreCase(module)).map(t->t.getPackageName()).findFirst().orElse(null);
    }
}
