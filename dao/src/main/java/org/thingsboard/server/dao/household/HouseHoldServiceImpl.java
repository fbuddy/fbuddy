/**
 * Copyright © 2016-2024 The BARC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.dao.household;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.exception.ThingsboardErrorCode;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.household.*;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.device.DeviceProfileService;
import org.thingsboard.server.dao.entity.AbstractEntityService;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;


@Service("HouseHoldDaoService")
@Slf4j
public class HouseHoldServiceImpl extends AbstractEntityService implements HouseHoldService {
    @Autowired
    protected DeviceProfileService deviceProfileService;

    @Autowired
    private HouseHoldDao houseHoldDao;

    @Autowired
    private HouseHoldLogDao houseHoldLogDao;

    @Override
    public List<HouseHold> findAll() {
        return houseHoldDao.findAll()
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public PageData<HouseHold> findAll(PageLink pageLink) {
        return houseHoldDao.findAll(pageLink);
    }

    @Override
    public HouseHold findByHouseHoldIdAndTvId(Long houseHoldId, Long tvId) {
        return houseHoldDao.findByHouseHoldIdAndTvId(houseHoldId, tvId);
    }

    @Override
    @Transactional
    public HouseHold save(HouseHold houseHold, User user) throws ThingsboardException {
        if(houseHold.getProfileId() == null || EntityId.NULL_UUID.equals(houseHold.getProfileId())) {
            houseHold.setProfileId(deviceProfileService.findDefaultDeviceProfile(TenantId.SYS_TENANT_ID).getUuidId());
        }
        HouseHold updatedHouseHold = houseHoldDao.save(TenantId.SYS_TENANT_ID, houseHold);


        this.saveChangeLog(new HouseHoldLog(), updatedHouseHold, HouseHoldActionType.MEMBER_INFO_CHANGE, user);
        if(houseHold.getId() == null) {
            HouseHoldLog hl = this.saveChangeLog(new HouseHoldLog(), updatedHouseHold, HouseHoldActionType.STATUS_CHANGE, user);
            updatedHouseHold.setActiveFieldStatusLogId(hl.getId());
            hl = this.saveChangeLog(new HouseHoldLog(), updatedHouseHold, HouseHoldActionType.PROFILE_CHANGE, user);
            updatedHouseHold.setActiveProfileLogId(hl.getId());
            updatedHouseHold = houseHoldDao.save(TenantId.SYS_TENANT_ID, updatedHouseHold);
        }
        return updatedHouseHold;
    }

    @Override
    public HouseHoldLog saveChangeLog(HouseHoldLog houseHoldUpdateRequest, HouseHold updatedHouseHold, HouseHoldActionType houseHoldActionType, User user) throws ThingsboardException {
        HouseHoldLog houseHoldLog = new HouseHoldLog();
        houseHoldLog.setHouseHoldEntityId(updatedHouseHold.getId());
        houseHoldLog.setActionType(houseHoldActionType);
        HouseHoldLog previousHouseHoldLog = findLatestLog(houseHoldLog.getHouseHoldEntityId(), houseHoldLog.getActionType());
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        //long millis = (System.currentTimeMillis() - c.getTimeInMillis());
        long curTs = c.getTimeInMillis();
        if(previousHouseHoldLog != null) {
            previousHouseHoldLog.setValidTill(houseHoldUpdateRequest.getCurrentValidTill());

            houseHoldLog.setPreviousId(previousHouseHoldLog.getId());
            if (previousHouseHoldLog.getValidTill() == null && houseHoldUpdateRequest.getValidFrom() == null) {

                previousHouseHoldLog.setValidTill(curTs-1);
                houseHoldUpdateRequest.setValidFrom(curTs);
            }
            else if (previousHouseHoldLog.getValidTill() == null) {
                previousHouseHoldLog.setValidTill(houseHoldUpdateRequest.getValidFrom());
            }

            /*if (houseHoldUpdateRequest.getValidFrom() > previousHouseHoldLog.getValidTill()) {
                throw new ThingsboardException("Invalid dates", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
            }
            if (houseHoldUpdateRequest.getValidFrom() < previousHouseHoldLog.getValidFrom()) {
                throw new ThingsboardException("Previous log has greater date", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
            }
            if (previousHouseHoldLog.getValidFrom() > previousHouseHoldLog.getValidTill()) {
                throw new ThingsboardException("Invalid date range", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
            }*/
            houseHoldLogDao.save(TenantId.SYS_TENANT_ID, previousHouseHoldLog);
        }
        else{
            houseHoldLog.setPreviousId(new HouseHoldLogId(EntityId.NULL_UUID));
        }
        if(HouseHoldActionType.MEMBER_INFO_CHANGE.equals(houseHoldActionType)) {
            houseHoldLog.setActionData(JacksonUtil.toJsonNode(JacksonUtil.valueToTree(updatedHouseHold.getMemberInfo()).toString()));
        }
        else if(HouseHoldActionType.STATUS_CHANGE.equals(houseHoldActionType)) {
            houseHoldLog.setActionData(JacksonUtil.toJsonNode(JacksonUtil.valueToTree(updatedHouseHold.getFieldStatus().getId()).toString()));
        }else if(HouseHoldActionType.PROFILE_CHANGE.equals(houseHoldActionType)) {
            houseHoldLog.setActionData(JacksonUtil.toJsonNode(JacksonUtil.valueToTree(updatedHouseHold.getProfileId()).toString()));
        }
        if(houseHoldUpdateRequest.getValidFrom() == null) {
            houseHoldLog.setValidFrom(curTs);
        }
        else{
            houseHoldLog.setValidFrom(houseHoldUpdateRequest.getValidFrom());
        }
        if(houseHoldUpdateRequest.getValidTill() == null) {

        }
        else{
            houseHoldLog.setValidTill(houseHoldUpdateRequest.getValidTill());
        }
        if(user == null)
        {
            houseHoldLog.setUserName("Automated");
        }
        else {
            houseHoldLog.setUpdatedBy(user.getId());
            houseHoldLog.setUserName(user.getUserName());
        }

        return houseHoldLogDao.save(TenantId.SYS_TENANT_ID, houseHoldLog);
    }
    @Override
    public PageData<HouseHold> getHouseHoldByActionType(HouseHoldFilterRequest houseHoldFilterRequest, HouseHoldActionType houseHoldActionType, PageLink pageLink) throws ThingsboardException{
        PageData<HouseHold> pageData = houseHoldDao.findHouseHoldByActionType(houseHoldFilterRequest, houseHoldActionType, pageLink);
        if(HouseHoldActionType.PROFILE_CHANGE.equals(houseHoldActionType)) {
            for (HouseHold houseHold : pageData.getData()) {
                DeviceProfileId deviceProfileId = new DeviceProfileId(houseHold.getProfileId());
                houseHold.setDeviceProfile(deviceProfileService.findDeviceProfileById(TenantId.SYS_TENANT_ID, deviceProfileId));
            }
        }

        for(HouseHold h: pageData.getData())
        {
            if(h.getValidTill() != null && h.getValidTill() < System.currentTimeMillis())
            {
                //TODO: This work should be done via crone
                updateHouseHoldStatusByCronJob(houseHoldActionType);
                return getHouseHoldByActionType(houseHoldFilterRequest, houseHoldActionType, pageLink);

            }
        }
        return pageData;
    }
    @Transactional
    public void updateHouseHoldStatusByCronJob(HouseHoldActionType houseHoldActionType) throws ThingsboardException {
        List<HouseHold> houseHoldList = houseHoldDao.findHouseHoldByActionType(new HouseHoldFilterRequest(),houseHoldActionType, new PageLink(999999999)).getData();
        for(HouseHold houseHold:houseHoldList) {
            if(houseHold.getValidTill() != null && houseHold.getValidTill() < System.currentTimeMillis())
            {
                HouseHoldLog log = findCurrentApplicableLog(houseHold.getId(), houseHoldActionType);
                if(HouseHoldActionType.STATUS_CHANGE.equals(houseHoldActionType)){
                    if(log == null) {
                        houseHold.setFieldStatus(HouseHoldStatus.ACCEPTED);
                        log = this.saveChangeLog(new HouseHoldLog(), houseHold, HouseHoldActionType.STATUS_CHANGE, null);
                    }
                    houseHold.setFieldStatus(JacksonUtil.convertValue(log.getActionData(), HouseHoldStatus.class));
                    houseHold.setActiveFieldStatusLogId(log.getId());
                }
                if(HouseHoldActionType.PROFILE_CHANGE.equals(houseHoldActionType)){
                    if(log == null) {
                        houseHold.setProfileId(deviceProfileService.findDefaultDeviceProfile(TenantId.SYS_TENANT_ID).getUuidId());
                        log = this.saveChangeLog(new HouseHoldLog(), houseHold, HouseHoldActionType.PROFILE_CHANGE, null);
                    }
                    houseHold.setProfileId(JacksonUtil.convertValue(log.getActionData(), UUID.class));
                    houseHold.setActiveProfileLogId(log.getId());
                }

                houseHoldDao.save(TenantId.SYS_TENANT_ID, houseHold);
            }
            else if(houseHold.getValidTill() == null)
            {
                HouseHoldLog log = findCurrentApplicableLog(houseHold.getId(), houseHoldActionType);
                if(log != null) {
                    houseHold.getHouseHoldLog().setValidTill(log.getValidFrom());
                    houseHoldLogDao.save(TenantId.SYS_TENANT_ID,houseHold.getHouseHoldLog());
                    if(HouseHoldActionType.STATUS_CHANGE.equals(houseHoldActionType))
                        houseHold.setActiveFieldStatusLogId(log.getId());
                    else if(HouseHoldActionType.PROFILE_CHANGE.equals(houseHoldActionType))
                        houseHold.setActiveProfileLogId(log.getId());
                }
            }

        }
    }

    @Override
    public PageData<HouseHoldLog> getHouseHoldLogsByActionType(HouseHoldFilterRequest houseHoldFilterRequest, HouseHoldActionType houseHoldActionType, PageLink pageLink) {
        PageData<HouseHoldLog> pageData =  houseHoldLogDao.findHouseHoldLogsByActionType(houseHoldFilterRequest, houseHoldActionType, pageLink);
        if(HouseHoldActionType.PROFILE_CHANGE.equals(houseHoldActionType)) {
            for (HouseHoldLog log : pageData.getData()) {
                DeviceProfileId deviceProfileId = new DeviceProfileId(JacksonUtil.convertValue(log.getActionData(), UUID.class));
                log.setDeviceProfile(deviceProfileService.findDeviceProfileById(TenantId.SYS_TENANT_ID, deviceProfileId));

                if(log.getActionDataNew() != null) {
                    DeviceProfileId deviceProfileIdNew = new DeviceProfileId(JacksonUtil.convertValue(log.getActionDataNew(), UUID.class));
                    log.setNewDeviceProfile(deviceProfileService.findDeviceProfileById(TenantId.SYS_TENANT_ID, deviceProfileIdNew));
                }
                DeviceProfileId deviceProfileIdCurrent = new DeviceProfileId(log.getHouseHold().getProfileId());
                log.setCurrentDeviceProfile(deviceProfileService.findDeviceProfileById(TenantId.SYS_TENANT_ID, deviceProfileIdCurrent));
            }
        }

        if(HouseHoldActionType.STATUS_CHANGE.equals(houseHoldActionType)) {
            for (HouseHoldLog log : pageData.getData()) {
                log.setFieldStatus(JacksonUtil.convertValue(log.getActionData(), HouseHoldStatus.class));
                if(log.getActionDataNew() != null) {
                    log.setNewFieldStatus(JacksonUtil.convertValue(log.getActionDataNew(), HouseHoldStatus.class));
                }
            }
        }
        return pageData;
    }

    @Transactional
    @Override
    public HouseHold updateFieldStatus(HouseHoldLog houseHoldUpdateRequest, User user) throws ThingsboardException {
        validateDates(houseHoldUpdateRequest);
        HouseHold houseHold = houseHoldDao.findById(TenantId.SYS_TENANT_ID, houseHoldUpdateRequest.getHouseHoldEntityId().getId());
        houseHold.setFieldStatus(JacksonUtil.convertValue(houseHoldUpdateRequest.getActionData(), HouseHoldStatus.class));
        houseHoldLogDao.deleteFutureLog(houseHoldUpdateRequest.getHouseHoldEntityId(), HouseHoldActionType.STATUS_CHANGE);

        HouseHoldLog hl = saveChangeLog(houseHoldUpdateRequest, houseHold, HouseHoldActionType.STATUS_CHANGE, user);
        long curTs = System.currentTimeMillis();
        if(hl.getValidFrom() >= curTs && (hl.getValidTill() == null || hl.getValidTill() <= curTs))
        {
            houseHold.setActiveFieldStatusLogId(hl.getId());
            houseHold = houseHoldDao.save(TenantId.SYS_TENANT_ID, houseHold);
        }
        return houseHold;
    }

    private void validateDates(HouseHoldLog request) throws ThingsboardException {
        if(request.getValidFrom() == null)
        {
            throw new ThingsboardException("From Date cannot be blank ", ThingsboardErrorCode.INVALID_ARGUMENTS);
        }
        if(request.getValidTill() != null && request.getValidTill() <= request.getValidFrom())
        {
            throw new ThingsboardException("From Date cannot be greater than To date ", ThingsboardErrorCode.INVALID_ARGUMENTS);
        }
    }

    @Transactional
    @Override
    public HouseHold updateProfile(HouseHoldLog houseHoldUpdateRequest, User user) throws ThingsboardException {
        validateDates(houseHoldUpdateRequest);
        HouseHold houseHold = houseHoldDao.findById(TenantId.SYS_TENANT_ID, houseHoldUpdateRequest.getHouseHoldEntityId().getId());
        houseHold.setProfileId(JacksonUtil.convertValue(houseHoldUpdateRequest.getActionData(), UUID.class));
        houseHoldLogDao.deleteFutureLog(houseHoldUpdateRequest.getHouseHoldEntityId(), HouseHoldActionType.PROFILE_CHANGE);
        HouseHoldLog hl = saveChangeLog(houseHoldUpdateRequest, houseHold, HouseHoldActionType.PROFILE_CHANGE, user);
        long curTs = System.currentTimeMillis();
        if(hl.getValidFrom() >= curTs && (hl.getValidTill() == null || hl.getValidTill() <= curTs))
        {
            houseHold.setActiveProfileLogId(hl.getId());
            houseHold = houseHoldDao.save(TenantId.SYS_TENANT_ID, houseHold);
        }
        return houseHold;
    }

    private HouseHoldLog findLatestLog(HouseHoldEntityId houseHoldEntityId, HouseHoldActionType actionType){
        return houseHoldLogDao.findLatestLog(houseHoldEntityId, actionType);
    }
    private HouseHoldLog findCurrentApplicableLog(HouseHoldEntityId houseHoldEntityId, HouseHoldActionType actionType){
        return houseHoldLogDao.findCurrentApplicableLog(houseHoldEntityId, actionType);
    }

}
