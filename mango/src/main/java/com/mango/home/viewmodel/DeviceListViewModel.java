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

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.auth0.android.result.Credentials;
import com.mango.home.domain.model.exception.NetworkDisconnectedException;
import com.mango.home.domain.usecase.CloseIotivityUseCase;
import com.mango.home.domain.usecase.GetModeUseCase;
import com.mango.home.domain.usecase.ResetClientModeUseCase;
import com.mango.home.domain.usecase.ResetObtModeUseCase;
import com.mango.home.domain.usecase.SetClientModeUseCase;
import com.mango.home.domain.usecase.SetObtModeUseCase;
import com.mango.home.domain.usecase.login.SaveCredentialsUseCase;
import com.mango.home.domain.usecase.wifi.CheckConnectionUseCase;
import com.mango.home.domain.usecase.InitializeIotivityUseCase;
import com.mango.home.domain.usecase.login.LogoutUseCase;
import com.mango.home.utils.handler.DisplayNotValidCertificateHandler;
import com.mango.home.utils.viewmodel.CommonError;
import com.mango.home.utils.viewmodel.Response;
import com.mango.home.utils.viewmodel.ViewModelError;
import com.mango.home.domain.usecase.GetDeviceIdUseCase;
import com.mango.home.utils.rx.SchedulersFacade;
import com.mango.home.utils.viewmodel.ViewModelErrorType;
import com.mango.home.domain.usecase.cloud.CloudDeregisterUseCase;
import com.mango.home.domain.usecase.cloud.CloudLoginUseCase;
import com.mango.home.domain.usecase.cloud.CloudLogoutUseCase;
import com.mango.home.domain.usecase.cloud.CloudRefreshTokenUseCase;
import com.mango.home.domain.usecase.cloud.CloudRegisterUseCase;
import com.mango.home.domain.usecase.cloud.RetrieveStatusUseCase;
import com.mango.home.domain.usecase.cloud.RetrieveTokenExpiryUseCase;

import javax.inject.Inject;

import androidx.room.EmptyResultSetException;
import io.reactivex.disposables.CompositeDisposable;

public class DeviceListViewModel extends ViewModel {

    private final SaveCredentialsUseCase saveCredentialsUseCase;

    private final SchedulersFacade mSchedulersFacade;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<Boolean> mProcessing = new MutableLiveData<>();
    private final MutableLiveData<ViewModelError> mError = new MutableLiveData<>();

    private final MutableLiveData<Boolean> mAuthenticated = new MutableLiveData<>();

    private Credentials mCredentials;

    private final InitializeIotivityUseCase mInitializeIotivityUseCase;
    private final GetModeUseCase mGetModeUseCase;
    private final CloseIotivityUseCase closeIotivityUseCase;
    private final LogoutUseCase logoutUseCase;
    private final SetClientModeUseCase setClientModeUseCase;
    private final ResetClientModeUseCase resetClientModeUseCase;
    private final SetObtModeUseCase setObtModeUseCase;
    private final ResetObtModeUseCase resetObtModeUseCase;
    private final CheckConnectionUseCase mCheckConnectionUseCase;
    private final GetDeviceIdUseCase mGetDeviceIdUseCase;
    private final RetrieveStatusUseCase retrieveStatusUseCase;
    private final CloudRegisterUseCase cloudRegisterUseCase;
    private final CloudDeregisterUseCase cloudDeregisterUseCase;
    private final CloudLoginUseCase cloudLoginUseCase;
    private final CloudLogoutUseCase cloudLogoutUseCase;
    private final CloudRefreshTokenUseCase refreshTokenUseCase;
    private final RetrieveTokenExpiryUseCase retrieveTokenExpiryUseCase;

    private final MutableLiveData<Boolean> mInit = new MutableLiveData<>();
    private final MutableLiveData<String> mMode = new MutableLiveData<>();
    private final MutableLiveData<Response<Void>> clientModeResponse = new MutableLiveData<>();
    private final MutableLiveData<Response<Void>> obtModeResponse = new MutableLiveData<>();
    private final MutableLiveData<Response<Void>> logoutResponse = new MutableLiveData<>();
    private final MutableLiveData<Response<Boolean>> connectedResponse = new MutableLiveData<>();
    private final MutableLiveData<String> mDeviceId = new MutableLiveData<>();
    private final MutableLiveData<Response<Integer>> statusResponse = new MutableLiveData<>();

