/*
 * Copyright 2018 DEKRA Testing and Certification, S.A.U. All Rights Reserved.
 *
 * ****************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *            http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mango.home.domain.usecase.trustanchor;

import com.mango.home.data.repository.CmsRepository;

import io.reactivex.Single;

import org.iotivity.OCCredUsage;
import org.iotivity.OCCredUtil;

import com.mango.home.domain.model.resource.secure.cred.OcCredential;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class GetTrustAnchorUseCase {
    private final CmsRepository cmsRepository;

    @Inject
    public GetTrustAnchorUseCase(CmsRepository cmsRepository) {
        this.cmsRepository = cmsRepository;
    }

    public Single<List<OcCredential>> execute() {
        return cmsRepository.retrieveOwnCredentials()
                .flatMap(creds ->  Single.create(emitter -> {

                    List<OcCredential> trustAnchorList = new ArrayList<>();
                    for (OcCredential cred : creds.getCredList()) {
                        if (cred.getCredusage() != null && !cred.getCredusage().isEmpty()
                                && (OCCredUtil.parseCredUsage(cred.getCredusage()) == OCCredUsage.OC_CREDUSAGE_MFG_TRUSTCA
                                || OCCredUtil.parseCredUsage(cred.getCredusage()) == OCCredUsage.OC_CREDUSAGE_TRUSTCA
                                || OCCredUtil.parseCredUsage(cred.getCredusage()) == OCCredUsage.OC_CREDUSAGE_IDENTITY_CERT
                                || OCCredUtil.parseCredUsage(cred.getCredusage()) == OCCredUsage.OC_CREDUSAGE_MFG_CERT)) {
                            trustAnchorList.add(cred);
                        }
                    }

                    emitter.onSuccess(trustAnchorList);
                }));
    }
}
