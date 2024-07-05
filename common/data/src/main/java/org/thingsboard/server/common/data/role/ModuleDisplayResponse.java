package org.thingsboard.server.common.data.role;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.thingsboard.server.common.data.id.ModuleId;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

import java.util.ArrayList;
import java.util.List;

@Data
public class ModuleDisplayResponse {
    @JsonIgnore
    private ModuleId moduleId;
    @NoXss
    @Length(fieldName = "name")
    @ApiModelProperty(position = 1, value = "Name of the Module", example = "Module 1")
    private String name;

    @NoXss
    @Length(fieldName = "modules")
    @ApiModelProperty(position = 2, value = "JSON object with the module Id")
    private List<SubModuleDisplayResponse> modules = new ArrayList<>();

    @NoXss
    @Length(fieldName = "orderNumber")
    @ApiModelProperty(position = 3, value = "Display Order Number")
    private Long orderNumber = 0L;

    @Data
    public static class SubModuleDisplayResponse {
        @JsonIgnore
        private ModuleId moduleId;
        @NoXss
        @Length(fieldName = "name")
        @ApiModelProperty(position = 1, value = "Name of the Sub Module", example = "Sub Module 1")
        private String name;

        private Long orderNumber = 0L;

        @NoXss
        @Length(fieldName = "permissions")
        @ApiModelProperty(position = 2, value = "JSON object with the module Id")
        private List<Module> subModules = new ArrayList<>();


    }
}
