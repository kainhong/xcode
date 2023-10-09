package cn.mercury.xcode.intention;

import cn.mercury.xcode.mybatis.generate.RepositoryMethodBuilder;
import cn.mercury.xcode.mybatis.language.dom.model.IdDomElement;
import cn.mercury.xcode.mybatis.language.dom.model.Mapper;
import cn.mercury.xcode.mybatis.utils.JavaUtils;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MybatisMapperIntention extends AbstractGenericIntention {

    static final MybatisMapperIntentionChooser chooser = new MybatisMapperIntentionChooser();

    public MybatisMapperIntention() {
        super(chooser);
    }

    @Override
    public @IntentionName @NotNull String getText() {
        return "[xCode] Generate Method";
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        XmlTag rootTag = ((XmlFile) file).getRootTag();
        DomElement rootDoc = DomUtil.getDomElement(rootTag);

        if (!(rootDoc instanceof Mapper)) {
            return;
        }

        Mapper mapper = (Mapper) rootDoc;

        @Nullable DomElement element = DomUtil.getContextElement(editor);

        @NotNull String namespace = mapper.getNamespace().getRawText();

        var parent = DomUtil.getParentOfType(element, IdDomElement.class, true);

        var clazz = JavaUtils.findClazz(project, namespace).orElse(null);

        if (clazz == null) {
            return;
        }

        var builder = new RepositoryMethodBuilder(project, clazz, parent);

        builder.build();
    }
}
