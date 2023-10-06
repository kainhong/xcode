package cn.mercury.xcode.mybatis.generate;

import cn.mercury.xcode.mybatis.utils.StringUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

public class ServiceClassMethodBuilder extends AbstractClassMethodBuilder {

    public ServiceClassMethodBuilder(Project project, PsiMethod method, String packageName, String beanName) {
        super(project, method, packageName.replaceAll("repository|dal","service."), beanName);
    }

    @Override
    protected String getDefaultReferenceName() {
        return this.oldVersion ? StringUtil.lowerCaseFirstChar(this.beanName) + "Mapper" : "repository";
    }

    @Override
    protected PsiClass getVariableType() {
        return method.getContainingClass();
    }

    @Override
    protected String getName() {
        return "Service";
    }




}