    @Inject
    DeviceListViewModel(
            InitializeIotivityUseCase initializeIotivityUseCase,
            GetModeUseCase getModeUseCase,
            CloseIotivityUseCase closeIotivityUseCase,
            LogoutUseCase logoutUseCase,
            SetClientModeUseCase setClientModeUseCase,
            ResetClientModeUseCase resetClientModeUseCase,
            SetObtModeUseCase setObtModeUseCase,
            ResetObtModeUseCase resetObtModeUseCase,
            CheckConnectionUseCase checkConnectionUseCase,
            GetDeviceIdUseCase getDeviceIdUseCase,
            RetrieveStatusUseCase retrieveStatusUseCase,
            CloudRegisterUseCase cloudRegisterUseCase,
            CloudDeregisterUseCase cloudDeregisterUseCase,
            CloudLoginUseCase cloudLoginUseCase,
            CloudLogoutUseCase cloudLogoutUseCase,
            CloudRefreshTokenUseCase refreshTokenUseCase,
            RetrieveTokenExpiryUseCase retrieveTokenExpiryUseCase,
            SchedulersFacade schedulersFacade,
            SaveCredentialsUseCase saveCredentialsUseCase) {
        this.mInitializeIotivityUseCase = initializeIotivityUseCase;
        this.mGetModeUseCase = getModeUseCase;
        this.closeIotivityUseCase = closeIotivityUseCase;
        this.logoutUseCase = logoutUseCase;
        this.setClientModeUseCase = setClientModeUseCase;
        this.resetClientModeUseCase = resetClientModeUseCase;
        this.setObtModeUseCase = setObtModeUseCase;
        this.resetObtModeUseCase = resetObtModeUseCase;
        this.mCheckConnectionUseCase = checkConnectionUseCase;
        this.mGetDeviceIdUseCase = getDeviceIdUseCase;
        this.retrieveStatusUseCase = retrieveStatusUseCase;
        this.cloudRegisterUseCase = cloudRegisterUseCase;
        this.cloudDeregisterUseCase = cloudDeregisterUseCase;
        this.cloudLoginUseCase = cloudLoginUseCase;
        this.cloudLogoutUseCase = cloudLogoutUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.retrieveTokenExpiryUseCase = retrieveTokenExpiryUseCase;
        this.mSchedulersFacade = schedulersFacade;
        this.saveCredentialsUseCase = saveCredentialsUseCase;
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }

    public LiveData<ViewModelError> getError() {
        return mError;
    }

    public LiveData<Boolean> getInit() {
        return mInit;
    }

    public LiveData<String> getMode() {
        return mMode;
    }

    public LiveData<Response<Void>> getClientModeResponse() {
        return clientModeResponse;
    }

    public LiveData<Response<Void>> getObtModeResponse() {
        return obtModeResponse;
    }

    public LiveData<Response<Void>> getLogoutResponse() {
        return logoutResponse;
    }

    public LiveData<Response<Boolean>> getConnectedResponse() {
        return connectedResponse;
    }

    public LiveData<String> getDeviceId() {
        return mDeviceId;
    }

    public LiveData<Response<Integer>> getStatusResponse() {
        return statusResponse;
    }

    public void initializeIotivityStack(Context context, DisplayNotValidCertificateHandler displayNotValidCertificateHandler) {
        disposables.add(mInitializeIotivityUseCase.execute(context, displayNotValidCertificateHandler)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .subscribe(
                        () ->  {
                            mInit.setValue(true);
                            disposables.add(mGetModeUseCase.execute()
                                    .subscribeOn(mSchedulersFacade.io())
                                    .observeOn(mSchedulersFacade.ui())
                                    .subscribe(
                                        mode -> mMode.setValue(mode),
                                        throwable -> mError.setValue(new ViewModelError(Error.GET_MODE, throwable.getMessage()))
                                    ));
                        },
                        throwable -> {
                            // mError.setValue()
                            mInit.setValue(false);
                        }
                ));
    }

    public void retrieveCloudStatus() {
        disposables.add(retrieveStatusUseCase.execute()
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .subscribe(
                        status -> statusResponse.setValue(Response.success(status)),
                        throwable -> statusResponse.setValue(Response.error(throwable))
                ));
    }

    public void cloudRegister() {
        disposables.add(cloudRegisterUseCase.execute()
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .subscribe(
                        status -> statusResponse.setValue(Response.success(status)),
                        throwable -> statusResponse.setValue(Response.error(throwable))
                ));
    }

    public void cloudDeregister() {
        disposables.add(cloudDeregisterUseCase.execute()
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .subscribe(
                        status -> statusResponse.setValue(Response.success(status)),
                        throwable -> statusResponse.setValue(Response.error(throwable))
                ));
    }

    public void cloudLogin() {
        disposables.add(cloudLoginUseCase.execute()
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .subscribe(
                        status -> statusResponse.setValue(Response.success(status)),
                        throwable -> statusResponse.setValue(Response.error(throwable))
                ));
    }

    public void cloudLogout() {
        disposables.add(cloudLogoutUseCase.execute()
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .subscribe(
                        status -> statusResponse.setValue(Response.success(status)),
                        throwable -> statusResponse.setValue(Response.error(throwable))
                ));
    }

    public void retrieveTokenExpiry() {
        disposables.add(retrieveTokenExpiryUseCase.execute()
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .subscribe(
                        () -> {},
                        throwable -> {}
                ));
    }

    public void refreshToken() {
        disposables.add(refreshTokenUseCase.execute()
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .subscribe(
                        status -> statusResponse.setValue(Response.success(status)),
                        throwable -> statusResponse.setValue(Response.error(throwable))
                ));
    }

