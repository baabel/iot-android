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
package com.mango.home.view.login;


import android.app.Dialog;
import android.content.Intent;
import android.widget.Toast;


import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.mango.home.R;
import com.mango.home.view.devicelist.DeviceListActivity;
import com.mango.home.utils.di.Injectable;
import com.mango.home.viewmodel.LoginViewModel;

import javax.inject.Inject;

import butterknife.ButterKnife;

import com.auth0.android.Auth0;
import com.auth0.android.Auth0Exception;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.VoidCallback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;

public class LoginActivity extends AppCompatActivity implements Injectable {
    static final ButterKnife.Setter<View, Boolean> ENABLED =
            (view, value, index) -> view.setEnabled(value);

    private Auth0 auth0;

    public static final String EXTRA_CLEAR_CREDENTIALS = "com.auth0.CLEAR_CREDENTIALS";
    public static final String EXTRA_ACCESS_TOKEN = "com.auth0.ACCESS_TOKEN";
    public static final String EXTRA_ID_TOKEN = "com.auth0.ID_TOKEN";

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    private LoginViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        ButterKnife.bind(this);
        initViews();
        initViewModel();
        //setContentView(R.layout.activity_login);
        //Button loginButton = findViewById(R.id.loginButton);
        //loginButton.setOnClickListener(new View.OnClickListener() {
        //    @Override
         //   public void onClick(View v) {
         //      login();
         //   }
       // });
        auth0 = new Auth0(this);
        auth0.setOIDCConformant(true);
        logout();
        login();
    }

    private void initViews() {

    }

    private void login() {
        WebAuthProvider.login(auth0)
                .withScheme("demo")
                .withAudience(String.format("https://%s/userinfo", getString(R.string.com_auth0_domain)))
                .withScope("openid offline_access")
                .start(this, new AuthCallback() {
                    @Override
                    public void onFailure(@NonNull final Dialog dialog) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dialog.show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(final AuthenticationException exception) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onSuccess(@NonNull final Credentials credentials) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mViewModel.authenticate(credentials);
                            }
                        });
                    }
                });
    }

    private void logout() {
        WebAuthProvider.logout(auth0)
                .withScheme("demo")
                .start(this, new VoidCallback() {
                    @Override
                    public void onSuccess(Void payload) {

                    }

                    @Override
                    public void onFailure(Auth0Exception error) {
                        //Log out canceled, keep the user logged in
                        showNextActivity();
                    }
                });
    }

    private void showNextActivity() {
        Intent intent = new Intent(LoginActivity.this, DeviceListActivity.class);
        startActivity(intent);
        finish();
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(LoginViewModel.class);
        mViewModel.isProcessing().observe(this, this::handleProcessing);
        mViewModel.isAuthenticated().observe(this, this::processAuthenticated);
    }

    private void handleProcessing(@NonNull Boolean isProcessing) {
        // TODO:
    }


    private void processAuthenticated(@NonNull Boolean isAuthenticated) {
        if (isAuthenticated) {
            Intent intent = new Intent(LoginActivity.this, DeviceListActivity.class);
            intent.putExtra(EXTRA_ACCESS_TOKEN, mViewModel.getCredentials().getAccessToken());
            intent.putExtra(EXTRA_ID_TOKEN, mViewModel.getCredentials().getIdToken());
            startActivity(intent);
            finish();
        }
    }
}
