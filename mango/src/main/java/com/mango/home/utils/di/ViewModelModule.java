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

import com.mango.home.utils.viewmodel.ViewModelFactory;
import com.mango.home.viewmodel.AccessControlViewModel;
import com.mango.home.viewmodel.AceViewModel;
import com.mango.home.viewmodel.CertificateViewModel;
import com.mango.home.viewmodel.CloudViewModel;
import com.mango.home.viewmodel.CredViewModel;
import com.mango.home.viewmodel.CredentialsViewModel;
import com.mango.home.viewmodel.DeviceListViewModel;
import com.mango.home.viewmodel.DoxsViewModel;
import com.mango.home.viewmodel.GenericClientViewModel;
import com.mango.home.viewmodel.LinkedRolesViewModel;
import com.mango.home.viewmodel.LoginViewModel;
import com.mango.home.viewmodel.ResourceViewModel;
import com.mango.home.viewmodel.SharedViewModel;
import com.mango.home.viewmodel.SplashViewModel;
import com.mango.home.viewmodel.TrustAnchorViewModel;
import com.mango.home.viewmodel.WlanScanViewModel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel.class)
    abstract ViewModel bindSplashViewModel(SplashViewModel splashViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    abstract ViewModel bindLoginViewModel(LoginViewModel loginViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(WlanScanViewModel.class)
    abstract ViewModel bindWlanScanViewModel(WlanScanViewModel wlanScanViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(DeviceListViewModel.class)
    abstract ViewModel bindDeviceListViewModel(DeviceListViewModel deviceListViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(DoxsViewModel.class)
    abstract ViewModel bindDoxsViewModel(DoxsViewModel doxsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(SharedViewModel.class)
    abstract ViewModel bindSharedViewModel(SharedViewModel sharedViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AccessControlViewModel.class)
    abstract ViewModel bindAccessControlViewModel(AccessControlViewModel accessControlViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(AceViewModel.class)
    abstract ViewModel bindAceViewModel(AceViewModel aceViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CredentialsViewModel.class)
    abstract ViewModel bindCredentialsViewModel(CredentialsViewModel credentialsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CredViewModel.class)
    abstract ViewModel bindCredViewModel(CredViewModel credViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(GenericClientViewModel.class)
    abstract ViewModel bindGenericClientViewModel(GenericClientViewModel genericClientViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(LinkedRolesViewModel.class)
    abstract ViewModel bindLinkedRolesViewModel(LinkedRolesViewModel linkedRolesViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ResourceViewModel.class)
    abstract ViewModel bindResourceViewModel(ResourceViewModel resourceViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);

    @Binds
    @IntoMap
    @ViewModelKey(TrustAnchorViewModel.class)
    abstract ViewModel bindTrustAnchorsViewModel(TrustAnchorViewModel trustAnchorViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CertificateViewModel.class)
    abstract ViewModel bindCertificateViewModel(CertificateViewModel certificateViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CloudViewModel.class)
    abstract ViewModel bindCloudViewModel(CloudViewModel cloudViewModel);
}
