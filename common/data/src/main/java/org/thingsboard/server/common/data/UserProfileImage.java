package org.thingsboard.server.common.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.common.data.validation.Length;

import java.io.Serializable;


@ApiModel
@Data
public class UserProfileImage implements Serializable {

    private static final long serialVersionUID = 3021989561267192281L;
    private static final ObjectMapper mapper = new ObjectMapper();

    @ApiModelProperty(position = 1, value = "JSON object with the User Profile ImageUpload Id. " +
            "Specify this field to upload the image.")
    private UserId userId;

    @ApiModelProperty(position = 2, value = "Base64 encoded image data")
    @Length(fieldName = "image")
    private String image;

    @ApiModelProperty(position = 2, value = " created time data")
    @Length(fieldName = "created_time")
    private Long created_time;

    public Long getCreated_time() {
        return created_time;
    }

    public void setCreated_time(Long created_time) {
        this.created_time = created_time;
    }

    public String getImage() {
        return image;
    }
    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public void setImage(String image) {
        this.image = image;
    }
}