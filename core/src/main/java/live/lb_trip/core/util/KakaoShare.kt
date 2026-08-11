package live.lb_trip.core.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.util.Log
import com.kakao.sdk.common.util.KakaoCustomTabsClient
import com.kakao.sdk.share.ShareClient
import com.kakao.sdk.share.WebSharerClient
import com.kakao.sdk.template.model.Button
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.FeedTemplate
import com.kakao.sdk.template.model.Link
import live.lb_trip.core.BuildConfig

private const val TAG = "Kakao Share"

fun kakaoShareReportFeed(
    title: String,
    username: String,
    imageUrl: String?,
    shareUrl: String? = BuildConfig.WEB_URL
): FeedTemplate {
    return FeedTemplate(
        content = Content(
            title = title,
            description = "${username}님의 여행 기록을 지금 확인해보세요!",
            imageUrl = imageUrl ?: BuildConfig.WEB_URL,
            link = Link(
                webUrl = shareUrl,
                mobileWebUrl = shareUrl
            )
        ),
        buttons = listOf(
            Button(
                "여행 일지 보기",
                link = Link(
                    webUrl = shareUrl,
                    mobileWebUrl = shareUrl
                )
            )
        )
    )
}

fun kakaoShare(
    context: Context,
    feed: FeedTemplate
): Boolean {
    if (ShareClient.instance.isKakaoTalkSharingAvailable(context)) {
        var success = false

        ShareClient.instance.shareDefault(
            context = context,
            defaultTemplate = feed,
        ) { sharingResult, error ->
            if (error != null) {
                Log.e(TAG, "카카오톡 공유 실패", error)
                success = false
            } else if (sharingResult != null) {
                Log.d(TAG, "카카오톡 공유 성공 ${sharingResult.intent}")
                context.startActivity(sharingResult.intent)
                success = true
            }
        }
        return success
    } else {
        val sharerUrl = WebSharerClient.instance.makeDefaultUrl(feed)

        try {
            KakaoCustomTabsClient.openWithDefault(context, sharerUrl)
            return true
        } catch (e: UnsupportedOperationException) {
            try {
                KakaoCustomTabsClient.open(context, sharerUrl)
                return true
            } catch (e: ActivityNotFoundException) {
                return false
            }
        }
    }
}
