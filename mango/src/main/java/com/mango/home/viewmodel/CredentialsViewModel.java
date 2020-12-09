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

import com.mango.home.domain.model.devicelist.DeviceType;
import com.mango.home.domain.model.resource.secure.cred.OcCredential;
import com.mango.home.domain.usecase.UpdateDeviceTypeUseCase;
import com.mango.home.utils.viewmodel.BaseViewModel;
import com.mango.home.utils.viewmodel.ViewModelError;
import com.mango.home.utils.viewmodel.ViewModelErrorType;
import com.mango.home.domain.usecase.credential.DeleteCredentialUseCase;
import com.mango.home.domain.usecase.credential.RetrieveCredentialsUseCase;
import com.mango.home.utils.rx.SchedulersFacade;
import com.mango.home.domain.model.devicelist.Device;

import javax.inject.Inject;

public class CredentialsViewModel extends BaseViewModel {

    private final RetrieveCredentialsUseCase mRetrieveCredentialsUseCase;
    private final DeleteCredentialUseCase mDeleteCredentialUseCase;
    private final UpdateDeviceTypeUseCase mUpdateDeviceTypeUseCase;

    private final SchedulersFacade mSchedulersFacade;

    private final MutableLiveData<String> mRownerUuid = new MutableLiveData<>();
    private final MutableLiveData<OcCredential> mCredential = new MutableLiveData<>();
    private final MutableLiveData<Long> mDeletedCredId = new MutableLiveData<>();

    @Inject
    CredentialsViewModel(
            RetrieveCredentialsUseCase retrieveCredentialsUseCase,
            DeleteCredentialUseCase deleteCredentialUseCase,
            UpdateDeviceTypeUseCase updateDeviceTypeUseCase,
            SchedulersFacade schedulersFacade) {
        this.mRetrieveCredentialsUseCase = retrieveCredentialsUseCase;
        this.mDeleteCredentialUseCase = deleteCredentialUseCase;
        this.mUpdateDeviceTypeUseCase = updateDeviceTypeUseCase;

        this.mSchedulersFacade = schedulersFacade;
    }

    public LiveData<String> getResourceOwner() {
        return mRownerUuid;
    }

    public LiveData<OcCredential> getCredential() {
        return mCredential;
    }

    public LiveData<Long> getDeletedCredId() {
        return mDeletedCredId;
    }

    public void retrieveCredentials(Device device) {
        mDisposables.add(mRetrieveCredentialsUseCase.execute(device)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .doOnSubscribe(__ -> mProcessing.setValue(true))
                .doFinally(() -> mProcessing.setValue(false))
                .subscribe(
                        credentials -> {
                            for (OcCredential cred : credentials.getCredList()) {
                                mCredential.setValue(cred);
                            }

                            if (!device.hasCREDpermit()
                                    && (device.getDeviceType() == DeviceType.OWNED_BY_OTHER
                                    || device.getDeviceType() == DeviceType.OWNED_BY_OTHER_WITH_PERMITS)) {
                                mDisposables.add(mUpdateDeviceTypeUseCase.execute(device.getDeviceId(),
                                                                                    DeviceType.OWNED_BY_OTHER_WITH_PERMITS,
                                                                                    device.getPermits() | Device.CRED_PERMITS)
                                        .subscribeOn(mSchedulersFacade.io())
                                        .observeOn(mSchedulersFacade.ui())
                                        .subscribe(
                                                () -> {},
                                                throwable -> mError.setValue(new ViewModelError(Error.DB_ERROR, null))
                                        ));
                            }
                        },
                        throwable -> {
                            mError.setValue(new ViewModelError(Error.RETRIEVE_CREDS, null));
                            if (device.hasCREDpermit()) {
                                mDisposables.add(mUpdateDeviceTypeUseCase.execute(device.getDeviceId(),
                                                                                    DeviceType.OWNED_BY_OTHER,
                                                                                    device.getPermits() & ~Device.CRED_PERMITS)
                                        .subscribeOn(mSchedulersFacade.io())
                                        .observeOn(mSchedulersFacade.ui())
                                        .subscribe(
                                                () -> {},
                                                throwable2 -> mError.setValue(new ViewModelError(Error.DB_ERROR, null))
                                        ));
                            }
                        }
                ));
    }

    public void deleteCredential(Device device, long credId) {
        mDisposables.add(mDeleteCredentialUseCase.execute(device, credId)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .doOnSubscribe(__ -> mProcessing.setValue(true))
                .doFinally(() -> mProcessing.setValue(false))
                .subscribe(
                        () -> mDeletedCredId.setValue(credId),
                        throwable -> mError.setValue(new ViewModelError(Error.DELETE, null))
                ));
    }

    public enum Error implements ViewModelErrorType {
        RETRIEVE_CREDS,
        DB_ERROR,
        DELETE
    }
}
