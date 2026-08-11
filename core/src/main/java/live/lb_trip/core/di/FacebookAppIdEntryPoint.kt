package live.lb_trip.core.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import live.lb_trip.core.FacebookAppId

@EntryPoint
@InstallIn(SingletonComponent::class)
interface FacebookAppIdEntryPoint {
    @FacebookAppId
    fun facebookAppId(): String
}
