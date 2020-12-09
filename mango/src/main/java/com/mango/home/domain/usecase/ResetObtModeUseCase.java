package com.mango.home.domain.usecase;

import com.mango.home.data.repository.DoxsRepository;
import com.mango.home.data.repository.IotivityRepository;
import com.mango.home.data.repository.PreferencesRepository;
import com.mango.home.data.repository.ProvisioningRepository;
import com.mango.home.utils.constant.OtgcMode;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Completable;

public class ResetObtModeUseCase {

    private final IotivityRepository iotivityRepository;
    private final DoxsRepository doxsRepository;
    private final ProvisioningRepository provisioningRepository;
    private final PreferencesRepository preferencesRepository;

    @Inject
    public ResetObtModeUseCase(IotivityRepository iotivityRepository,
                               DoxsRepository doxsRepository,
                               ProvisioningRepository provisioningRepository,
                               PreferencesRepository preferencesRepository) {
        this.iotivityRepository = iotivityRepository;
        this.doxsRepository = doxsRepository;
        this.provisioningRepository = provisioningRepository;
        this.preferencesRepository = preferencesRepository;
    }

    public Completable execute() {
        return iotivityRepository.scanOwnedDevices()
                .flatMapCompletable(device -> doxsRepository.resetDevice(device.getDeviceId()))
                .delay(preferencesRepository.getRequestsDelay(), TimeUnit.SECONDS)
                .andThen(provisioningRepository.resetSvrDb())
                .andThen(provisioningRepository.doSelfOwnership())
                .andThen(Completable.fromAction(() -> preferencesRepository.setMode(OtgcMode.OBT)));
    }
}
