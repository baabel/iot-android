/*
 *  Copyright 2018 DEKRA Testing and Certification, S.A.U. All Rights Reserved.
 *
 *  *****************************************************************
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.mango.home.domain.usecase.trustanchor;

import com.mango.home.data.repository.CmsRepository;
import com.mango.home.data.repository.IORepository;

import java.io.InputStream;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;

public class SaveEndEntityCertificateUseCase {

    private final IORepository ioRepository;
    private final CmsRepository cmsRepository;

    @Inject
    public SaveEndEntityCertificateUseCase(IORepository ioRepository,
                                           CmsRepository cmsRepository) {
        this.ioRepository = ioRepository;
        this.cmsRepository = cmsRepository;
    }

    public Completable execute(InputStream certIs, InputStream keyIs) {
        Single<byte[]> pemCertObservable = ioRepository.getBytesFromFile(certIs);

        Single<byte[]> pemKeyCertObservable = ioRepository.getBytesFromFile(keyIs);

        return pemCertObservable.flatMapCompletable(
                cert -> pemKeyCertObservable.flatMapCompletable(
                        keyCert -> cmsRepository.addEndEntityCertificate(cert, keyCert)
                ));
    }

}