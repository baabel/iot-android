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

import org.iotivity.OCResourcePropertiesMask;
import com.mango.home.domain.model.client.SerializableResource;
import com.mango.home.domain.model.devicelist.Device;
import com.mango.home.domain.model.resource.virtual.res.OcResource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

public class GetResourcesUseCase {
    private final IotivityRepository iotivityRepository;

    @Inject
    public GetResourcesUseCase(IotivityRepository iotivityRepository) {
        this.iotivityRepository = iotivityRepository;
    }

    public Single<List<SerializableResource>> execute(Device device) {
        return iotivityRepository.discoverVerticalResources(device.getDeviceId())
                .map(ocResources -> {
                    List<SerializableResource> serializableResources = new ArrayList<>();
                    for (OcResource resource : ocResources) {
                        SerializableResource serializableResource = new SerializableResource();
                        serializableResource.setUri(resource.getHref());
                        serializableResource.setResourceTypes(resource.getResourceTypes());
                        serializableResource.setResourceInterfaces(resource.getInterfaces());
                        serializableResource.setObservable(
                                (resource.getPropertiesMask() & OCResourcePropertiesMask.OC_OBSERVABLE) > 0);

                        serializableResources.add(serializableResource);
                    }

                    Collections.sort(serializableResources,
                            (r1, r2) -> r1.getUri().compareTo(r2.getUri()));

                    return serializableResources;
                });
    }
}
