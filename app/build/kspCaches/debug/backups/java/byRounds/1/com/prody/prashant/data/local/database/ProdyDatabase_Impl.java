package com.prody.prashant.data.local.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.prody.prashant.data.local.dao.FutureMessageDao;
import com.prody.prashant.data.local.dao.FutureMessageDao_Impl;
import com.prody.prashant.data.local.dao.IdiomDao;
import com.prody.prashant.data.local.dao.IdiomDao_Impl;
import com.prody.prashant.data.local.dao.JournalDao;
import com.prody.prashant.data.local.dao.JournalDao_Impl;
import com.prody.prashant.data.local.dao.PhraseDao;
import com.prody.prashant.data.local.dao.PhraseDao_Impl;
import com.prody.prashant.data.local.dao.ProverbDao;
import com.prody.prashant.data.local.dao.ProverbDao_Impl;
import com.prody.prashant.data.local.dao.QuoteDao;
import com.prody.prashant.data.local.dao.QuoteDao_Impl;
import com.prody.prashant.data.local.dao.UserDao;
import com.prody.prashant.data.local.dao.UserDao_Impl;
import com.prody.prashant.data.local.dao.VocabularyDao;
import com.prody.prashant.data.local.dao.VocabularyDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ProdyDatabase_Impl extends ProdyDatabase {
  private volatile JournalDao _journalDao;

  private volatile FutureMessageDao _futureMessageDao;

  private volatile VocabularyDao _vocabularyDao;

  private volatile QuoteDao _quoteDao;

  private volatile ProverbDao _proverbDao;

  private volatile IdiomDao _idiomDao;

  private volatile PhraseDao _phraseDao;

  private volatile UserDao _userDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `journal_entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `content` TEXT NOT NULL, `mood` TEXT NOT NULL, `moodIntensity` INTEGER NOT NULL, `buddhaResponse` TEXT, `tags` TEXT NOT NULL, `isBookmarked` INTEGER NOT NULL, `wordCount` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `future_messages` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `content` TEXT NOT NULL, `deliveryDate` INTEGER NOT NULL, `isDelivered` INTEGER NOT NULL, `isRead` INTEGER NOT NULL, `category` TEXT NOT NULL, `attachedGoal` TEXT, `createdAt` INTEGER NOT NULL, `deliveredAt` INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `vocabulary` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `word` TEXT NOT NULL, `definition` TEXT NOT NULL, `pronunciation` TEXT NOT NULL, `partOfSpeech` TEXT NOT NULL, `exampleSentence` TEXT NOT NULL, `synonyms` TEXT NOT NULL, `antonyms` TEXT NOT NULL, `origin` TEXT NOT NULL, `difficulty` INTEGER NOT NULL, `category` TEXT NOT NULL, `isLearned` INTEGER NOT NULL, `learnedAt` INTEGER, `reviewCount` INTEGER NOT NULL, `lastReviewedAt` INTEGER, `nextReviewAt` INTEGER, `masteryLevel` INTEGER NOT NULL, `isFavorite` INTEGER NOT NULL, `shownAsDaily` INTEGER NOT NULL, `shownAt` INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `quotes` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `content` TEXT NOT NULL, `author` TEXT NOT NULL, `source` TEXT NOT NULL, `category` TEXT NOT NULL, `tags` TEXT NOT NULL, `isFavorite` INTEGER NOT NULL, `shownAsDaily` INTEGER NOT NULL, `shownAt` INTEGER, `reflectionPrompt` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `proverbs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `content` TEXT NOT NULL, `meaning` TEXT NOT NULL, `origin` TEXT NOT NULL, `usage` TEXT NOT NULL, `category` TEXT NOT NULL, `isFavorite` INTEGER NOT NULL, `shownAsDaily` INTEGER NOT NULL, `shownAt` INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `idioms` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `phrase` TEXT NOT NULL, `meaning` TEXT NOT NULL, `origin` TEXT NOT NULL, `exampleSentence` TEXT NOT NULL, `category` TEXT NOT NULL, `isFavorite` INTEGER NOT NULL, `shownAsDaily` INTEGER NOT NULL, `shownAt` INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `phrases` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `phrase` TEXT NOT NULL, `meaning` TEXT NOT NULL, `usage` TEXT NOT NULL, `exampleSentence` TEXT NOT NULL, `formality` TEXT NOT NULL, `category` TEXT NOT NULL, `isFavorite` INTEGER NOT NULL, `shownAsDaily` INTEGER NOT NULL, `shownAt` INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `user_profile` (`id` INTEGER NOT NULL, `displayName` TEXT NOT NULL, `bio` TEXT NOT NULL, `avatarId` TEXT NOT NULL, `bannerId` TEXT NOT NULL, `titleId` TEXT NOT NULL, `totalPoints` INTEGER NOT NULL, `currentStreak` INTEGER NOT NULL, `longestStreak` INTEGER NOT NULL, `lastActiveDate` INTEGER NOT NULL, `joinedAt` INTEGER NOT NULL, `wordsLearned` INTEGER NOT NULL, `journalEntriesCount` INTEGER NOT NULL, `futureMessagesCount` INTEGER NOT NULL, `quotesReflected` INTEGER NOT NULL, `totalReflectionTime` INTEGER NOT NULL, `preferredWisdomCategories` TEXT NOT NULL, `dailyGoalMinutes` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `achievements` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, `iconId` TEXT NOT NULL, `category` TEXT NOT NULL, `requirement` INTEGER NOT NULL, `currentProgress` INTEGER NOT NULL, `isUnlocked` INTEGER NOT NULL, `unlockedAt` INTEGER, `rewardType` TEXT NOT NULL, `rewardValue` TEXT NOT NULL, `rarity` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `user_stats` (`id` INTEGER NOT NULL, `dailyPointsEarned` INTEGER NOT NULL, `weeklyPointsEarned` INTEGER NOT NULL, `monthlyPointsEarned` INTEGER NOT NULL, `dailyWordsLearned` INTEGER NOT NULL, `weeklyWordsLearned` INTEGER NOT NULL, `monthlyWordsLearned` INTEGER NOT NULL, `dailyJournalEntries` INTEGER NOT NULL, `weeklyJournalEntries` INTEGER NOT NULL, `monthlyJournalEntries` INTEGER NOT NULL, `lastResetDate` INTEGER NOT NULL, `weekStartDate` INTEGER NOT NULL, `monthStartDate` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `streak_history` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` INTEGER NOT NULL, `activitiesCompleted` TEXT NOT NULL, `pointsEarned` INTEGER NOT NULL, `streakDay` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `leaderboard` (`odId` TEXT NOT NULL, `displayName` TEXT NOT NULL, `avatarId` TEXT NOT NULL, `titleId` TEXT NOT NULL, `totalPoints` INTEGER NOT NULL, `weeklyPoints` INTEGER NOT NULL, `currentStreak` INTEGER NOT NULL, `rank` INTEGER NOT NULL, `previousRank` INTEGER NOT NULL, `isCurrentUser` INTEGER NOT NULL, `lastActiveAt` INTEGER NOT NULL, `boostsReceived` INTEGER NOT NULL, `congratsReceived` INTEGER NOT NULL, PRIMARY KEY(`odId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `peer_interactions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `peerId` TEXT NOT NULL, `interactionType` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `message` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `motivational_messages` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `senderId` TEXT NOT NULL, `senderName` TEXT NOT NULL, `message` TEXT NOT NULL, `messageType` TEXT NOT NULL, `isRead` INTEGER NOT NULL, `receivedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '0c934b5f20a4660b2cb4564ebef29bf0')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `journal_entries`");
        db.execSQL("DROP TABLE IF EXISTS `future_messages`");
        db.execSQL("DROP TABLE IF EXISTS `vocabulary`");
        db.execSQL("DROP TABLE IF EXISTS `quotes`");
        db.execSQL("DROP TABLE IF EXISTS `proverbs`");
        db.execSQL("DROP TABLE IF EXISTS `idioms`");
        db.execSQL("DROP TABLE IF EXISTS `phrases`");
        db.execSQL("DROP TABLE IF EXISTS `user_profile`");
        db.execSQL("DROP TABLE IF EXISTS `achievements`");
        db.execSQL("DROP TABLE IF EXISTS `user_stats`");
        db.execSQL("DROP TABLE IF EXISTS `streak_history`");
        db.execSQL("DROP TABLE IF EXISTS `leaderboard`");
        db.execSQL("DROP TABLE IF EXISTS `peer_interactions`");
        db.execSQL("DROP TABLE IF EXISTS `motivational_messages`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsJournalEntries = new HashMap<String, TableInfo.Column>(10);
        _columnsJournalEntries.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJournalEntries.put("content", new TableInfo.Column("content", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJournalEntries.put("mood", new TableInfo.Column("mood", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJournalEntries.put("moodIntensity", new TableInfo.Column("moodIntensity", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJournalEntries.put("buddhaResponse", new TableInfo.Column("buddhaResponse", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJournalEntries.put("tags", new TableInfo.Column("tags", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJournalEntries.put("isBookmarked", new TableInfo.Column("isBookmarked", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJournalEntries.put("wordCount", new TableInfo.Column("wordCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJournalEntries.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJournalEntries.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysJournalEntries = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesJournalEntries = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoJournalEntries = new TableInfo("journal_entries", _columnsJournalEntries, _foreignKeysJournalEntries, _indicesJournalEntries);
        final TableInfo _existingJournalEntries = TableInfo.read(db, "journal_entries");
        if (!_infoJournalEntries.equals(_existingJournalEntries)) {
          return new RoomOpenHelper.ValidationResult(false, "journal_entries(com.prody.prashant.data.local.entity.JournalEntryEntity).\n"
                  + " Expected:\n" + _infoJournalEntries + "\n"
                  + " Found:\n" + _existingJournalEntries);
        }
        final HashMap<String, TableInfo.Column> _columnsFutureMessages = new HashMap<String, TableInfo.Column>(10);
        _columnsFutureMessages.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFutureMessages.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFutureMessages.put("content", new TableInfo.Column("content", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFutureMessages.put("deliveryDate", new TableInfo.Column("deliveryDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFutureMessages.put("isDelivered", new TableInfo.Column("isDelivered", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFutureMessages.put("isRead", new TableInfo.Column("isRead", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFutureMessages.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFutureMessages.put("attachedGoal", new TableInfo.Column("attachedGoal", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFutureMessages.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFutureMessages.put("deliveredAt", new TableInfo.Column("deliveredAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFutureMessages = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFutureMessages = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoFutureMessages = new TableInfo("future_messages", _columnsFutureMessages, _foreignKeysFutureMessages, _indicesFutureMessages);
        final TableInfo _existingFutureMessages = TableInfo.read(db, "future_messages");
        if (!_infoFutureMessages.equals(_existingFutureMessages)) {
          return new RoomOpenHelper.ValidationResult(false, "future_messages(com.prody.prashant.data.local.entity.FutureMessageEntity).\n"
                  + " Expected:\n" + _infoFutureMessages + "\n"
                  + " Found:\n" + _existingFutureMessages);
        }
        final HashMap<String, TableInfo.Column> _columnsVocabulary = new HashMap<String, TableInfo.Column>(20);
        _columnsVocabulary.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("word", new TableInfo.Column("word", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("definition", new TableInfo.Column("definition", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("pronunciation", new TableInfo.Column("pronunciation", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("partOfSpeech", new TableInfo.Column("partOfSpeech", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("exampleSentence", new TableInfo.Column("exampleSentence", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("synonyms", new TableInfo.Column("synonyms", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("antonyms", new TableInfo.Column("antonyms", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("origin", new TableInfo.Column("origin", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("difficulty", new TableInfo.Column("difficulty", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("isLearned", new TableInfo.Column("isLearned", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("learnedAt", new TableInfo.Column("learnedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("reviewCount", new TableInfo.Column("reviewCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("lastReviewedAt", new TableInfo.Column("lastReviewedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("nextReviewAt", new TableInfo.Column("nextReviewAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("masteryLevel", new TableInfo.Column("masteryLevel", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("isFavorite", new TableInfo.Column("isFavorite", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("shownAsDaily", new TableInfo.Column("shownAsDaily", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVocabulary.put("shownAt", new TableInfo.Column("shownAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysVocabulary = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesVocabulary = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoVocabulary = new TableInfo("vocabulary", _columnsVocabulary, _foreignKeysVocabulary, _indicesVocabulary);
        final TableInfo _existingVocabulary = TableInfo.read(db, "vocabulary");
        if (!_infoVocabulary.equals(_existingVocabulary)) {
          return new RoomOpenHelper.ValidationResult(false, "vocabulary(com.prody.prashant.data.local.entity.VocabularyEntity).\n"
                  + " Expected:\n" + _infoVocabulary + "\n"
                  + " Found:\n" + _existingVocabulary);
        }
        final HashMap<String, TableInfo.Column> _columnsQuotes = new HashMap<String, TableInfo.Column>(10);
        _columnsQuotes.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuotes.put("content", new TableInfo.Column("content", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuotes.put("author", new TableInfo.Column("author", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuotes.put("source", new TableInfo.Column("source", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuotes.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuotes.put("tags", new TableInfo.Column("tags", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuotes.put("isFavorite", new TableInfo.Column("isFavorite", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuotes.put("shownAsDaily", new TableInfo.Column("shownAsDaily", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuotes.put("shownAt", new TableInfo.Column("shownAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQuotes.put("reflectionPrompt", new TableInfo.Column("reflectionPrompt", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysQuotes = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesQuotes = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoQuotes = new TableInfo("quotes", _columnsQuotes, _foreignKeysQuotes, _indicesQuotes);
        final TableInfo _existingQuotes = TableInfo.read(db, "quotes");
        if (!_infoQuotes.equals(_existingQuotes)) {
          return new RoomOpenHelper.ValidationResult(false, "quotes(com.prody.prashant.data.local.entity.QuoteEntity).\n"
                  + " Expected:\n" + _infoQuotes + "\n"
                  + " Found:\n" + _existingQuotes);
        }
        final HashMap<String, TableInfo.Column> _columnsProverbs = new HashMap<String, TableInfo.Column>(9);
        _columnsProverbs.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProverbs.put("content", new TableInfo.Column("content", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProverbs.put("meaning", new TableInfo.Column("meaning", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProverbs.put("origin", new TableInfo.Column("origin", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProverbs.put("usage", new TableInfo.Column("usage", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProverbs.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProverbs.put("isFavorite", new TableInfo.Column("isFavorite", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProverbs.put("shownAsDaily", new TableInfo.Column("shownAsDaily", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsProverbs.put("shownAt", new TableInfo.Column("shownAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysProverbs = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesProverbs = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoProverbs = new TableInfo("proverbs", _columnsProverbs, _foreignKeysProverbs, _indicesProverbs);
        final TableInfo _existingProverbs = TableInfo.read(db, "proverbs");
        if (!_infoProverbs.equals(_existingProverbs)) {
          return new RoomOpenHelper.ValidationResult(false, "proverbs(com.prody.prashant.data.local.entity.ProverbEntity).\n"
                  + " Expected:\n" + _infoProverbs + "\n"
                  + " Found:\n" + _existingProverbs);
        }
        final HashMap<String, TableInfo.Column> _columnsIdioms = new HashMap<String, TableInfo.Column>(9);
        _columnsIdioms.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIdioms.put("phrase", new TableInfo.Column("phrase", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIdioms.put("meaning", new TableInfo.Column("meaning", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIdioms.put("origin", new TableInfo.Column("origin", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIdioms.put("exampleSentence", new TableInfo.Column("exampleSentence", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIdioms.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIdioms.put("isFavorite", new TableInfo.Column("isFavorite", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIdioms.put("shownAsDaily", new TableInfo.Column("shownAsDaily", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIdioms.put("shownAt", new TableInfo.Column("shownAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysIdioms = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesIdioms = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoIdioms = new TableInfo("idioms", _columnsIdioms, _foreignKeysIdioms, _indicesIdioms);
        final TableInfo _existingIdioms = TableInfo.read(db, "idioms");
        if (!_infoIdioms.equals(_existingIdioms)) {
          return new RoomOpenHelper.ValidationResult(false, "idioms(com.prody.prashant.data.local.entity.IdiomEntity).\n"
                  + " Expected:\n" + _infoIdioms + "\n"
                  + " Found:\n" + _existingIdioms);
        }
        final HashMap<String, TableInfo.Column> _columnsPhrases = new HashMap<String, TableInfo.Column>(10);
        _columnsPhrases.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhrases.put("phrase", new TableInfo.Column("phrase", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhrases.put("meaning", new TableInfo.Column("meaning", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhrases.put("usage", new TableInfo.Column("usage", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhrases.put("exampleSentence", new TableInfo.Column("exampleSentence", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhrases.put("formality", new TableInfo.Column("formality", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhrases.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhrases.put("isFavorite", new TableInfo.Column("isFavorite", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhrases.put("shownAsDaily", new TableInfo.Column("shownAsDaily", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPhrases.put("shownAt", new TableInfo.Column("shownAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPhrases = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPhrases = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPhrases = new TableInfo("phrases", _columnsPhrases, _foreignKeysPhrases, _indicesPhrases);
        final TableInfo _existingPhrases = TableInfo.read(db, "phrases");
        if (!_infoPhrases.equals(_existingPhrases)) {
          return new RoomOpenHelper.ValidationResult(false, "phrases(com.prody.prashant.data.local.entity.PhraseEntity).\n"
                  + " Expected:\n" + _infoPhrases + "\n"
                  + " Found:\n" + _existingPhrases);
        }
        final HashMap<String, TableInfo.Column> _columnsUserProfile = new HashMap<String, TableInfo.Column>(18);
        _columnsUserProfile.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("displayName", new TableInfo.Column("displayName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("bio", new TableInfo.Column("bio", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("avatarId", new TableInfo.Column("avatarId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("bannerId", new TableInfo.Column("bannerId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("titleId", new TableInfo.Column("titleId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("totalPoints", new TableInfo.Column("totalPoints", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("currentStreak", new TableInfo.Column("currentStreak", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("longestStreak", new TableInfo.Column("longestStreak", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("lastActiveDate", new TableInfo.Column("lastActiveDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("joinedAt", new TableInfo.Column("joinedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("wordsLearned", new TableInfo.Column("wordsLearned", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("journalEntriesCount", new TableInfo.Column("journalEntriesCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("futureMessagesCount", new TableInfo.Column("futureMessagesCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("quotesReflected", new TableInfo.Column("quotesReflected", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("totalReflectionTime", new TableInfo.Column("totalReflectionTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("preferredWisdomCategories", new TableInfo.Column("preferredWisdomCategories", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserProfile.put("dailyGoalMinutes", new TableInfo.Column("dailyGoalMinutes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUserProfile = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUserProfile = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUserProfile = new TableInfo("user_profile", _columnsUserProfile, _foreignKeysUserProfile, _indicesUserProfile);
        final TableInfo _existingUserProfile = TableInfo.read(db, "user_profile");
        if (!_infoUserProfile.equals(_existingUserProfile)) {
          return new RoomOpenHelper.ValidationResult(false, "user_profile(com.prody.prashant.data.local.entity.UserProfileEntity).\n"
                  + " Expected:\n" + _infoUserProfile + "\n"
                  + " Found:\n" + _existingUserProfile);
        }
        final HashMap<String, TableInfo.Column> _columnsAchievements = new HashMap<String, TableInfo.Column>(12);
        _columnsAchievements.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAchievements.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAchievements.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAchievements.put("iconId", new TableInfo.Column("iconId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAchievements.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAchievements.put("requirement", new TableInfo.Column("requirement", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAchievements.put("currentProgress", new TableInfo.Column("currentProgress", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAchievements.put("isUnlocked", new TableInfo.Column("isUnlocked", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAchievements.put("unlockedAt", new TableInfo.Column("unlockedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAchievements.put("rewardType", new TableInfo.Column("rewardType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAchievements.put("rewardValue", new TableInfo.Column("rewardValue", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAchievements.put("rarity", new TableInfo.Column("rarity", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAchievements = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAchievements = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAchievements = new TableInfo("achievements", _columnsAchievements, _foreignKeysAchievements, _indicesAchievements);
        final TableInfo _existingAchievements = TableInfo.read(db, "achievements");
        if (!_infoAchievements.equals(_existingAchievements)) {
          return new RoomOpenHelper.ValidationResult(false, "achievements(com.prody.prashant.data.local.entity.AchievementEntity).\n"
                  + " Expected:\n" + _infoAchievements + "\n"
                  + " Found:\n" + _existingAchievements);
        }
        final HashMap<String, TableInfo.Column> _columnsUserStats = new HashMap<String, TableInfo.Column>(13);
        _columnsUserStats.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserStats.put("dailyPointsEarned", new TableInfo.Column("dailyPointsEarned", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserStats.put("weeklyPointsEarned", new TableInfo.Column("weeklyPointsEarned", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserStats.put("monthlyPointsEarned", new TableInfo.Column("monthlyPointsEarned", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserStats.put("dailyWordsLearned", new TableInfo.Column("dailyWordsLearned", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserStats.put("weeklyWordsLearned", new TableInfo.Column("weeklyWordsLearned", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserStats.put("monthlyWordsLearned", new TableInfo.Column("monthlyWordsLearned", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserStats.put("dailyJournalEntries", new TableInfo.Column("dailyJournalEntries", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserStats.put("weeklyJournalEntries", new TableInfo.Column("weeklyJournalEntries", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserStats.put("monthlyJournalEntries", new TableInfo.Column("monthlyJournalEntries", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserStats.put("lastResetDate", new TableInfo.Column("lastResetDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserStats.put("weekStartDate", new TableInfo.Column("weekStartDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserStats.put("monthStartDate", new TableInfo.Column("monthStartDate", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUserStats = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUserStats = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUserStats = new TableInfo("user_stats", _columnsUserStats, _foreignKeysUserStats, _indicesUserStats);
        final TableInfo _existingUserStats = TableInfo.read(db, "user_stats");
        if (!_infoUserStats.equals(_existingUserStats)) {
          return new RoomOpenHelper.ValidationResult(false, "user_stats(com.prody.prashant.data.local.entity.UserStatsEntity).\n"
                  + " Expected:\n" + _infoUserStats + "\n"
                  + " Found:\n" + _existingUserStats);
        }
        final HashMap<String, TableInfo.Column> _columnsStreakHistory = new HashMap<String, TableInfo.Column>(5);
        _columnsStreakHistory.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStreakHistory.put("date", new TableInfo.Column("date", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStreakHistory.put("activitiesCompleted", new TableInfo.Column("activitiesCompleted", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStreakHistory.put("pointsEarned", new TableInfo.Column("pointsEarned", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStreakHistory.put("streakDay", new TableInfo.Column("streakDay", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysStreakHistory = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesStreakHistory = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoStreakHistory = new TableInfo("streak_history", _columnsStreakHistory, _foreignKeysStreakHistory, _indicesStreakHistory);
        final TableInfo _existingStreakHistory = TableInfo.read(db, "streak_history");
        if (!_infoStreakHistory.equals(_existingStreakHistory)) {
          return new RoomOpenHelper.ValidationResult(false, "streak_history(com.prody.prashant.data.local.entity.StreakHistoryEntity).\n"
                  + " Expected:\n" + _infoStreakHistory + "\n"
                  + " Found:\n" + _existingStreakHistory);
        }
        final HashMap<String, TableInfo.Column> _columnsLeaderboard = new HashMap<String, TableInfo.Column>(13);
        _columnsLeaderboard.put("odId", new TableInfo.Column("odId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLeaderboard.put("displayName", new TableInfo.Column("displayName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLeaderboard.put("avatarId", new TableInfo.Column("avatarId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLeaderboard.put("titleId", new TableInfo.Column("titleId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLeaderboard.put("totalPoints", new TableInfo.Column("totalPoints", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLeaderboard.put("weeklyPoints", new TableInfo.Column("weeklyPoints", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLeaderboard.put("currentStreak", new TableInfo.Column("currentStreak", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLeaderboard.put("rank", new TableInfo.Column("rank", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLeaderboard.put("previousRank", new TableInfo.Column("previousRank", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLeaderboard.put("isCurrentUser", new TableInfo.Column("isCurrentUser", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLeaderboard.put("lastActiveAt", new TableInfo.Column("lastActiveAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLeaderboard.put("boostsReceived", new TableInfo.Column("boostsReceived", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLeaderboard.put("congratsReceived", new TableInfo.Column("congratsReceived", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLeaderboard = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesLeaderboard = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoLeaderboard = new TableInfo("leaderboard", _columnsLeaderboard, _foreignKeysLeaderboard, _indicesLeaderboard);
        final TableInfo _existingLeaderboard = TableInfo.read(db, "leaderboard");
        if (!_infoLeaderboard.equals(_existingLeaderboard)) {
          return new RoomOpenHelper.ValidationResult(false, "leaderboard(com.prody.prashant.data.local.entity.LeaderboardEntryEntity).\n"
                  + " Expected:\n" + _infoLeaderboard + "\n"
                  + " Found:\n" + _existingLeaderboard);
        }
        final HashMap<String, TableInfo.Column> _columnsPeerInteractions = new HashMap<String, TableInfo.Column>(5);
        _columnsPeerInteractions.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPeerInteractions.put("peerId", new TableInfo.Column("peerId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPeerInteractions.put("interactionType", new TableInfo.Column("interactionType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPeerInteractions.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPeerInteractions.put("message", new TableInfo.Column("message", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPeerInteractions = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPeerInteractions = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPeerInteractions = new TableInfo("peer_interactions", _columnsPeerInteractions, _foreignKeysPeerInteractions, _indicesPeerInteractions);
        final TableInfo _existingPeerInteractions = TableInfo.read(db, "peer_interactions");
        if (!_infoPeerInteractions.equals(_existingPeerInteractions)) {
          return new RoomOpenHelper.ValidationResult(false, "peer_interactions(com.prody.prashant.data.local.entity.PeerInteractionEntity).\n"
                  + " Expected:\n" + _infoPeerInteractions + "\n"
                  + " Found:\n" + _existingPeerInteractions);
        }
        final HashMap<String, TableInfo.Column> _columnsMotivationalMessages = new HashMap<String, TableInfo.Column>(7);
        _columnsMotivationalMessages.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMotivationalMessages.put("senderId", new TableInfo.Column("senderId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMotivationalMessages.put("senderName", new TableInfo.Column("senderName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMotivationalMessages.put("message", new TableInfo.Column("message", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMotivationalMessages.put("messageType", new TableInfo.Column("messageType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMotivationalMessages.put("isRead", new TableInfo.Column("isRead", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMotivationalMessages.put("receivedAt", new TableInfo.Column("receivedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMotivationalMessages = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMotivationalMessages = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMotivationalMessages = new TableInfo("motivational_messages", _columnsMotivationalMessages, _foreignKeysMotivationalMessages, _indicesMotivationalMessages);
        final TableInfo _existingMotivationalMessages = TableInfo.read(db, "motivational_messages");
        if (!_infoMotivationalMessages.equals(_existingMotivationalMessages)) {
          return new RoomOpenHelper.ValidationResult(false, "motivational_messages(com.prody.prashant.data.local.entity.MotivationalMessageEntity).\n"
                  + " Expected:\n" + _infoMotivationalMessages + "\n"
                  + " Found:\n" + _existingMotivationalMessages);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "0c934b5f20a4660b2cb4564ebef29bf0", "3bdc909cac9836942cb64560222b4476");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "journal_entries","future_messages","vocabulary","quotes","proverbs","idioms","phrases","user_profile","achievements","user_stats","streak_history","leaderboard","peer_interactions","motivational_messages");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `journal_entries`");
      _db.execSQL("DELETE FROM `future_messages`");
      _db.execSQL("DELETE FROM `vocabulary`");
      _db.execSQL("DELETE FROM `quotes`");
      _db.execSQL("DELETE FROM `proverbs`");
      _db.execSQL("DELETE FROM `idioms`");
      _db.execSQL("DELETE FROM `phrases`");
      _db.execSQL("DELETE FROM `user_profile`");
      _db.execSQL("DELETE FROM `achievements`");
      _db.execSQL("DELETE FROM `user_stats`");
      _db.execSQL("DELETE FROM `streak_history`");
      _db.execSQL("DELETE FROM `leaderboard`");
      _db.execSQL("DELETE FROM `peer_interactions`");
      _db.execSQL("DELETE FROM `motivational_messages`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(JournalDao.class, JournalDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(FutureMessageDao.class, FutureMessageDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(VocabularyDao.class, VocabularyDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(QuoteDao.class, QuoteDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ProverbDao.class, ProverbDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(IdiomDao.class, IdiomDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PhraseDao.class, PhraseDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(UserDao.class, UserDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public JournalDao journalDao() {
    if (_journalDao != null) {
      return _journalDao;
    } else {
      synchronized(this) {
        if(_journalDao == null) {
          _journalDao = new JournalDao_Impl(this);
        }
        return _journalDao;
      }
    }
  }

  @Override
  public FutureMessageDao futureMessageDao() {
    if (_futureMessageDao != null) {
      return _futureMessageDao;
    } else {
      synchronized(this) {
        if(_futureMessageDao == null) {
          _futureMessageDao = new FutureMessageDao_Impl(this);
        }
        return _futureMessageDao;
      }
    }
  }

  @Override
  public VocabularyDao vocabularyDao() {
    if (_vocabularyDao != null) {
      return _vocabularyDao;
    } else {
      synchronized(this) {
        if(_vocabularyDao == null) {
          _vocabularyDao = new VocabularyDao_Impl(this);
        }
        return _vocabularyDao;
      }
    }
  }

  @Override
  public QuoteDao quoteDao() {
    if (_quoteDao != null) {
      return _quoteDao;
    } else {
      synchronized(this) {
        if(_quoteDao == null) {
          _quoteDao = new QuoteDao_Impl(this);
        }
        return _quoteDao;
      }
    }
  }

  @Override
  public ProverbDao proverbDao() {
    if (_proverbDao != null) {
      return _proverbDao;
    } else {
      synchronized(this) {
        if(_proverbDao == null) {
          _proverbDao = new ProverbDao_Impl(this);
        }
        return _proverbDao;
      }
    }
  }

  @Override
  public IdiomDao idiomDao() {
    if (_idiomDao != null) {
      return _idiomDao;
    } else {
      synchronized(this) {
        if(_idiomDao == null) {
          _idiomDao = new IdiomDao_Impl(this);
        }
        return _idiomDao;
      }
    }
  }

  @Override
  public PhraseDao phraseDao() {
    if (_phraseDao != null) {
      return _phraseDao;
    } else {
      synchronized(this) {
        if(_phraseDao == null) {
          _phraseDao = new PhraseDao_Impl(this);
        }
        return _phraseDao;
      }
    }
  }

  @Override
  public UserDao userDao() {
    if (_userDao != null) {
      return _userDao;
    } else {
      synchronized(this) {
        if(_userDao == null) {
          _userDao = new UserDao_Impl(this);
        }
        return _userDao;
      }
    }
  }
}
