package cn.mercury.xcode.intention;

import cn.mercury.xcode.mybatis.language.dom.model.*;
import cn.mercury.xcode.mybatis.utils.JavaUtils;
import cn.mercury.xcode.mybatis.utils.MapperUtils;
import cn.mercury.xcode.mybatis.utils.StringUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class MybatisMapperIntentionChooser implements IntentionChooser {


    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        if( file instanceof XmlFile == false)
            return false;

        XmlTag rootTag = ((XmlFile) file).getRootTag();

        DomElement rootDoc = DomUtil.getDomElement(rootTag);

        if (rootDoc instanceof Mapper == false) {
            return false;
        }

        DomElement element = DomUtil.getContextElement(editor);
        if (element == null)
            return false;

        @NotNull var parent = DomUtil.getParentOfType(element, IdDomElement.class, true);

        if (parent instanceof Select || parent instanceof Delete || parent instanceof Update || parent instanceof Insert) {

            Mapper mapper = (Mapper) rootDoc;

            String ns = mapper.getNamespace().getRawText();
            String id = MapperUtils.getId(parent);

            if (StringUtil.isEmpty(ns) || StringUtil.isEmpty(id))
                return false;

            var clazz = JavaUtils.findClazz(project, ns).orElse(null);

            if (clazz == null)
                return false;

            PsiMethod method = Arrays.stream(clazz.getAllMethods())
                    .filter(m -> m.getName().equals(id))
                    .findFirst()
                    .orElse(null);

            return method == null;
        }

        return false;
    }
}
