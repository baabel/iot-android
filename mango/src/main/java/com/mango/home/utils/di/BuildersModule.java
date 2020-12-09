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
package com.mango.home.utils.di;

import com.mango.home.view.accesscontrol.AccessControlActivity;
import com.mango.home.view.accesscontrol.AceActivity;
import com.mango.home.view.client.ClientBuildersModule;
import com.mango.home.view.client.GenericClientActivity;
import com.mango.home.view.cloud.CloudActivity;
import com.mango.home.view.credential.CredActivity;
import com.mango.home.view.credential.CredentialsActivity;
import com.mango.home.view.devicelist.DeviceListBuildersModule;
import com.mango.home.view.devicelist.DeviceListActivity;
import com.mango.home.view.link.LinkedRolesActivity;
import com.mango.home.view.login.LoginActivity;
import com.mango.home.view.splash.SplashActivity;
import com.mango.home.view.trustanchor.CertificateActivity;
import com.mango.home.view.trustanchor.TrustAnchorActivity;
import com.mango.home.view.wlanscan.WlanScanActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public interface BuildersModule {

    @ContributesAndroidInjector
    abstract SplashActivity bindSplashActivity();

    @ContributesAndroidInjector
    abstract LoginActivity bindLoginActivity();

    @ContributesAndroidInjector
    abstract WlanScanActivity bindWlanScanActivity();

    @ContributesAndroidInjector(modules = DeviceListBuildersModule.class)
    abstract DeviceListActivity bindDevicesActivity();

    @ContributesAndroidInjector
    abstract AccessControlActivity bindAccessControlActivity();

    @ContributesAndroidInjector
    abstract AceActivity bindAceActivity();

    @ContributesAndroidInjector
    abstract CredentialsActivity bindCredentialsActivity();

    @ContributesAndroidInjector
    abstract CredActivity bindCredActivity();

    @ContributesAndroidInjector(modules = ClientBuildersModule.class)
    abstract GenericClientActivity bindGenericClientActivity();

    @ContributesAndroidInjector
    abstract LinkedRolesActivity bindLinkedRolesActivity();

    @ContributesAndroidInjector
    abstract TrustAnchorActivity bindTrustAnchorActivity();

    @ContributesAndroidInjector
    abstract CertificateActivity bindCertificateActivity();

    @ContributesAndroidInjector
    abstract CloudActivity bindCloudActivity();
}
