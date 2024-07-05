package org.thingsboard.server.dao.sql.household;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.household.HouseHoldActionType;
import org.thingsboard.server.common.data.household.HouseHoldLog;
import org.thingsboard.server.common.data.id.HouseHoldEntityId;
import org.thingsboard.server.dao.model.sql.HouseHoldEntity;
import org.thingsboard.server.dao.model.sql.HouseHoldLogEntity;

import java.util.List;
import java.util.UUID;

/**
 * Created by Prashant on 5/16/2024.
 */
public interface HouseHoldLogRepository extends JpaRepository<HouseHoldLogEntity, UUID> {
    HouseHoldLogEntity findFirstByHouseHoldEntityIdAndActionTypeOrderByCreatedTimeDesc(UUID houseHoldEntityId, HouseHoldActionType actionType);
    Page<HouseHoldLogEntity> findByActionTypeOrderByCreatedTimeDesc(HouseHoldActionType actionType, Pageable pageable);
    @Query("SELECT new HouseHoldLogEntity(hl, h, hlnew) FROM HouseHoldLogEntity hl " +
            "LEFT JOIN HouseHoldEntity h ON h.id = hl.houseHoldEntityId " +
            "LEFT JOIN HouseHoldLogEntity hlnew ON hl.id = hlnew.previousId " +
            "where hlnew.id is not null AND hl.actionType = :actionType " +
            " AND ((:latestData = true AND hlnew.id IS NULL) OR (:latestData <> true))" +
            " AND (COALESCE(:houseHoldIds, NULL) IS NULL OR h.houseHoldId in :houseHoldIds) " +
            " AND (:updatedStartDate IS NULL OR hl.createdTime >= :updatedStartDate) " +
            " AND (:updatedEndDate IS NULL OR hl.createdTime <= :updatedEndDate) " +
            " AND (:audiSessionStartDate IS NULL OR hl.validFrom >= :audiSessionStartDate OR (CASE WHEN (hl.validTill IS NULL) THEN CAST((extract(epoch from CURRENT_TIMESTAMP)*1000) as long) ELSE hl.validTill END) >= :audiSessionStartDate ) " +
            " AND (:audiSessionEndDate IS NULL OR (CASE WHEN (hl.validTill IS NULL) THEN CAST((extract(epoch from CURRENT_TIMESTAMP)*1000) as long) ELSE hl.validTill END) <= :audiSessionEndDate OR hl.validFrom <= :audiSessionEndDate )")
    Page<HouseHoldLogEntity> findByActionType(@Param("actionType") HouseHoldActionType actionType,
                                              @Param("latestData") Boolean latestData,
                                              @Param("houseHoldIds") List<Long> houseHoldId,
                                              @Param("updatedStartDate") Long updatedStartDate,
                                              @Param("updatedEndDate") Long updatedEndDate,
                                              @Param("audiSessionStartDate") Long audiSessionStartDate,
                                              @Param("audiSessionEndDate") Long audiSessionEndDate,
                                              Pageable pageable);

    @Query("SELECT new HouseHoldLogEntity(hl, h, hlnew) FROM HouseHoldLogEntity hl " +
            "LEFT JOIN HouseHoldEntity h ON h.id = hl.houseHoldEntityId " +
            "LEFT JOIN HouseHoldLogEntity hlnew ON hl.id = hlnew.previousId " +
            "where hlnew.id is not null AND hl.actionType=:actionType " +
            " AND hl.actionData = :actionData " +
            " AND ((:latestData = true AND hlnew.id IS NULL) OR (:latestData <> true))" +
            " AND (COALESCE(:houseHoldIds, NULL) IS NULL OR h.houseHoldId in :houseHoldIds) " +
            " AND (:updatedStartDate IS NULL OR hl.createdTime >= :updatedStartDate) " +
            " AND (:updatedEndDate IS NULL OR hl.createdTime <= :updatedEndDate) " +
            " AND (:audiSessionStartDate IS NULL OR hl.validFrom >= :audiSessionStartDate OR (CASE WHEN (hl.validTill IS NULL) THEN CAST((extract(epoch from CURRENT_TIMESTAMP)*1000) as long) ELSE hl.validTill END) >= :audiSessionStartDate ) " +
            " AND (:audiSessionEndDate IS NULL OR (CASE WHEN (hl.validTill IS NULL) THEN CAST((extract(epoch from CURRENT_TIMESTAMP)*1000) as long) ELSE hl.validTill END) <= :audiSessionEndDate OR hl.validFrom <= :audiSessionEndDate )")
    Page<HouseHoldLogEntity> findByActionTypeAndActionData(
            @Param("actionType") HouseHoldActionType actionType,
            @Param("actionData") JsonNode actionData,
            @Param("latestData") Boolean latestData,
            @Param("houseHoldIds") List<Long> houseHoldId,
            @Param("updatedStartDate") Long updatedStartDate,
            @Param("updatedEndDate") Long updatedEndDate,
            @Param("audiSessionStartDate") Long audiSessionStartDate,
            @Param("audiSessionEndDate") Long audiSessionEndDate,
            Pageable pageable);

