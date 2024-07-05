package org.thingsboard.server.dao.sql.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.thingsboard.server.dao.model.sql.UserProfileImageEntity;

import java.util.UUID;

public interface UserProfileImageRepository extends JpaRepository<UserProfileImageEntity, UUID> {

}
