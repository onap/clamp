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

package org.onap.clamp.clds.sdc.controller.installer;

import org.onap.clamp.clds.exception.policy.PolicyModelException;
import org.onap.clamp.clds.exception.sdc.controller.SdcArtifactInstallerException;

public interface CsarInstaller {
    String TEMPLATE_NAME_PREFIX = "DCAE-Designer-Template-";

    boolean isCsarAlreadyDeployed(CsarHandler csar) throws SdcArtifactInstallerException;

    public void installTheCsar(CsarHandler csar)
        throws SdcArtifactInstallerException, InterruptedException, PolicyModelException;
}
