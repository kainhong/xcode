package cn.mercury.xcode.mybatis.language.alias;

import cn.mercury.xcode.mybatis.utils.JavaUtils;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;



import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * The type Inner alias resolver.
 *

 */
public class InnerAliasResolver extends AliasResolver {
    private static final Logger logger = com.intellij.openapi.diagnostic.Logger.getInstance(InnerAliasResolver.class);
    private volatile Set<AliasDesc> innerAliasDescs = null;

    /**
     * Instantiates a new Inner alias resolver.
     *
     * @param project the project
     */
    public InnerAliasResolver(Project project) {
        super(project);
    }

    private Set<AliasDesc> getAliasDescSet() {
        Set<AliasDesc> aliasDescs = new HashSet<>();
        addAliasDesc(aliasDescs, "java.lang.String", "string");
        addAliasDesc(aliasDescs, "java.lang.Byte", "byte");
        addAliasDesc(aliasDescs, "java.lang.Long", "long");
        addAliasDesc(aliasDescs, "java.lang.Short", "short");
        addAliasDesc(aliasDescs, "java.lang.Integer", "int");
        addAliasDesc(aliasDescs, "java.lang.Integer", "integer");
        addAliasDesc(aliasDescs, "java.lang.Double", "double");
        addAliasDesc(aliasDescs, "java.lang.Float", "float");
        addAliasDesc(aliasDescs, "java.lang.Boolean", "boolean");
        addAliasDesc(aliasDescs, "java.util.Date", "date");
        addAliasDesc(aliasDescs, "java.math.BigDecimal", "decimal");
        addAliasDesc(aliasDescs, "java.lang.Object", "object");
        addAliasDesc(aliasDescs, "java.util.Map", "map");
        addAliasDesc(aliasDescs, "java.util.HashMap", "hashmap");
        addAliasDesc(aliasDescs, "java.util.List", "list");
        addAliasDesc(aliasDescs, "java.util.ArrayList", "arraylist");
        addAliasDesc(aliasDescs, "java.util.Collection", "collection");
        addAliasDesc(aliasDescs, "java.util.Iterator", "iterator");
        return aliasDescs;
    }

    private void addAliasDesc(Set<AliasDesc> aliasDescs, String clazz, String alias) {
        Optional<PsiClass> psiClassOptional = JavaUtils.findClazz(project, clazz);
        if (psiClassOptional.isPresent()) {
            PsiClass psiClass = psiClassOptional.get();
            AliasDesc aliasDesc = AliasDesc.create(psiClass, alias);
            aliasDescs.add(aliasDesc);
        } else {
            logger.error("无法找到别名映射, class: {}, alias: {}", clazz, alias);
        }

    }

    /**
     * 支持延迟识别, 当项目第一次打开时，可能未配置JDK， 在未配置JDK时， 内部别名无法注册。
     * 这里支持等手动配置JDK后才开始缓存
     *
     * @param element the element
     * @return
     */
    @NotNull
    @Override
    public Set<AliasDesc> getClassAliasDescriptions(@Nullable PsiElement element) {
        if (innerAliasDescs == null) {
            Set<AliasDesc> aliasDescSet = getAliasDescSet();
            if (!aliasDescSet.isEmpty()) {
                synchronized (this) {
                    this.innerAliasDescs = aliasDescSet;
                }
            }
        }
        if (innerAliasDescs == null) {
            return Collections.emptySet();
        }
        return innerAliasDescs;
    }

}
