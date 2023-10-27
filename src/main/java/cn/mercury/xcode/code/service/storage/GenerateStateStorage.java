package cn.mercury.xcode.code.service.storage;

import cn.mercury.xcode.code.setting.type.DbTypeMappingGroup;
import cn.mercury.xcode.model.GlobalConfig;
import cn.mercury.xcode.model.GlobalConfigGroup;
import lombok.Data;

@Data
public class GenerateStateStorage {

    private GenerateSetting generateSetting;

    private DbTypeMappingGroup dbTypeMappingGroup;

    private GlobalConfigGroup globalConfigGroup;
}
