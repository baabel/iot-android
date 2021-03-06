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

import io.reactivex.Completable;

public class UnlinkRoleForClientUseCase {
    private final CmsRepository cmsRepository;

    @Inject
    public UnlinkRoleForClientUseCase(CmsRepository cmsRepository)
    {
        this.cmsRepository = cmsRepository;
    }

    public Completable execute(Device device, String roleId) {
        return cmsRepository.getCredentials(device.getDeviceId())
                .flatMapCompletable(ocCredentials -> {
                    List<Completable> deleteCredList = new ArrayList<>();
                    for(OcCredential cred : ocCredentials.getCredList()) {
                        if (cred.getRoleid() != null && cred.getRoleid().getRole().equals(roleId)) {
                            Completable deleteCred = cmsRepository.deleteCredential(device.getDeviceId(), cred.getCredid());
                            deleteCredList.add(deleteCred);
                        }
                    }
                    return Completable.merge(deleteCredList);
                });
    }
}
