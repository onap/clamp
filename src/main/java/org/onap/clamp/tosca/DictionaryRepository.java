/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights
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

package org.onap.clamp.tosca;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DictionaryRepository extends CrudRepository<Dictionary, String> {

    @Query("SELECT dict.dictionaryName FROM Dictionary as dict")    
    List<String> getAllDictionaryNames();
    
    Dictionary findByDictionaryName(String dictionaryName);
    
    List<Dictionary> findByDictionaryIdOrDictionaryName(String dictionaryId, String dictionaryName);
            
    @Query("SELECT dict.dictionaryId FROM Dictionary as dict where dict.dictionaryName=:dictionaryName")
    String getDictionaryId(@Param("dictionaryName") String dictionaryName);
                          
}
