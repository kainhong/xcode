package cn.mercury.xcode.code.setting;

import cn.mercury.xcode.utils.CloneUtils;

import java.util.List;


public interface IEntryGroup<T, E extends IEntry<E>> {
    /**
     * 获取分组名称
     *
     * @return 分组名称
     */
    String getName();

    /**
     * 设置分组名称
     *
     * @param name 分组名称
     */
    void setName(String name);

    /**
     * 获取元素集合
     *
     * @return 元素集合
     */
    List<E> getItems();

    /**
     * 设置元素集合
     *
     * @param values 元素集合
     */
    void setItems(List<E> values);

    /**
     * 克隆对象
     *
     * @return {@link T}
     */
    @SuppressWarnings("unchecked")
    default T clone() {
        return (T) CloneUtils.cloneByJson(this);
    }
}
