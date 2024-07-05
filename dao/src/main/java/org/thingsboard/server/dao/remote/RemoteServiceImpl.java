package org.thingsboard.server.dao.remote;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.Remote;
import org.thingsboard.server.dao.sql.remote.JpaRemoteDao;

import java.util.UUID;

@Service(value = "RemoteDaoService")
@Slf4j
public class RemoteServiceImpl {
    @Autowired
    private JpaRemoteDao jpaRemoteDao;

    public Remote saveRemoteDetails(Remote remote) {
        return jpaRemoteDao.saveAndFlush(remote);
    }

    public Remote findRemoteByRemoteId(UUID remoteId) {
        return jpaRemoteDao.findRemoteDetailsById(remoteId);
    }
}
