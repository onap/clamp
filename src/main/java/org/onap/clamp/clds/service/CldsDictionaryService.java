/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights
 *                             reserved.
 * ================================================================================
 * Modifications Copyright (c) 2019 Samsung
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END============================================
 * ===================================================================
 *
 */

package org.onap.clamp.clds.service;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.onap.clamp.clds.dao.CldsDao;
import org.onap.clamp.clds.model.CldsDictionary;
import org.onap.clamp.clds.model.CldsDictionaryItem;
import org.onap.clamp.clds.util.LoggingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * REST services to manage dictionary and dictionary items for Tosca Model.
 */
@Component
public class CldsDictionaryService extends SecureServiceBase {

    @Value("${clamp.config.security.permission.type.tosca:permission-type-tosca}")
    private String                  cldsPermissionTypeTosca;
    @Value("${clamp.config.security.permission.instance:dev}")
    private String                  cldsPermissionInstance;
    private SecureServicePermission permissionReadTosca;
    private SecureServicePermission permissionUpdateTosca;

    @Autowired
    private CldsDao                 cldsDao;
    
    private LoggingUtils util = new LoggingUtils(logger);
    

    @PostConstruct
    private final void initConstruct() {
        permissionReadTosca = SecureServicePermission.create(cldsPermissionTypeTosca, cldsPermissionInstance, "read");
        permissionUpdateTosca = SecureServicePermission.create(cldsPermissionTypeTosca, cldsPermissionInstance,
                "update");
    }

    /**
     * REST Service that creates or Updates a Dictionary.
     * 
     * @param dictionaryName dictionary name
     * @param cldsDictionary clds dictionary
     * @return CldsDictionary that was created in DB.
     */
    public ResponseEntity<CldsDictionary> createOrUpdateDictionary(String dictionaryName,
            CldsDictionary cldsDictionary) {
        final Date startTime = new Date();
        LoggingUtils.setRequestContext("CldsDictionaryService: createOrUpdateDictionary", getPrincipalName());
        // TODO revisit based on new permissions
        isAuthorized(permissionUpdateTosca);
        if (cldsDictionary == null) {
            cldsDictionary = new CldsDictionary();
            cldsDictionary.setDictionaryName(dictionaryName);
        }
        cldsDictionary.save(dictionaryName, cldsDao, getUserId());
        auditLogInfo("createOrUpdateDictionary", startTime);
        return new ResponseEntity<>(cldsDictionary, HttpStatus.OK);
    }

    /**
     * REST Service that creates or Updates a Dictionary Elements for dictionary
     * in DB.
     * 
     * @param dictionaryName dictionary name
     * @param dictionaryItem dictionary item
     * @return CldsDictionaryItem A dictionary items that was created or updated
     *         in DB
     */
    public ResponseEntity<CldsDictionaryItem> createOrUpdateDictionaryElements(String dictionaryName,
            CldsDictionaryItem dictionaryItem) {
        final Date startTime = new Date();
        LoggingUtils.setRequestContext("CldsDictionaryService: createOrUpdateDictionaryElements", getPrincipalName());
        // TODO revisit based on new permissions
        isAuthorized(permissionUpdateTosca);
        dictionaryItem.save(dictionaryName, cldsDao, getUserId());
        auditLogInfo("createOrUpdateDictionaryElements", startTime);
        return new ResponseEntity<>(dictionaryItem, HttpStatus.OK);
    }

    /**
     * Rest Service that retrieves all CLDS dictionary in DB.
     * 
     * @return CldsDictionary List List of CldsDictionary available in DB
     */
    public ResponseEntity<List<CldsDictionary>> getAllDictionaryNames() {
        final Date startTime = new Date();
        LoggingUtils.setRequestContext("CldsDictionaryService: getAllDictionaryNames", getPrincipalName());
        // TODO revisit based on new permissions
        isAuthorized(permissionReadTosca);
        List<CldsDictionary> dictionaries = cldsDao.getDictionary(null, null);
        auditLogInfo("getAllDictionaryNames", startTime);
        return new ResponseEntity<>(dictionaries, HttpStatus.OK);
    }

    /**
     * Rest Service that retrieves all CLDS dictionary items in DB for a give
     * dictionary name.
     * 
     * @param dictionaryName dictionary name
     * @return CldsDictionaryItem list List of CLDS Dictionary items for a given
     *         dictionary name
     */
    public ResponseEntity<List<CldsDictionaryItem>> getDictionaryElementsByName(String dictionaryName) {
        final Date startTime = new Date();
        LoggingUtils.setRequestContext("CldsDictionaryService: getDictionaryElementsByName", getPrincipalName());
        // TODO revisit based on new permissions
        isAuthorized(permissionReadTosca);
        List<CldsDictionaryItem> dictionaryItems = cldsDao.getDictionaryElements(dictionaryName, null, null);
        auditLogInfo("getDictionaryElementsByName", startTime);
        return new ResponseEntity<>(dictionaryItems, HttpStatus.OK);
    }

    private void auditLogInfo(String methodNamed, Date startTime) {
        LoggingUtils.setTimeContext(startTime, new Date());
        LoggingUtils.setResponseContext("0", methodNamed + " success", this.getClass().getName());
        auditLogger.info(methodNamed + " completed");
    }

    public ResponseEntity<?> deleteDictionary() {
        return null;
    }

    // Created for the integration test
    public void setLoggingUtil(LoggingUtils utilP) {
        util = utilP;
    }

}
