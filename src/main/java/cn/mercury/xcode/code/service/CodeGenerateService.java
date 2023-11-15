package cn.mercury.xcode.code.service;

import cn.mercury.xcode.GlobalDict;
import cn.mercury.xcode.SaveFile;
import cn.mercury.xcode.code.dto.TableInfoDTO;
import cn.mercury.xcode.code.model.table.TableInfo;
import cn.mercury.xcode.code.generate.GenerateContext;
import cn.mercury.xcode.code.generate.GenerateOptions;
import cn.mercury.xcode.code.setting.Template;
import cn.mercury.xcode.utils.*;
import com.intellij.database.psi.DbTable;
import com.intellij.database.util.DasUtil;
import com.intellij.database.util.DbUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.util.ReflectionUtil;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class CodeGenerateService {
    private static final String FILTER_PACKAGE_NAME = "java.lang";

    final Project project;
    private List<Template> templates;
    private GenerateOptions options;
    private List<TableInfo> tables;

    ProgressIndicator indicator;

    public CodeGenerateService(Project project){
        this.project = project;
    }

    public CodeGenerateService(Project project, List<Template> templates, GenerateOptions generateOptions, List<DbTable> tables) {
        this.project = project;
        this.templates = templates;
        this.options = generateOptions;
        this.tables = tables.stream().map(item ->TableInfo.from(item)).collect(Collectors.toList());

    }

    public void generate(Map<String, Object> otherParam) {
        doGenerate(otherParam);
    }

    private void doGenerate(Map<String, Object> otherParam) {
        for (TableInfo tableInfo : CodeGenerateService.this.tables) {
            updateProcessInfo("generating " + tableInfo.getName(), null);
            generate(tableInfo, otherParam);
        }

        updateProcessInfo("generating mybatis configuration", null);

        updateConfiguration();
    }

    private void updateConfiguration() {
        Template template = this.templates.stream().filter(item -> item.getName().equals("mapper")).findFirst().orElse(null);
        if (template == null)
            return;

        if (StringUtils.isEmpty(this.options.getMybatisConfiguration()))
            return;

        new MybatisConfigurationGenerate(this.project, this.options, this.tables).update();

    }

    private void updateProcessInfo(String title, String info) {

        ApplicationManager.getApplication().runWriteAction(() -> {
            if (indicator == null)
                return;

            indicator.setText(title);
            if (StringUtils.isNotEmpty(info))
                indicator.setText2(info);
        });
    }


    void generate(TableInfo tableInfo, Map<String, Object> otherParam) {
        if (!StringUtils.isEmpty(tableInfo.getPreName()) && tableInfo.getObj().getName().startsWith(tableInfo.getPreName())) {
            String newName = tableInfo.getObj().getName().substring(tableInfo.getPreName().length());
            tableInfo.setName(NameUtils.getInstance().getClassName(newName));
        }

        Map<String, Object> params = getDefaultParam();
        // 其他参数
        if (otherParam != null) {
            params.putAll(otherParam);
        }

        setAllPackageName(params, tableInfo);
        // 表信息对象
        params.put("tableInfo", tableInfo);

        for (var template : templates) {
            if(!template.isEnable())
                continue;

            updateProcessInfo("generating " + tableInfo.getName(), template.getName());

            generate(tableInfo, params, template);
        }

        //generateTable(templates, generateOptions, otherParam, tableInfo);
    }

    private void setAllPackageName(Map<String, Object> param, TableInfo tableInfo) {
        // 设置包名
        param.put("packageName", this.options.getPackageName());

        for (var template : templates) {
            String name = template.getName();
            param.put(name + "PackageName", template.getPackageName());
            if (!StringUtils.isEmpty(template.getNameFormat()))
                param.put(name + "ClassName", String.format(template.getNameFormat(), tableInfo.getName()));
            else
                param.put(name + "ClassName", tableInfo.getName());
        }
    }

    void generate(TableInfo tableInfo, Map<String, Object> values, Template template) {
        List<String> baseEntryFields = Arrays.asList("id", "createUser", "createDate", "updateUser", "updateTime");
        Map<String,Object> params = new HashMap<>();

        params.putAll(values);

        // 设置模型路径与导包列表
        setModulePathAndImportList(params, tableInfo, template);

        GenerateContext context = new GenerateContext();
        context.setWriteFile(true);
        context.setReformat(this.options.getReFormat());
        context.setTemplates(this.templates);

        // 设置回调对象
        String path = template.getModulePath();

        params.put("context", context);
        params.put("modulePath", path);
        params.put("baseEntryFields", baseEntryFields);

        String packageName = template.getPackageName();

        String packagePath = packageName.replaceAll("\\.", "/");

        String sourcePath = path + "/src/main/java/" + packagePath;

        params.put("sourcePath", sourcePath);

        params.put(template.getName() + "PackageName", packageName);

        params.put("mapperFolder", this.options.getMapperFolder());

        context.setSavePath(sourcePath);

        String content = TemplateUtils.parseTemplate(template.getContent());

        // 开始生成
        String code = VelocityUtils.generate(content, params);

        // 设置一个默认保存路径与默认文件名
        new SaveFile(project, code, context, this.options).asyncWrite();

    }

    /**
     * 设置模型路径与导包列表
     *
     * @param param     参数
     * @param tableInfo 表信息对象
     */
    private void setModulePathAndImportList(Map<String, Object> param, TableInfo tableInfo, Template template) {
        Module module = null;

        // 设置modulePath
        param.put("modulePath", template.getModulePath());

        // 设置要导入的包
        param.put("importList", getImportList(tableInfo));
    }


    private Set<String> getImportList(TableInfo tableInfo) {
        // 创建一个自带排序的集合
        Set<String> result = new TreeSet<>();
        tableInfo.getFullColumn().forEach(columnInfo -> {
            if (!columnInfo.getType().startsWith(FILTER_PACKAGE_NAME)) {
                String type = NameUtils.getInstance().getClsFullNameRemoveGeneric(columnInfo.getType());
                result.add(type);
            }
        });
        return result;
    }

    private Map<String, Object> getDefaultParam() {
        // 系统设置
        Map<String, Object> param = new HashMap<>(20);
        // 作者
        param.put("author", GlobalDict.AUTHOR);
        //工具类
        param.put("tool", GlobalTool.getInstance());
        param.put("time", TimeUtils.getInstance());
        // 项目路径
        param.put("projectPath", project.getBasePath());
        // Database数据库工具
        param.put("dbUtil", ReflectionUtil.newInstance(DbUtil.class));
        param.put("dasUtil", ReflectionUtil.newInstance(DasUtil.class));
        return param;
    }

    public String generate(Template template, TableInfo tableInfo) {
        // 获取默认参数
        Map<String, Object> param = getDefaultParam();
        // 表信息对象，进行克隆，防止篡改
        param.put("tableInfo", tableInfo);
        // 设置模型路径与导包列表
        setModulePathAndImportList(param, tableInfo,template);

        return VelocityUtils.generate(template, param);
    }
}
