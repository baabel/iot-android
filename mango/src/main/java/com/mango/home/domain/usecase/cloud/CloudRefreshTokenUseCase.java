package com.mango.home.domain.usecase.cloud;

import com.mango.home.data.repository.CloudRepository;

import javax.inject.Inject;

import io.reactivex.Single;

public class CloudRefreshTokenUseCase {
    private final CloudRepository cloudRepository;

    @Inject
    public CloudRefreshTokenUseCase(CloudRepository cloudRepository) {
        this.cloudRepository = cloudRepository;
    }

    public Single<Integer> execute() {
        return cloudRepository.refreshToken()
                .andThen(cloudRepository.retrieveState());
    }
}
