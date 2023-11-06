package cn.mercury.xcode.code.service.storage;

import cn.mercury.xcode.code.setting.type.DbTypeMappingEntryGroup;
import cn.mercury.xcode.model.GlobalConfigEntryGroup;
import lombok.Data;

@Data
public class GenerateStateStorage {

    private GenerateSetting generateSetting;

    private DbTypeMappingEntryGroup dbTypeMappingGroup;

    private GlobalConfigEntryGroup globalConfigGroup;

    private String extendTemplateGroup;
}
