package cn.mercury.xcode.sql.generate;

import cn.hutool.core.io.FileUtil;
import cn.mercury.mybatis.JsonUtils;
import cn.mercury.xcode.sql.setting.TemplateGroup;
import org.junit.Test;

import java.io.IOException;
import java.util.regex.Pattern;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class UtilsTest {

    @Test
    public void json_test() throws IOException {
        String json = FileUtil.readUtf8String("E:\\work\\xcode\\src\\main\\resources\\extensions\\templates\\Spring\\.meta.json");
        TemplateGroup group = JsonUtils.fromJson(json, TemplateGroup.class);

        assertNotNull(group);
    }

    @Test
    public void regex_test(){
        Pattern regex = Pattern.compile("varchar", Pattern.CASE_INSENSITIVE);
        boolean b = regex.matcher("varchar(20)").find();
        assertTrue(b);
    }
}
