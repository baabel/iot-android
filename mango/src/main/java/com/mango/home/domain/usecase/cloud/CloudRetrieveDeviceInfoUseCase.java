package com.mango.home.domain.usecase.cloud;

import io.reactivex.Single;
import com.mango.home.data.repository.CloudRepository;
import com.mango.home.domain.model.devicelist.Device;
import com.mango.home.domain.model.resource.virtual.d.OcDeviceInfo;
import com.mango.home.utils.constant.OcfResourceUri;

import javax.inject.Inject;

public class CloudRetrieveDeviceInfoUseCase {
    private final CloudRepository cloudRepository;

    @Inject
    public CloudRetrieveDeviceInfoUseCase(CloudRepository cloudRepository) {
        this.cloudRepository = cloudRepository;
    }

    public Single<OcDeviceInfo> execute(Device device) {
        return cloudRepository.retrieveUri(device.getDeviceId(), OcfResourceUri.DEVICE_INFO_URI)
                .flatMap(uri -> cloudRepository.retrieveEndpoint()
                        .flatMap(endpoint -> cloudRepository.retrieveDeviceInfo(endpoint, uri)));
    }
}
