package cn.mercury.xcode.mybatis.language.dom.converter;


import cn.mercury.xcode.mybatis.language.dom.model.Mapper;
import cn.mercury.xcode.mybatis.utils.JavaUtils;
import cn.mercury.xcode.mybatis.utils.MapperUtils;
import com.intellij.psi.PsiMethod;
import com.intellij.util.xml.ConvertContext;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * The type Dao method converter.
 *

 */
public class DaoMethodConverter extends ConverterAdaptor<Object> {

    /**
     * id 的转换允许有空值 ， （这是一个不合理的结构）
     * 例如 selectKey 没有id
     * select,insert,update,delete 有 id
     *
     * @param id
     * @param context
     * @return
     */
    @Nullable
    @Override
    public Object fromString(@Nullable @NonNls String id, ConvertContext context) {
        Mapper mapper = MapperUtils.getMapper(context.getInvocationElement());
        Optional<PsiMethod> method = JavaUtils.findMethod(context.getProject(), MapperUtils.getNamespace(mapper), id);
        if (method.isPresent()) {
            return method.get();
        }
        return context.getInvocationElement();
    }

}
