package org.thingsboard.server.dao.sql.util;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.dao.model.sql.StatusEntity;

import java.util.Optional;
import java.util.UUID;

public interface StatusRepository extends JpaRepository<StatusEntity, UUID> {

    Optional<StatusEntity> findByTypeAndName(String type, String name);

}

