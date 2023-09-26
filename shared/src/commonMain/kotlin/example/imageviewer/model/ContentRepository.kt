package example.imageviewer.model

import com.jesusdmedinac.feedbackapp.data.model.Answer
import com.jesusdmedinac.feedbackapp.data.model.AnswerResponse
import com.jesusdmedinac.feedbackapp.data.model.QuestionResponse

interface ContentRepository<T> {
    suspend fun loadContent(url: String): T
}

interface WrappedHttpClient {
    suspend fun getAsBytes(urlString: String): ByteArray
    suspend fun get(urlString: String): QuestionResponse
    suspend fun post(urlString: String, answer: Answer): AnswerResponse
}

fun createNetworkRepository(ktorClient: WrappedHttpClient) = object : ContentRepository<ByteArray> {
    override suspend fun loadContent(url: String): ByteArray =
        ktorClient.getAsBytes(url)
//        ktorClient.get(urlString = url).readBytes()
}

fun <A, B> ContentRepository<A>.adapter(transform: (A) -> B): ContentRepository<B> {
    val origin = this
    return object : ContentRepository<B> {
        override suspend fun loadContent(url: String): B {
            return transform(origin.loadContent(url))
        }
    }
}
