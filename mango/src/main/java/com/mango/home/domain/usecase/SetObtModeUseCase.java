package com.mango.home.domain.usecase;

import com.mango.home.data.repository.PreferencesRepository;
import com.mango.home.data.repository.ProvisioningRepository;
import com.mango.home.utils.constant.OtgcMode;

import javax.inject.Inject;

import io.reactivex.Completable;

public class SetObtModeUseCase {

    private final ProvisioningRepository provisioningRepository;
    private final PreferencesRepository preferencesRepository;

    @Inject
    public SetObtModeUseCase(ProvisioningRepository provisioningRepository,
                             PreferencesRepository preferencesRepository) {
        this.provisioningRepository = provisioningRepository;
        this.preferencesRepository = preferencesRepository;
    }

    public Completable execute() {
        return provisioningRepository.resetSvrDb()
                .andThen(provisioningRepository.doSelfOwnership())
                .andThen(Completable.fromAction(() -> preferencesRepository.setMode(OtgcMode.OBT)));
    }
}
