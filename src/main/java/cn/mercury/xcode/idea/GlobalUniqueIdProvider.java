package cn.mercury.xcode.idea;

import com.intellij.ide.util.PropertiesComponent;

import java.util.UUID;

public class GlobalUniqueIdProvider {
    private static final String UNIQUE_ID_KEY = "xCodeGlobalUniqueId";

    public static String getGlobalUniqueId() {
        PropertiesComponent propertiesComponent = PropertiesComponent.getInstance();
        String uniqueId = propertiesComponent.getValue(UNIQUE_ID_KEY);

        if (uniqueId == null || uniqueId.isEmpty()) {
            uniqueId = generateUniqueId(); // 生成新的唯一标识符
            propertiesComponent.setValue(UNIQUE_ID_KEY, uniqueId);
        }

        return uniqueId;
    }

    private static String generateUniqueId() {
        UUID uniqueId = UUID.randomUUID();
        return uniqueId.toString();
    }
}
