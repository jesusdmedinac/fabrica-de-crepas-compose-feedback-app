import androidx.compose.runtime.Composable
import com.jesusdmedinac.feedbackapp.presentation.ui.FeedbackApp
import example.imageviewer.model.WrappedHttpClient

@Composable
internal fun FeedbackAppWeb() {
    val httpClient: WrappedHttpClient = createWrappedHttpClient()
    FeedbackApp(httpClient)
}