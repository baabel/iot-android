package com.mango.home.domain.usecase.cloud;

import io.reactivex.Single;
import com.mango.home.data.repository.CloudRepository;
import com.mango.home.domain.model.devicelist.Device;
import com.mango.home.domain.model.resource.virtual.p.OcPlatformInfo;
import com.mango.home.utils.constant.OcfResourceUri;

import javax.inject.Inject;

public class CloudRetrievePlatformInfoUseCase {
    private final CloudRepository cloudRepository;

    @Inject
    public CloudRetrievePlatformInfoUseCase(CloudRepository cloudRepository) {
        this.cloudRepository = cloudRepository;
    }

    public Single<OcPlatformInfo> execute(Device device) {
        return cloudRepository.retrieveUri(device.getDeviceId(), OcfResourceUri.PLATFORM_INFO_URI)
                .flatMap(uri -> cloudRepository.retrieveEndpoint()
                        .flatMap(endpoint -> cloudRepository.retrievePlatformInfo(endpoint, uri)));
    }
}
