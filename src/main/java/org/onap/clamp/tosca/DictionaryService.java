/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2020 AT&T Intellectual Property. All rights
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

import org.onap.clamp.clds.service.SecureServiceBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DictionaryService extends SecureServiceBase {

    private final DictionaryRepository dictionaryRepository;
    private final DictionaryElementsRepository dictionaryElementsRepository;

    /**
     * Constructor.
     */
    @Autowired
    public DictionaryService(DictionaryRepository dictionaryRepository,
            DictionaryElementsRepository dictionaryElementsRepository) {
        this.dictionaryRepository = dictionaryRepository;
        this.dictionaryElementsRepository = dictionaryElementsRepository;

    }

    public Dictionary saveOrUpdateDictionary(Dictionary dictionary) {
        return dictionaryRepository.save(dictionary);
    }

    public Dictionary saveOrUpdateDictionaryElement(String dictionaryName, Dictionary dictionary) {
        Dictionary dict = getDictionary(dictionaryName);
        dictionary.getDictionaryElements().forEach(element -> dict.addDictionaryElements(element));
        return dictionaryRepository.save(dict);
    }

    public void deleteDictionary(Dictionary dictionary) {
        dictionaryRepository.delete(dictionary);
    }

    public void deleteDictionary(String dictionaryName) {
        dictionaryRepository.deleteById(dictionaryName);
    }

    public List<Dictionary> getAllDictionaries() {
        return dictionaryRepository.findAll();
    }

    public Dictionary getDictionary(String dictionaryName) {
        return dictionaryRepository.findById(dictionaryName).orElse(null);
    }

    public void deleteDictionaryElement(String dictionaryName, String dictionaryElementShortName) {
        if (dictionaryRepository.existsById(dictionaryName)) {
            DictionaryElement element = dictionaryElementsRepository.findById(dictionaryElementShortName).orElse(null);
            if (element != null) {
                Dictionary dict = getDictionary(dictionaryName);
                dict.removeDictionaryElement(element);
                dictionaryRepository.save(dict);
            }
        }
    }

}
