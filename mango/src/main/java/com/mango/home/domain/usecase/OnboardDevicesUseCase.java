package com.mango.home.domain.usecase;

import com.mango.home.data.repository.DoxsRepository;
import com.mango.home.data.repository.IotivityRepository;
import com.mango.home.data.repository.PreferencesRepository;
import com.mango.home.domain.model.devicelist.Device;
import com.mango.home.utils.constant.OcfOxmType;
import com.mango.home.utils.rx.SchedulersFacade;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Single;

public class OnboardDevicesUseCase {

    /* Repositories */
    private final IotivityRepository iotivityRepository;
    private final DoxsRepository doxsRepository;
    private final PreferencesRepository settingRepository;
    /* Scheduler */
    private final SchedulersFacade schedulersFacade;

    @Inject
    public OnboardDevicesUseCase(IotivityRepository iotivityRepository,
                                 DoxsRepository doxsRepository,
                                 PreferencesRepository settingRepository,
                                 SchedulersFacade schedulersFacade) {
        this.iotivityRepository = iotivityRepository;
        this.doxsRepository = doxsRepository;
        this.settingRepository = settingRepository;

        this.schedulersFacade = schedulersFacade;
    }

    public Single<Device> execute(Device device, List<OcfOxmType> oxms) {
        int it1 = oxms.size() - 1;
        if (it1 < 0) {
            return null;
        }

        return executeOnboard(device, oxms.get(it1))
                .onErrorResumeNext(error1 -> {
                    int it2 = it1 - 1;
                    if (it2 < 0) {
                        return null;
                    }
                    return iotivityRepository.scanUnownedDevices()
                            .filter(device1 -> device.getDeviceId().equals(device1.getDeviceId()))
                            .firstOrError()
                            .flatMap(device1 ->
                                    executeOnboard(device1, oxms.get(it2))
                                            .onErrorResumeNext(error -> {
                                                int it3 = it1 - 2;
                                                if (it3 < 0) {
                                                    return null;
                                                }
                                                return iotivityRepository.scanUnownedDevices()
                                                        .filter(device2 -> device.getDeviceId().equals(device2.getDeviceId()))
                                                        .firstOrError()
                                                        .flatMap(device2 -> executeOnboard(device2, oxms.get(it3)));
                                            })
                            );
                });
    }

    private Single<Device> executeOnboard(Device deviceToOnboard, OcfOxmType oxm) {
        int delay = settingRepository.getRequestsDelay();

        final Single<Device> getUpdatedOcSecureResource = iotivityRepository.scanOwnedDevices()
                .filter(device -> deviceToOnboard.getDeviceId().equals(device.getDeviceId())
                        || deviceToOnboard.equalsHosts(device))
                .singleOrError();

        return doxsRepository.doOwnershipTransfer(deviceToOnboard.getDeviceId(), oxm)
                .delay(2 * delay, TimeUnit.SECONDS, schedulersFacade.ui())
                .andThen(getUpdatedOcSecureResource
                        .onErrorResumeNext(error -> getUpdatedOcSecureResource
                                .retry(2)
                                .onErrorResumeNext(Single.error(error)))
                );
    }
}
