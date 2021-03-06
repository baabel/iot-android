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

import com.auth0.android.result.Credentials;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mango.home.utils.viewmodel.ViewModelError;
import com.mango.home.utils.viewmodel.ViewModelErrorType;
import com.mango.home.domain.usecase.login.SaveCredentialsUseCase;
import com.mango.home.utils.rx.SchedulersFacade;

import javax.inject.Inject;

import androidx.room.EmptyResultSetException;
import io.reactivex.disposables.CompositeDisposable;

public class LoginViewModel extends ViewModel {

    private final SaveCredentialsUseCase saveCredentialsUseCase;

    private final SchedulersFacade mSchedulersFacade;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<Boolean> mProcessing = new MutableLiveData<>();
    private final MutableLiveData<ViewModelError> mError = new MutableLiveData<>();

    private final MutableLiveData<Boolean> mAuthenticated = new MutableLiveData<>();

    private Credentials mCredentials;

    @Inject
    LoginViewModel(
            SaveCredentialsUseCase saveCredentialsUseCase,
            SchedulersFacade schedulersFacade) {
        this.saveCredentialsUseCase = saveCredentialsUseCase;
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
        AUTHENTICATE
    }
}