    public void closeIotivityStack() {
        closeIotivityUseCase.execute();
    }

    public void logout() {
        disposables.add(logoutUseCase.execute()
                .observeOn(mSchedulersFacade.ui())
                .subscribe(
                        (success) -> logoutResponse.setValue(Response.success(null)),
                        throwable -> logoutResponse.setValue(Response.error(throwable))
                ));
    }

    public void setClientMode() {
        disposables.add(setClientModeUseCase.execute()
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .doOnSubscribe(__ -> clientModeResponse.setValue(Response.loading()))
                .subscribe(
                        () -> {
                            clientModeResponse.setValue(Response.success(null));
                            disposables.add(mGetModeUseCase.execute()
                                    .subscribeOn(mSchedulersFacade.io())
                                    .observeOn(mSchedulersFacade.ui())
                                    .subscribe(
                                            mode -> mMode.setValue(mode),
                                            throwable -> mError.setValue(new ViewModelError(Error.GET_MODE, throwable.getMessage()))
                                    ));
                        },
                        throwable -> clientModeResponse.setValue(Response.error(throwable))
                ));
    }

    public void resetClientMode() {
        disposables.add(resetClientModeUseCase.execute()
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .doOnSubscribe(__ -> clientModeResponse.setValue(Response.loading()))
                .subscribe(
                        () -> {
                            clientModeResponse.setValue(Response.success(null));
                            disposables.add(mGetModeUseCase.execute()
                                    .subscribeOn(mSchedulersFacade.io())
                                    .observeOn(mSchedulersFacade.ui())
                                    .subscribe(
                                            mode -> mMode.setValue(mode),
                                            throwable -> mError.setValue(new ViewModelError(Error.GET_MODE, throwable.getMessage()))
                                    ));
                        },
                        throwable -> clientModeResponse.setValue(Response.error(throwable))
                ));
    }

    public void setObtMode() {
        disposables.add(setObtModeUseCase.execute()
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .doOnSubscribe(__ -> obtModeResponse.setValue(Response.loading()))
                .subscribe(
                        () -> {
                            obtModeResponse.setValue(Response.success(null));
                            disposables.add(mGetModeUseCase.execute()
                                    .subscribeOn(mSchedulersFacade.io())
                                    .observeOn(mSchedulersFacade.ui())
                                    .subscribe(
                                            mode -> mMode.setValue(mode),
                                            throwable -> mError.setValue(new ViewModelError(Error.GET_MODE, throwable.getMessage()))
                                    ));
                        },
                        throwable -> obtModeResponse.setValue(Response.error(throwable))
                ));
    }

    public void resetObtMode() {
        disposables.add(resetObtModeUseCase.execute()
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .doOnSubscribe(__ -> obtModeResponse.setValue(Response.loading()))
                .subscribe(
                        () -> {
                            obtModeResponse.setValue(Response.success(null));
                            disposables.add(mGetModeUseCase.execute()
                                    .subscribeOn(mSchedulersFacade.io())
                                    .observeOn(mSchedulersFacade.ui())
                                    .subscribe(
                                            mode -> mMode.setValue(mode),
                                            throwable -> mError.setValue(new ViewModelError(Error.GET_MODE, throwable.getMessage()))
                                    ));
                        },
                        throwable -> obtModeResponse.setValue(Response.error(throwable))
                ));
    }

    public void checkIfIsConnectedToWifi() {
        disposables.add(mCheckConnectionUseCase.execute()
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .subscribe(
                        isConnected -> connectedResponse.setValue(Response.success(isConnected)),
                        throwable -> {
                            if (throwable instanceof NetworkDisconnectedException) {
                                mError.setValue(new ViewModelError(CommonError.NETWORK_DISCONNECTED, null));
                            } else {
                                connectedResponse.setValue(Response.error(throwable));
                            }
                        }
                ));
    }

    public void retrieveDeviceId() {
        disposables.add(mGetDeviceIdUseCase.execute()
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .subscribe(
                        mDeviceId::setValue,
                        throwable -> {}
                ));
    }

    public LiveData<Boolean> isProcessing() {
        return mProcessing;
    }

    public LiveData<Boolean> isAuthenticated() {
        return mAuthenticated;
    }

    public void authenticate(@NonNull Credentials credentials) {
        this.setCredentials(credentials);
        disposables.add( saveCredentialsUseCase.execute(credentials)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .subscribe(
                        mAuthenticated::setValue,
                        throwable -> {
                            if (throwable instanceof EmptyResultSetException) {
                                mAuthenticated.setValue(false);
                            } else {
                                mError.setValue(
                                        new ViewModelError(
                                                LoginViewModel.Error.AUTHENTICATE,
                                                throwable.getLocalizedMessage()
                                        )
                                );
                            }
                        }
                ));
    }

    private void setCredentials(Credentials credentials) {
        this.mCredentials = credentials;
    }

    public Credentials getCredentials() {
        return this.mCredentials;
    }

    public enum Error implements ViewModelErrorType {
        GET_MODE,
        AUTHENTICATE
    }
}
