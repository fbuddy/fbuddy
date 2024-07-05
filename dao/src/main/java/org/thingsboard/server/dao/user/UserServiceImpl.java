/**
 * Copyright © 2016-2024 The Thingsboard Authors
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
package org.thingsboard.server.dao.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.common.util.JacksonUtil;
import org.thingsboard.server.common.data.*;
import org.thingsboard.server.common.data.audit.ActionType;
import org.thingsboard.server.common.data.id.*;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.common.data.security.UserCredentials;
import org.thingsboard.server.common.data.security.event.UserCredentialsInvalidationEvent;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.entity.EntityCountService;
import org.thingsboard.server.dao.eventsourcing.ActionEntityEvent;
import org.thingsboard.server.dao.eventsourcing.DeleteEntityEvent;
import org.thingsboard.server.dao.eventsourcing.SaveEntityEvent;
import org.thingsboard.server.dao.exception.IncorrectParameterException;
import org.thingsboard.server.dao.role.ModuleService;
import org.thingsboard.server.dao.role.RoleService;
import org.thingsboard.server.dao.service.DataValidator;
import org.thingsboard.server.dao.service.PaginatedRemover;
import org.thingsboard.server.dao.sql.role.RolePermissionRepository;


import java.util.*;

import static org.thingsboard.server.common.data.StringUtils.generateSafeToken;
import static org.thingsboard.server.dao.service.Validator.validateId;
import static org.thingsboard.server.dao.service.Validator.validatePageLink;
import static org.thingsboard.server.dao.service.Validator.validateString;

@Service("UserDaoService")
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl extends AbstractEntityService implements UserService {

    public static final String USER_PASSWORD_HISTORY = "userPasswordHistory";

    public static final String LAST_LOGIN_TS = "lastLoginTs";
    public static final String FAILED_LOGIN_ATTEMPTS = "failedLoginAttempts";

    private static final int DEFAULT_TOKEN_LENGTH = 30;
    public static final String INCORRECT_USER_ID = "Incorrect userId ";
    public static final String INCORRECT_TENANT_ID = "Incorrect tenantId ";

    private static final String USER_CREDENTIALS_ENABLED = "userCredentialsEnabled";

    @Value("${security.user_login_case_sensitive:true}")
    private boolean userLoginCaseSensitive;

    private final UserDao userDao;
    private final UserRoleDao userRoleDao;
    private final UserCredentialsDao userCredentialsDao;
    private final UserAuthSettingsDao userAuthSettingsDao;
    private final DataValidator<User> userValidator;
    private final DataValidator<UserCredentials> userCredentialsValidator;
    private final ApplicationEventPublisher eventPublisher;
    private final EntityCountService countService;
    private final RoleService roleService;
    private final ModuleService moduleService;
    private final RolePermissionRepository rolePermissionRepository;



    @Override
    public User findUserByEmail(TenantId tenantId, String email) {
        log.trace("Executing findUserByEmail [{}]", email);
        validateString(email, "Incorrect email " + email);
        if (userLoginCaseSensitive) {
            return userDao.findByEmail(tenantId, email);
        } else {
            return userDao.findByEmail(tenantId, email.toLowerCase());
        }
    }
    @Override
    public User findUserByUserName(TenantId tenantId, String userName) {
        log.trace("Executing findByUserName [{}]", userName);
        if (userLoginCaseSensitive) {
            return userDao.findByUserName(tenantId, userName);
        } else {
            return userDao.findByUserName(tenantId, userName.toLowerCase());
        }
    }
    @Override
    public User findUserBySerialNumber(TenantId tenantId, Integer serialNumber) {
        log.trace("Executing findBySerialNumber [{}]", serialNumber);
        return userDao.findBySerialNumber(tenantId, serialNumber);

    }

    @Override
    public User findByEmailOrUserNameOrPhone(TenantId sysTenantId, String username) {
        log.trace("Executing findByEmailOrUserNameOrSerialNumberOrPhone [{}]", username);
        validateString(username, "Incorrect username " + username);
        if (userLoginCaseSensitive) {
            return userDao.findByEmailOrUserNameOrPhone(username);
        } else {
            return userDao.findByEmailOrUserNameOrPhone(username.toLowerCase());
        }
    }

    @Override
    public User findUserByPhone(TenantId tenantId, String phone) {
        return userDao.findByPhone(phone);
    }


    @Override
    public User setUserRolesAndAuthorities(User user){
        if(user!=null) {
            user.setRoles(userRoleDao.findByUserId(TenantId.SYS_TENANT_ID, user.getId()));
            user.setModules(roleService.findModulesByRoleIds(user.getRoleIds()));
            //user.setNewAuthorities(roleService.findAuthoritiesByRoleIds(user.getRoleIds()));
        }
        return user;
    }

    private User setUserRoleNames(User user){
        if(user!=null) {
            user.setRoles(userRoleDao.findByUserId(TenantId.SYS_TENANT_ID, user.getId()));
            user.setRoleNames(roleService.getRoleNamesByRoleIds(user.getRoleIds()));
            //user.setNewAuthorities(roleService.findAuthoritiesByRoleIds(user.getRoleIds()));
        }
        return user;
    }





    @Override
    public User findUserByTenantIdAndEmail(TenantId tenantId, String email) {
        log.trace("Executing findUserByTenantIdAndEmail [{}][{}]", tenantId, email);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateString(email, "Incorrect email " + email);
        return userDao.findByTenantIdAndEmail(tenantId, email);
    }

    @Override
    public User findUserById(TenantId tenantId, UserId userId) {
        log.trace("Executing findUserById [{}]", userId);
        validateId(userId, INCORRECT_USER_ID + userId);
        User user = userDao.findById(tenantId, userId.getId());
        user = setUserRolesAndAuthorities(user);
        user.setRoleNames(roleService.getRoleNamesByRoleIds(user.getRoleIds()));
        return user;
    }

    @Override
    public ListenableFuture<User> findUserByIdAsync(TenantId tenantId, UserId userId) {
        log.trace("Executing findUserByIdAsync [{}]", userId);
        validateId(userId, INCORRECT_USER_ID + userId);
        return userDao.findByIdAsync(tenantId, userId.getId());
    }

    @Override
    public User saveUser(TenantId tenantId, User user) {
        log.trace("Executing saveUser [{}]", user);
        User oldUser = userValidator.validate(user, User::getTenantId);
        if(oldUser!=null)
            oldUser.setRoles(userRoleDao.findByUserId(TenantId.SYS_TENANT_ID, oldUser.getId()));
        if (!userLoginCaseSensitive) {
            user.setEmail(user.getEmail().toLowerCase());
        }
        List<RoleId> roleIds = user.getRoleIds();
        User savedUser = userDao.save(user.getTenantId(), user);
        ///// Update User Role Start //////////////////
        log.info("Role Update Start");
        if(oldUser!=null)
            this.updateUserRoles(savedUser, oldUser.getRoles(), roleIds);
        else
            this.updateUserRoles(savedUser, new ArrayList<>(), roleIds);
        log.info("Role Update end");
        ///// Update User Role End //////////////////
        if (user.getId() == null) {
            countService.publishCountEntityEvictEvent(savedUser.getTenantId(), EntityType.USER);
            UserCredentials userCredentials = new UserCredentials();


            if(UserProfileType.COMPUTER.equals(savedUser.getUserProfileType())){
                userCredentials.setEnabled(true);
            }
            else{
                userCredentials.setEnabled(false);
                userCredentials.setActivateToken(generateSafeToken(DEFAULT_TOKEN_LENGTH));
            }

            userCredentials.setUserId(new UserId(savedUser.getUuidId()));
            userCredentials.setAdditionalInfo(JacksonUtil.newObjectNode());
            userCredentialsDao.save(user.getTenantId(), userCredentials);
        }
        eventPublisher.publishEvent(SaveEntityEvent.builder()
                .tenantId(tenantId == null ? TenantId.SYS_TENANT_ID : tenantId)
                .entity(savedUser)
                .oldEntity(oldUser)
                .entityId(savedUser.getId())
                .created(user.getId() == null).build());
        return savedUser;
    }

    private User updateUserRoles(User savedUser, List<UserRole> oldUserRoles, List<RoleId> roleIds)
    {
        List<UUID> userRoleIdsToRemove = new ArrayList<>();
        for(UserRole userRole : oldUserRoles)
        {
            if(!roleIds.contains(userRole.getRoleId()))
            {
                userRoleIdsToRemove.add(userRole.getId().getId());
            }
        }
        if(!userRoleIdsToRemove.isEmpty()) {
            userRoleDao.removeAllByIds(userRoleIdsToRemove);
        }
        List<UserRole> newUserRoles = new ArrayList<>();
        for(RoleId roleId:roleIds)
        {
            boolean alreadyExists = false;
            for(UserRole userRole : oldUserRoles)
            {
                if(userRole.getRoleId().equals(roleId))
                {
                    alreadyExists = true;
                    break;
                }
            }
            if(!alreadyExists) {
                UserRole userRole = new UserRole();
                userRole.setUserId(savedUser.getId());
                userRole.setRoleId(roleId);
                userRole.setActive(true);
                newUserRoles.add(userRole);
                userRoleDao.save(savedUser.getTenantId(), userRole);
            }
        }
        savedUser.setRoles(userRoleDao.findByUserId(TenantId.SYS_TENANT_ID, savedUser.getId()));
        return savedUser;
    }

    @Override
    public UserCredentials findUserCredentialsByUserId(TenantId tenantId, UserId userId) {
        log.trace("Executing findUserCredentialsByUserId [{}]", userId);
        validateId(userId, INCORRECT_USER_ID + userId);
        return userCredentialsDao.findByUserId(tenantId, userId.getId());
    }

    @Override
    public UserCredentials findUserCredentialsByActivateToken(TenantId tenantId, String activateToken) {
        log.trace("Executing findUserCredentialsByActivateToken [{}]", activateToken);
        validateString(activateToken, "Incorrect activateToken " + activateToken);
        return userCredentialsDao.findByActivateToken(tenantId, activateToken);
    }

    @Override
    public UserCredentials findUserCredentialsByResetToken(TenantId tenantId, String resetToken) {
        log.trace("Executing findUserCredentialsByResetToken [{}]", resetToken);
        validateString(resetToken, "Incorrect resetToken " + resetToken);
        return userCredentialsDao.findByResetToken(tenantId, resetToken);
    }

    @Override
    public UserCredentials saveUserCredentials(TenantId tenantId, UserCredentials userCredentials) {
        log.trace("Executing saveUserCredentials [{}]", userCredentials);
        userCredentialsValidator.validate(userCredentials, data -> tenantId);
        UserCredentials result = userCredentialsDao.save(tenantId, userCredentials);
        eventPublisher.publishEvent(ActionEntityEvent.builder()
                .tenantId(tenantId)
                .entityId(userCredentials.getUserId())
                .actionType(ActionType.CREDENTIALS_UPDATED).build());
        return result;
    }

    @Override
    public UserCredentials activateUserCredentials(TenantId tenantId, String activateToken, String password) {
        log.trace("Executing activateUserCredentials activateToken [{}], password [{}]", activateToken, password);
        validateString(activateToken, "Incorrect activateToken " + activateToken);
        validateString(password, "Incorrect password " + password);
        UserCredentials userCredentials = userCredentialsDao.findByActivateToken(tenantId, activateToken);
        if (userCredentials == null) {
            throw new IncorrectParameterException(String.format("Unable to find user credentials by activateToken [%s]", activateToken));
        }
        if (userCredentials.isEnabled()) {
            throw new IncorrectParameterException("User credentials already activated");
        }
        userCredentials.setEnabled(true);
        userCredentials.setActivateToken(null);
        userCredentials.setPassword(password);
        if (userCredentials.getPassword() != null) {
            updatePasswordHistory(userCredentials);
        }
        return saveUserCredentials(tenantId, userCredentials);
    }

    @Override
    public UserCredentials requestPasswordReset(TenantId tenantId, String email) {
        log.trace("Executing requestPasswordReset email [{}]", email);
        DataValidator.validateEmail(email);
        User user = findUserByEmail(tenantId, email);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("Unable to find user by email [%s]", email));
        }
        UserCredentials userCredentials = userCredentialsDao.findByUserId(tenantId, user.getUuidId());
        if (!userCredentials.isEnabled()) {
            throw new DisabledException(String.format("User credentials not enabled [%s]", email));
        }
        userCredentials.setResetToken(generateSafeToken(DEFAULT_TOKEN_LENGTH));
        return saveUserCredentials(tenantId, userCredentials);
    }

    @Override
    public UserCredentials requestExpiredPasswordReset(TenantId tenantId, UserCredentialsId userCredentialsId) {
        UserCredentials userCredentials = userCredentialsDao.findById(tenantId, userCredentialsId.getId());
        if (!userCredentials.isEnabled()) {
            throw new IncorrectParameterException("Unable to reset password for inactive user");
        }
        userCredentials.setResetToken(generateSafeToken(DEFAULT_TOKEN_LENGTH));
        return saveUserCredentials(tenantId, userCredentials);
    }

    @Override
    public UserCredentials replaceUserCredentials(TenantId tenantId, UserCredentials userCredentials) {
        log.trace("Executing replaceUserCredentials [{}]", userCredentials);
        userCredentialsValidator.validate(userCredentials, data -> tenantId);
        userCredentialsDao.removeById(tenantId, userCredentials.getUuidId());
        userCredentials.setId(null);
        if (userCredentials.getPassword() != null) {
            updatePasswordHistory(userCredentials);
        }
        UserCredentials result = userCredentialsDao.save(tenantId, userCredentials);
        eventPublisher.publishEvent(ActionEntityEvent.builder()
                .tenantId(tenantId)
                .entityId(userCredentials.getUserId())
                .actionType(ActionType.CREDENTIALS_UPDATED).build());
        return result;
    }

    @Override
    @Transactional
    public void deleteUser(TenantId tenantId, User user) {
        Objects.requireNonNull(user, "User is null");
        UserId userId = user.getId();
        log.trace("[{}] Executing deleteUser [{}]", tenantId, userId);
        validateId(userId, INCORRECT_USER_ID + userId);
        userCredentialsDao.removeByUserId(tenantId, userId);
        userAuthSettingsDao.removeByUserId(userId);
        deleteEntityRelations(tenantId, userId);

        userDao.removeById(tenantId, userId.getId());
        eventPublisher.publishEvent(new UserCredentialsInvalidationEvent(userId));
        countService.publishCountEntityEvictEvent(tenantId, EntityType.USER);
        eventPublisher.publishEvent(DeleteEntityEvent.builder()
                .tenantId(tenantId)
                .entityId(userId)
                .entity(user).build());
    }

    @Override
    public PageData<User> findUsersByTenantId(TenantId tenantId, PageLink pageLink) {
        log.trace("Executing findUsersByTenantId, tenantId [{}], pageLink [{}]", tenantId, pageLink);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validatePageLink(pageLink);
        return userDao.findByTenantId(tenantId.getId(), pageLink);
    }

    @Override
    public PageData<User> findTenantAdmins(TenantId tenantId, PageLink pageLink) {
        log.trace("Executing findTenantAdmins, tenantId [{}], pageLink [{}]", tenantId, pageLink);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validatePageLink(pageLink);
        return userDao.findTenantAdmins(tenantId.getId(), pageLink);
    }

    @Override
    public PageData<User> findSysAdmins(PageLink pageLink) {
        return userDao.findAllByAuthority(Authority.SYS_ADMIN, pageLink);
    }

    @Override
    public PageData<User> findAllTenantAdmins(PageLink pageLink) {
        return userDao.findAllByAuthority(Authority.TENANT_ADMIN, pageLink);
    }

    @Override
    public PageData<User> findTenantAdminsByTenantsIds(List<TenantId> tenantsIds, PageLink pageLink) {
        return userDao.findByAuthorityAndTenantsIds(Authority.TENANT_ADMIN, tenantsIds, pageLink);
    }

    @Override
    public PageData<User> findTenantAdminsByTenantProfilesIds(List<TenantProfileId> tenantProfilesIds, PageLink pageLink) {
        return userDao.findByAuthorityAndTenantProfilesIds(Authority.TENANT_ADMIN, tenantProfilesIds, pageLink);
    }

    @Override
    public PageData<User> findAllUsers(PageLink pageLink) {
        return userDao.findAll(pageLink);
    }
    @Override
    public PageData<User> findAllUsersWithFilters(UserFilterForm userFilter, PageLink pageLink) {
        PageData<User> users;
        if(userFilter.getRoleIds() != null && userFilter.getRoleIds().isEmpty())
            users = userDao.findAllWithFilter(userFilter, pageLink);
        else
            users = userDao.findAllByRolesWithFilter(userFilter, pageLink);

        for(User user:users.getData())
        {
            setUserRoleNames(user);
        }
        return users;
    }

    @Override
    public void deleteTenantAdmins(TenantId tenantId) {
        log.trace("Executing deleteTenantAdmins, tenantId [{}]", tenantId);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        tenantAdminsRemover.removeEntities(tenantId, tenantId);
    }

    @Override
    public PageData<User> findCustomerUsers(TenantId tenantId, CustomerId customerId, PageLink pageLink) {
        log.trace("Executing findCustomerUsers, tenantId [{}], customerId [{}], pageLink [{}]", tenantId, customerId, pageLink);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateId(customerId, "Incorrect customerId " + customerId);
        validatePageLink(pageLink);
        return userDao.findCustomerUsers(tenantId.getId(), customerId.getId(), pageLink);
    }

    @Override
    public PageData<User> findUsersByCustomerIds(TenantId tenantId, List<CustomerId> customerIds, PageLink pageLink) {
        log.trace("Executing findTenantAndCustomerUsers, tenantId [{}], customerIds [{}], pageLink [{}]", tenantId, customerIds, pageLink);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validatePageLink(pageLink);
        customerIds.forEach(customerId -> {validateId(customerId, "Incorrect customerId " + customerId);});
        return userDao.findUsersByCustomerIds(tenantId.getId(), customerIds, pageLink);
    }

    @Override
    public void deleteCustomerUsers(TenantId tenantId, CustomerId customerId) {
        log.trace("Executing deleteCustomerUsers, customerId [{}]", customerId);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateId(customerId, "Incorrect customerId " + customerId);
        customerUsersRemover.removeEntities(tenantId, customerId);
    }

    @Override
    public void setUserCredentialsEnabled(TenantId tenantId, UserId userId, boolean enabled) {
        log.trace("Executing setUserCredentialsEnabled [{}], [{}]", userId, enabled);
        validateId(userId, INCORRECT_USER_ID + userId);
        UserCredentials userCredentials = userCredentialsDao.findByUserId(tenantId, userId.getId());
        userCredentials.setEnabled(enabled);
        saveUserCredentials(tenantId, userCredentials);

        User user = findUserById(tenantId, userId);
        JsonNode additionalInfo = user.getAdditionalInfo();
        if (!(additionalInfo instanceof ObjectNode)) {
            additionalInfo = JacksonUtil.newObjectNode();
        }
        ((ObjectNode) additionalInfo).put(USER_CREDENTIALS_ENABLED, enabled);
        user.setAdditionalInfo(additionalInfo);
        if (enabled) {
            resetFailedLoginAttempts(user);
        }
        userDao.save(user.getTenantId(), user);
    }


    @Override
    public void resetFailedLoginAttempts(TenantId tenantId, UserId userId) {
        log.trace("Executing onUserLoginSuccessful [{}]", userId);
        User user = findUserById(tenantId, userId);
        resetFailedLoginAttempts(user);
        saveUser(tenantId, user);
    }

    private void resetFailedLoginAttempts(User user) {
        JsonNode additionalInfo = user.getAdditionalInfo();
        if (!(additionalInfo instanceof ObjectNode)) {
            additionalInfo = JacksonUtil.newObjectNode();
        }
        ((ObjectNode) additionalInfo).put(FAILED_LOGIN_ATTEMPTS, 0);
        user.setAdditionalInfo(additionalInfo);
    }

    @Override
    public void setLastLoginTs(TenantId tenantId, UserId userId) {
        User user = findUserById(tenantId, userId);
        JsonNode additionalInfo = user.getAdditionalInfo();
        if (!(additionalInfo instanceof ObjectNode)) {
            additionalInfo = JacksonUtil.newObjectNode();
        }
        ((ObjectNode) additionalInfo).put(LAST_LOGIN_TS, System.currentTimeMillis());
        user.setAdditionalInfo(additionalInfo);
        saveUser(tenantId, user);
    }

    @Override
    public int increaseFailedLoginAttempts(TenantId tenantId, UserId userId) {
        log.trace("Executing onUserLoginIncorrectCredentials [{}]", userId);
        User user = findUserById(tenantId, userId);
        int failedLoginAttempts = increaseFailedLoginAttempts(user);
        saveUser(tenantId, user);
        return failedLoginAttempts;
    }

    private int increaseFailedLoginAttempts(User user) {
        JsonNode additionalInfo = user.getAdditionalInfo();
        if (!(additionalInfo instanceof ObjectNode)) {
            additionalInfo = JacksonUtil.newObjectNode();
        }
        int failedLoginAttempts = 0;
        if (additionalInfo.has(FAILED_LOGIN_ATTEMPTS)) {
            failedLoginAttempts = additionalInfo.get(FAILED_LOGIN_ATTEMPTS).asInt();
        }
        failedLoginAttempts = failedLoginAttempts + 1;
        ((ObjectNode) additionalInfo).put(FAILED_LOGIN_ATTEMPTS, failedLoginAttempts);
        user.setAdditionalInfo(additionalInfo);
        return failedLoginAttempts;
    }

    private void updatePasswordHistory(UserCredentials userCredentials) {
        JsonNode additionalInfo = userCredentials.getAdditionalInfo();
        if (!(additionalInfo instanceof ObjectNode)) {
            additionalInfo = JacksonUtil.newObjectNode();
        }
        Map<String, String> userPasswordHistoryMap = null;
        JsonNode userPasswordHistoryJson;
        if (additionalInfo.has(USER_PASSWORD_HISTORY)) {
            userPasswordHistoryJson = additionalInfo.get(USER_PASSWORD_HISTORY);
            userPasswordHistoryMap = JacksonUtil.convertValue(userPasswordHistoryJson, new TypeReference<>() {
            });
        }
        if (userPasswordHistoryMap != null) {
            userPasswordHistoryMap.put(Long.toString(System.currentTimeMillis()), userCredentials.getPassword());
            userPasswordHistoryJson = JacksonUtil.valueToTree(userPasswordHistoryMap);
            ((ObjectNode) additionalInfo).replace(USER_PASSWORD_HISTORY, userPasswordHistoryJson);
        } else {
            userPasswordHistoryMap = new HashMap<>();
            userPasswordHistoryMap.put(Long.toString(System.currentTimeMillis()), userCredentials.getPassword());
            userPasswordHistoryJson = JacksonUtil.valueToTree(userPasswordHistoryMap);
            ((ObjectNode) additionalInfo).set(USER_PASSWORD_HISTORY, userPasswordHistoryJson);
        }
        userCredentials.setAdditionalInfo(additionalInfo);
    }

    private final PaginatedRemover<TenantId, User> tenantAdminsRemover = new PaginatedRemover<>() {
        @Override
        protected PageData<User> findEntities(TenantId tenantId, TenantId id, PageLink pageLink) {
            return userDao.findTenantAdmins(id.getId(), pageLink);
        }

        @Override
        protected void removeEntity(TenantId tenantId, User user) {
            deleteUser(tenantId, user);
        }
    };

    private final PaginatedRemover<CustomerId, User> customerUsersRemover = new PaginatedRemover<>() {
        @Override
        protected PageData<User> findEntities(TenantId tenantId, CustomerId id, PageLink pageLink) {
            return userDao.findCustomerUsers(tenantId.getId(), id.getId(), pageLink);

        }

        @Override
        protected void removeEntity(TenantId tenantId, User entity) {
            deleteUser(tenantId, entity);
        }
    };

    @Override
    public Optional<HasId<?>> findEntity(TenantId tenantId, EntityId entityId) {
        return Optional.ofNullable(findUserById(tenantId, new UserId(entityId.getId())));
    }

    @Override
    public long countByTenantId(TenantId tenantId) {
        return userDao.countByTenantId(tenantId);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.USER;
    }

}
