package cn.mercury.xcode.mybatis.generate;

import cn.mercury.xcode.mybatis.utils.StringUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

import java.util.Arrays;

public class ManagerClassMethodBuilder extends AbstractClassMethodBuilder {

    ServiceClassMethodBuilder serviceClassMethodBuilder;

    public ManagerClassMethodBuilder(Project project, PsiMethod method,
                                     String packageName,
                                     String beanName) {

        super(project, method, packageName.replaceAll("repository|dal", "manager"), beanName);

        this.serviceClassMethodBuilder = new ServiceClassMethodBuilder(project, method, packageName, beanName);
    }

    protected PsiClass getVariableType() {
        return serviceClassMethodBuilder.getInterfaceClazz();
    }

    protected PsiClass getReferenceClazz() {
        return this.serviceClassMethodBuilder.getInterfaceClazz();
    }

    public void build() {
        this.serviceClassMethodBuilder.build();
        if (this.serviceClassMethodBuilder.getInterfaceClazz() != null && this.serviceClassMethodBuilder.getImplClazz() != null) {
            this.init();
            super.build();
        }
    }

    @Override
    protected boolean enableConvertMapParameter() {
        return false;
    }

    @Override
    protected String getDefaultReferenceName() {
        return this.oldVersion ? StringUtil.lowerCaseFirstChar(this.beanName) + "Service" : "service";
    }

    @Override
    protected String getName() {
        return "Manager";
    }

}
