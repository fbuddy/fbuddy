package org.thingsboard.server.common.data.remote;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.thingsboard.server.common.data.Remote;
import org.thingsboard.server.common.data.asset.AssetInstallation;

@ApiModel
@Data
@EqualsAndHashCode(callSuper = true)
public class RemoteInfo extends Remote {
    private String statusName;

    public RemoteInfo() {
        super();
    }

    public RemoteInfo(Remote remote, String statusName) {
        super(remote);
        this.statusName = statusName;
    }
}
