package com.policyboss.policybosspro.utils.networkManager.javaBase;

public interface JavaConnectivityObserver {

    enum Status {
        Available,
        Losing,
        Lost,
        Unavailable
    }

    interface Listener {

        void onStatusChanged(Status status);
    }

    void startObserving(Listener listener);

    void stopObserving();
}