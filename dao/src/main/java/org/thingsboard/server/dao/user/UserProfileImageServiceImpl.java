package org.thingsboard.server.dao.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.server.common.data.UserProfileImage;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.dao.entity.AbstractEntityService;

import java.util.UUID;

import static org.thingsboard.server.dao.service.Validator.validateId;

@Service("UserProfileImageDaoService")
@Slf4j
@RequiredArgsConstructor
public class UserProfileImageServiceImpl extends AbstractEntityService implements UserProfileImageService {

    public static final String INCORRECT_USER_ID = "Incorrect userId ";
    private final UserProfileImageDao userProfileImageDao;

    @Override
    public UserProfileImage userImage(UserProfileImage userProfileImage) {
        log.trace("Executing Image upload for user profile [{}], [{}]", userProfileImage.getUserId(), userProfileImage);
        validateId(userProfileImage.getUserId(), INCORRECT_USER_ID + userProfileImage.getUserId());
        return userProfileImageDao.saveUserImage(userProfileImage);
    }


    @Override
    public UserProfileImage getImage( String userId) {
        log.trace("Executing getImage for user profile [{}], [{}]",  userId);
        validateId(UUID.fromString(userId), INCORRECT_USER_ID + userId);
        return userProfileImageDao.getImageById(userId);
    }
}

