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

package org.onap.clamp.loop.service;

import java.util.List;

import org.onap.clamp.loop.template.LoopTemplate;
import org.onap.clamp.loop.template.LoopTemplatesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoopTemplateService {
	
	private final LoopTemplatesRepository loopTemplatesRepository;

	/**
     * Constructor.
     */
	@Autowired
	public LoopTemplateService(LoopTemplatesRepository loopTemplatesRepository) {
        this.loopTemplatesRepository = loopTemplatesRepository;
    }

    LoopTemplate saveOrUpdateLoopTemplate(LoopTemplate loopTemplate) {
        return loopTemplatesRepository.save(loopTemplate);
    }

    public List<String> getLoopTemplateNames() {
        return loopTemplatesRepository.getAllLoopTemplateNames();
    }

    public List<LoopTemplate> getAllLoopTemplates() {
        return (List<LoopTemplate>)loopTemplatesRepository.findAll();
    }

    public LoopTemplate getLoopTemplate(String loopTemplateName) {
       return loopTemplatesRepository.findById(loopTemplateName).orElse(null);
    }

    public void deleteLoopTemplate(String loopTemplateName) {
    	loopTemplatesRepository.deleteById(loopTemplateName);
    }
}
