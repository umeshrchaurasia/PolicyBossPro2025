package com.policyboss.policybosspro.utils.networkManager.javaBase;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

public class JavaNetworkConnectivityObserver
        implements JavaConnectivityObserver {

    private final ConnectivityManager connectivityManager;

    private ConnectivityManager.NetworkCallback networkCallback;

    public JavaNetworkConnectivityObserver(
            Context context
    ) {

        connectivityManager =
                (ConnectivityManager)
                        context.getSystemService(
                                Context.CONNECTIVITY_SERVICE
                        );
    }

    @Override
    public void startObserving(
            Listener listener
    ) {

        if (networkCallback != null) {
            return;
        }

        networkCallback =
                new ConnectivityManager.NetworkCallback() {

                    @Override
                    public void onAvailable(Network network) {

                        listener.onStatusChanged(
                                Status.Available
                        );
                    }

                    @Override
                    public void onLosing(
                            Network network,
                            int maxMsToLive
                    ) {

                        listener.onStatusChanged(
                                Status.Losing
                        );
                    }

                    @Override
                    public void onLost(Network network) {

                        listener.onStatusChanged(
                                Status.Lost
                        );
                    }

                    @Override
                    public void onUnavailable() {

                        listener.onStatusChanged(
                                Status.Unavailable
                        );
                    }
                };

        NetworkRequest request =
                new NetworkRequest.Builder()
                        .addCapability(
                                NetworkCapabilities.NET_CAPABILITY_INTERNET
                        )
                        .build();

        connectivityManager.registerNetworkCallback(
                request,
                networkCallback
        );

        listener.onStatusChanged(
                isNetworkAvailable()
                        ? Status.Available
                        : Status.Unavailable
        );
    }

    @Override
    public void stopObserving() {

        if (networkCallback != null) {

            connectivityManager.unregisterNetworkCallback(
                    networkCallback
            );

            networkCallback = null;
        }
    }

    private boolean isNetworkAvailable() {

        Network network =
                connectivityManager.getActiveNetwork();

        if (network == null) {
            return false;
        }

        NetworkCapabilities capabilities =
                connectivityManager.getNetworkCapabilities(
                        network
                );

        return capabilities != null
                &&
                capabilities.hasCapability(
                        NetworkCapabilities.NET_CAPABILITY_INTERNET
                )
                &&
                capabilities.hasCapability(
                        NetworkCapabilities.NET_CAPABILITY_VALIDATED
                );
    }
}