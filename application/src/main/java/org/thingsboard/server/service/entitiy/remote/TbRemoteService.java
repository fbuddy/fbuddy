package org.thingsboard.server.service.entitiy.remote;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.thingsboard.server.common.data.Remote;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.remote.RemoteFilter;
import org.thingsboard.server.common.data.remote.RemoteInfo;

import java.util.Map;

public interface TbRemoteService {
    ResponseEntity<?> processBulkUpload(MultipartFile file, User currentUser);

    Remote saveRemote(Remote remote, User user) throws ThingsboardException;

    PageData<RemoteInfo> getRemote(RemoteFilter remoteFilter, PageLink pageLink);

    Map<String, Map<String, Long>> getRemoteSummary();
}
