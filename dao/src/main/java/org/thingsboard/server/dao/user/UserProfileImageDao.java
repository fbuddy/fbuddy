package org.thingsboard.server.dao.user;

import org.thingsboard.server.common.data.Tenant;
import org.thingsboard.server.common.data.UserProfileImage;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.dao.Dao;

public interface UserProfileImageDao {

        UserProfileImage saveUserImage(UserProfileImage userProfileImage);
        UserProfileImage getImageById(String userId);


}

