package org.thingsboard.server.dao.sql.remote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.Remote;
import org.thingsboard.server.common.data.asset.RemoteInstallationStatus;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.remote.RemoteFilter;
import org.thingsboard.server.common.data.remote.RemoteInfo;
import org.thingsboard.server.common.data.util.CollectionsUtil;
import org.thingsboard.server.common.data.util.TypeCastUtil;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.sql.RemoteEntity;
import org.thingsboard.server.dao.model.sql.RemoteInfoEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@SqlDao
public class JpaRemoteDao extends JpaAbstractDao<RemoteEntity, Remote> {
    @Autowired
    private RemoteRepository remoteRepository;

    @Override
    protected Class<RemoteEntity> getEntityClass() {
        return RemoteEntity.class;
    }

    @Override
    protected JpaRepository<RemoteEntity, UUID> getRepository() {
        return remoteRepository;
    }

    public Remote findRemoteDetailsById(UUID remoteId) {
        return DaoUtil.getData(remoteRepository.findRemoteEntityById(remoteId));
    }

    public Remote findRemoteDetailsByRemoteNo(Long remoteNo) {
        return DaoUtil.getData(remoteRepository.findRemoteEntityByRemoteNo(remoteNo));
    }

    public Long findMaxRemoteNo() {
        return remoteRepository.findMaxRemoteNo();
    }

    @Transactional
    public Remote saveAndFlush(Remote remote) {
        Remote result = this.save(null, remote);
        remoteRepository.flush();
        return result;
    }

    public PageData<RemoteInfo> findRemote(RemoteFilter remoteFilter, PageLink pageLink) {
        List<Long> remoteNos = null;

        if (remoteFilter.getRemoteNo() != null)
            remoteNos = TypeCastUtil.getSerialNumberData(remoteFilter.getRemoteNo());

        return DaoUtil.toPageData(remoteRepository.findRemote(
                remoteNos != null && remoteNos.size() == 1 ? remoteNos.get(0) : null,
                remoteNos != null && remoteNos.size() > 1 && !remoteNos.get(0).equals(0L) ? remoteNos : null,
                remoteNos != null && remoteNos.size() > 1 && remoteNos.get(0).equals(0L) ? remoteNos.get(1) : null,
                remoteNos != null && remoteNos.size() > 1 && remoteNos.get(0).equals(0L) ? remoteNos.get(2) : null,
                remoteFilter.getStatusId(),
                remoteFilter.getHwVersion(),
                remoteFilter.getProductionDateFrom(), remoteFilter.getProductionDateTo(),
                DaoUtil.toPageable(pageLink, RemoteInfoEntity.remoteStatusColumnMap)));
    }

    public Map<String, Map<String, Long>> findRemoteSummary() {
        Map<String, Map<String, Long>> remoteSummary = null;
        Map<String, Long> remoteSummaryData = null;

        remoteSummaryData = remoteRepository.findRemoteSummary();
        if (remoteSummaryData != null && !remoteSummaryData.isEmpty()) {
            remoteSummary = new HashMap<>();
            remoteSummary.put(RemoteInstallationStatus.STANDBY.getId(), CollectionsUtil.getCtPctData(remoteSummaryData.get("standByCt"), remoteSummaryData.get("standByPct")));
            remoteSummary.put(RemoteInstallationStatus.INSTALLED.getId(), CollectionsUtil.getCtPctData(remoteSummaryData.get("installedCt"), remoteSummaryData.get("installedPct")));
            remoteSummary.put(RemoteInstallationStatus.UNINSTALL_IN_PROGRESS.getId(), CollectionsUtil.getCtPctData(remoteSummaryData.get("uninstallInProgressCt"), remoteSummaryData.get("uninstallInProgressPct")));
        }
        return remoteSummary;
    }
}
