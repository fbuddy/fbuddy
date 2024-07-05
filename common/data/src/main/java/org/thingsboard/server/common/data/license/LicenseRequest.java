package org.thingsboard.server.common.data.license;

import lombok.Data;

@Data
public class LicenseRequest {
    private String type;
    private String pool;
    private Parameters parameters;
}
