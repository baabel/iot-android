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

import com.mango.home.domain.model.devicelist.Device;
import com.mango.home.utils.rx.SchedulersFacade;
import com.mango.home.utils.viewmodel.BaseViewModel;
import com.mango.home.utils.viewmodel.ViewModelError;
import com.mango.home.utils.viewmodel.ViewModelErrorType;
import com.mango.home.domain.usecase.credential.ProvisionIdentityCertificateUseCase;
import com.mango.home.domain.usecase.credential.ProvisionRoleCertificateUseCase;

import javax.inject.Inject;

public class CredViewModel extends BaseViewModel {
    private final ProvisionIdentityCertificateUseCase mProvisionIdentityCertificateUseCase;
    private final ProvisionRoleCertificateUseCase mProvisionRoleCertificateUseCase;

    private final SchedulersFacade mSchedulersFacade;

    private final MutableLiveData<Boolean> mSuccess = new MutableLiveData<>();

    @Inject
    CredViewModel(
            ProvisionIdentityCertificateUseCase provisionIdentityCertificateUseCase,
            ProvisionRoleCertificateUseCase provisionRoleCertificateUseCase,
            SchedulersFacade schedulersFacade) {
        this.mProvisionIdentityCertificateUseCase = provisionIdentityCertificateUseCase;
        this.mProvisionRoleCertificateUseCase = provisionRoleCertificateUseCase;

        this.mSchedulersFacade = schedulersFacade;
    }

    public LiveData<Boolean> getSuccess() {
        return mSuccess;
    }

    public void provisionIdentityCertificate(Device device) {
        mDisposables.add(mProvisionIdentityCertificateUseCase.execute(device)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .doOnSubscribe(__ -> mProcessing.setValue(true))
                .doFinally(() -> mProcessing.setValue(false))
                .subscribe(
                        () -> mSuccess.setValue(true),
                        throwable -> {
                            mSuccess.setValue(false);
                            mError.setValue(
                                    new ViewModelError(
                                            Error.PROVISION_IDENTITY_CERT, null));
                        }
                ));
    }

    public void provisionRoleCertificate(Device device, String roleId, String roleAuthority) {
        mDisposables.add(mProvisionRoleCertificateUseCase.execute(device, roleId, roleAuthority)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .doOnSubscribe(__ -> mProcessing.setValue(true))
                .doFinally(() -> mProcessing.setValue(false))
                .subscribe(
                        () -> mSuccess.setValue(true),
                        throwable -> {
                            mSuccess.setValue(false);
                            mError.setValue(
                                    new ViewModelError(
                                            Error.PROVISION_ROLE_CERT, null));
                        }
                ));
    }

    public enum Error implements ViewModelErrorType {
        PROVISION_IDENTITY_CERT,
        PROVISION_ROLE_CERT
    }
}
