package com.prody.prashant.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.prody.prashant.data.local.entity.AchievementEntity;
import com.prody.prashant.data.local.entity.LeaderboardEntryEntity;
import com.prody.prashant.data.local.entity.MotivationalMessageEntity;
import com.prody.prashant.data.local.entity.PeerInteractionEntity;
import com.prody.prashant.data.local.entity.StreakHistoryEntity;
import com.prody.prashant.data.local.entity.UserProfileEntity;
import com.prody.prashant.data.local.entity.UserStatsEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class UserDao_Impl implements UserDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UserProfileEntity> __insertionAdapterOfUserProfileEntity;

  private final EntityInsertionAdapter<UserStatsEntity> __insertionAdapterOfUserStatsEntity;

  private final EntityInsertionAdapter<AchievementEntity> __insertionAdapterOfAchievementEntity;

  private final EntityInsertionAdapter<StreakHistoryEntity> __insertionAdapterOfStreakHistoryEntity;

  private final EntityInsertionAdapter<LeaderboardEntryEntity> __insertionAdapterOfLeaderboardEntryEntity;

  private final EntityInsertionAdapter<PeerInteractionEntity> __insertionAdapterOfPeerInteractionEntity;

  private final EntityInsertionAdapter<MotivationalMessageEntity> __insertionAdapterOfMotivationalMessageEntity;

  private final EntityDeletionOrUpdateAdapter<UserProfileEntity> __updateAdapterOfUserProfileEntity;

  private final EntityDeletionOrUpdateAdapter<UserStatsEntity> __updateAdapterOfUserStatsEntity;

  private final EntityDeletionOrUpdateAdapter<AchievementEntity> __updateAdapterOfAchievementEntity;

  private final EntityDeletionOrUpdateAdapter<LeaderboardEntryEntity> __updateAdapterOfLeaderboardEntryEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateDisplayName;

  private final SharedSQLiteStatement __preparedStmtOfUpdateAvatar;

  private final SharedSQLiteStatement __preparedStmtOfUpdateBanner;

  private final SharedSQLiteStatement __preparedStmtOfUpdateTitle;

  private final SharedSQLiteStatement __preparedStmtOfAddPoints;

  private final SharedSQLiteStatement __preparedStmtOfUpdateStreak;

  private final SharedSQLiteStatement __preparedStmtOfUpdateLastActiveDate;

  private final SharedSQLiteStatement __preparedStmtOfIncrementWordsLearned;

  private final SharedSQLiteStatement __preparedStmtOfIncrementJournalEntries;

  private final SharedSQLiteStatement __preparedStmtOfIncrementFutureMessages;

  private final SharedSQLiteStatement __preparedStmtOfAddDailyPoints;

  private final SharedSQLiteStatement __preparedStmtOfResetDailyStats;

  private final SharedSQLiteStatement __preparedStmtOfUpdateAchievementProgress;

  private final SharedSQLiteStatement __preparedStmtOfUnlockAchievement;

  private final SharedSQLiteStatement __preparedStmtOfIncrementBoosts;

  private final SharedSQLiteStatement __preparedStmtOfIncrementCongrats;

  private final SharedSQLiteStatement __preparedStmtOfMarkMessageAsRead;

  public UserDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUserProfileEntity = new EntityInsertionAdapter<UserProfileEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `user_profile` (`id`,`displayName`,`bio`,`avatarId`,`bannerId`,`titleId`,`totalPoints`,`currentStreak`,`longestStreak`,`lastActiveDate`,`joinedAt`,`wordsLearned`,`journalEntriesCount`,`futureMessagesCount`,`quotesReflected`,`totalReflectionTime`,`preferredWisdomCategories`,`dailyGoalMinutes`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserProfileEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getDisplayName());
        statement.bindString(3, entity.getBio());
        statement.bindString(4, entity.getAvatarId());
        statement.bindString(5, entity.getBannerId());
        statement.bindString(6, entity.getTitleId());
        statement.bindLong(7, entity.getTotalPoints());
        statement.bindLong(8, entity.getCurrentStreak());
        statement.bindLong(9, entity.getLongestStreak());
        statement.bindLong(10, entity.getLastActiveDate());
        statement.bindLong(11, entity.getJoinedAt());
        statement.bindLong(12, entity.getWordsLearned());
        statement.bindLong(13, entity.getJournalEntriesCount());
        statement.bindLong(14, entity.getFutureMessagesCount());
        statement.bindLong(15, entity.getQuotesReflected());
        statement.bindLong(16, entity.getTotalReflectionTime());
        statement.bindString(17, entity.getPreferredWisdomCategories());
        statement.bindLong(18, entity.getDailyGoalMinutes());
      }
    };
    this.__insertionAdapterOfUserStatsEntity = new EntityInsertionAdapter<UserStatsEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `user_stats` (`id`,`dailyPointsEarned`,`weeklyPointsEarned`,`monthlyPointsEarned`,`dailyWordsLearned`,`weeklyWordsLearned`,`monthlyWordsLearned`,`dailyJournalEntries`,`weeklyJournalEntries`,`monthlyJournalEntries`,`lastResetDate`,`weekStartDate`,`monthStartDate`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserStatsEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getDailyPointsEarned());
        statement.bindLong(3, entity.getWeeklyPointsEarned());
        statement.bindLong(4, entity.getMonthlyPointsEarned());
        statement.bindLong(5, entity.getDailyWordsLearned());
        statement.bindLong(6, entity.getWeeklyWordsLearned());
        statement.bindLong(7, entity.getMonthlyWordsLearned());
        statement.bindLong(8, entity.getDailyJournalEntries());
        statement.bindLong(9, entity.getWeeklyJournalEntries());
        statement.bindLong(10, entity.getMonthlyJournalEntries());
        statement.bindLong(11, entity.getLastResetDate());
        statement.bindLong(12, entity.getWeekStartDate());
        statement.bindLong(13, entity.getMonthStartDate());
      }
    };
    this.__insertionAdapterOfAchievementEntity = new EntityInsertionAdapter<AchievementEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `achievements` (`id`,`name`,`description`,`iconId`,`category`,`requirement`,`currentProgress`,`isUnlocked`,`unlockedAt`,`rewardType`,`rewardValue`,`rarity`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AchievementEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getDescription());
        statement.bindString(4, entity.getIconId());
        statement.bindString(5, entity.getCategory());
        statement.bindLong(6, entity.getRequirement());
        statement.bindLong(7, entity.getCurrentProgress());
        final int _tmp = entity.isUnlocked() ? 1 : 0;
        statement.bindLong(8, _tmp);
        if (entity.getUnlockedAt() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getUnlockedAt());
        }
        statement.bindString(10, entity.getRewardType());
        statement.bindString(11, entity.getRewardValue());
        statement.bindString(12, entity.getRarity());
      }
    };
    this.__insertionAdapterOfStreakHistoryEntity = new EntityInsertionAdapter<StreakHistoryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `streak_history` (`id`,`date`,`activitiesCompleted`,`pointsEarned`,`streakDay`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final StreakHistoryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getDate());
        statement.bindString(3, entity.getActivitiesCompleted());
        statement.bindLong(4, entity.getPointsEarned());
        statement.bindLong(5, entity.getStreakDay());
      }
    };
    this.__insertionAdapterOfLeaderboardEntryEntity = new EntityInsertionAdapter<LeaderboardEntryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `leaderboard` (`odId`,`displayName`,`avatarId`,`titleId`,`totalPoints`,`weeklyPoints`,`currentStreak`,`rank`,`previousRank`,`isCurrentUser`,`lastActiveAt`,`boostsReceived`,`congratsReceived`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final LeaderboardEntryEntity entity) {
        statement.bindString(1, entity.getOdId());
        statement.bindString(2, entity.getDisplayName());
        statement.bindString(3, entity.getAvatarId());
        statement.bindString(4, entity.getTitleId());
        statement.bindLong(5, entity.getTotalPoints());
        statement.bindLong(6, entity.getWeeklyPoints());
        statement.bindLong(7, entity.getCurrentStreak());
        statement.bindLong(8, entity.getRank());
        statement.bindLong(9, entity.getPreviousRank());
        final int _tmp = entity.isCurrentUser() ? 1 : 0;
        statement.bindLong(10, _tmp);
        statement.bindLong(11, entity.getLastActiveAt());
        statement.bindLong(12, entity.getBoostsReceived());
        statement.bindLong(13, entity.getCongratsReceived());
      }
    };
    this.__insertionAdapterOfPeerInteractionEntity = new EntityInsertionAdapter<PeerInteractionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `peer_interactions` (`id`,`peerId`,`interactionType`,`timestamp`,`message`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PeerInteractionEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getPeerId());
        statement.bindString(3, entity.getInteractionType());
        statement.bindLong(4, entity.getTimestamp());
        if (entity.getMessage() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getMessage());
        }
      }
    };
    this.__insertionAdapterOfMotivationalMessageEntity = new EntityInsertionAdapter<MotivationalMessageEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `motivational_messages` (`id`,`senderId`,`senderName`,`message`,`messageType`,`isRead`,`receivedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MotivationalMessageEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getSenderId());
        statement.bindString(3, entity.getSenderName());
        statement.bindString(4, entity.getMessage());
        statement.bindString(5, entity.getMessageType());
        final int _tmp = entity.isRead() ? 1 : 0;
        statement.bindLong(6, _tmp);
        statement.bindLong(7, entity.getReceivedAt());
      }
    };
    this.__updateAdapterOfUserProfileEntity = new EntityDeletionOrUpdateAdapter<UserProfileEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `user_profile` SET `id` = ?,`displayName` = ?,`bio` = ?,`avatarId` = ?,`bannerId` = ?,`titleId` = ?,`totalPoints` = ?,`currentStreak` = ?,`longestStreak` = ?,`lastActiveDate` = ?,`joinedAt` = ?,`wordsLearned` = ?,`journalEntriesCount` = ?,`futureMessagesCount` = ?,`quotesReflected` = ?,`totalReflectionTime` = ?,`preferredWisdomCategories` = ?,`dailyGoalMinutes` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserProfileEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getDisplayName());
        statement.bindString(3, entity.getBio());
        statement.bindString(4, entity.getAvatarId());
        statement.bindString(5, entity.getBannerId());
        statement.bindString(6, entity.getTitleId());
        statement.bindLong(7, entity.getTotalPoints());
        statement.bindLong(8, entity.getCurrentStreak());
        statement.bindLong(9, entity.getLongestStreak());
        statement.bindLong(10, entity.getLastActiveDate());
        statement.bindLong(11, entity.getJoinedAt());
        statement.bindLong(12, entity.getWordsLearned());
        statement.bindLong(13, entity.getJournalEntriesCount());
        statement.bindLong(14, entity.getFutureMessagesCount());
        statement.bindLong(15, entity.getQuotesReflected());
        statement.bindLong(16, entity.getTotalReflectionTime());
        statement.bindString(17, entity.getPreferredWisdomCategories());
        statement.bindLong(18, entity.getDailyGoalMinutes());
        statement.bindLong(19, entity.getId());
      }
    };
    this.__updateAdapterOfUserStatsEntity = new EntityDeletionOrUpdateAdapter<UserStatsEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `user_stats` SET `id` = ?,`dailyPointsEarned` = ?,`weeklyPointsEarned` = ?,`monthlyPointsEarned` = ?,`dailyWordsLearned` = ?,`weeklyWordsLearned` = ?,`monthlyWordsLearned` = ?,`dailyJournalEntries` = ?,`weeklyJournalEntries` = ?,`monthlyJournalEntries` = ?,`lastResetDate` = ?,`weekStartDate` = ?,`monthStartDate` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserStatsEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getDailyPointsEarned());
        statement.bindLong(3, entity.getWeeklyPointsEarned());
        statement.bindLong(4, entity.getMonthlyPointsEarned());
        statement.bindLong(5, entity.getDailyWordsLearned());
        statement.bindLong(6, entity.getWeeklyWordsLearned());
        statement.bindLong(7, entity.getMonthlyWordsLearned());
        statement.bindLong(8, entity.getDailyJournalEntries());
        statement.bindLong(9, entity.getWeeklyJournalEntries());
        statement.bindLong(10, entity.getMonthlyJournalEntries());
        statement.bindLong(11, entity.getLastResetDate());
        statement.bindLong(12, entity.getWeekStartDate());
        statement.bindLong(13, entity.getMonthStartDate());
        statement.bindLong(14, entity.getId());
      }
    };
    this.__updateAdapterOfAchievementEntity = new EntityDeletionOrUpdateAdapter<AchievementEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `achievements` SET `id` = ?,`name` = ?,`description` = ?,`iconId` = ?,`category` = ?,`requirement` = ?,`currentProgress` = ?,`isUnlocked` = ?,`unlockedAt` = ?,`rewardType` = ?,`rewardValue` = ?,`rarity` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AchievementEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getDescription());
        statement.bindString(4, entity.getIconId());
        statement.bindString(5, entity.getCategory());
        statement.bindLong(6, entity.getRequirement());
        statement.bindLong(7, entity.getCurrentProgress());
        final int _tmp = entity.isUnlocked() ? 1 : 0;
        statement.bindLong(8, _tmp);
        if (entity.getUnlockedAt() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getUnlockedAt());
        }
        statement.bindString(10, entity.getRewardType());
        statement.bindString(11, entity.getRewardValue());
        statement.bindString(12, entity.getRarity());
        statement.bindString(13, entity.getId());
      }
    };
    this.__updateAdapterOfLeaderboardEntryEntity = new EntityDeletionOrUpdateAdapter<LeaderboardEntryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `leaderboard` SET `odId` = ?,`displayName` = ?,`avatarId` = ?,`titleId` = ?,`totalPoints` = ?,`weeklyPoints` = ?,`currentStreak` = ?,`rank` = ?,`previousRank` = ?,`isCurrentUser` = ?,`lastActiveAt` = ?,`boostsReceived` = ?,`congratsReceived` = ? WHERE `odId` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final LeaderboardEntryEntity entity) {
        statement.bindString(1, entity.getOdId());
        statement.bindString(2, entity.getDisplayName());
        statement.bindString(3, entity.getAvatarId());
        statement.bindString(4, entity.getTitleId());
        statement.bindLong(5, entity.getTotalPoints());
        statement.bindLong(6, entity.getWeeklyPoints());
        statement.bindLong(7, entity.getCurrentStreak());
        statement.bindLong(8, entity.getRank());
        statement.bindLong(9, entity.getPreviousRank());
        final int _tmp = entity.isCurrentUser() ? 1 : 0;
        statement.bindLong(10, _tmp);
        statement.bindLong(11, entity.getLastActiveAt());
        statement.bindLong(12, entity.getBoostsReceived());
        statement.bindLong(13, entity.getCongratsReceived());
        statement.bindString(14, entity.getOdId());
      }
    };
    this.__preparedStmtOfUpdateDisplayName = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE user_profile SET displayName = ? WHERE id = 1";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateAvatar = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE user_profile SET avatarId = ? WHERE id = 1";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateBanner = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE user_profile SET bannerId = ? WHERE id = 1";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateTitle = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE user_profile SET titleId = ? WHERE id = 1";
        return _query;
      }
    };
    this.__preparedStmtOfAddPoints = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE user_profile SET totalPoints = totalPoints + ? WHERE id = 1";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateStreak = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE user_profile SET currentStreak = ?, longestStreak = CASE WHEN ? > longestStreak THEN ? ELSE longestStreak END WHERE id = 1";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateLastActiveDate = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE user_profile SET lastActiveDate = ? WHERE id = 1";
        return _query;
      }
    };
    this.__preparedStmtOfIncrementWordsLearned = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE user_profile SET wordsLearned = wordsLearned + 1 WHERE id = 1";
        return _query;
      }
    };
    this.__preparedStmtOfIncrementJournalEntries = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE user_profile SET journalEntriesCount = journalEntriesCount + 1 WHERE id = 1";
        return _query;
      }
    };
    this.__preparedStmtOfIncrementFutureMessages = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE user_profile SET futureMessagesCount = futureMessagesCount + 1 WHERE id = 1";
        return _query;
      }
    };
    this.__preparedStmtOfAddDailyPoints = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE user_stats SET dailyPointsEarned = dailyPointsEarned + ? WHERE id = 1";
        return _query;
      }
    };
    this.__preparedStmtOfResetDailyStats = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE user_stats SET dailyPointsEarned = 0, lastResetDate = ? WHERE id = 1";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateAchievementProgress = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE achievements SET currentProgress = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUnlockAchievement = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE achievements SET isUnlocked = 1, unlockedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfIncrementBoosts = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE leaderboard SET boostsReceived = boostsReceived + 1 WHERE odId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfIncrementCongrats = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE leaderboard SET congratsReceived = congratsReceived + 1 WHERE odId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkMessageAsRead = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE motivational_messages SET isRead = 1 WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertUserProfile(final UserProfileEntity profile,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfUserProfileEntity.insert(profile);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertUserStats(final UserStatsEntity stats,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfUserStatsEntity.insert(stats);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAchievement(final AchievementEntity achievement,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAchievementEntity.insert(achievement);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAchievements(final List<AchievementEntity> achievements,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAchievementEntity.insert(achievements);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertStreakHistory(final StreakHistoryEntity streak,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfStreakHistoryEntity.insert(streak);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertLeaderboardEntry(final LeaderboardEntryEntity entry,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfLeaderboardEntryEntity.insert(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertLeaderboardEntries(final List<LeaderboardEntryEntity> entries,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfLeaderboardEntryEntity.insert(entries);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertPeerInteraction(final PeerInteractionEntity interaction,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPeerInteractionEntity.insert(interaction);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertMotivationalMessage(final MotivationalMessageEntity message,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMotivationalMessageEntity.insert(message);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateUserProfile(final UserProfileEntity profile,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfUserProfileEntity.handle(profile);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateUserStats(final UserStatsEntity stats,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfUserStatsEntity.handle(stats);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateAchievement(final AchievementEntity achievement,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfAchievementEntity.handle(achievement);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateLeaderboardEntry(final LeaderboardEntryEntity entry,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfLeaderboardEntryEntity.handle(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateDisplayName(final String name, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateDisplayName.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, name);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateDisplayName.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateAvatar(final String avatarId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateAvatar.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, avatarId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateAvatar.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateBanner(final String bannerId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateBanner.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, bannerId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateBanner.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTitle(final String titleId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateTitle.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, titleId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateTitle.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object addPoints(final int points, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfAddPoints.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, points);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfAddPoints.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateStreak(final int streak, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateStreak.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, streak);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, streak);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, streak);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateStreak.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateLastActiveDate(final long date,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateLastActiveDate.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, date);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateLastActiveDate.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementWordsLearned(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementWordsLearned.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfIncrementWordsLearned.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementJournalEntries(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementJournalEntries.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfIncrementJournalEntries.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementFutureMessages(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementFutureMessages.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfIncrementFutureMessages.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object addDailyPoints(final int points, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfAddDailyPoints.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, points);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfAddDailyPoints.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object resetDailyStats(final long resetDate,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfResetDailyStats.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, resetDate);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfResetDailyStats.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateAchievementProgress(final String id, final int progress,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateAchievementProgress.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, progress);
        _argIndex = 2;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateAchievementProgress.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object unlockAchievement(final String id, final long unlockedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUnlockAchievement.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, unlockedAt);
        _argIndex = 2;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUnlockAchievement.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementBoosts(final String odId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementBoosts.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, odId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfIncrementBoosts.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementCongrats(final String odId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementCongrats.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, odId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfIncrementCongrats.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markMessageAsRead(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkMessageAsRead.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfMarkMessageAsRead.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<UserProfileEntity> getUserProfile() {
    final String _sql = "SELECT * FROM user_profile WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"user_profile"}, new Callable<UserProfileEntity>() {
      @Override
      @Nullable
      public UserProfileEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfBio = CursorUtil.getColumnIndexOrThrow(_cursor, "bio");
          final int _cursorIndexOfAvatarId = CursorUtil.getColumnIndexOrThrow(_cursor, "avatarId");
          final int _cursorIndexOfBannerId = CursorUtil.getColumnIndexOrThrow(_cursor, "bannerId");
          final int _cursorIndexOfTitleId = CursorUtil.getColumnIndexOrThrow(_cursor, "titleId");
          final int _cursorIndexOfTotalPoints = CursorUtil.getColumnIndexOrThrow(_cursor, "totalPoints");
          final int _cursorIndexOfCurrentStreak = CursorUtil.getColumnIndexOrThrow(_cursor, "currentStreak");
          final int _cursorIndexOfLongestStreak = CursorUtil.getColumnIndexOrThrow(_cursor, "longestStreak");
          final int _cursorIndexOfLastActiveDate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastActiveDate");
          final int _cursorIndexOfJoinedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "joinedAt");
          final int _cursorIndexOfWordsLearned = CursorUtil.getColumnIndexOrThrow(_cursor, "wordsLearned");
          final int _cursorIndexOfJournalEntriesCount = CursorUtil.getColumnIndexOrThrow(_cursor, "journalEntriesCount");
          final int _cursorIndexOfFutureMessagesCount = CursorUtil.getColumnIndexOrThrow(_cursor, "futureMessagesCount");
          final int _cursorIndexOfQuotesReflected = CursorUtil.getColumnIndexOrThrow(_cursor, "quotesReflected");
          final int _cursorIndexOfTotalReflectionTime = CursorUtil.getColumnIndexOrThrow(_cursor, "totalReflectionTime");
          final int _cursorIndexOfPreferredWisdomCategories = CursorUtil.getColumnIndexOrThrow(_cursor, "preferredWisdomCategories");
          final int _cursorIndexOfDailyGoalMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyGoalMinutes");
          final UserProfileEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpBio;
            _tmpBio = _cursor.getString(_cursorIndexOfBio);
            final String _tmpAvatarId;
            _tmpAvatarId = _cursor.getString(_cursorIndexOfAvatarId);
            final String _tmpBannerId;
            _tmpBannerId = _cursor.getString(_cursorIndexOfBannerId);
            final String _tmpTitleId;
            _tmpTitleId = _cursor.getString(_cursorIndexOfTitleId);
            final int _tmpTotalPoints;
            _tmpTotalPoints = _cursor.getInt(_cursorIndexOfTotalPoints);
            final int _tmpCurrentStreak;
            _tmpCurrentStreak = _cursor.getInt(_cursorIndexOfCurrentStreak);
            final int _tmpLongestStreak;
            _tmpLongestStreak = _cursor.getInt(_cursorIndexOfLongestStreak);
            final long _tmpLastActiveDate;
            _tmpLastActiveDate = _cursor.getLong(_cursorIndexOfLastActiveDate);
            final long _tmpJoinedAt;
            _tmpJoinedAt = _cursor.getLong(_cursorIndexOfJoinedAt);
            final int _tmpWordsLearned;
            _tmpWordsLearned = _cursor.getInt(_cursorIndexOfWordsLearned);
            final int _tmpJournalEntriesCount;
            _tmpJournalEntriesCount = _cursor.getInt(_cursorIndexOfJournalEntriesCount);
            final int _tmpFutureMessagesCount;
            _tmpFutureMessagesCount = _cursor.getInt(_cursorIndexOfFutureMessagesCount);
            final int _tmpQuotesReflected;
            _tmpQuotesReflected = _cursor.getInt(_cursorIndexOfQuotesReflected);
            final long _tmpTotalReflectionTime;
            _tmpTotalReflectionTime = _cursor.getLong(_cursorIndexOfTotalReflectionTime);
            final String _tmpPreferredWisdomCategories;
            _tmpPreferredWisdomCategories = _cursor.getString(_cursorIndexOfPreferredWisdomCategories);
            final int _tmpDailyGoalMinutes;
            _tmpDailyGoalMinutes = _cursor.getInt(_cursorIndexOfDailyGoalMinutes);
            _result = new UserProfileEntity(_tmpId,_tmpDisplayName,_tmpBio,_tmpAvatarId,_tmpBannerId,_tmpTitleId,_tmpTotalPoints,_tmpCurrentStreak,_tmpLongestStreak,_tmpLastActiveDate,_tmpJoinedAt,_tmpWordsLearned,_tmpJournalEntriesCount,_tmpFutureMessagesCount,_tmpQuotesReflected,_tmpTotalReflectionTime,_tmpPreferredWisdomCategories,_tmpDailyGoalMinutes);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getUserProfileSync(final Continuation<? super UserProfileEntity> $completion) {
    final String _sql = "SELECT * FROM user_profile WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<UserProfileEntity>() {
      @Override
      @Nullable
      public UserProfileEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfBio = CursorUtil.getColumnIndexOrThrow(_cursor, "bio");
          final int _cursorIndexOfAvatarId = CursorUtil.getColumnIndexOrThrow(_cursor, "avatarId");
          final int _cursorIndexOfBannerId = CursorUtil.getColumnIndexOrThrow(_cursor, "bannerId");
          final int _cursorIndexOfTitleId = CursorUtil.getColumnIndexOrThrow(_cursor, "titleId");
          final int _cursorIndexOfTotalPoints = CursorUtil.getColumnIndexOrThrow(_cursor, "totalPoints");
          final int _cursorIndexOfCurrentStreak = CursorUtil.getColumnIndexOrThrow(_cursor, "currentStreak");
          final int _cursorIndexOfLongestStreak = CursorUtil.getColumnIndexOrThrow(_cursor, "longestStreak");
          final int _cursorIndexOfLastActiveDate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastActiveDate");
          final int _cursorIndexOfJoinedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "joinedAt");
          final int _cursorIndexOfWordsLearned = CursorUtil.getColumnIndexOrThrow(_cursor, "wordsLearned");
          final int _cursorIndexOfJournalEntriesCount = CursorUtil.getColumnIndexOrThrow(_cursor, "journalEntriesCount");
          final int _cursorIndexOfFutureMessagesCount = CursorUtil.getColumnIndexOrThrow(_cursor, "futureMessagesCount");
          final int _cursorIndexOfQuotesReflected = CursorUtil.getColumnIndexOrThrow(_cursor, "quotesReflected");
          final int _cursorIndexOfTotalReflectionTime = CursorUtil.getColumnIndexOrThrow(_cursor, "totalReflectionTime");
          final int _cursorIndexOfPreferredWisdomCategories = CursorUtil.getColumnIndexOrThrow(_cursor, "preferredWisdomCategories");
          final int _cursorIndexOfDailyGoalMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyGoalMinutes");
          final UserProfileEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpBio;
            _tmpBio = _cursor.getString(_cursorIndexOfBio);
            final String _tmpAvatarId;
            _tmpAvatarId = _cursor.getString(_cursorIndexOfAvatarId);
            final String _tmpBannerId;
            _tmpBannerId = _cursor.getString(_cursorIndexOfBannerId);
            final String _tmpTitleId;
            _tmpTitleId = _cursor.getString(_cursorIndexOfTitleId);
            final int _tmpTotalPoints;
            _tmpTotalPoints = _cursor.getInt(_cursorIndexOfTotalPoints);
            final int _tmpCurrentStreak;
            _tmpCurrentStreak = _cursor.getInt(_cursorIndexOfCurrentStreak);
            final int _tmpLongestStreak;
            _tmpLongestStreak = _cursor.getInt(_cursorIndexOfLongestStreak);
            final long _tmpLastActiveDate;
            _tmpLastActiveDate = _cursor.getLong(_cursorIndexOfLastActiveDate);
            final long _tmpJoinedAt;
            _tmpJoinedAt = _cursor.getLong(_cursorIndexOfJoinedAt);
            final int _tmpWordsLearned;
            _tmpWordsLearned = _cursor.getInt(_cursorIndexOfWordsLearned);
            final int _tmpJournalEntriesCount;
            _tmpJournalEntriesCount = _cursor.getInt(_cursorIndexOfJournalEntriesCount);
            final int _tmpFutureMessagesCount;
            _tmpFutureMessagesCount = _cursor.getInt(_cursorIndexOfFutureMessagesCount);
            final int _tmpQuotesReflected;
            _tmpQuotesReflected = _cursor.getInt(_cursorIndexOfQuotesReflected);
            final long _tmpTotalReflectionTime;
            _tmpTotalReflectionTime = _cursor.getLong(_cursorIndexOfTotalReflectionTime);
            final String _tmpPreferredWisdomCategories;
            _tmpPreferredWisdomCategories = _cursor.getString(_cursorIndexOfPreferredWisdomCategories);
            final int _tmpDailyGoalMinutes;
            _tmpDailyGoalMinutes = _cursor.getInt(_cursorIndexOfDailyGoalMinutes);
            _result = new UserProfileEntity(_tmpId,_tmpDisplayName,_tmpBio,_tmpAvatarId,_tmpBannerId,_tmpTitleId,_tmpTotalPoints,_tmpCurrentStreak,_tmpLongestStreak,_tmpLastActiveDate,_tmpJoinedAt,_tmpWordsLearned,_tmpJournalEntriesCount,_tmpFutureMessagesCount,_tmpQuotesReflected,_tmpTotalReflectionTime,_tmpPreferredWisdomCategories,_tmpDailyGoalMinutes);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<UserStatsEntity> getUserStats() {
    final String _sql = "SELECT * FROM user_stats WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"user_stats"}, new Callable<UserStatsEntity>() {
      @Override
      @Nullable
      public UserStatsEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDailyPointsEarned = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyPointsEarned");
          final int _cursorIndexOfWeeklyPointsEarned = CursorUtil.getColumnIndexOrThrow(_cursor, "weeklyPointsEarned");
          final int _cursorIndexOfMonthlyPointsEarned = CursorUtil.getColumnIndexOrThrow(_cursor, "monthlyPointsEarned");
          final int _cursorIndexOfDailyWordsLearned = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyWordsLearned");
          final int _cursorIndexOfWeeklyWordsLearned = CursorUtil.getColumnIndexOrThrow(_cursor, "weeklyWordsLearned");
          final int _cursorIndexOfMonthlyWordsLearned = CursorUtil.getColumnIndexOrThrow(_cursor, "monthlyWordsLearned");
          final int _cursorIndexOfDailyJournalEntries = CursorUtil.getColumnIndexOrThrow(_cursor, "dailyJournalEntries");
          final int _cursorIndexOfWeeklyJournalEntries = CursorUtil.getColumnIndexOrThrow(_cursor, "weeklyJournalEntries");
          final int _cursorIndexOfMonthlyJournalEntries = CursorUtil.getColumnIndexOrThrow(_cursor, "monthlyJournalEntries");
          final int _cursorIndexOfLastResetDate = CursorUtil.getColumnIndexOrThrow(_cursor, "lastResetDate");
          final int _cursorIndexOfWeekStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "weekStartDate");
          final int _cursorIndexOfMonthStartDate = CursorUtil.getColumnIndexOrThrow(_cursor, "monthStartDate");
          final UserStatsEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpDailyPointsEarned;
            _tmpDailyPointsEarned = _cursor.getInt(_cursorIndexOfDailyPointsEarned);
            final int _tmpWeeklyPointsEarned;
            _tmpWeeklyPointsEarned = _cursor.getInt(_cursorIndexOfWeeklyPointsEarned);
            final int _tmpMonthlyPointsEarned;
            _tmpMonthlyPointsEarned = _cursor.getInt(_cursorIndexOfMonthlyPointsEarned);
            final int _tmpDailyWordsLearned;
            _tmpDailyWordsLearned = _cursor.getInt(_cursorIndexOfDailyWordsLearned);
            final int _tmpWeeklyWordsLearned;
            _tmpWeeklyWordsLearned = _cursor.getInt(_cursorIndexOfWeeklyWordsLearned);
            final int _tmpMonthlyWordsLearned;
            _tmpMonthlyWordsLearned = _cursor.getInt(_cursorIndexOfMonthlyWordsLearned);
            final int _tmpDailyJournalEntries;
            _tmpDailyJournalEntries = _cursor.getInt(_cursorIndexOfDailyJournalEntries);
            final int _tmpWeeklyJournalEntries;
            _tmpWeeklyJournalEntries = _cursor.getInt(_cursorIndexOfWeeklyJournalEntries);
            final int _tmpMonthlyJournalEntries;
            _tmpMonthlyJournalEntries = _cursor.getInt(_cursorIndexOfMonthlyJournalEntries);
            final long _tmpLastResetDate;
            _tmpLastResetDate = _cursor.getLong(_cursorIndexOfLastResetDate);
            final long _tmpWeekStartDate;
            _tmpWeekStartDate = _cursor.getLong(_cursorIndexOfWeekStartDate);
            final long _tmpMonthStartDate;
            _tmpMonthStartDate = _cursor.getLong(_cursorIndexOfMonthStartDate);
            _result = new UserStatsEntity(_tmpId,_tmpDailyPointsEarned,_tmpWeeklyPointsEarned,_tmpMonthlyPointsEarned,_tmpDailyWordsLearned,_tmpWeeklyWordsLearned,_tmpMonthlyWordsLearned,_tmpDailyJournalEntries,_tmpWeeklyJournalEntries,_tmpMonthlyJournalEntries,_tmpLastResetDate,_tmpWeekStartDate,_tmpMonthStartDate);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<AchievementEntity>> getAllAchievements() {
    final String _sql = "SELECT * FROM achievements ORDER BY isUnlocked DESC, rarity DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"achievements"}, new Callable<List<AchievementEntity>>() {
      @Override
      @NonNull
      public List<AchievementEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfIconId = CursorUtil.getColumnIndexOrThrow(_cursor, "iconId");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfRequirement = CursorUtil.getColumnIndexOrThrow(_cursor, "requirement");
          final int _cursorIndexOfCurrentProgress = CursorUtil.getColumnIndexOrThrow(_cursor, "currentProgress");
          final int _cursorIndexOfIsUnlocked = CursorUtil.getColumnIndexOrThrow(_cursor, "isUnlocked");
          final int _cursorIndexOfUnlockedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "unlockedAt");
          final int _cursorIndexOfRewardType = CursorUtil.getColumnIndexOrThrow(_cursor, "rewardType");
          final int _cursorIndexOfRewardValue = CursorUtil.getColumnIndexOrThrow(_cursor, "rewardValue");
          final int _cursorIndexOfRarity = CursorUtil.getColumnIndexOrThrow(_cursor, "rarity");
          final List<AchievementEntity> _result = new ArrayList<AchievementEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AchievementEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpIconId;
            _tmpIconId = _cursor.getString(_cursorIndexOfIconId);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final int _tmpRequirement;
            _tmpRequirement = _cursor.getInt(_cursorIndexOfRequirement);
            final int _tmpCurrentProgress;
            _tmpCurrentProgress = _cursor.getInt(_cursorIndexOfCurrentProgress);
            final boolean _tmpIsUnlocked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsUnlocked);
            _tmpIsUnlocked = _tmp != 0;
            final Long _tmpUnlockedAt;
            if (_cursor.isNull(_cursorIndexOfUnlockedAt)) {
              _tmpUnlockedAt = null;
            } else {
              _tmpUnlockedAt = _cursor.getLong(_cursorIndexOfUnlockedAt);
            }
            final String _tmpRewardType;
            _tmpRewardType = _cursor.getString(_cursorIndexOfRewardType);
            final String _tmpRewardValue;
            _tmpRewardValue = _cursor.getString(_cursorIndexOfRewardValue);
            final String _tmpRarity;
            _tmpRarity = _cursor.getString(_cursorIndexOfRarity);
            _item = new AchievementEntity(_tmpId,_tmpName,_tmpDescription,_tmpIconId,_tmpCategory,_tmpRequirement,_tmpCurrentProgress,_tmpIsUnlocked,_tmpUnlockedAt,_tmpRewardType,_tmpRewardValue,_tmpRarity);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAchievementById(final String id,
      final Continuation<? super AchievementEntity> $completion) {
    final String _sql = "SELECT * FROM achievements WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AchievementEntity>() {
      @Override
      @Nullable
      public AchievementEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfIconId = CursorUtil.getColumnIndexOrThrow(_cursor, "iconId");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfRequirement = CursorUtil.getColumnIndexOrThrow(_cursor, "requirement");
          final int _cursorIndexOfCurrentProgress = CursorUtil.getColumnIndexOrThrow(_cursor, "currentProgress");
          final int _cursorIndexOfIsUnlocked = CursorUtil.getColumnIndexOrThrow(_cursor, "isUnlocked");
          final int _cursorIndexOfUnlockedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "unlockedAt");
          final int _cursorIndexOfRewardType = CursorUtil.getColumnIndexOrThrow(_cursor, "rewardType");
          final int _cursorIndexOfRewardValue = CursorUtil.getColumnIndexOrThrow(_cursor, "rewardValue");
          final int _cursorIndexOfRarity = CursorUtil.getColumnIndexOrThrow(_cursor, "rarity");
          final AchievementEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpIconId;
            _tmpIconId = _cursor.getString(_cursorIndexOfIconId);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final int _tmpRequirement;
            _tmpRequirement = _cursor.getInt(_cursorIndexOfRequirement);
            final int _tmpCurrentProgress;
            _tmpCurrentProgress = _cursor.getInt(_cursorIndexOfCurrentProgress);
            final boolean _tmpIsUnlocked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsUnlocked);
            _tmpIsUnlocked = _tmp != 0;
            final Long _tmpUnlockedAt;
            if (_cursor.isNull(_cursorIndexOfUnlockedAt)) {
              _tmpUnlockedAt = null;
            } else {
              _tmpUnlockedAt = _cursor.getLong(_cursorIndexOfUnlockedAt);
            }
            final String _tmpRewardType;
            _tmpRewardType = _cursor.getString(_cursorIndexOfRewardType);
            final String _tmpRewardValue;
            _tmpRewardValue = _cursor.getString(_cursorIndexOfRewardValue);
            final String _tmpRarity;
            _tmpRarity = _cursor.getString(_cursorIndexOfRarity);
            _result = new AchievementEntity(_tmpId,_tmpName,_tmpDescription,_tmpIconId,_tmpCategory,_tmpRequirement,_tmpCurrentProgress,_tmpIsUnlocked,_tmpUnlockedAt,_tmpRewardType,_tmpRewardValue,_tmpRarity);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<AchievementEntity>> getUnlockedAchievements() {
    final String _sql = "SELECT * FROM achievements WHERE isUnlocked = 1 ORDER BY unlockedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"achievements"}, new Callable<List<AchievementEntity>>() {
      @Override
      @NonNull
      public List<AchievementEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfIconId = CursorUtil.getColumnIndexOrThrow(_cursor, "iconId");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfRequirement = CursorUtil.getColumnIndexOrThrow(_cursor, "requirement");
          final int _cursorIndexOfCurrentProgress = CursorUtil.getColumnIndexOrThrow(_cursor, "currentProgress");
          final int _cursorIndexOfIsUnlocked = CursorUtil.getColumnIndexOrThrow(_cursor, "isUnlocked");
          final int _cursorIndexOfUnlockedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "unlockedAt");
          final int _cursorIndexOfRewardType = CursorUtil.getColumnIndexOrThrow(_cursor, "rewardType");
          final int _cursorIndexOfRewardValue = CursorUtil.getColumnIndexOrThrow(_cursor, "rewardValue");
          final int _cursorIndexOfRarity = CursorUtil.getColumnIndexOrThrow(_cursor, "rarity");
          final List<AchievementEntity> _result = new ArrayList<AchievementEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AchievementEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpIconId;
            _tmpIconId = _cursor.getString(_cursorIndexOfIconId);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final int _tmpRequirement;
            _tmpRequirement = _cursor.getInt(_cursorIndexOfRequirement);
            final int _tmpCurrentProgress;
            _tmpCurrentProgress = _cursor.getInt(_cursorIndexOfCurrentProgress);
            final boolean _tmpIsUnlocked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsUnlocked);
            _tmpIsUnlocked = _tmp != 0;
            final Long _tmpUnlockedAt;
            if (_cursor.isNull(_cursorIndexOfUnlockedAt)) {
              _tmpUnlockedAt = null;
            } else {
              _tmpUnlockedAt = _cursor.getLong(_cursorIndexOfUnlockedAt);
            }
            final String _tmpRewardType;
            _tmpRewardType = _cursor.getString(_cursorIndexOfRewardType);
            final String _tmpRewardValue;
            _tmpRewardValue = _cursor.getString(_cursorIndexOfRewardValue);
            final String _tmpRarity;
            _tmpRarity = _cursor.getString(_cursorIndexOfRarity);
            _item = new AchievementEntity(_tmpId,_tmpName,_tmpDescription,_tmpIconId,_tmpCategory,_tmpRequirement,_tmpCurrentProgress,_tmpIsUnlocked,_tmpUnlockedAt,_tmpRewardType,_tmpRewardValue,_tmpRarity);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<AchievementEntity>> getLockedAchievements() {
    final String _sql = "SELECT * FROM achievements WHERE isUnlocked = 0 ORDER BY currentProgress DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"achievements"}, new Callable<List<AchievementEntity>>() {
      @Override
      @NonNull
      public List<AchievementEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfIconId = CursorUtil.getColumnIndexOrThrow(_cursor, "iconId");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfRequirement = CursorUtil.getColumnIndexOrThrow(_cursor, "requirement");
          final int _cursorIndexOfCurrentProgress = CursorUtil.getColumnIndexOrThrow(_cursor, "currentProgress");
          final int _cursorIndexOfIsUnlocked = CursorUtil.getColumnIndexOrThrow(_cursor, "isUnlocked");
          final int _cursorIndexOfUnlockedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "unlockedAt");
          final int _cursorIndexOfRewardType = CursorUtil.getColumnIndexOrThrow(_cursor, "rewardType");
          final int _cursorIndexOfRewardValue = CursorUtil.getColumnIndexOrThrow(_cursor, "rewardValue");
          final int _cursorIndexOfRarity = CursorUtil.getColumnIndexOrThrow(_cursor, "rarity");
          final List<AchievementEntity> _result = new ArrayList<AchievementEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AchievementEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpIconId;
            _tmpIconId = _cursor.getString(_cursorIndexOfIconId);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final int _tmpRequirement;
            _tmpRequirement = _cursor.getInt(_cursorIndexOfRequirement);
            final int _tmpCurrentProgress;
            _tmpCurrentProgress = _cursor.getInt(_cursorIndexOfCurrentProgress);
            final boolean _tmpIsUnlocked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsUnlocked);
            _tmpIsUnlocked = _tmp != 0;
            final Long _tmpUnlockedAt;
            if (_cursor.isNull(_cursorIndexOfUnlockedAt)) {
              _tmpUnlockedAt = null;
            } else {
              _tmpUnlockedAt = _cursor.getLong(_cursorIndexOfUnlockedAt);
            }
            final String _tmpRewardType;
            _tmpRewardType = _cursor.getString(_cursorIndexOfRewardType);
            final String _tmpRewardValue;
            _tmpRewardValue = _cursor.getString(_cursorIndexOfRewardValue);
            final String _tmpRarity;
            _tmpRarity = _cursor.getString(_cursorIndexOfRarity);
            _item = new AchievementEntity(_tmpId,_tmpName,_tmpDescription,_tmpIconId,_tmpCategory,_tmpRequirement,_tmpCurrentProgress,_tmpIsUnlocked,_tmpUnlockedAt,_tmpRewardType,_tmpRewardValue,_tmpRarity);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<AchievementEntity>> getAchievementsByCategory(final String category) {
    final String _sql = "SELECT * FROM achievements WHERE category = ? ORDER BY isUnlocked DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, category);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"achievements"}, new Callable<List<AchievementEntity>>() {
      @Override
      @NonNull
      public List<AchievementEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfIconId = CursorUtil.getColumnIndexOrThrow(_cursor, "iconId");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfRequirement = CursorUtil.getColumnIndexOrThrow(_cursor, "requirement");
          final int _cursorIndexOfCurrentProgress = CursorUtil.getColumnIndexOrThrow(_cursor, "currentProgress");
          final int _cursorIndexOfIsUnlocked = CursorUtil.getColumnIndexOrThrow(_cursor, "isUnlocked");
          final int _cursorIndexOfUnlockedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "unlockedAt");
          final int _cursorIndexOfRewardType = CursorUtil.getColumnIndexOrThrow(_cursor, "rewardType");
          final int _cursorIndexOfRewardValue = CursorUtil.getColumnIndexOrThrow(_cursor, "rewardValue");
          final int _cursorIndexOfRarity = CursorUtil.getColumnIndexOrThrow(_cursor, "rarity");
          final List<AchievementEntity> _result = new ArrayList<AchievementEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AchievementEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpIconId;
            _tmpIconId = _cursor.getString(_cursorIndexOfIconId);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final int _tmpRequirement;
            _tmpRequirement = _cursor.getInt(_cursorIndexOfRequirement);
            final int _tmpCurrentProgress;
            _tmpCurrentProgress = _cursor.getInt(_cursorIndexOfCurrentProgress);
            final boolean _tmpIsUnlocked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsUnlocked);
            _tmpIsUnlocked = _tmp != 0;
            final Long _tmpUnlockedAt;
            if (_cursor.isNull(_cursorIndexOfUnlockedAt)) {
              _tmpUnlockedAt = null;
            } else {
              _tmpUnlockedAt = _cursor.getLong(_cursorIndexOfUnlockedAt);
            }
            final String _tmpRewardType;
            _tmpRewardType = _cursor.getString(_cursorIndexOfRewardType);
            final String _tmpRewardValue;
            _tmpRewardValue = _cursor.getString(_cursorIndexOfRewardValue);
            final String _tmpRarity;
            _tmpRarity = _cursor.getString(_cursorIndexOfRarity);
            _item = new AchievementEntity(_tmpId,_tmpName,_tmpDescription,_tmpIconId,_tmpCategory,_tmpRequirement,_tmpCurrentProgress,_tmpIsUnlocked,_tmpUnlockedAt,_tmpRewardType,_tmpRewardValue,_tmpRarity);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Integer> getUnlockedCount() {
    final String _sql = "SELECT COUNT(*) FROM achievements WHERE isUnlocked = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"achievements"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<StreakHistoryEntity>> getStreakHistory() {
    final String _sql = "SELECT * FROM streak_history ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"streak_history"}, new Callable<List<StreakHistoryEntity>>() {
      @Override
      @NonNull
      public List<StreakHistoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfActivitiesCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "activitiesCompleted");
          final int _cursorIndexOfPointsEarned = CursorUtil.getColumnIndexOrThrow(_cursor, "pointsEarned");
          final int _cursorIndexOfStreakDay = CursorUtil.getColumnIndexOrThrow(_cursor, "streakDay");
          final List<StreakHistoryEntity> _result = new ArrayList<StreakHistoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final StreakHistoryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final String _tmpActivitiesCompleted;
            _tmpActivitiesCompleted = _cursor.getString(_cursorIndexOfActivitiesCompleted);
            final int _tmpPointsEarned;
            _tmpPointsEarned = _cursor.getInt(_cursorIndexOfPointsEarned);
            final int _tmpStreakDay;
            _tmpStreakDay = _cursor.getInt(_cursorIndexOfStreakDay);
            _item = new StreakHistoryEntity(_tmpId,_tmpDate,_tmpActivitiesCompleted,_tmpPointsEarned,_tmpStreakDay);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getStreakForDate(final long date,
      final Continuation<? super StreakHistoryEntity> $completion) {
    final String _sql = "SELECT * FROM streak_history WHERE date = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, date);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<StreakHistoryEntity>() {
      @Override
      @Nullable
      public StreakHistoryEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfActivitiesCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "activitiesCompleted");
          final int _cursorIndexOfPointsEarned = CursorUtil.getColumnIndexOrThrow(_cursor, "pointsEarned");
          final int _cursorIndexOfStreakDay = CursorUtil.getColumnIndexOrThrow(_cursor, "streakDay");
          final StreakHistoryEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final String _tmpActivitiesCompleted;
            _tmpActivitiesCompleted = _cursor.getString(_cursorIndexOfActivitiesCompleted);
            final int _tmpPointsEarned;
            _tmpPointsEarned = _cursor.getInt(_cursorIndexOfPointsEarned);
            final int _tmpStreakDay;
            _tmpStreakDay = _cursor.getInt(_cursorIndexOfStreakDay);
            _result = new StreakHistoryEntity(_tmpId,_tmpDate,_tmpActivitiesCompleted,_tmpPointsEarned,_tmpStreakDay);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<StreakHistoryEntity>> getRecentStreakHistory(final int days) {
    final String _sql = "SELECT * FROM streak_history ORDER BY date DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, days);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"streak_history"}, new Callable<List<StreakHistoryEntity>>() {
      @Override
      @NonNull
      public List<StreakHistoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfActivitiesCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "activitiesCompleted");
          final int _cursorIndexOfPointsEarned = CursorUtil.getColumnIndexOrThrow(_cursor, "pointsEarned");
          final int _cursorIndexOfStreakDay = CursorUtil.getColumnIndexOrThrow(_cursor, "streakDay");
          final List<StreakHistoryEntity> _result = new ArrayList<StreakHistoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final StreakHistoryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final String _tmpActivitiesCompleted;
            _tmpActivitiesCompleted = _cursor.getString(_cursorIndexOfActivitiesCompleted);
            final int _tmpPointsEarned;
            _tmpPointsEarned = _cursor.getInt(_cursorIndexOfPointsEarned);
            final int _tmpStreakDay;
            _tmpStreakDay = _cursor.getInt(_cursorIndexOfStreakDay);
            _item = new StreakHistoryEntity(_tmpId,_tmpDate,_tmpActivitiesCompleted,_tmpPointsEarned,_tmpStreakDay);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getStreakDaysInRange(final long startDate,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM streak_history WHERE date >= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDate);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<LeaderboardEntryEntity>> getLeaderboard() {
    final String _sql = "SELECT * FROM leaderboard ORDER BY totalPoints DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"leaderboard"}, new Callable<List<LeaderboardEntryEntity>>() {
      @Override
      @NonNull
      public List<LeaderboardEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfOdId = CursorUtil.getColumnIndexOrThrow(_cursor, "odId");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfAvatarId = CursorUtil.getColumnIndexOrThrow(_cursor, "avatarId");
          final int _cursorIndexOfTitleId = CursorUtil.getColumnIndexOrThrow(_cursor, "titleId");
          final int _cursorIndexOfTotalPoints = CursorUtil.getColumnIndexOrThrow(_cursor, "totalPoints");
          final int _cursorIndexOfWeeklyPoints = CursorUtil.getColumnIndexOrThrow(_cursor, "weeklyPoints");
          final int _cursorIndexOfCurrentStreak = CursorUtil.getColumnIndexOrThrow(_cursor, "currentStreak");
          final int _cursorIndexOfRank = CursorUtil.getColumnIndexOrThrow(_cursor, "rank");
          final int _cursorIndexOfPreviousRank = CursorUtil.getColumnIndexOrThrow(_cursor, "previousRank");
          final int _cursorIndexOfIsCurrentUser = CursorUtil.getColumnIndexOrThrow(_cursor, "isCurrentUser");
          final int _cursorIndexOfLastActiveAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastActiveAt");
          final int _cursorIndexOfBoostsReceived = CursorUtil.getColumnIndexOrThrow(_cursor, "boostsReceived");
          final int _cursorIndexOfCongratsReceived = CursorUtil.getColumnIndexOrThrow(_cursor, "congratsReceived");
          final List<LeaderboardEntryEntity> _result = new ArrayList<LeaderboardEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LeaderboardEntryEntity _item;
            final String _tmpOdId;
            _tmpOdId = _cursor.getString(_cursorIndexOfOdId);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpAvatarId;
            _tmpAvatarId = _cursor.getString(_cursorIndexOfAvatarId);
            final String _tmpTitleId;
            _tmpTitleId = _cursor.getString(_cursorIndexOfTitleId);
            final int _tmpTotalPoints;
            _tmpTotalPoints = _cursor.getInt(_cursorIndexOfTotalPoints);
            final int _tmpWeeklyPoints;
            _tmpWeeklyPoints = _cursor.getInt(_cursorIndexOfWeeklyPoints);
            final int _tmpCurrentStreak;
            _tmpCurrentStreak = _cursor.getInt(_cursorIndexOfCurrentStreak);
            final int _tmpRank;
            _tmpRank = _cursor.getInt(_cursorIndexOfRank);
            final int _tmpPreviousRank;
            _tmpPreviousRank = _cursor.getInt(_cursorIndexOfPreviousRank);
            final boolean _tmpIsCurrentUser;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCurrentUser);
            _tmpIsCurrentUser = _tmp != 0;
            final long _tmpLastActiveAt;
            _tmpLastActiveAt = _cursor.getLong(_cursorIndexOfLastActiveAt);
            final int _tmpBoostsReceived;
            _tmpBoostsReceived = _cursor.getInt(_cursorIndexOfBoostsReceived);
            final int _tmpCongratsReceived;
            _tmpCongratsReceived = _cursor.getInt(_cursorIndexOfCongratsReceived);
            _item = new LeaderboardEntryEntity(_tmpOdId,_tmpDisplayName,_tmpAvatarId,_tmpTitleId,_tmpTotalPoints,_tmpWeeklyPoints,_tmpCurrentStreak,_tmpRank,_tmpPreviousRank,_tmpIsCurrentUser,_tmpLastActiveAt,_tmpBoostsReceived,_tmpCongratsReceived);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<LeaderboardEntryEntity>> getWeeklyLeaderboard() {
    final String _sql = "SELECT * FROM leaderboard ORDER BY weeklyPoints DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"leaderboard"}, new Callable<List<LeaderboardEntryEntity>>() {
      @Override
      @NonNull
      public List<LeaderboardEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfOdId = CursorUtil.getColumnIndexOrThrow(_cursor, "odId");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfAvatarId = CursorUtil.getColumnIndexOrThrow(_cursor, "avatarId");
          final int _cursorIndexOfTitleId = CursorUtil.getColumnIndexOrThrow(_cursor, "titleId");
          final int _cursorIndexOfTotalPoints = CursorUtil.getColumnIndexOrThrow(_cursor, "totalPoints");
          final int _cursorIndexOfWeeklyPoints = CursorUtil.getColumnIndexOrThrow(_cursor, "weeklyPoints");
          final int _cursorIndexOfCurrentStreak = CursorUtil.getColumnIndexOrThrow(_cursor, "currentStreak");
          final int _cursorIndexOfRank = CursorUtil.getColumnIndexOrThrow(_cursor, "rank");
          final int _cursorIndexOfPreviousRank = CursorUtil.getColumnIndexOrThrow(_cursor, "previousRank");
          final int _cursorIndexOfIsCurrentUser = CursorUtil.getColumnIndexOrThrow(_cursor, "isCurrentUser");
          final int _cursorIndexOfLastActiveAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastActiveAt");
          final int _cursorIndexOfBoostsReceived = CursorUtil.getColumnIndexOrThrow(_cursor, "boostsReceived");
          final int _cursorIndexOfCongratsReceived = CursorUtil.getColumnIndexOrThrow(_cursor, "congratsReceived");
          final List<LeaderboardEntryEntity> _result = new ArrayList<LeaderboardEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LeaderboardEntryEntity _item;
            final String _tmpOdId;
            _tmpOdId = _cursor.getString(_cursorIndexOfOdId);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpAvatarId;
            _tmpAvatarId = _cursor.getString(_cursorIndexOfAvatarId);
            final String _tmpTitleId;
            _tmpTitleId = _cursor.getString(_cursorIndexOfTitleId);
            final int _tmpTotalPoints;
            _tmpTotalPoints = _cursor.getInt(_cursorIndexOfTotalPoints);
            final int _tmpWeeklyPoints;
            _tmpWeeklyPoints = _cursor.getInt(_cursorIndexOfWeeklyPoints);
            final int _tmpCurrentStreak;
            _tmpCurrentStreak = _cursor.getInt(_cursorIndexOfCurrentStreak);
            final int _tmpRank;
            _tmpRank = _cursor.getInt(_cursorIndexOfRank);
            final int _tmpPreviousRank;
            _tmpPreviousRank = _cursor.getInt(_cursorIndexOfPreviousRank);
            final boolean _tmpIsCurrentUser;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCurrentUser);
            _tmpIsCurrentUser = _tmp != 0;
            final long _tmpLastActiveAt;
            _tmpLastActiveAt = _cursor.getLong(_cursorIndexOfLastActiveAt);
            final int _tmpBoostsReceived;
            _tmpBoostsReceived = _cursor.getInt(_cursorIndexOfBoostsReceived);
            final int _tmpCongratsReceived;
            _tmpCongratsReceived = _cursor.getInt(_cursorIndexOfCongratsReceived);
            _item = new LeaderboardEntryEntity(_tmpOdId,_tmpDisplayName,_tmpAvatarId,_tmpTitleId,_tmpTotalPoints,_tmpWeeklyPoints,_tmpCurrentStreak,_tmpRank,_tmpPreviousRank,_tmpIsCurrentUser,_tmpLastActiveAt,_tmpBoostsReceived,_tmpCongratsReceived);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<LeaderboardEntryEntity> getCurrentUserRank() {
    final String _sql = "SELECT * FROM leaderboard WHERE isCurrentUser = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"leaderboard"}, new Callable<LeaderboardEntryEntity>() {
      @Override
      @Nullable
      public LeaderboardEntryEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfOdId = CursorUtil.getColumnIndexOrThrow(_cursor, "odId");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfAvatarId = CursorUtil.getColumnIndexOrThrow(_cursor, "avatarId");
          final int _cursorIndexOfTitleId = CursorUtil.getColumnIndexOrThrow(_cursor, "titleId");
          final int _cursorIndexOfTotalPoints = CursorUtil.getColumnIndexOrThrow(_cursor, "totalPoints");
          final int _cursorIndexOfWeeklyPoints = CursorUtil.getColumnIndexOrThrow(_cursor, "weeklyPoints");
          final int _cursorIndexOfCurrentStreak = CursorUtil.getColumnIndexOrThrow(_cursor, "currentStreak");
          final int _cursorIndexOfRank = CursorUtil.getColumnIndexOrThrow(_cursor, "rank");
          final int _cursorIndexOfPreviousRank = CursorUtil.getColumnIndexOrThrow(_cursor, "previousRank");
          final int _cursorIndexOfIsCurrentUser = CursorUtil.getColumnIndexOrThrow(_cursor, "isCurrentUser");
          final int _cursorIndexOfLastActiveAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastActiveAt");
          final int _cursorIndexOfBoostsReceived = CursorUtil.getColumnIndexOrThrow(_cursor, "boostsReceived");
          final int _cursorIndexOfCongratsReceived = CursorUtil.getColumnIndexOrThrow(_cursor, "congratsReceived");
          final LeaderboardEntryEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpOdId;
            _tmpOdId = _cursor.getString(_cursorIndexOfOdId);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpAvatarId;
            _tmpAvatarId = _cursor.getString(_cursorIndexOfAvatarId);
            final String _tmpTitleId;
            _tmpTitleId = _cursor.getString(_cursorIndexOfTitleId);
            final int _tmpTotalPoints;
            _tmpTotalPoints = _cursor.getInt(_cursorIndexOfTotalPoints);
            final int _tmpWeeklyPoints;
            _tmpWeeklyPoints = _cursor.getInt(_cursorIndexOfWeeklyPoints);
            final int _tmpCurrentStreak;
            _tmpCurrentStreak = _cursor.getInt(_cursorIndexOfCurrentStreak);
            final int _tmpRank;
            _tmpRank = _cursor.getInt(_cursorIndexOfRank);
            final int _tmpPreviousRank;
            _tmpPreviousRank = _cursor.getInt(_cursorIndexOfPreviousRank);
            final boolean _tmpIsCurrentUser;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCurrentUser);
            _tmpIsCurrentUser = _tmp != 0;
            final long _tmpLastActiveAt;
            _tmpLastActiveAt = _cursor.getLong(_cursorIndexOfLastActiveAt);
            final int _tmpBoostsReceived;
            _tmpBoostsReceived = _cursor.getInt(_cursorIndexOfBoostsReceived);
            final int _tmpCongratsReceived;
            _tmpCongratsReceived = _cursor.getInt(_cursorIndexOfCongratsReceived);
            _result = new LeaderboardEntryEntity(_tmpOdId,_tmpDisplayName,_tmpAvatarId,_tmpTitleId,_tmpTotalPoints,_tmpWeeklyPoints,_tmpCurrentStreak,_tmpRank,_tmpPreviousRank,_tmpIsCurrentUser,_tmpLastActiveAt,_tmpBoostsReceived,_tmpCongratsReceived);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<PeerInteractionEntity>> getPeerInteractions() {
    final String _sql = "SELECT * FROM peer_interactions ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"peer_interactions"}, new Callable<List<PeerInteractionEntity>>() {
      @Override
      @NonNull
      public List<PeerInteractionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPeerId = CursorUtil.getColumnIndexOrThrow(_cursor, "peerId");
          final int _cursorIndexOfInteractionType = CursorUtil.getColumnIndexOrThrow(_cursor, "interactionType");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "message");
          final List<PeerInteractionEntity> _result = new ArrayList<PeerInteractionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PeerInteractionEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPeerId;
            _tmpPeerId = _cursor.getString(_cursorIndexOfPeerId);
            final String _tmpInteractionType;
            _tmpInteractionType = _cursor.getString(_cursorIndexOfInteractionType);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpMessage;
            if (_cursor.isNull(_cursorIndexOfMessage)) {
              _tmpMessage = null;
            } else {
              _tmpMessage = _cursor.getString(_cursorIndexOfMessage);
            }
            _item = new PeerInteractionEntity(_tmpId,_tmpPeerId,_tmpInteractionType,_tmpTimestamp,_tmpMessage);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getInteractionCount(final String peerId, final String type, final long since,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM peer_interactions WHERE peerId = ? AND interactionType = ? AND timestamp >= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, peerId);
    _argIndex = 2;
    _statement.bindString(_argIndex, type);
    _argIndex = 3;
    _statement.bindLong(_argIndex, since);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<MotivationalMessageEntity>> getMotivationalMessages() {
    final String _sql = "SELECT * FROM motivational_messages ORDER BY receivedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"motivational_messages"}, new Callable<List<MotivationalMessageEntity>>() {
      @Override
      @NonNull
      public List<MotivationalMessageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSenderId = CursorUtil.getColumnIndexOrThrow(_cursor, "senderId");
          final int _cursorIndexOfSenderName = CursorUtil.getColumnIndexOrThrow(_cursor, "senderName");
          final int _cursorIndexOfMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "message");
          final int _cursorIndexOfMessageType = CursorUtil.getColumnIndexOrThrow(_cursor, "messageType");
          final int _cursorIndexOfIsRead = CursorUtil.getColumnIndexOrThrow(_cursor, "isRead");
          final int _cursorIndexOfReceivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "receivedAt");
          final List<MotivationalMessageEntity> _result = new ArrayList<MotivationalMessageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MotivationalMessageEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpSenderId;
            _tmpSenderId = _cursor.getString(_cursorIndexOfSenderId);
            final String _tmpSenderName;
            _tmpSenderName = _cursor.getString(_cursorIndexOfSenderName);
            final String _tmpMessage;
            _tmpMessage = _cursor.getString(_cursorIndexOfMessage);
            final String _tmpMessageType;
            _tmpMessageType = _cursor.getString(_cursorIndexOfMessageType);
            final boolean _tmpIsRead;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsRead);
            _tmpIsRead = _tmp != 0;
            final long _tmpReceivedAt;
            _tmpReceivedAt = _cursor.getLong(_cursorIndexOfReceivedAt);
            _item = new MotivationalMessageEntity(_tmpId,_tmpSenderId,_tmpSenderName,_tmpMessage,_tmpMessageType,_tmpIsRead,_tmpReceivedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<MotivationalMessageEntity>> getUnreadMotivationalMessages() {
    final String _sql = "SELECT * FROM motivational_messages WHERE isRead = 0 ORDER BY receivedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"motivational_messages"}, new Callable<List<MotivationalMessageEntity>>() {
      @Override
      @NonNull
      public List<MotivationalMessageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSenderId = CursorUtil.getColumnIndexOrThrow(_cursor, "senderId");
          final int _cursorIndexOfSenderName = CursorUtil.getColumnIndexOrThrow(_cursor, "senderName");
          final int _cursorIndexOfMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "message");
          final int _cursorIndexOfMessageType = CursorUtil.getColumnIndexOrThrow(_cursor, "messageType");
          final int _cursorIndexOfIsRead = CursorUtil.getColumnIndexOrThrow(_cursor, "isRead");
          final int _cursorIndexOfReceivedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "receivedAt");
          final List<MotivationalMessageEntity> _result = new ArrayList<MotivationalMessageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MotivationalMessageEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpSenderId;
            _tmpSenderId = _cursor.getString(_cursorIndexOfSenderId);
            final String _tmpSenderName;
            _tmpSenderName = _cursor.getString(_cursorIndexOfSenderName);
            final String _tmpMessage;
            _tmpMessage = _cursor.getString(_cursorIndexOfMessage);
            final String _tmpMessageType;
            _tmpMessageType = _cursor.getString(_cursorIndexOfMessageType);
            final boolean _tmpIsRead;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsRead);
            _tmpIsRead = _tmp != 0;
            final long _tmpReceivedAt;
            _tmpReceivedAt = _cursor.getLong(_cursorIndexOfReceivedAt);
            _item = new MotivationalMessageEntity(_tmpId,_tmpSenderId,_tmpSenderName,_tmpMessage,_tmpMessageType,_tmpIsRead,_tmpReceivedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Integer> getUnreadMessageCount() {
    final String _sql = "SELECT COUNT(*) FROM motivational_messages WHERE isRead = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"motivational_messages"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
