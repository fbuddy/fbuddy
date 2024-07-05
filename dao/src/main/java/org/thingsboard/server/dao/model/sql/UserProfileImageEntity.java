package org.thingsboard.server.dao.model.sql;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TypeDef;
import org.thingsboard.server.common.data.UserProfileImage;
import org.thingsboard.server.common.data.id.UserId;

import org.thingsboard.server.dao.model.BaseSqlEntity;
import org.thingsboard.server.dao.model.ModelConstants;
import org.thingsboard.server.dao.model.ToData;
import org.thingsboard.server.dao.util.mapping.JsonStringType;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity

@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ModelConstants.USER_PROFILE_IMAGE_UPLOAD_TABLE_NAME)
public class UserProfileImageEntity  implements ToData<UserProfileImage> {

    @Id
    @Column(name = ModelConstants.USER_ID_PROPERTY)
    private UUID userId;


    @Column(name = ModelConstants.USER_PROFILE_IMAGE_PROPERTY)
    private String image;

    @Column(name = ModelConstants.CREATED_TIME_PROPERTY)
    private Long created_time;

    public UserProfileImageEntity() {
        super();
    }

    public UserProfileImageEntity(UserProfileImage userProfileImage) {
        if (userProfileImage.getUserId() != null) {
            this.userId = userProfileImage.getUserId().getId();
        }
        this.image = userProfileImage.getImage();
        this.created_time= userProfileImage.getCreated_time();
    }

    @Override
    public UserProfileImage toData() {
        UserProfileImage userProfileImage = new UserProfileImage();
        userProfileImage.setUserId(new UserId(userId));
        userProfileImage.setImage(image);
        userProfileImage.setCreated_time(created_time);
        return userProfileImage;
    }

}

