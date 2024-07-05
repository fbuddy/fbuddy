package org.thingsboard.server.dao.sql.user;

import com.google.common.util.concurrent.ListenableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.UserProfileImage;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.model.sql.UserProfileImageEntity;
import org.thingsboard.server.dao.user.UserProfileImageDao;
import org.thingsboard.server.dao.sql.JpaAbstractDaoListeningExecutorService;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
@Slf4j
@Component
@SqlDao
public class JpaUserProfileImageDao extends JpaAbstractDaoListeningExecutorService implements UserProfileImageDao {

    @Autowired
    private UserProfileImageRepository userProfileImageRepository;


    @Override
    public UserProfileImage saveUserImage(UserProfileImage userProfileImage) {
        return DaoUtil.getData(userProfileImageRepository.save(new UserProfileImageEntity(userProfileImage)));
    }

    @Override
    public UserProfileImage getImageById(String userId) {
        return DaoUtil.getData(userProfileImageRepository.findById(UUID.fromString(userId)));

    }
}
