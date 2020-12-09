package com.mango.home.domain.usecase.cloud;

import com.mango.home.data.repository.CloudRepository;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;

public class RetrieveTokenExpiryUseCase {
    private final CloudRepository cloudRepository;

    @Inject
    public RetrieveTokenExpiryUseCase(CloudRepository cloudRepository) {
        this.cloudRepository = cloudRepository;
    }

    public Completable execute() {
        return cloudRepository.retrieveTokenExpiry();
    }
}
