package live.lb_trip.localbalancetrip

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import live.lb_trip.core.FacebookAppId
import live.lb_trip.data.di.qualifier.BaseUrl

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @FacebookAppId
    @Provides
    fun provideFacebookAppId(): String = BuildConfig.FACEBOOK_APP_ID

    @Provides
    @BaseUrl
    fun provideBaseUrl(): String = BuildConfig.BASE_URL
}
