package com.marcosvperboni.bankingapp.di

import com.marcosvperboni.bankingapp.core.coroutines.DefaultDispatcherProvider
import com.marcosvperboni.bankingapp.core.coroutines.DispatcherProvider
import com.marcosvperboni.bankingapp.data.account.AccountRepositoryImpl
import com.marcosvperboni.bankingapp.data.auth.AuthRepositoryImpl
import com.marcosvperboni.bankingapp.data.notification.NotificationRepositoryImpl
import com.marcosvperboni.bankingapp.data.transfer.TransferRepositoryImpl
import com.marcosvperboni.bankingapp.domain.account.AccountRepository
import com.marcosvperboni.bankingapp.domain.auth.AuthRepository
import com.marcosvperboni.bankingapp.domain.notification.NotificationRepository
import com.marcosvperboni.bankingapp.domain.transfer.TransferRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindAccountRepository(impl: AccountRepositoryImpl): AccountRepository

    @Binds
    @Singleton
    abstract fun bindTransferRepository(impl: TransferRepositoryImpl): TransferRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindDispatcherProvider(impl: DefaultDispatcherProvider): DispatcherProvider
}
