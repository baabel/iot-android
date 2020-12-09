package com.mango.home.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mango.home.domain.usecase.trustanchor.SaveEndEntityCertificateUseCase;
import com.mango.home.domain.usecase.trustanchor.SaveIntermediateCertificateUseCase;
import com.mango.home.domain.usecase.trustanchor.StoreTrustAnchorUseCase;
import com.mango.home.utils.rx.SchedulersFacade;
import com.mango.home.utils.viewmodel.ViewModelError;
import com.mango.home.utils.viewmodel.ViewModelErrorType;

import java.io.InputStream;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class CertificateViewModel extends ViewModel {

    private final SchedulersFacade mSchedulersFacade;

    private final CompositeDisposable mDisposables = new CompositeDisposable();

    private final StoreTrustAnchorUseCase storeTrustAnchorUseCase;
    private final SaveIntermediateCertificateUseCase saveIntermediateCertificateUseCase;
    private final SaveEndEntityCertificateUseCase saveEndEntityCertificateUseCase;

    private final MutableLiveData<Boolean> mProcessing = new MutableLiveData<>();
    private final MutableLiveData<ViewModelError> mError = new MutableLiveData<>();

    private final MutableLiveData<Boolean> mSuccess = new MutableLiveData<>();

    @Inject
    CertificateViewModel(SchedulersFacade schedulersFacade,
                         StoreTrustAnchorUseCase storeTrustAnchorUseCase,
                         SaveIntermediateCertificateUseCase saveIntermediateCertificateUseCase,
                         SaveEndEntityCertificateUseCase saveEndEntityCertificateUseCase) {
        this.mSchedulersFacade = schedulersFacade;
        this.storeTrustAnchorUseCase = storeTrustAnchorUseCase;
        this.saveIntermediateCertificateUseCase = saveIntermediateCertificateUseCase;
        this.saveEndEntityCertificateUseCase = saveEndEntityCertificateUseCase;
    }

    @Override
    protected void onCleared() {
        mDisposables.clear();
    }

    public LiveData<Boolean> isProcessing() {
        return mProcessing;
    }

    public LiveData<ViewModelError> getError() {
        return mError;
    }

    public LiveData<Boolean> getSuccess() {
        return mSuccess;
    }

    public void saveTrustAnchor(InputStream is) {
        mDisposables.add(storeTrustAnchorUseCase.execute(is)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .subscribe(
                        () -> mSuccess.setValue(true),
                        throwable -> {
                            mSuccess.setValue(false);
                            mError.setValue(new ViewModelError(Error.ROOT_CERTIFICATE, null));
                        }
                ));
    }

    public void saveIntermediateCertificate(int credid, InputStream is) {
        mDisposables.add(saveIntermediateCertificateUseCase.execute(credid, is)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .subscribe(
                        () -> mSuccess.setValue(true),
                        throwable -> {
                            mSuccess.setValue(false);
                            mError.setValue(new ViewModelError(Error.INTERMEDIATE_CERTIFICATE, null));
                        }
                ));
    }

    public void saveEndEntityCertificate(InputStream fileIs, InputStream keyIs) {
        mDisposables.add(saveEndEntityCertificateUseCase.execute(fileIs, keyIs)
                .subscribeOn(mSchedulersFacade.io())
                .observeOn(mSchedulersFacade.ui())
                .subscribe(
                        () -> mSuccess.setValue(true),
                        throwable -> {
                            mSuccess.setValue(false);
                            mError.setValue(new ViewModelError(Error.END_ENTITY_CERTIFICATE, null));
                        }
                ));
    }

    public enum Error implements ViewModelErrorType {
        ROOT_CERTIFICATE,
        INTERMEDIATE_CERTIFICATE,
        END_ENTITY_CERTIFICATE
    }

}