    @Query("SELECT new HouseHoldLogEntity(hl, h, hlnew) FROM HouseHoldLogEntity hl " +
            "LEFT JOIN HouseHoldEntity h ON h.id = hl.houseHoldEntityId " +
            "LEFT JOIN HouseHoldLogEntity hlnew ON hl.id = hlnew.previousId " +
            "where hl.actionType = :actionType " +
            " AND ((:latestData = true AND hlnew.id IS NULL) OR (:latestData <> true))" +
            " AND (COALESCE(:houseHoldIds, NULL) IS NULL OR h.houseHoldId in :houseHoldIds) " +
            " AND (:updatedStartDate IS NULL OR hl.createdTime >= :updatedStartDate) " +
            " AND (:updatedEndDate IS NULL OR hl.createdTime <= :updatedEndDate) " +
            " AND (:audiSessionStartDate IS NULL OR hl.validFrom >= :audiSessionStartDate OR (CASE WHEN (hl.validTill IS NULL) THEN CAST((extract(epoch from CURRENT_TIMESTAMP)*1000) as long) ELSE hl.validTill END) >= :audiSessionStartDate ) " +
            " AND (:audiSessionEndDate IS NULL OR (CASE WHEN (hl.validTill IS NULL) THEN CAST((extract(epoch from CURRENT_TIMESTAMP)*1000) as long) ELSE hl.validTill END) <= :audiSessionEndDate OR hl.validFrom <= :audiSessionEndDate )")
    Page<HouseHoldLogEntity> findMemberInfoHistory(@Param("actionType") HouseHoldActionType actionType,
                                                   @Param("latestData") Boolean latestData,
                                                   @Param("houseHoldIds") List<Long> houseHoldIds,
                                                   @Param("updatedStartDate") Long updatedStartDate,
                                                   @Param("updatedEndDate") Long updatedEndDate,
                                                   @Param("audiSessionStartDate") Long audiSessionStartDate,
                                                   @Param("audiSessionEndDate") Long audiSessionEndDate,
                                                   Pageable toPageable);
    @Transactional
    @Modifying
    @Query("DELETE FROM HouseHoldLogEntity hl " +
            "where hl.houseHoldEntityId = :houseHoldEntityId AND hl.actionType = :actionType " +
            " AND hl.validFrom >= :date"
            )
    void deleteAllByHouseholdIdAndActionTypeAndDate(@Param("actionType") HouseHoldActionType actionType,
                                                   @Param("houseHoldEntityId") UUID houseHoldEntityId,
                                                   @Param("date") Long date);

    @Query("SELECT hl FROM HouseHoldLogEntity hl " +
            "where hl.houseHoldEntityId = :houseHoldEntityId AND hl.actionType = :actionType " +
            " AND hl.validFrom >= :date " +
            " AND (hl.validTill is null OR hl.validTill <= :date) "
    )
    HouseHoldLog findByHouseholdIdAndActionTypeAndDate(@Param("actionType") HouseHoldActionType actionType,
                                                    @Param("houseHoldEntityId") UUID houseHoldEntityId,
                                                    @Param("date") Long date);

}
