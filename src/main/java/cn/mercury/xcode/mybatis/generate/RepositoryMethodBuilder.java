package cn.mercury.xcode.mybatis.generate;

import cn.mercury.xcode.mybatis.language.dom.model.IdDomElement;
import cn.mercury.xcode.mybatis.language.dom.model.Mapper;
import cn.mercury.xcode.mybatis.language.dom.model.ResultMap;
import cn.mercury.xcode.mybatis.language.dom.model.Select;
import cn.mercury.xcode.mybatis.utils.StringUtil;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiMethod;
import com.intellij.util.xml.DomUtil;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;


import java.util.Arrays;

public class RepositoryMethodBuilder {
    static final Logger logger = Logger.getInstance(RepositoryMethodBuilder.class);

    private final Project project;
    private final PsiClass clazz;
    private final IdDomElement element;


    public RepositoryMethodBuilder(Project project, PsiClass clazz, IdDomElement element) {
        this.project = project;
        this.clazz = clazz;
        this.element = element;
    }


    private String getMethodReturnType(Select select) {
        String returnType = select.getResultType().getRawText();
        if (!StringUtil.isEmpty(returnType)) {
            return returnType;
        }

        String resultMap = select.getResultMap().getRawText();
        if(StringUtil.isEmpty(resultMap))
            return "void";

        @Nullable Mapper mapper = DomUtil.getParentOfType(element, Mapper.class, true);

        ResultMap rs = mapper.getResultMaps().stream().filter(m -> m.getId().getRawText().equals(resultMap)).findFirst().orElse(null);

        if (rs == null) {
            return "void";
        }

        return rs.getType().getRawText();
    }

    public void build() {
        if (!(element instanceof Select)) {
            if (logger.isDebugEnabled())
                logger.debug("skip: {}", element);
            return;
        }

        Select select = (Select) element;

        String id = element.getId().getRawText();
        if(StringUtil.isEmpty(id))
            return;

        PsiMethod method = Arrays.stream(clazz.getAllMethods())
                .filter(m -> m.getName().equals(id))
                .findFirst()
                .orElse(null);

        if (method != null)
            return;

        String name = id.toLowerCase();

        String returnType = getMethodReturnType(select);

        if( name.contains("select") && !returnType.contains("java.lang"))
            returnType = "List<" + returnType + ">";

        String params = "";

        if( name.contains("select") || name.contains("param"))
            params = "@Param(\"params\") java.util.Map<String,Object> params";

        StringBuilder sb = new StringBuilder()
                .append(returnType)
                .append(" ")
                .append(id)
                .append(" (")
                .append(params)
                .append(");");

        //String parameterType = select.getParameterType().getStringValue();

        PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();

        PsiMethod targetMethod = factory.createMethodFromText(sb.toString(), clazz);

        WriteCommandAction.runWriteCommandAction(clazz.getProject(), () -> {
            clazz.add(targetMethod);

            VirtualFile file = clazz.getContainingFile().getVirtualFile();

            if (file != null) {
                OpenFileDescriptor fileDescriptor = new OpenFileDescriptor(project, file);
                if (fileDescriptor.canNavigate()) {
                    fileDescriptor.navigate(true);
                    targetMethod.navigate(true);
                }
            }
            //OpenFileAction.openFile(clazz.getContainingFile().getVirtualFile(), project);
        });
    }
}
