package cn.mercury.xcode.mybatis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowColorConfig {
    @JsonProperty
    private String keyword;

    @JsonProperty(index = 1)
    private boolean enabled;

    @JsonProperty(index = 2)
    private String bg;

    @JsonProperty(index = 3)
    private boolean bgEnabled;

    @JsonProperty(index = 4)
    private String fg;

    @JsonProperty(index = 5)
    private boolean fgEnabled;
}
