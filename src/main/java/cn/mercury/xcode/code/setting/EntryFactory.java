package cn.mercury.xcode.code.setting;

import java.lang.reflect.InvocationTargetException;

/**
 * 抽象的项目工厂
 *

 * @version 1.0.0
 * @date 2021/08/11 10:44
 */
public class EntryFactory {

    public static <T extends IEntry<T>> T createDefaultVal(Class<T> cls) {
        try {
            T instance = cls.getDeclaredConstructor().newInstance();
            return instance.defaultVal();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalArgumentException("构建示例失败", e);
        }
    }

}
