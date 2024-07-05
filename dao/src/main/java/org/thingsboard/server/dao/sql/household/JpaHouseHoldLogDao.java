package org.thingsboard.server.dao.sql.household;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.household.*;
import org.thingsboard.server.common.data.id.HouseHoldEntityId;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.household.HouseHoldLogDao;
import org.thingsboard.server.dao.model.sql.HouseHoldLogEntity;
import org.thingsboard.server.dao.sql.JpaAbstractDao;
import org.thingsboard.server.dao.util.SqlDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Prashant on 5/16/2024.
 */
@Component
@SqlDao
public class JpaHouseHoldLogDao extends JpaAbstractDao<HouseHoldLogEntity, HouseHoldLog> implements HouseHoldLogDao {
    @Autowired
    private HouseHoldLogRepository houseHoldLogRepository;

    @Override
    public List<HouseHoldLog> findAll() {
        List<HouseHoldLogEntity> entities = Lists.newArrayList(houseHoldLogRepository.findAll());
        return DaoUtil.convertDataList(entities);
    }

    @Override
    public PageData<HouseHoldLog> findAll(PageLink pageLink) {
        return DaoUtil.toPageData(houseHoldLogRepository.findAll(DaoUtil.toPageable(pageLink)));
    }

    @Override
    public HouseHoldLog findLatestLog(HouseHoldEntityId houseHoldEntityId, HouseHoldActionType actionType) {
        return DaoUtil.getData(houseHoldLogRepository.findFirstByHouseHoldEntityIdAndActionTypeOrderByCreatedTimeDesc(houseHoldEntityId.getId(),actionType));
    }
    @Override
    public void deleteFutureLog(HouseHoldEntityId houseHoldEntityId, HouseHoldActionType actionType) {
        houseHoldLogRepository.deleteAllByHouseholdIdAndActionTypeAndDate(actionType, houseHoldEntityId.getId(), System.currentTimeMillis());
    }
    @Override
    public HouseHoldLog findCurrentApplicableLog(HouseHoldEntityId houseHoldEntityId, HouseHoldActionType actionType) {
        return houseHoldLogRepository.findByHouseholdIdAndActionTypeAndDate(actionType, houseHoldEntityId.getId(), System.currentTimeMillis());
    }

    @Override
    public PageData<HouseHoldLog> findHouseHoldLogsByActionType(HouseHoldFilterRequest houseHoldFilterRequest, HouseHoldActionType houseHoldActionType, PageLink pageLink) {
        JsonNode actionData = null;
        if(houseHoldFilterRequest.getFieldStatus()!=null)
            actionData = JacksonUtil.toJsonNode(JacksonUtil.valueToTree(houseHoldFilterRequest.getFieldStatus().getId()).toString());
        else if(houseHoldFilterRequest.getProfileId()!=null)
            actionData = JacksonUtil.toJsonNode(JacksonUtil.valueToTree(houseHoldFilterRequest.getProfileId()).toString());

        Map<String,String> columnMap = new HashMap<>();

        columnMap.put("householdId", "h.houseHoldId");
        columnMap.put("houseHoldId", "h.houseHoldId");
        columnMap.put("tvId", "h.tvId");
        columnMap.put("fieldStatus", "actionData");
        columnMap.put("profileName", "actionData");
        columnMap.put("newProfileName", "hlnew.actionData");
        columnMap.put("newFieldStatus", "hlnew.actionData");

        if(HouseHoldActionType.MEMBER_INFO_CHANGE.equals(houseHoldActionType))
        {
            return DaoUtil.toPageData(houseHoldLogRepository.findMemberInfoHistory(
                    houseHoldActionType,
                    houseHoldFilterRequest.getShowLatestData(),
                    houseHoldFilterRequest.getNumberList(houseHoldFilterRequest.getSerialNumberHouseHoldRange()),
                    houseHoldFilterRequest.getUpdatedStartDate(),
                    houseHoldFilterRequest.getUpdatedEndDate(),
                    houseHoldFilterRequest.getAudiSessionStartDate(),
                    houseHoldFilterRequest.getAudiSessionEndDate(),
                    DaoUtil.toPageable(pageLink, columnMap)));
        }
        if(actionData != null){
            return DaoUtil.toPageData(houseHoldLogRepository.findByActionTypeAndActionData(
                    houseHoldActionType,
                    actionData,
                    houseHoldFilterRequest.getShowLatestData(),
                    houseHoldFilterRequest.getNumberList(houseHoldFilterRequest.getSerialNumberHouseHoldRange()),
                    houseHoldFilterRequest.getUpdatedStartDate(),
                    houseHoldFilterRequest.getUpdatedEndDate(),
                    houseHoldFilterRequest.getAudiSessionStartDate(),
                    houseHoldFilterRequest.getAudiSessionEndDate(),
                    DaoUtil.toPageable(pageLink, columnMap)));
        }
        return DaoUtil.toPageData(houseHoldLogRepository.findByActionType(
                houseHoldActionType,
                houseHoldFilterRequest.getShowLatestData(),
                houseHoldFilterRequest.getNumberList(houseHoldFilterRequest.getSerialNumberHouseHoldRange()),
                houseHoldFilterRequest.getUpdatedStartDate(),
                houseHoldFilterRequest.getUpdatedEndDate(),
                houseHoldFilterRequest.getAudiSessionStartDate(),
                houseHoldFilterRequest.getAudiSessionEndDate(),
                DaoUtil.toPageable(pageLink, columnMap)));
    }

    @Override
    protected Class<HouseHoldLogEntity> getEntityClass() {
        return HouseHoldLogEntity.class;
    }

    @Override
    protected JpaRepository<HouseHoldLogEntity, UUID> getRepository() {
        return houseHoldLogRepository;
    }
}
