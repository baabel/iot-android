package com.mango.home.domain.usecase.cloud;

import io.reactivex.Single;
import com.mango.home.data.repository.CloudRepository;
import com.mango.home.domain.model.devicelist.Device;
import com.mango.home.domain.model.devicelist.DeviceRole;

import javax.inject.Inject;

public class CloudRetrieveDeviceRoleUseCase {
    private final CloudRepository cloudRepository;

    @Inject
    public CloudRetrieveDeviceRoleUseCase(CloudRepository cloudRepository) {
        this.cloudRepository = cloudRepository;
    }

    public Single<DeviceRole> execute(Device device) {
        return cloudRepository.getResources(device.getDeviceId())
                .map(ocRes -> {
                    DeviceRole deviceRole = DeviceRole.CLIENT;
                    for (String resource : ocRes) {
                        if (!resource.endsWith("/oic/cloud/s")
                                && !resource.endsWith("/oic/d")
                                && !resource.endsWith("/oic/p")) {
                            deviceRole = DeviceRole.SERVER;
                        }

                        if (deviceRole.equals(DeviceRole.SERVER))
                            break;
                    }
                    return deviceRole;
                });
    }
}
