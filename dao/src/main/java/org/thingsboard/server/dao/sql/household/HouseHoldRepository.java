package org.thingsboard.server.dao.sql.household;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.common.data.household.HouseHold;
import org.thingsboard.server.common.data.household.HouseHoldActionType;
import org.thingsboard.server.common.data.household.HouseHoldStatus;
import org.thingsboard.server.dao.model.sql.HouseHoldEntity;
import org.thingsboard.server.dao.model.sql.HouseHoldLogEntity;

import java.util.List;
import java.util.UUID;
/**
 * Created by Prashant on 5/16/2024.
 */
public interface HouseHoldRepository extends JpaRepository<HouseHoldEntity, UUID> {

    HouseHoldEntity findByHouseHoldIdAndTvId(Long houseHoldId, Long tvId);

    @Query("SELECT new HouseHoldEntity(h, hl) FROM HouseHoldEntity h " +
            "LEFT JOIN HouseHoldLogEntity hl ON hl.id = h.activeFieldStatusLogId " +
            " WHERE (COALESCE(:houseHoldIds, NULL) IS NULL OR h.houseHoldId IN (:houseHoldIds)) " +
            " AND  (:fieldStatus IS NULL OR h.fieldStatus = :fieldStatus)" +
            " AND (:updatedStartDate IS NULL OR hl.createdTime >= :updatedStartDate) " +
            " AND (:updatedEndDate IS NULL OR hl.createdTime <= :updatedEndDate) " +
            " AND (:audiSessionStartDate IS NULL OR hl.validFrom >= :audiSessionStartDate OR (CASE WHEN (hl.validTill IS NULL) THEN CAST((extract(epoch from CURRENT_TIMESTAMP)*1000) as long) ELSE hl.validTill END) >= :audiSessionStartDate ) " +
            " AND (:audiSessionEndDate IS NULL OR (CASE WHEN (hl.validTill IS NULL) THEN CAST((extract(epoch from CURRENT_TIMESTAMP)*1000) as long) ELSE hl.validTill END) <= :audiSessionEndDate OR hl.validFrom <= :audiSessionEndDate )")
    Page<HouseHoldEntity> findWithFieldStatusInfo(@Param("houseHoldIds") List<Long> houseHoldIds,
                                              @Param("fieldStatus") HouseHoldStatus fieldStatus,
                                              @Param("updatedStartDate") Long updatedStartDate,
                                              @Param("updatedEndDate") Long updatedEndDate,
                                              @Param("audiSessionStartDate") Long audiSessionStartDate,
                                              @Param("audiSessionEndDate") Long audiSessionEndDate,
                                              Pageable pageable);
    @Query("SELECT new HouseHoldEntity(h, hl) FROM HouseHoldEntity h " +
            "LEFT JOIN HouseHoldLogEntity hl ON hl.id = h.activeProfileLogId " +
            "LEFT JOIN DeviceProfileEntity d ON d.id = h.profileId " +
            " WHERE (COALESCE(:houseHoldIds, NULL) IS NULL OR h.houseHoldId IN (:houseHoldIds)) " +
            " AND (:updatedStartDate IS NULL OR hl.createdTime >= :updatedStartDate) " +
            " AND (:updatedEndDate IS NULL OR hl.createdTime <= :updatedEndDate) " +
            " AND (:audiSessionStartDate IS NULL OR hl.validFrom >= :audiSessionStartDate OR (CASE WHEN (hl.validTill IS NULL) THEN CAST((extract(epoch from CURRENT_TIMESTAMP)*1000) as long) ELSE hl.validTill END) >= :audiSessionStartDate ) " +
            " AND (:audiSessionEndDate IS NULL OR (CASE WHEN (hl.validTill IS NULL) THEN CAST((extract(epoch from CURRENT_TIMESTAMP)*1000) as long) ELSE hl.validTill END) <= :audiSessionEndDate OR hl.validFrom <= :audiSessionEndDate )")
    Page<HouseHoldEntity> findWithProfileInfo(@Param("houseHoldIds") List<Long> houseHoldIds,
                                                  @Param("updatedStartDate") Long updatedStartDate,
                                                  @Param("updatedEndDate") Long updatedEndDate,
                                                  @Param("audiSessionStartDate") Long audiSessionStartDate,
                                                  @Param("audiSessionEndDate") Long audiSessionEndDate,
                                                  Pageable pageable);
    @Query("SELECT new HouseHoldEntity(h, hl) FROM HouseHoldEntity h " +
            "LEFT JOIN HouseHoldLogEntity hl ON hl.id = h.activeProfileLogId " +
            "LEFT JOIN DeviceProfileEntity d ON d.id = h.profileId " +
            " WHERE (COALESCE(:houseHoldIds, NULL) IS NULL OR h.houseHoldId IN (:houseHoldIds)) " +
            " AND (h.profileId = :profileId)" +
            " AND (:updatedStartDate IS NULL OR hl.createdTime >= :updatedStartDate) " +
            " AND (:updatedEndDate IS NULL OR hl.createdTime <= :updatedEndDate) " +
            " AND (:audiSessionStartDate IS NULL OR hl.validFrom >= :audiSessionStartDate OR (CASE WHEN (hl.validTill IS NULL) THEN CAST((extract(epoch from CURRENT_TIMESTAMP)*1000) as long) ELSE hl.validTill END) >= :audiSessionStartDate ) " +
            " AND (:audiSessionEndDate IS NULL OR (CASE WHEN (hl.validTill IS NULL) THEN CAST((extract(epoch from CURRENT_TIMESTAMP)*1000) as long) ELSE hl.validTill END) <= :audiSessionEndDate OR hl.validFrom <= :audiSessionEndDate )")
    Page<HouseHoldEntity> findWithProfileInfo(@Param("houseHoldIds") List<Long> houseHoldIds,
                                              @Param("profileId") UUID profileId,
                                              @Param("updatedStartDate") Long updatedStartDate,
                                              @Param("updatedEndDate") Long updatedEndDate,
                                              @Param("audiSessionStartDate") Long audiSessionStartDate,
                                              @Param("audiSessionEndDate") Long audiSessionEndDate,
                                              Pageable pageable);
}
