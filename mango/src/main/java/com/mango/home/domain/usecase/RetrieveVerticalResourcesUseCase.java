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

package com.mango.home.domain.usecase;

import com.mango.home.data.repository.IotivityRepository;

import io.reactivex.Single;

import com.mango.home.domain.model.devicelist.Device;
import com.mango.home.domain.model.resource.virtual.res.OcResource;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class RetrieveVerticalResourcesUseCase {
    private final IotivityRepository iotivityRepository;

    @Inject
    public RetrieveVerticalResourcesUseCase(IotivityRepository iotivityRepository) {
        this.iotivityRepository = iotivityRepository;
    }

    public Single<List<String>> execute(Device device) {
        return iotivityRepository.getNonSecureEndpoint(device)
                .flatMap(iotivityRepository::findResources)
                .map(ocRes -> {
                    List<String> verticalResources = new ArrayList<>();
                    for (OcResource resource : ocRes.getResourceList()) {
                        verticalResources.add(resource.getHref());
                    }

                    return verticalResources;
                });
    }
}

