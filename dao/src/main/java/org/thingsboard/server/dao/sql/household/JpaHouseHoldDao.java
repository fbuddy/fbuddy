package org.thingsboard.server.dao.sql.household;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.household.HouseHold;
import org.thingsboard.server.common.data.household.HouseHoldActionType;
import org.thingsboard.server.common.data.household.HouseHoldFilterRequest;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.dao.DaoUtil;
import org.thingsboard.server.dao.household.HouseHoldDao;
import org.thingsboard.server.dao.model.sql.HouseHoldEntity;
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
public class JpaHouseHoldDao extends JpaAbstractDao<HouseHoldEntity, HouseHold> implements HouseHoldDao {
    @Autowired
    private HouseHoldRepository houseHoldRepository;

    @Override
    public List<HouseHold> findAll() {
        List<HouseHoldEntity> entities = Lists.newArrayList(houseHoldRepository.findAll());
        return DaoUtil.convertDataList(entities);
    }

    @Override
    public PageData<HouseHold> findAll(PageLink pageLink) {
        return DaoUtil.toPageData(houseHoldRepository.findAll(DaoUtil.toPageable(pageLink)));
    }

    @Override
    protected Class<HouseHoldEntity> getEntityClass() {
        return HouseHoldEntity.class;
    }

    @Override
    protected JpaRepository<HouseHoldEntity, UUID> getRepository() {
        return houseHoldRepository;
    }

    @Override
    public HouseHold findByHouseHoldIdAndTvId(Long houseHoldId, Long tvId) {
        return DaoUtil.getData(houseHoldRepository.findByHouseHoldIdAndTvId(houseHoldId, tvId));
    }

    @Override
    public PageData<HouseHold> findHouseHoldByActionType(HouseHoldFilterRequest houseHoldFilterRequest, HouseHoldActionType houseHoldActionType, PageLink pageLink) {

        Map<String,String> columnMap = new HashMap<>();
        columnMap.put("validFrom", "hl.validFrom");
        columnMap.put("validFrom", "hl.validFrom");
        columnMap.put("validTill", "hl.validTill");
        columnMap.put("updatedTime", "hl.createdTime");
        columnMap.put("updatedBy", "hl.userName");
        columnMap.put("fieldStatusName", "fieldStatus");
        columnMap.put("profileName", "d.name");
        if(HouseHoldActionType.STATUS_CHANGE.equals(houseHoldActionType)) {
            return DaoUtil.toPageData(houseHoldRepository.findWithFieldStatusInfo(
                    houseHoldFilterRequest.getNumberList(houseHoldFilterRequest.getSerialNumberHouseHoldRange()),
                    houseHoldFilterRequest.getFieldStatus(),
                    houseHoldFilterRequest.getUpdatedStartDate(),
                    houseHoldFilterRequest.getUpdatedEndDate(),
                    houseHoldFilterRequest.getAudiSessionStartDate(),
                    houseHoldFilterRequest.getAudiSessionEndDate(),
                    DaoUtil.toPageable(pageLink, columnMap)));
        }
        if(HouseHoldActionType.PROFILE_CHANGE.equals(houseHoldActionType)) {
            if(houseHoldFilterRequest.getProfileId() == null) {
                return DaoUtil.toPageData(houseHoldRepository.findWithProfileInfo(
                        houseHoldFilterRequest.getNumberList(houseHoldFilterRequest.getSerialNumberHouseHoldRange()),
                        houseHoldFilterRequest.getUpdatedStartDate(),
                        houseHoldFilterRequest.getUpdatedEndDate(),
                        houseHoldFilterRequest.getAudiSessionStartDate(),
                        houseHoldFilterRequest.getAudiSessionEndDate(),
                        DaoUtil.toPageable(pageLink, columnMap)));
            }
            else{
                return DaoUtil.toPageData(houseHoldRepository.findWithProfileInfo(
                        houseHoldFilterRequest.getNumberList(houseHoldFilterRequest.getSerialNumberHouseHoldRange()),
                        houseHoldFilterRequest.getProfileId(),
                        houseHoldFilterRequest.getUpdatedStartDate(),
                        houseHoldFilterRequest.getUpdatedEndDate(),
                        houseHoldFilterRequest.getAudiSessionStartDate(),
                        houseHoldFilterRequest.getAudiSessionEndDate(),
                        DaoUtil.toPageable(pageLink, columnMap)));
            }
        }
        else{
            return DaoUtil.toPageData(null);
        }
    }
}
