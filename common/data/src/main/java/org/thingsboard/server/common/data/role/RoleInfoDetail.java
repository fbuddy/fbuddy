package org.thingsboard.server.common.data.role;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.thingsboard.server.common.data.validation.Length;
import org.thingsboard.server.common.data.validation.NoXss;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class RoleInfoDetail extends Role{
    @NoXss
    @Length(fieldName = "permissions")
    @ApiModelProperty(position = 6, value = "JSON object with the module Id and permission of the Role")
    private List<RolePermissionDetail> modules = new ArrayList<>();

    @Data
    @NoArgsConstructor
    public static class RolePermissionDetail extends RolePermission{
        @NoXss
        @Length(fieldName = "name")
        @ApiModelProperty(position = 4, value = "Name of the Module", example = "Module 1")
        private String name;

        public RolePermissionDetail(RolePermission permission)
        {
            super(permission);

        }
    }

    public RoleInfoDetail(Role role)
    {
        super(role);

    }
}
