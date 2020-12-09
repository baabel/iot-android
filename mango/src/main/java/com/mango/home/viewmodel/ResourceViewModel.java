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
import androidx.lifecycle.ViewModel;

import org.iotivity.OCRepresentation;
import com.mango.home.domain.model.client.SerializableResource;
import com.mango.home.domain.model.devicelist.DeviceType;
import com.mango.home.domain.usecase.UpdateDeviceTypeUseCase;
import com.mango.home.domain.usecase.client.CancelObserveResourceUseCase;
import com.mango.home.domain.usecase.client.GetRequestUseCase;
import com.mango.home.domain.usecase.client.ObserveResourceUseCase;
import com.mango.home.domain.usecase.client.PostRequestUseCase;
import com.mango.home.utils.viewmodel.ViewModelError;
import com.mango.home.utils.viewmodel.ViewModelErrorType;
import com.mango.home.utils.rx.SchedulersFacade;
import com.mango.home.domain.model.devicelist.Device;

import java.util.Map;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class ResourceViewModel extends ViewModel {

    private final GetRequestUseCase mGetRequestUseCase;
    private final PostRequestUseCase mPostRequestUseCase;
    private final ObserveResourceUseCase mObserveResource;
    private final CancelObserveResourceUseCase mCancelObserveResourceUseCase;
    private final UpdateDeviceTypeUseCase mUpdateDeviceTypeUseCase;

    private final SchedulersFacade mSchedulersFacade;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<Boolean> mProcessing = new MutableLiveData<>();
    private final MutableLiveData<ViewModelError> mError = new MutableLiveData<>();

    private final MutableLiveData<SerializableResource> mResponse = new MutableLiveData<>();

    @Inject
    ResourceViewModel(
            GetRequestUseCase getRequestUseCase,
            PostRequestUseCase postRequestUseCase,
            ObserveResourceUseCase observeResource,
            CancelObserveResourceUseCase cancelObserveResourceUseCase,
            UpdateDeviceTypeUseCase updateDeviceTypeUseCase,
            SchedulersFacade schedulersFacade) {
        this.mGetRequestUseCase = getRequestUseCase;
        this.mPostRequestUseCase = postRequestUseCase;
        this.mObserveResource = observeResource;
        this.mCancelObserveResourceUseCase = cancelObserveResourceUseCase;
        this.mUpdateDeviceTypeUseCase = updateDeviceTypeUseCase;

        this.mSchedulersFacade = schedulersFacade;
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }

    public LiveData<Boolean> isProcessing() {
        return mProcessing;
    }

    public LiveData<ViewModelError> getError() {
        return mError;
    }

    public LiveData<SerializableResource> getResponse() {
        return mResponse;
    }

    public void getRequest(Device device, SerializableResource resource) {
        disposables.add(mGetRequestUseCase.execute(device, resource)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .subscribe(
                        value -> {
                            mResponse.setValue(value);
                            if (!device.hasDOXSpermit()
                                    && (device.getDeviceType() == DeviceType.OWNED_BY_OTHER
                                    || device.getDeviceType() == DeviceType.OWNED_BY_OTHER_WITH_PERMITS)) {
                                disposables.add(mUpdateDeviceTypeUseCase.execute(device.getDeviceId(),
                                                                                DeviceType.OWNED_BY_OTHER_WITH_PERMITS,
                                                                                device.getPermits() | Device.DOXS_PERMITS)
                                        .subscribeOn(mSchedulersFacade.io())
                                        .observeOn(mSchedulersFacade.ui())
                                        .subscribe(
                                                () -> {},
                                                throwable -> mError.setValue(new ViewModelError(Error.DB, null))
                                        ));

                            }
                        },
                        throwable -> {
                            mError.setValue(new ViewModelError(Error.GET, null));
                            if (device.hasDOXSpermit()) {
                                disposables.add(mUpdateDeviceTypeUseCase.execute(device.getDeviceId(),
                                                                                DeviceType.OWNED_BY_OTHER,
                                                                                device.getPermits() & ~Device.DOXS_PERMITS)
                                        .subscribeOn(mSchedulersFacade.io())
                                        .observeOn(mSchedulersFacade.ui())
                                        .subscribe(
                                                () -> {},
                                                throwable2 -> mError.setValue(new ViewModelError(Error.DB, null))
                                        ));
                            }
                        }
                ));
    }

    public void observeRequest(Device device, SerializableResource resource) {
        disposables.add(mObserveResource.execute(device, resource)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .subscribe(
                        mResponse::setValue,
                        throwable -> mError.setValue(new ViewModelError(Error.OBSERVE, null))
                ));
    }

    public void cancelObserveRequest(SerializableResource resource) {
        disposables.add(mCancelObserveResourceUseCase.execute(resource)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .subscribe(
                        () -> {},
                        throwable -> mError.setValue(new ViewModelError(Error.CANCEL_OBSERVE, null))
                ));
    }

    public void postRequest(Device device, SerializableResource resource, OCRepresentation rep, Object valueArray) {
        disposables.add(mPostRequestUseCase.execute(device, resource, rep, valueArray)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .subscribe(
                        () -> {},//mResponse::setValue,
                        throwable -> {
                            mError.setValue(new ViewModelError(Error.POST, null));
                            mResponse.setValue(resource);
                        }
                ));
    }

    public void postRequest(Device device, SerializableResource resource, Map<String, Object> values) {
        disposables.add(mPostRequestUseCase.execute(device, resource, values)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .subscribe(
                        () -> {},//mResponse::setValue,
                        throwable -> {
                            mError.setValue(new ViewModelError(Error.POST, null));
                            mResponse.setValue(resource);
                        }
                ));
    }

    public enum Error implements ViewModelErrorType {
        GET,
        POST,
        PUT,
        OBSERVE,
        CANCEL_OBSERVE,
        DB
    }
}
