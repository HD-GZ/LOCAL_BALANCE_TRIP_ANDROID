package live.lb_trip.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import live.lb_trip.data.repository.ReceiptRepositoryImpl
import live.lb_trip.domain.repository.ReceiptRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ReceiptRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindsReceiptRepository(receiptRepositoryImpl: ReceiptRepositoryImpl): ReceiptRepository
}
