package com.mango.home.domain.usecase.cloud;

import io.reactivex.Single;
import com.mango.home.data.repository.CloudRepository;
import com.mango.home.domain.model.client.SerializableResource;
import com.mango.home.domain.model.devicelist.Device;

import javax.inject.Inject;

public class CloudGetResourceUseCase {
    private final CloudRepository cloudRepository;

    @Inject
    public CloudGetResourceUseCase(CloudRepository cloudRepository) {
        this.cloudRepository = cloudRepository;
    }

    public Single<SerializableResource> execute(Device device, SerializableResource resource) {
        return cloudRepository.retrieveEndpoint()
                .flatMap(endpoint -> cloudRepository.get(endpoint, resource.getUri(), device.getDeviceId()))
                .map(ocRepresentation -> {
                    resource.setProperties(ocRepresentation);
                    return resource;
                });
    }
}
