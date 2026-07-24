package com.kairos.app.notification

import com.kairos.app.data.auth.UserIdProvider
import com.kairos.app.domain.repository.DailyPlanRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

data class NotificationCopy(
    val title: String,
    val body: String
)

/** Uses the exact same daily plan as the Today screen. */
@Singleton
class DailyNotificationContentProvider @Inject constructor(
    private val userIdProvider: UserIdProvider,
    private val dailyPlanRepository: DailyPlanRepository
) {
    suspend fun dailyMoment(date: LocalDate = LocalDate.now()): NotificationCopy {
        val plan = dailyPlanRepository.getOrCreateDailyPlan(userIdProvider.getUserId(), date)
        val word = plan.word?.item
        return when {
            word != null -> NotificationCopy(
                title = "Today's word: ${word.word}",
                body = word.definition.take(MAX_BODY_LENGTH)
            )
            plan.quote != null -> NotificationCopy(
                title = "A thought for today",
                body = plan.quote.item.content.take(MAX_BODY_LENGTH)
            )
            else -> NotificationCopy(
                title = "Your daily moment is ready",
                body = "Open the app for one useful idea and a short reflection."
            )
        }
    }

    suspend fun eveningReflection(date: LocalDate = LocalDate.now()): NotificationCopy {
        val quote = dailyPlanRepository
            .getOrCreateDailyPlan(userIdProvider.getUserId(), date)
            .quote
            ?.item
        return when {
            quote == null -> NotificationCopy(
                title = "A minute to reflect",
                body = "What is one thing from today you want to remember?"
            )
            quote.reflectionPrompt.isNotBlank() -> NotificationCopy(
                title = "A minute to reflect",
                body = quote.reflectionPrompt.take(MAX_BODY_LENGTH)
            )
            else -> NotificationCopy(
                title = "A minute to reflect",
                body = "How did today's thought show up in your day?"
            )
        }
    }

    private companion object {
        const val MAX_BODY_LENGTH = 180
    }
}
