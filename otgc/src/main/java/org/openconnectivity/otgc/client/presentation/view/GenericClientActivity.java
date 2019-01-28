/*
 * *****************************************************************
 *
 *  Copyright 2018 DEKRA Testing and Certification, S.A.U. All Rights Reserved.
 *
 *  ******************************************************************
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  ******************************************************************
 */
package org.openconnectivity.otgc.client.presentation.view;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.openconnectivity.otgc.R;
import org.openconnectivity.otgc.client.domain.model.DynamicUiElement;
import org.openconnectivity.otgc.client.domain.model.SerializableResource;
import org.openconnectivity.otgc.client.presentation.viewmodel.GenericClientViewModel;
import org.openconnectivity.otgc.common.domain.model.OcDevice;
import org.openconnectivity.otgc.common.domain.model.OicPlatform;
import org.openconnectivity.otgc.common.presentation.viewmodel.ViewModelError;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class GenericClientActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Fragment> mDispatchingAndroidInjector;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @BindView(R.id.navigation_drawer) DrawerLayout mDrawerLayout;
    @BindView(R.id.right_drawer) RecyclerView mRecyclerView;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    private GenericClientViewModel mViewModel;

    private DeviceInfoAdapter mAdapter;

    private String mDeviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_client);

        ButterKnife.bind(this);
        initViews();
        initViewModel();

        // Get the intent that started this activity and extract the string
        Intent intent = getIntent();
        mDeviceId = intent.getStringExtra("DeviceId");

        mViewModel.loadDeviceName(mDeviceId);
        mViewModel.loadDeviceInfo(mDeviceId);
        mViewModel.loadPlatformInfo(mDeviceId);
        mViewModel.introspect(mDeviceId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_generic_client, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.buttonInfo:
                onInfoPressed();
                break;
            default:
                onBackPressed();
                break;
        }

        return true;
    }

    @Override
    public DispatchingAndroidInjector<Fragment> supportFragmentInjector() {
        return mDispatchingAndroidInjector;
    }

    private void initViews() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAdapter = new DeviceInfoAdapter(getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(GenericClientViewModel.class);
        mViewModel.isProcessing().observe(this, this::handleProcessing);
        mViewModel.getError().observe(this, this::handleError);

        mViewModel.getDeviceName().observe(this, this::processDeviceName);
        mViewModel.getDeviceInfo().observe(this, this::processDeviceInfo);
        mViewModel.getPlatformInfo().observe(this, this::processPlatformInfo);
        mViewModel.getResources().observe(this, this::processResources);
        mViewModel.getIntrospection().observe(this, this::processIntrospection);
    }

    private void handleProcessing(@NonNull Boolean isProcessing) {
        // TODO:
    }

    private void handleError(@NonNull ViewModelError error) {
        int errorId = 0;
        switch ((GenericClientViewModel.Error)error.getType()) {
            case DEVICE_NAME:
                errorId = R.string.client_cannot_retrieve_device_name;
                break;
            case DEVICE_INFO:
                errorId = R.string.client_cannot_retrieve_device_info;
                break;
            case PLATFORM_INFO:
                errorId = R.string.client_cannot_retrieve_platform_info;
                break;
            case INTROSPECTION:
                mViewModel.findResources(mDeviceId);
                break;
            case FIND_RESOURCES:
                errorId = R.string.client_cannot_introspect_device;
                break;
        }

        if (errorId != 0) {
            Toast.makeText(getApplicationContext(), errorId, Toast.LENGTH_SHORT).show();
        }
    }

    private void processDeviceName(String deviceName) {
        mToolbar.setTitle(deviceName);
    }

    private void processDeviceInfo(OcDevice deviceInfo) {
        if (mToolbar.getTitle() == null || mToolbar.getTitle().toString().isEmpty()) {
            mToolbar.setTitle(deviceInfo.getName().isEmpty() ? getString(R.string.client_title_no_name) : deviceInfo.getName());
        }

        mAdapter.setDeviceInfo(deviceInfo);
    }

    private void processPlatformInfo(OicPlatform platformInfo) {
        mAdapter.setPlatformInfo(platformInfo);
    }

    private void processResources(List<SerializableResource> resources) {
        if (!resources.isEmpty()) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            for (SerializableResource resource : resources) {
                Fragment fragment = new ResourceFragment();
                Bundle args = new Bundle();
                args.putString("deviceId", mDeviceId);
                args.putSerializable("resource", resource);
                fragment.setArguments(args);
                fragmentTransaction.add(R.id.fragment_container, fragment);
            }
            fragmentTransaction.commit();
        } else {
            // TODO:
        }
    }

    private void processIntrospection(List<DynamicUiElement> uiElements) {
        if (!uiElements.isEmpty()) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            for (DynamicUiElement uiElement : uiElements) {
                Fragment fragment = new IntrospectedFragment();
                Bundle args = new Bundle();
                args.putString("deviceId", mDeviceId);
                args.putSerializable("uiInfo", uiElement);
                fragment.setArguments(args);
                fragmentTransaction.add(R.id.fragment_container, fragment);
            }
            fragmentTransaction.commit();
        } else {
            mViewModel.findResources(mDeviceId);
        }
    }

    private void onInfoPressed() {
        mDrawerLayout.openDrawer(Gravity.END);
    }
}
