package com.mango.home.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mango.home.domain.model.resource.secure.cred.OcCredential;
import com.mango.home.domain.usecase.trustanchor.GetTrustAnchorUseCase;
import com.mango.home.domain.usecase.trustanchor.RemoveTrustAnchorByCredidUseCase;
import com.mango.home.domain.usecase.trustanchor.SaveEndEntityCertificateUseCase;
import com.mango.home.domain.usecase.trustanchor.SaveIntermediateCertificateUseCase;
import com.mango.home.domain.usecase.trustanchor.StoreTrustAnchorUseCase;
import com.mango.home.utils.rx.SchedulersFacade;
import com.mango.home.utils.viewmodel.ViewModelError;
import com.mango.home.utils.viewmodel.ViewModelErrorType;

import java.io.InputStream;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class TrustAnchorViewModel extends ViewModel {

    private CompositeDisposable disposable = new CompositeDisposable();

    private final SchedulersFacade schedulersFacade;

    private final MutableLiveData<Boolean> mProcessing = new MutableLiveData<>();
    private final MutableLiveData<ViewModelError> mError = new MutableLiveData<>();

    // Use cases
    private final StoreTrustAnchorUseCase storeTrustAnchorUseCase;
    private final SaveIntermediateCertificateUseCase saveIntermediateCertificateUseCase;
    private final SaveEndEntityCertificateUseCase saveEndEntityCertificateUseCase;
    private final GetTrustAnchorUseCase getTrustAnchorUseCase;
    private final RemoveTrustAnchorByCredidUseCase removeTrustAnchorByCredidUseCase;

    // Observable values
    private final MutableLiveData<OcCredential> credential = new MutableLiveData<>();
    private final MutableLiveData<Long> deleteCredid = new MutableLiveData<>();

    @Inject
    public TrustAnchorViewModel(SchedulersFacade schedulersFacade,
                                StoreTrustAnchorUseCase storeTrustAnchorUseCase,
                                SaveIntermediateCertificateUseCase saveIntermediateCertificateUseCase,
                                SaveEndEntityCertificateUseCase saveEndEntityCertificateUseCase,
                                GetTrustAnchorUseCase getTrustAnchorUseCase,
                                RemoveTrustAnchorByCredidUseCase removeTrustAnchorByCredidUseCase) {
        this.schedulersFacade = schedulersFacade;
        this.storeTrustAnchorUseCase = storeTrustAnchorUseCase;
        this.saveIntermediateCertificateUseCase = saveIntermediateCertificateUseCase;
        this.saveEndEntityCertificateUseCase = saveEndEntityCertificateUseCase;
        this.getTrustAnchorUseCase = getTrustAnchorUseCase;
        this.removeTrustAnchorByCredidUseCase = removeTrustAnchorByCredidUseCase;
    }

    @Override
    protected void onCleared() {
        disposable.clear();
    }

    public LiveData<Boolean> isProcessing() {
        return mProcessing;
    }

    public LiveData<ViewModelError> getError() {
        return mError;
    }

    public LiveData<OcCredential> getCredential() {
        return credential;
    }

    public LiveData<Long> getDeletedCredid() {
        return deleteCredid;
    }

    public void retrieveCertificates() {
        disposable.add(getTrustAnchorUseCase.execute()
            .subscribeOn(schedulersFacade.io())
            .observeOn(schedulersFacade.ui())
            .subscribe(
                    trustAnchors -> {
                        credential.setValue(null);
                        for (OcCredential trustAnchor : trustAnchors) {
                            credential.setValue(trustAnchor);
                        }
                    },
                    throwable -> {}
            ));
    }

    public void addTrustAnchor(InputStream is) {
        disposable.add(storeTrustAnchorUseCase.execute(is)
            .subscribeOn(schedulersFacade.io())
            .observeOn(schedulersFacade.ui())
            .subscribe(
                    () -> retrieveCertificates(),
                    throwable -> mError.setValue(new ViewModelError(Error.ADD_ROOT_CERT, throwable.getMessage()))
            ));
    }

    public void saveIntermediateCertificate(Integer credid, InputStream is) {
        disposable.add(saveIntermediateCertificateUseCase.execute(credid, is)
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(
                        () -> retrieveCertificates(),
                        throwable -> mError.setValue(new ViewModelError(Error.ADD_ROOT_CERT, throwable.getMessage()))
                ));
    }

    public void saveEndEntityCertificate(InputStream fileIs, InputStream keyIs) {
        disposable.add(saveEndEntityCertificateUseCase.execute(fileIs, keyIs)
                .subscribeOn(schedulersFacade.io())
                .observeOn(schedulersFacade.ui())
                .subscribe(
                        () -> retrieveCertificates(),
                        throwable -> mError.setValue(new ViewModelError(Error.ADD_ROOT_CERT, throwable.getMessage()))
                ));
    }

    public void removeCertificateByCredid(long credid) {
        disposable.add(removeTrustAnchorByCredidUseCase.execute(credid)
            .subscribeOn(schedulersFacade.io())
            .observeOn(schedulersFacade.ui())
            .subscribe(
                    () -> deleteCredid.setValue(credid),
                    throwable -> {}
            ));
    }

    public enum Error implements ViewModelErrorType {
        ADD_ROOT_CERT
    }
}
