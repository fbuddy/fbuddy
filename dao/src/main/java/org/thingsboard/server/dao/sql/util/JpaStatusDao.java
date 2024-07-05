package org.thingsboard.server.dao.sql.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.Status;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.sql.StatusEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.UUID;

@Component
@SqlDao
@Slf4j
public class JpaStatusDao extends JpaAbstractDao<StatusEntity, Status> {
    @Autowired
    private StatusRepository statusRepository;

    @Override
    protected Class<StatusEntity> getEntityClass() {
        return StatusEntity.class;
    }

    @Override
    protected JpaRepository<StatusEntity, UUID> getRepository() {
        return statusRepository;
    }

    public StatusEntity findByTypeAndName(String type, String name) {
        if (type == null || name == null)
            return null;
        return statusRepository.findByTypeAndName(type, name).orElse(null);
    }

    @Transactional
    public Status saveAndFlush(Status status) {
        Status result = this.save(null, status);
        statusRepository.flush();
        return result;
    }


    public PageData<Status> findAll(PageLink pageLink) {
        return DaoUtil.toPageData(statusRepository.findAll(DaoUtil.toPageable(pageLink)));
    }
}
