package org.thingsboard.server.service.component;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Date;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.household.HouseHold;
import org.thingsboard.server.common.data.household.HouseHoldActionType;
import org.thingsboard.server.common.data.household.HouseHoldLog;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.role.Role;

@Component
public class CsvFileGenerator {
    public void writeUsersToCsv(List<User> users, Writer writer) {
        try {
            CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT);
            printer.printRecord("User Id", "User Name", "User Type", "Roles", "User Profile Type", "User Profile Status", "Email", "Phone");
            for (User user : users) {
                printer.printRecord(user.getSerialNumber(), user.getUserName(), user.getUserType(), user.getRoleNames(), user.getUserProfileType(), user.getUserProfileStatus(), user.getEmail(),
                        user.getPhone());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeRolesToCsv(List<Role> roles, PrintWriter writer) {
        try {
            CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT);
            printer.printRecord("Role Id", "Role Name", "Description", "Created Time", "Updated By", "Updated Time", "Status");
            for (Role role : roles) {
                printer.printRecord(role.getRoleId(), role.getName(), role.getDescription(), new Date(role.getCreatedTime()), role.getUpdatedByUserName(), (role.getUpdatedTime()>0?new Date(role.getUpdatedTime()):""), (role.isActive()?"Active":"In-active"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writeHouseHoldInfoToCsv(HouseHoldActionType houseHoldActionType, List<HouseHold> houseHolds, PrintWriter writer) {
        try {
            CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT);
            if(HouseHoldActionType.STATUS_CHANGE.equals(houseHoldActionType)) {
                printer.printRecord("Household Id", "Field Status", "Valid From", "Valid To", "Updated At", "Updated By");
                for (HouseHold houseHold : houseHolds) {
                    printer.printRecord(houseHold.getHouseHoldId(), houseHold.getFieldStatusName(), new Date(houseHold.getValidFrom()), (houseHold.getValidTill() != null ? new Date(houseHold.getValidTill()) : "Present"), new Date(houseHold.getUpdatedTime()), houseHold.getUpdatedBy());
                }
            }
            else if(HouseHoldActionType.PROFILE_CHANGE.equals(houseHoldActionType)) {
                printer.printRecord("Household Id", "Profile Status", "Valid From", "Valid To", "Updated At", "Updated By");
                for (HouseHold houseHold : houseHolds) {
                    printer.printRecord(houseHold.getHouseHoldId(), houseHold.getProfileName(), new Date(houseHold.getValidFrom()), (houseHold.getValidTill() != null ? new Date(houseHold.getValidTill()) : "Present"), new Date(houseHold.getUpdatedTime()), houseHold.getUpdatedBy());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writeHouseHoldLogToCsv(HouseHoldActionType houseHoldActionType, List<HouseHoldLog> houseHoldLogs, PrintWriter writer) {
        try {
            CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT);
            if(HouseHoldActionType.MEMBER_INFO_CHANGE.equals(houseHoldActionType)) {
                printer.printRecord("Household Id", "Active TV ID", "Active Member ID", "Valid From", "Valid To");
                for (HouseHoldLog houseHoldLog : houseHoldLogs) {
                    printer.printRecord(houseHoldLog.getHouseHoldId(), houseHoldLog.getTvId(), houseHoldLog.getHouseHold().getMemberInfoString(), new Date(houseHoldLog.getValidFrom()), (houseHoldLog.getValidTill() != null ? new Date(houseHoldLog.getValidTill()) : "Present"));
                }
            }
            else if(HouseHoldActionType.STATUS_CHANGE.equals(houseHoldActionType)) {
                printer.printRecord("Household Id", "Old field Status", "New field Status", "Valid From", "Valid To", "Updated By");
                for (HouseHoldLog houseHoldLog : houseHoldLogs) {
                    printer.printRecord(houseHoldLog.getHouseHoldId(), houseHoldLog.getFieldStatusName(), houseHoldLog.getNewFieldStatus(), new Date(houseHoldLog.getValidFrom()), (houseHoldLog.getValidTill() != null ? new Date(houseHoldLog.getValidTill()) : "Present"), houseHoldLog.getUserName());
                }
            }
            else if(HouseHoldActionType.PROFILE_CHANGE.equals(houseHoldActionType)) {
                printer.printRecord("Household Id", "Old profile Status", "New profile Status", "Valid From", "Valid To", "Updated By");
                for (HouseHoldLog houseHoldLog : houseHoldLogs) {
                    printer.printRecord(houseHoldLog.getHouseHoldId(), houseHoldLog.getProfileName(), houseHoldLog.getNewProfileName(), new Date(houseHoldLog.getValidFrom()), (houseHoldLog.getValidTill() != null ? new Date(houseHoldLog.getValidTill()) : "Present"), houseHoldLog.getUserName());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
