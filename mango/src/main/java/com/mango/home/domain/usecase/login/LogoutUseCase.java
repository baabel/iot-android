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
package com.mango.home.domain.usecase.login;

import android.content.Context;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.storage.SecureCredentialsManager;
import com.auth0.android.authentication.storage.SharedPreferencesStorage;

import javax.inject.Inject;

import io.reactivex.Single;


public class LogoutUseCase {

   Context mContext;

   @Inject
    public LogoutUseCase(Context context) {
        this.mContext = context;
    }

    public Single<Boolean> execute() {
        return Single.create(emitter -> {
            Auth0 auth0 = new Auth0(this.mContext);
            auth0.setOIDCConformant(true);
            SecureCredentialsManager credentialsManager = new SecureCredentialsManager(mContext, new AuthenticationAPIClient(auth0), new SharedPreferencesStorage(this.mContext));
            credentialsManager.clearCredentials();
            emitter.onSuccess(true);
        });
    }
}
