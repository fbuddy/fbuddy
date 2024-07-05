package org.thingsboard.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.asset.AssetInstallation;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.Entity;
import javax.persistence.Table;

import static org.thingsboard.server.dao.model.ModelConstants.ASSET_INSTALLATION_TABLE_NAME;


@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ASSET_INSTALLATION_TABLE_NAME)
public class AssetInstallationEntity extends AbstractAssetInstallationEntity<AssetInstallation> {
    public AssetInstallationEntity() {
        super();
    }

    public AssetInstallationEntity(AssetInstallation assetInstallation) {
        super(assetInstallation);
    }

    @Override
    public AssetInstallation toData() {
        return super.toAssetInstallation();
    }

}
