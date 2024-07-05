package org.thingsboard.server.dao.user;

import org.thingsboard.server.common.data.UserProfileImage;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.dao.entity.EntityDaoService;

public interface UserProfileImageService {

    UserProfileImage userImage(UserProfileImage userProfileImage);

    UserProfileImage getImage(String userId);
}

