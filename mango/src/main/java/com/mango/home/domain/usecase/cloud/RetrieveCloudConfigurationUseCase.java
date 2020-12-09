package com.mango.home.domain.usecase.cloud;

import com.mango.home.data.repository.CloudRepository;
import com.mango.home.domain.model.resource.cloud.OcCloudConfiguration;

import javax.inject.Inject;

import io.reactivex.Single;

public class RetrieveCloudConfigurationUseCase {

    private final CloudRepository cloudRepository;

    @Inject
    public RetrieveCloudConfigurationUseCase(CloudRepository cloudRepository) {
        this.cloudRepository = cloudRepository;
    }

    public Single<OcCloudConfiguration> execute() {
        return cloudRepository.retrieveCloudConfiguration();
    }
}
