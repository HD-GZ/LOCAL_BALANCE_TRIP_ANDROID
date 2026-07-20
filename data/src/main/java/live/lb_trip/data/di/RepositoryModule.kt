package live.lb_trip.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import live.lb_trip.data.repository.AuthRepositoryImpl
import live.lb_trip.data.repository.PropensityRepositoryImpl
import live.lb_trip.data.repository.RecommendationRepositoryImpl
import live.lb_trip.data.repository.SavedCourseRepositoryImpl
import live.lb_trip.data.repository.UserRepositoryImpl
import live.lb_trip.domain.repository.AuthRepository
import live.lb_trip.domain.repository.PropensityRepository
import live.lb_trip.domain.repository.RecommendationRepository
import live.lb_trip.domain.repository.SavedCourseRepository
import live.lb_trip.domain.repository.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindsUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindsAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindsPropensityRepository(propensityRepositoryImpl: PropensityRepositoryImpl): PropensityRepository

    @Binds
    @Singleton
    abstract fun bindsRecommendationRepository(
        recommendationRepositoryImpl: RecommendationRepositoryImpl,
    ): RecommendationRepository

    @Binds
    @Singleton
    abstract fun bindsSavedCourseRepository(
        savedCourseRepositoryImpl: SavedCourseRepositoryImpl,
    ): SavedCourseRepository
}
