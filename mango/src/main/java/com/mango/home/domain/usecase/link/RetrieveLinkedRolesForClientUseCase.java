/*
 *  *****************************************************************
 *
 *  Copyright 2018 DEKRA Testing and Certification, S.A.U. All Rights Reserved.
 *
 *  *****************************************************************
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  *****************************************************************
 */

package com.mango.home.domain.usecase.link;

import com.mango.home.data.repository.CmsRepository;
import com.mango.home.domain.model.devicelist.Device;
import com.mango.home.domain.model.resource.secure.cred.OcCredential;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

public class RetrieveLinkedRolesForClientUseCase {
    private final CmsRepository cmsRepository;

    @Inject
    public RetrieveLinkedRolesForClientUseCase(CmsRepository cmsRepository)
    {
        this.cmsRepository = cmsRepository;
    }

    public Single<List<String>> execute(Device device)
    {
        return cmsRepository.getCredentials(device.getDeviceId())
                .map(credentials -> {
                    List<String> roles = new ArrayList<>();

                    for (OcCredential cred : credentials.getCredList()) {
                        if (cred.getRoleid() != null) {
                            roles.add(cred.getRoleid().getRole());
                        }
                    }

                    return roles;
                });
    }
}
