//package cn.mercury.xcode.model.template;
//
//import lombok.Data;
//
///**
// * 模板信息类
// *
//
// * @version 1.0.0
// * @since 2018/07/17 13:10
// */
//@Data
//public class Template implements AbstractEditorItem<Template> {
//    /**
//     * 模板名称
//     */
//    private String name;
//
//    /**
//     * 代码
//     */
//    private String code;
//
//    /**
//     * 模板路径
//     * classpath:/template/springboot/controller.java.vm
//     */
//    private String uri;
//
//    private String type;
//
//    /**
//     * package 路径
//     * %s-web
//     */
//    private String path;
//
//    private String packageName;
//
//    /**
//     * 包名后缀
//     */
//    private String packageSuffix;
//
//    /**
//     * 模板内容
//     */
//    private String value;
//
//    private String modulePath;
//
//    public Template(){
//
//    }
//
//    public Template(String name, String code) {
//        this.name = name;
//        this.code = code;
//    }
//
//
//    @Override
//    public Template defaultVal() {
//        return new Template("demo", "template__");
//    }
//
//    @Override
//    public void changeFileName(String name) {
//        this.name = name;
//    }
//
//    @Override
//    public String fileName() {
//        return this.name;
//    }
//
//    @Override
//    public void changeFileContent(String content) {
//        this.code = content;
//    }
//
//    @Override
//    public String fileContent() {
//        return this.value;
//    }
//}
