package com.kairos.app.di

import com.kairos.app.data.repository.AchievementRepositoryImpl
import com.kairos.app.data.repository.CollaborativeMessageRepositoryImpl
import com.kairos.app.data.repository.DailyRitualRepositoryImpl
import com.kairos.app.data.repository.DeepDiveRepositoryImpl
import com.kairos.app.data.repository.EvidenceRepositoryImpl
import com.kairos.app.data.repository.FutureMessageReplyRepositoryImpl
import com.kairos.app.data.repository.FutureMessageRepositoryImpl
import com.kairos.app.data.repository.GamificationRepositoryImpl
import com.kairos.app.data.repository.JournalRepositoryImpl
import com.kairos.app.data.repository.MicroEntryRepositoryImpl
import com.kairos.app.data.repository.MonthlyLetterRepositoryImpl
import com.kairos.app.data.repository.OnboardingRepositoryImpl
import com.kairos.app.data.repository.ProfileRepositoryImpl
import com.kairos.app.data.repository.SocialRepositoryImpl
import com.kairos.app.data.repository.SoulLayerRepositoryImpl
import com.kairos.app.data.repository.TodayProgressRepositoryImpl
import com.kairos.app.data.repository.VocabularyRepositoryImpl
import com.kairos.app.data.repository.WeeklyDigestRepositoryImpl
import com.kairos.app.data.repository.WisdomCollectionRepositoryImpl
import com.kairos.app.data.repository.WisdomLibraryRepositoryImpl
import com.kairos.app.data.repository.YearlyWrappedRepositoryImpl
import com.kairos.app.domain.repository.AchievementRepository
import com.kairos.app.domain.repository.CollaborativeMessageRepository
import com.kairos.app.domain.repository.DailyRitualRepository
import com.kairos.app.domain.repository.DeepDiveRepository
import com.kairos.app.domain.repository.EvidenceRepository
import com.kairos.app.domain.repository.FutureMessageReplyRepository
import com.kairos.app.domain.repository.FutureMessageRepository
import com.kairos.app.domain.repository.GamificationRepository
import com.kairos.app.domain.repository.JournalRepository
import com.kairos.app.domain.repository.MicroEntryRepository
import com.kairos.app.domain.repository.MonthlyLetterRepository
import com.kairos.app.domain.repository.OnboardingRepository
import com.kairos.app.domain.repository.ProfileRepository
import com.kairos.app.domain.repository.SocialRepository
import com.kairos.app.domain.repository.SoulLayerRepository
import com.kairos.app.domain.repository.TodayProgressRepository
import com.kairos.app.domain.repository.VocabularyRepository
import com.kairos.app.domain.repository.WeeklyDigestRepository
import com.kairos.app.domain.repository.WisdomCollectionRepository
import com.kairos.app.domain.repository.WisdomLibraryRepository
import com.kairos.app.domain.repository.YearlyWrappedRepository
import com.kairos.app.domain.summary.WeeklySummaryEngine
import com.kairos.app.domain.summary.WeeklySummaryEngineImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides repository bindings.
 * Uses @Binds to connect interface implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {


    @Binds
    @Singleton
    abstract fun bindAchievementRepository(
        impl: AchievementRepositoryImpl
    ): AchievementRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        impl: ProfileRepositoryImpl
    ): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindVocabularyRepository(
        impl: VocabularyRepositoryImpl
    ): VocabularyRepository

    @Binds
    @Singleton
    abstract fun bindJournalRepository(
        impl: JournalRepositoryImpl
    ): JournalRepository

    @Binds
    @Singleton
    abstract fun bindGamificationRepository(
        impl: GamificationRepositoryImpl
    ): GamificationRepository

    @Binds
    @Singleton
    abstract fun bindWeeklyDigestRepository(
        impl: WeeklyDigestRepositoryImpl
    ): WeeklyDigestRepository

    @Binds
    @Singleton
    abstract fun bindWeeklySummaryEngine(
        impl: WeeklySummaryEngineImpl
    ): WeeklySummaryEngine

    @Binds
    @Singleton
    abstract fun bindCollaborativeMessageRepository(
        impl: CollaborativeMessageRepositoryImpl
    ): CollaborativeMessageRepository

    @Binds
    @Singleton
    abstract fun bindYearlyWrappedRepository(
        impl: YearlyWrappedRepositoryImpl
    ): YearlyWrappedRepository

    @Binds
    @Singleton
    abstract fun bindSoulLayerRepository(
        impl: SoulLayerRepositoryImpl
    ): SoulLayerRepository

    @Binds
    @Singleton
    abstract fun bindMicroEntryRepository(
        impl: MicroEntryRepositoryImpl
    ): MicroEntryRepository

    @Binds
    @Singleton
    abstract fun bindMonthlyLetterRepository(
        impl: MonthlyLetterRepositoryImpl
    ): MonthlyLetterRepository

    @Binds
    @Singleton
    abstract fun bindSocialRepository(
        impl: SocialRepositoryImpl
    ): SocialRepository

    @Binds
    @Singleton
    abstract fun bindWisdomCollectionRepository(
        impl: WisdomCollectionRepositoryImpl
    ): WisdomCollectionRepository

    @Binds
    @Singleton
    abstract fun bindWisdomLibraryRepository(
        impl: WisdomLibraryRepositoryImpl
    ): WisdomLibraryRepository

    @Binds
    @Singleton
    abstract fun bindTodayProgressRepository(
        impl: TodayProgressRepositoryImpl
    ): TodayProgressRepository

    @Binds
    @Singleton
    abstract fun bindOnboardingRepository(
        impl: OnboardingRepositoryImpl
    ): OnboardingRepository

    @Binds
    @Singleton
    abstract fun bindDailyRitualRepository(
        impl: DailyRitualRepositoryImpl
    ): DailyRitualRepository

    @Binds
    @Singleton
    abstract fun bindDeepDiveRepository(
        impl: DeepDiveRepositoryImpl
    ): DeepDiveRepository

    @Binds
    @Singleton
    abstract fun bindFutureMessageRepository(
        impl: FutureMessageRepositoryImpl
    ): FutureMessageRepository

    @Binds
    @Singleton
    abstract fun bindFutureMessageReplyRepository(
        impl: FutureMessageReplyRepositoryImpl
    ): FutureMessageReplyRepository

    @Binds
    @Singleton
    abstract fun bindEvidenceRepository(
        impl: EvidenceRepositoryImpl
    ): EvidenceRepository
}
