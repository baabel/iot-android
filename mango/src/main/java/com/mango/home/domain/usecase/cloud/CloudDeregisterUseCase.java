package com.mango.home.domain.usecase.cloud;

import com.mango.home.data.repository.CloudRepository;

import javax.inject.Inject;

import io.reactivex.Single;

public class CloudDeregisterUseCase {
    private final CloudRepository cloudRepository;

    @Inject
    public CloudDeregisterUseCase(CloudRepository cloudRepository) {
        this.cloudRepository = cloudRepository;
    }

    public Single<Integer> execute() {
        return cloudRepository.deregister()
                .andThen(cloudRepository.retrieveState());
    }
}
