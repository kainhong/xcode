package cn.mercury.xcode.code.service;

import cn.mercury.xcode.code.model.table.TableInfo;
import cn.mercury.xcode.mybatis.language.dom.model.Configuration;
import cn.mercury.xcode.mybatis.utils.MapperUtils;
import cn.mercury.xcode.code.generate.GenerateOptions;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.xml.XmlTag;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MybatisConfigurationGenerate {

    private Project project;

    private final List<TableInfo> tables;

    private GenerateOptions options;

    public MybatisConfigurationGenerate(Project project, GenerateOptions options, List<TableInfo> tables) {
        this.project = project;
        this.tables = tables;
        this.options = options;
    }

    public void update() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        WriteCommandAction.runWriteCommandAction(this.project, () -> {
            // 在此处进行文件修改
            try {
                this.updateMybatisConfiguration();
            } catch (Exception e) {

            }
            countDownLatch.countDown();
        });

        try {
            countDownLatch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {

        }
    }

    private void updateMybatisConfiguration() {
        if (StringUtils.isEmpty(this.options.getMybatisConfiguration()))
            return;

        Configuration configuration = findMybatisConfiguration(this.options.getMybatisConfiguration());
        if (configuration == null)
            return;


        @Nullable XmlTag xmlTag = configuration.getXmlTag();
        //PsiElement psiFile = xmlTag.getParent().getContainingFile().copy();

        if (xmlTag == null)
            return;

        XmlTag mapper = findMappersTag(xmlTag);
        if (mapper == null)
            return;

        for (var table : this.tables) {
            addMapper(mapper, table);
        }

        var psiFile = xmlTag.getContainingFile();
        VirtualFile virtualFile = psiFile.getVirtualFile();

        Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
        if (document != null) {
            FileDocumentManager.getInstance().saveDocument(document);
        }

    }


    private XmlTag findMappersTag(XmlTag xmlTag) {
        PsiElement[] children = xmlTag.getChildren();
        for (var e : children) {
            if (e instanceof XmlTag) {
                XmlTag tag = (XmlTag) e;
                if (tag.getName().equals("mappers"))
                    return tag;
            }
        }
        return null;
    }

    private void addMapper(XmlTag mappers, TableInfo tableInfo) {
        String resourceUrl = this.options.getMapperFolder() + tableInfo.getName() + "Mapper.xml";

        final Flag flag = new Flag();
        flag.value = false;

        try {
            mappers.acceptChildren(new PsiRecursiveElementVisitor() {
                public void visitElement(PsiElement element) {
                    if (element instanceof XmlTag) {
                        XmlTag tag = (XmlTag) element;
                        if (tag.getName().equals("mapper")) {
                            String resource = tag.getAttributeValue("resource");
                            if (resource != null && resource.equals(resourceUrl)) {
                                flag.value = true;
                                throw new IllegalStateException("mapper already exists");
                            }
                        }
                    }
                    super.visitElement(element);
                }
            });
        } catch (IllegalStateException e) {

        }
        if (flag.value)
            return;

        XmlTag mapper = mappers.createChildTag("mapper", null, null, false);

        mapper.setAttribute("resource", resourceUrl);
        mappers.addSubTag(mapper, false);
    }


    private Configuration findMybatisConfiguration(String name) {
        Collection<Configuration> configurations = MapperUtils.getMybatisConfigurations(this.project);
        if (configurations.size() == 0)
            return null;

        Configuration config = configurations.stream()
                .filter(c -> {
                    PsiFile vsfile = c.getXmlTag().getContainingFile();
                    return vsfile.getName().equals(name);
                })
                .findFirst()
                .orElse(null);

        return config;
    }

    private void updateMybatisConfiguration(Configuration configuration, TableInfo tableInfo, Element element) {
        Element mapper = element.addElement("mapper");

        mapper.addAttribute("resource", this.options.getMapperFolder() + "/" + tableInfo.getName() + "Mapper.xml");
    }

    class Flag {
        public boolean value;
    }

}
