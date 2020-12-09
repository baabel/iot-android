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
package com.mango.home.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.mango.home.domain.model.client.DynamicUiElement;
import com.mango.home.domain.model.client.SerializableResource;
import com.mango.home.domain.model.devicelist.Device;
import com.mango.home.domain.model.devicelist.DeviceType;
import com.mango.home.domain.model.resource.virtual.d.OcDeviceInfo;
import com.mango.home.domain.model.resource.virtual.p.OcPlatformInfo;
import com.mango.home.domain.usecase.GetDeviceInfoUseCase;
import com.mango.home.domain.usecase.client.GetPlatformInfoUseCase;
import com.mango.home.domain.usecase.GetResourcesUseCase;
import com.mango.home.domain.usecase.client.IntrospectUseCase;
import com.mango.home.domain.usecase.client.UiFromSwaggerUseCase;
import com.mango.home.domain.usecase.GetDeviceNameUseCase;
import com.mango.home.utils.viewmodel.BaseViewModel;
import com.mango.home.utils.viewmodel.ViewModelError;
import com.mango.home.utils.viewmodel.ViewModelErrorType;
import com.mango.home.utils.rx.SchedulersFacade;
import com.mango.home.domain.usecase.cloud.CloudDiscoverResourcesUseCase;
import com.mango.home.domain.usecase.cloud.CloudGetResourceUseCase;
import com.mango.home.domain.usecase.cloud.CloudPostResourceUseCase;
import com.mango.home.domain.usecase.cloud.CloudRetrieveDeviceInfoUseCase;
import com.mango.home.domain.usecase.cloud.CloudRetrievePlatformInfoUseCase;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

public class GenericClientViewModel extends BaseViewModel {

    private final GetDeviceNameUseCase mGetDeviceNameUseCase;
    private final GetDeviceInfoUseCase mGetDeviceInfoUseCase;
    private final GetPlatformInfoUseCase mGetPlatformInfoUseCase;
    private final GetResourcesUseCase mGetResourcesUseCase;
    private final IntrospectUseCase mIntrospectUseCase;
    private final UiFromSwaggerUseCase mUiFromSwaggerUseCase;
    private final CloudRetrieveDeviceInfoUseCase cloudRetrieveDeviceInfoUseCase;
    private final CloudRetrievePlatformInfoUseCase cloudRetrievePlatformInfoUseCase;
    private final CloudDiscoverResourcesUseCase cloudDiscoverResourcesUseCase;

    private final SchedulersFacade schedulersFacade;

    private final MutableLiveData<String> mDeviceName = new MutableLiveData<>();
    private final MutableLiveData<OcDeviceInfo> mDeviceInfo = new MutableLiveData<>();
    private final MutableLiveData<OcPlatformInfo> mPlatformInfo = new MutableLiveData<>();
    private final MutableLiveData<List<SerializableResource>> mResources = new MutableLiveData<>();
    private final MutableLiveData<List<DynamicUiElement>> mIntrospection = new MutableLiveData<>();

    @Inject
    GenericClientViewModel(
            GetDeviceNameUseCase getDeviceNameUseCase,
            GetDeviceInfoUseCase getDeviceInfoUseCase,
            GetPlatformInfoUseCase getPlatformInfoUseCase,
            GetResourcesUseCase getResourcesUseCase,
            IntrospectUseCase introspectUseCase,
            UiFromSwaggerUseCase uiFromSwaggerUseCase,
            SchedulersFacade schedulersFacade,
            CloudRetrieveDeviceInfoUseCase cloudRetrieveDeviceInfoUseCase,
            CloudRetrievePlatformInfoUseCase cloudRetrievePlatformInfoUseCase,
            CloudDiscoverResourcesUseCase cloudDiscoverResourcesUseCase) {
        this.mGetDeviceNameUseCase = getDeviceNameUseCase;
        this.mGetDeviceInfoUseCase = getDeviceInfoUseCase;
        this.mGetPlatformInfoUseCase = getPlatformInfoUseCase;
        this.mGetResourcesUseCase = getResourcesUseCase;
        this.mIntrospectUseCase = introspectUseCase;
        this.mUiFromSwaggerUseCase = uiFromSwaggerUseCase;
        this.schedulersFacade = schedulersFacade;
        this.cloudRetrieveDeviceInfoUseCase = cloudRetrieveDeviceInfoUseCase;
        this.cloudRetrievePlatformInfoUseCase = cloudRetrievePlatformInfoUseCase;
        this.cloudDiscoverResourcesUseCase = cloudDiscoverResourcesUseCase;
    }

