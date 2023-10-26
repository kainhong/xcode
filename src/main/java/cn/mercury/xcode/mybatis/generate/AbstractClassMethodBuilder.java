package cn.mercury.xcode.mybatis.generate;

import cn.mercury.xcode.mybatis.utils.JavaUtils;
import com.intellij.ide.actions.OpenFileAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

public abstract class AbstractClassMethodBuilder {

    protected final Project project;

    protected final String packageName;

    protected final String beanName;

    protected boolean oldVersion = false;

    protected abstract String getName();

    protected PsiMethod method;

    protected PsiClass implClazz;

    public PsiClass getImplClazz() {
        return implClazz;
    }

    public PsiClass getInterfaceClazz() {
        return interfaceClazz;
    }

    protected PsiClass interfaceClazz;

    protected AbstractClassMethodBuilder(Project project, PsiMethod method, String packageName, String beanName) {
        this.project = project;
        this.packageName = packageName + "." + this.getName().toLowerCase() + ".";
        this.beanName = beanName;
        this.method = method;
    }

    protected void init() {
        implClazz = findClass();
        if (implClazz != null) {
            interfaceClazz = findInterface();
        }
        if (implClazz == null || interfaceClazz == null)
            this.findWillBeCompletedClass();
    }

    protected PsiClass getReferenceClazz() {
        return method.getContainingClass();
    }

    protected void findWillBeCompletedClass() {
        var all = ReferencesSearch.search(getReferenceClazz(),
                GlobalSearchScope.projectScope(project),
                false).findAll();

        String type = this.getName();

        for (PsiReference psiReference : all) {
            PsiClass referenceClazz = PsiTreeUtil.getParentOfType(psiReference.getElement(), PsiClass.class);
            if (referenceClazz == null)
                continue;

            String className = referenceClazz.getName();
            if (className.equals(beanName + type) || className.equals(beanName + type + "Impl")) {
                this.implClazz = referenceClazz;
                break;
            }
        }
        if (implClazz == null)
            return;

        var interfaces = implClazz.getInterfaces();
        for (var clazz : interfaces) {
            if (clazz.getName().equals(beanName + type) || clazz.getName().equals("I" + beanName + type)) {
                this.interfaceClazz = clazz;
                break;
            }
        }
    }

    public void build() {
        if (interfaceClazz != null && implClazz != null) {
            createMethod(this.interfaceClazz);
            createMethod(this.implClazz);
        }
    }

    protected PsiClass findClass() {
        String className = this.packageName + "impl." + this.beanName + this.getName();
        Optional<PsiClass> op = JavaUtils.findClazz(project, className);

        if (op.isPresent()) {
            PsiClass clz = op.get();

            if (clz.isInterface()) {
                this.oldVersion = true;
            } else {
                return clz;
            }
        }

        className = this.packageName + this.beanName + this.getName() + "Impl";
        op = JavaUtils.findClazz(project, className);
        if (!op.isPresent())
            return null;
        return op.orElseGet(null);
    }

    protected PsiClass findInterface() {
        String className = null;
        if (oldVersion)
            className = this.packageName + this.beanName + this.getName();
        else
            className = this.packageName + "I" + this.beanName + this.getName();

        Optional<PsiClass> clz = JavaUtils.findClazz(project, className);

        return clz.orElseGet(null);
    }

    protected static final String QUERY_TYPE = "cn.mercury.basic.query.Query";
    protected static final String MAP_TYPE = "java.util.Map<java.lang.String,java.lang.Object>";

    protected abstract boolean enableConvertMapParameter();

    protected String getMethodParameterString() {
        PsiParameter[] parameters = this.method.getParameterList().getParameters();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parameters.length; i++) {
            PsiParameter parameter = parameters[i];
            String parameterType = parameter.getType().getCanonicalText();
            String name = parameter.getName();
            if (!this.oldVersion && MAP_TYPE.equals(parameterType)) {
                parameterType = QUERY_TYPE;
                name = "query";
            }
            sb.append(parameterType).append(" ").append(name);
            if (i < parameters.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    protected PsiParameter[] getParameters() {
        return this.method.getParameterList().getParameters();
    }

    protected String getMethodParameterVariable() {
        PsiParameter[] parameters = getParameters();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parameters.length; i++) {
            PsiParameter parameter = parameters[i];
            String parameterType = parameter.getType().getCanonicalText();
            String name = parameter.getName();
            if (!this.oldVersion && MAP_TYPE.equals(parameterType)) {
                if (enableConvertMapParameter())
                    name = "query.asMap()";
                else
                    name = "query";
            }
            sb.append(" ").append(name);
            if (i < parameters.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    protected abstract String getDefaultReferenceName();

    protected abstract PsiClass getVariableType();

    protected String findVariableByType() {
        @Nullable PsiClass type = getVariableType();
        if (type == null)
            return getDefaultReferenceName();

        var fields = this.implClazz.getFields();

        var op = Arrays.stream(fields)
                .filter(f -> f.getType().getCanonicalText().equals(type.getQualifiedName()))
                .findFirst();

        if (op.isPresent()) {
            return op.get().getName();
        }
        return getDefaultReferenceName();
    }

    protected boolean validate(PsiClass clazz) {
        if (clazz == null)
            return false;

        return Arrays.stream(clazz.getMethods()).anyMatch(m ->
                m.getName().equals(this.method.getName())
                        && m.getParameterList().getParametersCount() == m.getParameterList().getParametersCount()
        );
    }


    protected void createMethod(PsiClass clazz) {
        if (validate(clazz))
            return;

        PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
        String methodName = this.method.getName();
        StringBuilder sb = new StringBuilder();
        String refenceName = findVariableByType();

        sb.append("public ")
                .append(this.method.getReturnType().getCanonicalText())
                .append(" ")
                .append(methodName)
                .append(" (")
                .append(getMethodParameterString());

        if (!clazz.isInterface()) {
            sb.append("){");
            sb.append("return  ")
                    .append(refenceName)
                    .append(".")
                    .append(methodName)
                    .append("(")
                    .append(getMethodParameterVariable())
                    .append(");}");
        } else {
            sb.append(");");
        }

        String methodText = sb.toString();

        PsiMethod newMethod = factory.createMethodFromText(methodText, clazz);

        ApplicationManager.getApplication().invokeLater(() -> WriteCommandAction.runWriteCommandAction(clazz.getProject(), beanName, method.getName(), () -> {
            clazz.add(newMethod);
            OpenFileAction.openFile(clazz.getContainingFile().getVirtualFile(), project);
        }));

    }
}
