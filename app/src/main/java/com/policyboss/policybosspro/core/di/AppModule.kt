package com.policyboss.policybosspro.core.di

import android.app.Application
import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.policyboss.policybosspro.PolicyBossProApplication
import com.policyboss.policybosspro.facade.PolicyBossPrefsManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Singleton
    @Provides
    fun providePolicyBossPrefsManager(@ApplicationContext context: Context): PolicyBossPrefsManager {
        return PolicyBossPrefsManager(context)
    }


    @Provides
    @Singleton
    fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }

    @Provides
    fun provideApplication(
        application: Application
    ): PolicyBossProApplication {
        return application as PolicyBossProApplication
    }

}