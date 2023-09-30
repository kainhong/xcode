package cn.mercury.xcode.mybatis.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogErrorMod {
    /**
     * sql
     */
    private String sql;
    /**
     * 参数
     */
    private String params;
    /**
     * 发生时间
     */
    private String time;
    /**
     * 错误信息
     */
    private String errorMsg;

    public Object[] toArray() {
        return new Object[]{sql, params, time, errorMsg};
    }

    public LogErrorMod toMod(Object[] obj) {
        return new LogErrorMod(
                (String) obj[0],
                (String) obj[1],
                (String) obj[2],
                (String) obj[3]);
    }
}
