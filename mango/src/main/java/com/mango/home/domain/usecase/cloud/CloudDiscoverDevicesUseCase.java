package com.mango.home.domain.usecase.cloud;

import com.mango.home.data.repository.CloudRepository;
import com.mango.home.domain.model.devicelist.Device;

import javax.inject.Inject;

import io.reactivex.Observable;

public class CloudDiscoverDevicesUseCase {
    private final CloudRepository cloudRepository;

    @Inject
    public CloudDiscoverDevicesUseCase(CloudRepository cloudRepository) {
        this.cloudRepository = cloudRepository;
    }

    public Observable<Device> execute() {
        return cloudRepository.discoverDevices();
    }
}
