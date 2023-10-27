package cn.mercury.xcode.code.setting;

import cn.mercury.xcode.utils.CloneUtils;

/**
 * 抽象的项
 *

 * @version 1.0.0
 * @date 2021/08/11 09:47
 */
public interface AbstractItem<T extends AbstractItem> {
    /**
     * 默认值
     *
     * @return {@link T}
     */
    T defaultVal();

    /**
     * 克隆对象
     *
     * @return 克隆结果
     */
    @SuppressWarnings("unchecked")
    default T cloneObj() {
        return (T) CloneUtils.cloneByJson(this);
    }
}
