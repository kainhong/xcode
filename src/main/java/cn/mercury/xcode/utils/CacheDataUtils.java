package cn.mercury.xcode.utils;

import com.intellij.database.psi.DbTable;
import lombok.Data;

import java.util.List;

/**
 * 缓存数据工具类
 *

 * @version 1.0.0
 * @since 2018/07/17 13:10
 */
@Data
public class CacheDataUtils {
    private volatile static CacheDataUtils cacheDataUtils;

    /**
     * 单例模式
     */
    public static CacheDataUtils getInstance() {
        if (cacheDataUtils == null) {
            synchronized (CacheDataUtils.class) {
                if (cacheDataUtils == null) {
                    cacheDataUtils = new CacheDataUtils();
                }
            }
        }
        return cacheDataUtils;
    }

    private CacheDataUtils() {
    }


    /**
     * 所有选中的表
     */
    private List<DbTable> dbTableList;

}
