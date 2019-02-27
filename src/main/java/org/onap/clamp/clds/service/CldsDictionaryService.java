/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights
 *                             reserved.
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
import org.onap.clamp.clds.util.PrincipalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

/**
 * REST services to manage dictionary and dictionary items for Tosca Model
 */
@Component
public class CldsDictionaryService {

    @Autowired
    private CldsDao                 cldsDao;
    private static final EELFLogger auditLogger = EELFManager.getInstance().getAuditLogger();
    private static final EELFLogger logger = EELFManager.getInstance().getLogger(CldsDictionaryService.class);
    private LoggingUtils util = new LoggingUtils(logger);

    /**
     * REST Service that creates or Updates a Dictionary
     * 
     * @param dictionaryName
     * @param cldsDictionary
     * @return CldsDictionary that was created in DB.
     */
    public ResponseEntity<CldsDictionary> createOrUpdateDictionary(String dictionaryName,
            CldsDictionary cldsDictionary) {
        Date startTime = new Date();
        LoggingUtils.setRequestContext("CldsDictionaryService: createOrUpdateDictionary", PrincipalUtils.getPrincipalName());
        if (cldsDictionary == null) {
            cldsDictionary = new CldsDictionary();
            cldsDictionary.setDictionaryName(dictionaryName);
        }
        cldsDictionary.save(dictionaryName, cldsDao, PrincipalUtils.getUserId());
        LoggingUtils.setTimeContext(startTime, new Date());
        LoggingUtils.setResponseContext("0", "createOrUpdateDictionary success", this.getClass().getName());
        auditLogger.info("createOrUpdateDictionary completed");
        return new ResponseEntity<>(cldsDictionary, HttpStatus.OK);
    }

    /**
     * REST Service that creates or Updates a Dictionary Elements for dictionary
     * in DB
     * 
     * @param dictionaryName
     * @param dictionaryItem
     * @return CldsDictionaryItem A dictionary items that was created or updated
     *         in DB
     */
    public ResponseEntity<CldsDictionaryItem> createOrUpdateDictionaryElements(String dictionaryName,
            CldsDictionaryItem dictionaryItem) {
        Date startTime = new Date();
        LoggingUtils.setRequestContext("CldsDictionaryService: createOrUpdateDictionaryElements", PrincipalUtils.getPrincipalName());
        dictionaryItem.save(dictionaryName, cldsDao, PrincipalUtils.getUserId());
        LoggingUtils.setTimeContext(startTime, new Date());
        LoggingUtils.setResponseContext("0", "createOrUpdateDictionaryElements success", this.getClass().getName());
        auditLogger.info("createOrUpdateDictionaryElements completed");
        return new ResponseEntity<>(dictionaryItem, HttpStatus.OK);
    }

    /**
     * Rest Service that retrieves all CLDS dictionary in DB
     * 
     * @return CldsDictionary List List of CldsDictionary available in DB
     */
    public ResponseEntity<List<CldsDictionary>> getAllDictionaryNames() {
        Date startTime = new Date();
        LoggingUtils.setRequestContext("CldsDictionaryService: getAllDictionaryNames", PrincipalUtils.getPrincipalName());
        List<CldsDictionary> dictionaries = cldsDao.getDictionary(null, null);
        LoggingUtils.setTimeContext(startTime, new Date());
        LoggingUtils.setResponseContext("0", "getAllDictionaryNames success", this.getClass().getName());
        auditLogger.info("getAllDictionaryNames completed");
        return new ResponseEntity<>(dictionaries, HttpStatus.OK);
    }

    /**
     * Rest Service that retrieves all CLDS dictionary items in DB for a give
     * dictionary name
     * 
     * @param dictionaryName
     * @return CldsDictionaryItem list List of CLDS Dictionary items for a given
     *         dictionary name
     */
    public ResponseEntity<List<CldsDictionaryItem>> getDictionaryElementsByName(String dictionaryName) {
        Date startTime = new Date();
        LoggingUtils.setRequestContext("CldsDictionaryService: getDictionaryElementsByName", PrincipalUtils.getPrincipalName());
        List<CldsDictionaryItem> dictionaryItems = cldsDao.getDictionaryElements(dictionaryName, null, null);
        LoggingUtils.setTimeContext(startTime, new Date());
        LoggingUtils.setResponseContext("0", "getAllDictionaryNames success", this.getClass().getName());
        auditLogger.info("getAllDictionaryNames completed");
        return new ResponseEntity<>(dictionaryItems, HttpStatus.OK);
    }

    public ResponseEntity<?> deleteDictionary() {
        return null;
    }

    // Created for the integration test
    public void setLoggingUtil(LoggingUtils utilP) {
        util = utilP;
    }

}
