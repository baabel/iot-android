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
import com.mango.home.data.repository.PreferencesRepository;
import com.mango.home.domain.model.devicelist.Device;
import com.mango.home.domain.model.resource.virtual.d.OcDeviceInfo;
import com.mango.home.utils.rx.SchedulersFacade;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Single;

public class GetDeviceInfoUseCase {
    /* Repositories */
    private final IotivityRepository iotivityRepository;
    private final PreferencesRepository preferencesRepository;
    /* Scheduler */
    private final SchedulersFacade schedulersFacade;

    @Inject
    public GetDeviceInfoUseCase(IotivityRepository iotivityRepository,
                                PreferencesRepository preferencesRepository,
                                SchedulersFacade schedulersFacade) {
        this.iotivityRepository = iotivityRepository;
        this.preferencesRepository = preferencesRepository;

        this.schedulersFacade = schedulersFacade;
    }

    public Single<OcDeviceInfo> execute(Device device) {
        return iotivityRepository.getNonSecureEndpoint(device)
                .flatMap(iotivityRepository::getDeviceInfo)
                .delay(preferencesRepository.getRequestsDelay(), TimeUnit.SECONDS, schedulersFacade.ui());
    }
}