    public LiveData<String> getDeviceName() {
        return mDeviceName;
    }

    public LiveData<OcDeviceInfo> getDeviceInfo() {
        return mDeviceInfo;
    }

    public LiveData<OcPlatformInfo> getPlatformInfo() {
        return mPlatformInfo;
    }

    public LiveData<List<SerializableResource>> getResources() {
        return mResources;
    }

    public LiveData<List<DynamicUiElement>> getIntrospection() {
        return mIntrospection;
    }

    public void loadDeviceName(String deviceId) {
        mDisposables.add(mGetDeviceNameUseCase.execute(deviceId)
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(
                        mDeviceName::setValue,
                        throwable -> mError.setValue(
                                new ViewModelError(Error.DEVICE_NAME, null)
                        )
                ));
    }

    public void loadDeviceInfo(Device device) {
        Single<OcDeviceInfo> deviceInfoSingle = device.getDeviceType() != DeviceType.CLOUD
                ? mGetDeviceInfoUseCase.execute(device)
                : cloudRetrieveDeviceInfoUseCase.execute(device);

        mDisposables.add(deviceInfoSingle
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .doOnSubscribe(__ -> mProcessing.setValue(true))
                .subscribe(
                        mDeviceInfo::setValue,
                        throwable -> mError.setValue(
                                new ViewModelError(Error.DEVICE_INFO, null))
                ));
    }

    public void loadPlatformInfo(Device device) {
        Single<OcPlatformInfo> platformInfoSingle = device.getDeviceType() != DeviceType.CLOUD
                ? mGetPlatformInfoUseCase.execute(device)
                : cloudRetrievePlatformInfoUseCase.execute(device);

        mDisposables.add(platformInfoSingle
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .doOnSubscribe(__ -> mProcessing.setValue(true))
                .subscribe(
                        mPlatformInfo::setValue,
                        throwable -> mError.setValue(
                                new ViewModelError(Error.PLATFORM_INFO, null))
                ));
    }

    public void introspect(Device device) {
        if (device.getDeviceType() != DeviceType.CLOUD) {
            mDisposables.add(mIntrospectUseCase.execute(device)
                    .flatMap(mUiFromSwaggerUseCase::execute)
                    .subscribeOn(schedulersFacade.io())
                    .observeOn(schedulersFacade.ui())
                    .doOnSubscribe(__ -> mProcessing.setValue(true))
                    .subscribe(
                            mIntrospection::setValue,
                            throwable -> mError.setValue(
                                    new ViewModelError(Error.INTROSPECTION, null))
                    ));
        }
    }

    public void findResources(Device device) {
        Single<List<SerializableResource>> discoverResourcesSingle = device.getDeviceType() != DeviceType.CLOUD
                ? mGetResourcesUseCase.execute(device)
                : cloudDiscoverResourcesUseCase.execute(device);

        mDisposables.add(discoverResourcesSingle
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .doOnSubscribe(__ -> mProcessing.setValue(true))
                .subscribe(
                        mResources::setValue,
                        throwable -> mError.setValue(
                                new ViewModelError(Error.FIND_RESOURCES, null))
                ));
    }

    public enum Error implements ViewModelErrorType {
        DEVICE_NAME,
        DEVICE_INFO,
        PLATFORM_INFO,
        INTROSPECTION,
        FIND_RESOURCES
    }
}
