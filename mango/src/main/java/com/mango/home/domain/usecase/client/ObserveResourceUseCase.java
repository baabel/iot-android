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

package com.mango.home.domain.usecase.client;

import com.mango.home.data.repository.CloudRepository;
import com.mango.home.data.repository.IotivityRepository;
import com.mango.home.data.repository.ResourceRepository;
import com.mango.home.domain.model.client.SerializableResource;
import com.mango.home.domain.model.devicelist.Device;
import com.mango.home.domain.model.devicelist.DeviceType;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;

public class ObserveResourceUseCase {

    private final IotivityRepository iotivityRepository;
    private final ResourceRepository resourceRepository;
    private final CloudRepository cloudRepository;

    @Inject
    public ObserveResourceUseCase(IotivityRepository iotivityRepository,
                                  ResourceRepository resourceRepository,
                                  CloudRepository cloudRepository) {
        this.iotivityRepository = iotivityRepository;
        this.resourceRepository = resourceRepository;
        this.cloudRepository = cloudRepository;
    }

    public Observable<SerializableResource> execute (Device device, SerializableResource resource) {
        Single<String> secureEndpoint = device.getDeviceType() == DeviceType.CLOUD ? cloudRepository.getSecureEndpoint() : iotivityRepository.getSecureEndpoint(device);
        return secureEndpoint
                .flatMapObservable(endpoint -> resourceRepository.observeResource(endpoint, device.getDeviceId(), resource));
    }
}

