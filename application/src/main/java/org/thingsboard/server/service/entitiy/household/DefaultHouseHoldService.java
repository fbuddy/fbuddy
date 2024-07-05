package org.thingsboard.server.service.entitiy.household;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.EntityType;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.household.*;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.EntityId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.device.DeviceProfileService;
import org.thingsboard.server.dao.household.HouseHoldService;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.entitiy.AbstractTbEntityService;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.util.UUID;

@AllArgsConstructor
@TbCoreComponent
@Service
@Slf4j
public class DefaultHouseHoldService extends AbstractTbEntityService {
    private final HouseHoldService houseHoldService;


    public HouseHold save(HouseHold houseHold, User user, boolean overwriteExisting) throws ThingsboardException {
        HouseHold existingHouseHold = houseHoldService.findByHouseHoldIdAndTvId(houseHold.getHouseHoldId(), houseHold.getTvId());
        if(existingHouseHold != null) {
            if(overwriteExisting) {
                houseHold.setId(existingHouseHold.getId());
                houseHold.setCreatedTime(existingHouseHold.getCreatedTime());
                houseHold.setProfileId(existingHouseHold.getProfileId());
                houseHold.setFieldStatus(existingHouseHold.getFieldStatus());
            }
            else{
                throw new ThingsboardException("Household details already exists.", ThingsboardErrorCode.GENERAL);
            }
        }
        ActionType actionType = houseHold.getId() == null ? ActionType.ADDED : ActionType.UPDATED;
        try {

            HouseHold houseHoldUpdated = checkNotNull(houseHoldService.save(houseHold, user));
            notificationEntityService.logEntityAction(TenantId.SYS_TENANT_ID, houseHoldUpdated.getId(), houseHoldUpdated, actionType, user);
            return houseHoldUpdated;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(TenantId.SYS_TENANT_ID, emptyId(EntityType.HOUSE_HOLD), houseHold, actionType, user, e);
            throw e;
        }

    }
    public PageData<HouseHold> getList(HouseHoldFilterRequest houseHoldFilterRequest, PageLink pageLink, HouseHoldActionType houseHoldActionType, User currentUser) throws ThingsboardException{
        PageData<HouseHold> pageData = houseHoldService.getHouseHoldByActionType(houseHoldFilterRequest, houseHoldActionType, pageLink);
        return pageData;
    }
    public PageData<HouseHoldLog> getHistory(HouseHoldFilterRequest houseHoldFilterRequest, PageLink pageLink, HouseHoldActionType houseHoldActionType, SecurityUser currentUser) {
        PageData<HouseHoldLog> pageData = houseHoldService.getHouseHoldLogsByActionType(houseHoldFilterRequest, houseHoldActionType, pageLink);


        return pageData;
    }

    public HouseHold updateFieldStatus(HouseHoldLog houseHoldUpdateRequest, User user) throws ThingsboardException{
        ActionType actionType = ActionType.UPDATED;
        HouseHold houseHoldUpdated = checkNotNull(houseHoldService.updateFieldStatus(houseHoldUpdateRequest, user));
        try {
            notificationEntityService.logEntityAction(TenantId.SYS_TENANT_ID, houseHoldUpdated.getId(), houseHoldUpdated, actionType, user);
            return houseHoldUpdated;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(TenantId.SYS_TENANT_ID, emptyId(EntityType.HOUSE_HOLD), actionType, user, e, houseHoldUpdateRequest);
            throw e;
        }
    }

    public HouseHold updateProfile(HouseHoldLog houseHoldUpdateRequest, User user) throws ThingsboardException {
        ActionType actionType = ActionType.UPDATED;
        HouseHold houseHoldUpdated = checkNotNull(houseHoldService.updateProfile(houseHoldUpdateRequest, user));
        try {
            notificationEntityService.logEntityAction(TenantId.SYS_TENANT_ID, houseHoldUpdated.getId(), houseHoldUpdated, actionType, user);
            return houseHoldUpdated;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(TenantId.SYS_TENANT_ID, emptyId(EntityType.HOUSE_HOLD), actionType, user, e, houseHoldUpdateRequest);
            throw e;
        }
    }


}
