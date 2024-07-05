package org.thingsboard.server.dao.sql.remote;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.thingsboard.server.dao.model.sql.RemoteEntity;
import org.thingsboard.server.dao.model.sql.RemoteInfoEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface RemoteRepository extends JpaRepository<RemoteEntity, UUID> {
    @Query("SELECT d FROM RemoteEntity d WHERE d.id = :remoteId")
    RemoteEntity findRemoteEntityById(@Param("remoteId") UUID remoteId);

    @Query("SELECT d FROM RemoteEntity d WHERE d.remoteNo = :remoteNo")
    RemoteEntity findRemoteEntityByRemoteNo(@Param("remoteNo") Long remoteNo);

    @Query("SELECT COALESCE(MAX(e.remoteNo), 0) FROM RemoteEntity e")
    Long findMaxRemoteNo();

    @Query("SELECT new org.thingsboard.server.dao.model.sql.RemoteInfoEntity(r,s.name) FROM RemoteEntity r " +
            "JOIN StatusEntity s ON s.id = r.statusId " +
            "WHERE (:remoteNo IS NULL OR r.remoteNo = :remoteNo) " +
            "AND (COALESCE(:remoteNos) IS NULL OR r.remoteNo in :remoteNos) " +
            "AND (:remoteNoFrom IS NULL OR r.remoteNo >= :remoteNoFrom) " +
            "AND (:remoteNoTo IS NULL OR r.remoteNo <= :remoteNoTo) " +
            "AND (:statusId IS NULL OR r.statusId = uuid(:statusId)) " +
            "AND (:hwVersion IS NULL OR r.hwVersion = :hwVersion) " +
            "AND (:productionDateFrom IS NULL OR r.productionDate >= :productionDateFrom) " +
            "AND (:productionDateTo IS NULL OR r.productionDate <= :productionDateTo) ")
    Page<RemoteInfoEntity> findRemote(@Param("remoteNo") Long remoteNo,
                                      @Param("remoteNos") List<Long> remoteNos,
                                      @Param("remoteNoFrom") Long remoteNoFrom,
                                      @Param("remoteNoTo") Long remoteNoTo,
                                      @Param("statusId") String statusId,
                                      @Param("hwVersion") String hwVersion,
                                      @Param("productionDateFrom") Long productionDateFrom,
                                      @Param("productionDateTo") Long productionDateTo,
                                      Pageable pageable);

    @Query("SELECT " +
            "SUM(CASE WHEN s.name ='STANDBY' THEN 1 ELSE 0 END) AS standByCt, " +
            "(SUM(CASE WHEN s.name ='STANDBY' THEN 1 ELSE 0 END) / COUNT(*)) * 100 AS standByPct, " +
            "SUM(CASE WHEN s.name = 'INSTALLED' THEN 1 ELSE 0 END) AS installedCt, " +
            "(SUM(CASE WHEN s.name = 'INSTALLED' THEN 1 ELSE 0 END) / COUNT(*)) * 100 AS installedPct, " +
            "SUM(CASE WHEN s.name = 'UNINSTALL_IN_PROGRESS' THEN 1 ELSE 0 END) AS uninstallInProgressCt, " +
            "(SUM(CASE WHEN s.name = 'UNINSTALL_IN_PROGRESS' THEN 1 ELSE 0 END) / COUNT(*)) * 100 AS uninstallInProgressPct " +
            "FROM RemoteEntity r " +
            "JOIN StatusEntity s ON s.id = r.statusId ")
    Map<String, Long> findRemoteSummary();
}
