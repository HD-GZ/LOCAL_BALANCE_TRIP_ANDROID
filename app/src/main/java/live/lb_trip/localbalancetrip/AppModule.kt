package live.lb_trip.localbalancetrip

import android.content.Context
import androidx.core.os.ConfigurationCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import live.lb_trip.core.FacebookAppId
import live.lb_trip.data.di.qualifier.BaseUrl
import live.lb_trip.data.di.qualifier.Language

private val supportedLocales = arrayOf("ko", "ko-KR", "en", "en-US", "en-GB")
private const val DEFAULT_LANGUAGE = "en"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @FacebookAppId
    @Provides
    fun provideFacebookAppId(): String = BuildConfig.FACEBOOK_APP_ID

    @Provides
    @BaseUrl
    fun provideBaseUrl(): String = BuildConfig.BASE_URL

    @Provides
    @Language
    fun provideLanguage(
        @ApplicationContext context: Context,
    ): String {
        val locales = ConfigurationCompat.getLocales(context.resources.configuration)
        val locale = locales.getFirstMatch(supportedLocales) ?: return DEFAULT_LANGUAGE

        val languageTag = "${locale.language}-${locale.country}"
        return when {
            supportedLocales.contains(languageTag) -> languageTag
            supportedLocales.contains(locale.language) -> locale.language
            else -> DEFAULT_LANGUAGE
        }
    }
}
