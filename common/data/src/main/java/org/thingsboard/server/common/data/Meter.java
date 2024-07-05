package org.thingsboard.server.common.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.thingsboard.server.common.data.validation.DateFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@JsonIgnoreProperties({"licenseKey", "licenseNo"})
public class Meter implements Serializable {
    private Long meterNo;
    @NotNull(message = "os details missing!")
    private OsType osType;
    @NotNull(message = "swVersion cannot be null !")
    private String swVersion;
    @NotNull(message = "production cannot be null !")
    @DateFormat(pattern = "dd-MM-yyyy", message = "Production date must be in the format dd-MM-yyyy")
    private String productionDate;
    @NotNull(message = "otaCommitHash cannot be null !")
    private String otaCommitHash;
    @NotNull(message = "imeiBaseboard cannot be null !")
    private String imeiBaseboard;
    @NotNull(message = "cpuSerial cannot be null !")
    private String cpuSerial;
    @NotNull(message = "powerPcbSerial cannot be null !")
    private String powerPcbSerial;
    @NotNull(message = "ethernetMac cannot be null !")
    private String ethernetMac;
    @NotNull(message = "bleAddress cannot be null !")
    private String bleAddress;
    @NotNull(message = "wifiMac cannot be null !")
    private String wifiMac;
    @NotNull(message = "sdCardPartNo cannot be null !")
    private String sdCardPartNo;
    @NotNull(message = "sdCardSerial cannot be null !")
    private String sdCardSerial;
    private String licenseKey;
    private String licenseNo;
    @Size(min = 1, message = "There must be at least one SIM detail")
    private List<SimDetails> simDetails;
    @NotNull(message = "testReport cannot be null !")
    private TestReport testReport;
    @DateFormat(pattern = "dd-MM-yyyy", message = "validFrom must be in the format dd-MM-yyyy")
    private String validFrom;
    @DateFormat(pattern = "dd-MM-yyyy", message = "validTo must be in the format dd-MM-yyyy")
    private String validTo;
    private String authcode;
    private String deviceId;
    private Long createdTime;
    private UUID createdBy;
    private UUID updatedBy;
    private UUID statusId;
    private UUID osTypeId;
    private Long updatedTime;
    private String remarks;
}
