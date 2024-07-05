package org.thingsboard.server.dao.sql.asset;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.asset.MeterInstallationStatus;
import org.thingsboard.server.common.data.asset.RemoteInstallationStatus;
import org.thingsboard.server.dao.model.sql.AssetInstallationEntity;
import org.thingsboard.server.dao.model.sql.AssetInstallationInfoEntity;

import java.util.List;
import java.util.UUID;

public interface AssetInstallationRepository extends JpaRepository<AssetInstallationEntity, UUID> {

    @Query("SELECT ai FROM AssetInstallationEntity ai " +
            "WHERE (:houseHoldId IS NULL OR ai.houseHoldId = :houseHoldId) " +
            "AND (:meterNo IS NULL OR ai.meterNo = :meterNo) " +
            "AND (:remoteNo IS NULL OR ai.remoteNo = :remoteNo) " +
            "AND (:tvId IS NULL OR ai.tvId = :tvId) " +
            "AND (:isActive IS NULL OR ai.isActive = :isActive) " +
            "AND (:meterType IS NULL OR ai.meterType = :meterType) ")
    AssetInstallationEntity findByAssetInstallationData(
            @Param("houseHoldId") Long houseHoldId,
            @Param("meterNo") Long meterNo,
            @Param("remoteNo") Long remoteNo,
            @Param("tvId") Integer tvId,
            @Param("meterType") String meterType,
            @Param("isActive") Boolean isActive);

    @Query("SELECT new org.thingsboard.server.dao.model.sql.AssetInstallationInfoEntity(ai, os.hversionName, r.hwVersion) FROM AssetInstallationEntity ai " +
            "JOIN DeviceEntity d ON d.meterNo = ai.meterNo " +
            "JOIN OSTypeEntity os ON os.id = d.osTypeId " +
            "JOIN RemoteEntity r ON r.remoteNo = ai.remoteNo " +
            "WHERE (:houseHoldId IS NULL OR ai.houseHoldId = :houseHoldId) " +
            "AND (COALESCE(:houseHoldIds) IS NULL OR ai.houseHoldId in :houseHoldIds) " +
            "AND (:houseHoldIdFrom IS NULL OR ai.houseHoldId >= :houseHoldIdFrom) " +
            "AND (:houseHoldIdTo IS NULL OR ai.houseHoldId <= :houseHoldIdTo) " +
            "AND (:meterNo IS NULL OR ai.meterNo = :meterNo) " +
            "AND (COALESCE(:meterNos) IS NULL OR ai.meterNo in :meterNos) " +
            "AND (:meterNoFrom IS NULL OR ai.meterNo >= :meterNoFrom) " +
            "AND (:meterNoTo IS NULL OR ai.meterNo <= :meterNoTo) " +
            "AND (:remoteNo IS NULL OR ai.remoteNo = :remoteNo) " +
            "AND (COALESCE(:remoteNos) IS NULL OR ai.remoteNo in :remoteNos) " +
            "AND (:remoteNoFrom IS NULL OR ai.remoteNo >= :remoteNoFrom) " +
            "AND (:remoteNoTo IS NULL OR ai.remoteNo <= :remoteNoTo) " +
            "AND (:meterInstallationStatus IS NULL OR ai.meterInstallationStatus = :meterInstallationStatus OR ai.remoteInstallationStatus = :remoteInstallationStatus) " +
            "AND (:isActive IS NULL OR ai.isActive = :isActive) " +
            "AND (:meterType IS NULL OR ai.meterType = :meterType) " +
            "AND (:meterHardwareVersion IS NULL OR os.hversionName = :meterHardwareVersion) " +
            "AND (:remoteHardwareVersion IS NULL OR r.hwVersion = :remoteHardwareVersion) " +
            "AND (:meterInstallationDateFrom IS NULL OR ai.mInstallationActiveFrom >= :meterInstallationDateFrom) " +
            "AND (:meterInstallationDateTo IS NULL OR ai.mInstallationActiveTo <= :meterInstallationDateTo) " +
            "AND (:remoteInstallationDateFrom IS NULL OR ai.rPairingActiveFrom >= :remoteInstallationDateFrom) " +
            "AND (:remoteInstallationDateTo IS NULL OR ai.rPairingActiveTo <= :remoteInstallationDateTo) " +
            "AND (:createdDateFrom IS NULL OR ai.createdTime >= :createdDateFrom) " +
            "AND (:createdDateTo IS NULL OR ai.createdTime <= :createdDateTo) ")
    Page<AssetInstallationInfoEntity> findAssetInstallation(@Param("houseHoldId") Long houseHoldId,
                                                            @Param("houseHoldIds") List<Long> houseHoldIds,
                                                            @Param("houseHoldIdFrom") Long houseHoldIdFrom,
                                                            @Param("houseHoldIdTo") Long houseHoldIdTo,
                                                            @Param("meterNo") Long meterNo,
                                                            @Param("meterNos") List<Long> meterNos,
                                                            @Param("meterNoFrom") Long meterNoFrom,
                                                            @Param("meterNoTo") Long meterNoTo,
                                                            @Param("remoteNo") Long remoteNo,
                                                            @Param("remoteNos") List<Long> remoteNos,
                                                            @Param("remoteNoFrom") Long remoteNoFrom,
                                                            @Param("remoteNoTo") Long remoteNoTo,
                                                            @Param("isActive") Boolean isActive,
                                                            @Param("meterInstallationStatus") MeterInstallationStatus meterInstallationStatus,
                                                            @Param("remoteInstallationStatus") RemoteInstallationStatus remoteInstallationStatus,
                                                            @Param("meterType") String meterType,
                                                            @Param("createdDateFrom") Long createdDateFrom,
                                                            @Param("createdDateTo") Long createdDateTo,
                                                            @Param("meterInstallationDateFrom") Long meterInstallationDateFrom,
                                                            @Param("meterInstallationDateTo") Long meterInstallationDateTo,
                                                            @Param("remoteInstallationDateFrom") Long remoteInstallationDateFrom,
                                                            @Param("remoteInstallationDateTo") Long remoteInstallationDateTo,
                                                            @Param("remoteHardwareVersion") String remoteHardwareVersion,
                                                            @Param("meterHardwareVersion") String meterHardwareVersion,
                                                            Pageable pageable);

    @Query("SELECT distinct h.tvId FROM HouseHoldEntity h " +
            "JOIN AssetInstallationEntity ai ON ai.houseHoldId = h.houseHoldId " +
            "WHERE h.houseHoldId = :houseHoldId " +
            "AND ai.tvId != h.tvId ")
    List<Integer> findHoulseholdUninstalledTVIds(@Param("houseHoldId") Long houseHoldId);
}
