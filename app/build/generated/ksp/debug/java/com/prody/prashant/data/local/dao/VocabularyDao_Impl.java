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
import com.prody.prashant.data.local.entity.VocabularyEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Float;
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
public final class VocabularyDao_Impl implements VocabularyDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<VocabularyEntity> __insertionAdapterOfVocabularyEntity;

  private final EntityDeletionOrUpdateAdapter<VocabularyEntity> __deletionAdapterOfVocabularyEntity;

  private final EntityDeletionOrUpdateAdapter<VocabularyEntity> __updateAdapterOfVocabularyEntity;

  private final SharedSQLiteStatement __preparedStmtOfMarkAsLearned;

  private final SharedSQLiteStatement __preparedStmtOfUpdateFavoriteStatus;

  private final SharedSQLiteStatement __preparedStmtOfMarkAsShownDaily;

  private final SharedSQLiteStatement __preparedStmtOfUpdateReviewProgress;

  public VocabularyDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfVocabularyEntity = new EntityInsertionAdapter<VocabularyEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `vocabulary` (`id`,`word`,`definition`,`pronunciation`,`partOfSpeech`,`exampleSentence`,`synonyms`,`antonyms`,`origin`,`difficulty`,`category`,`isLearned`,`learnedAt`,`reviewCount`,`lastReviewedAt`,`nextReviewAt`,`masteryLevel`,`isFavorite`,`shownAsDaily`,`shownAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VocabularyEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getWord());
        statement.bindString(3, entity.getDefinition());
        statement.bindString(4, entity.getPronunciation());
        statement.bindString(5, entity.getPartOfSpeech());
        statement.bindString(6, entity.getExampleSentence());
        statement.bindString(7, entity.getSynonyms());
        statement.bindString(8, entity.getAntonyms());
        statement.bindString(9, entity.getOrigin());
        statement.bindLong(10, entity.getDifficulty());
        statement.bindString(11, entity.getCategory());
        final int _tmp = entity.isLearned() ? 1 : 0;
        statement.bindLong(12, _tmp);
        if (entity.getLearnedAt() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getLearnedAt());
        }
        statement.bindLong(14, entity.getReviewCount());
        if (entity.getLastReviewedAt() == null) {
          statement.bindNull(15);
        } else {
          statement.bindLong(15, entity.getLastReviewedAt());
        }
        if (entity.getNextReviewAt() == null) {
          statement.bindNull(16);
        } else {
          statement.bindLong(16, entity.getNextReviewAt());
        }
        statement.bindLong(17, entity.getMasteryLevel());
        final int _tmp_1 = entity.isFavorite() ? 1 : 0;
        statement.bindLong(18, _tmp_1);
        final int _tmp_2 = entity.getShownAsDaily() ? 1 : 0;
        statement.bindLong(19, _tmp_2);
        if (entity.getShownAt() == null) {
          statement.bindNull(20);
        } else {
          statement.bindLong(20, entity.getShownAt());
        }
      }
    };
    this.__deletionAdapterOfVocabularyEntity = new EntityDeletionOrUpdateAdapter<VocabularyEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `vocabulary` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VocabularyEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfVocabularyEntity = new EntityDeletionOrUpdateAdapter<VocabularyEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `vocabulary` SET `id` = ?,`word` = ?,`definition` = ?,`pronunciation` = ?,`partOfSpeech` = ?,`exampleSentence` = ?,`synonyms` = ?,`antonyms` = ?,`origin` = ?,`difficulty` = ?,`category` = ?,`isLearned` = ?,`learnedAt` = ?,`reviewCount` = ?,`lastReviewedAt` = ?,`nextReviewAt` = ?,`masteryLevel` = ?,`isFavorite` = ?,`shownAsDaily` = ?,`shownAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VocabularyEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getWord());
        statement.bindString(3, entity.getDefinition());
        statement.bindString(4, entity.getPronunciation());
        statement.bindString(5, entity.getPartOfSpeech());
        statement.bindString(6, entity.getExampleSentence());
        statement.bindString(7, entity.getSynonyms());
        statement.bindString(8, entity.getAntonyms());
        statement.bindString(9, entity.getOrigin());
        statement.bindLong(10, entity.getDifficulty());
        statement.bindString(11, entity.getCategory());
        final int _tmp = entity.isLearned() ? 1 : 0;
        statement.bindLong(12, _tmp);
        if (entity.getLearnedAt() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getLearnedAt());
        }
        statement.bindLong(14, entity.getReviewCount());
        if (entity.getLastReviewedAt() == null) {
          statement.bindNull(15);
        } else {
          statement.bindLong(15, entity.getLastReviewedAt());
        }
        if (entity.getNextReviewAt() == null) {
          statement.bindNull(16);
        } else {
          statement.bindLong(16, entity.getNextReviewAt());
        }
        statement.bindLong(17, entity.getMasteryLevel());
        final int _tmp_1 = entity.isFavorite() ? 1 : 0;
        statement.bindLong(18, _tmp_1);
        final int _tmp_2 = entity.getShownAsDaily() ? 1 : 0;
        statement.bindLong(19, _tmp_2);
        if (entity.getShownAt() == null) {
          statement.bindNull(20);
        } else {
          statement.bindLong(20, entity.getShownAt());
        }
        statement.bindLong(21, entity.getId());
      }
    };
    this.__preparedStmtOfMarkAsLearned = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE vocabulary SET isLearned = 1, learnedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateFavoriteStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE vocabulary SET isFavorite = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkAsShownDaily = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE vocabulary SET shownAsDaily = 1, shownAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateReviewProgress = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE vocabulary SET reviewCount = reviewCount + 1, lastReviewedAt = ?, nextReviewAt = ?, masteryLevel = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertWord(final VocabularyEntity word,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfVocabularyEntity.insertAndReturnId(word);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertWords(final List<VocabularyEntity> words,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfVocabularyEntity.insert(words);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteWord(final VocabularyEntity word,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfVocabularyEntity.handle(word);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateWord(final VocabularyEntity word,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfVocabularyEntity.handle(word);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object markAsLearned(final long id, final long learnedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkAsLearned.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, learnedAt);
        _argIndex = 2;
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
          __preparedStmtOfMarkAsLearned.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateFavoriteStatus(final long id, final boolean isFavorite,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateFavoriteStatus.acquire();
        int _argIndex = 1;
        final int _tmp = isFavorite ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
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
          __preparedStmtOfUpdateFavoriteStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markAsShownDaily(final long id, final long shownAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkAsShownDaily.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, shownAt);
        _argIndex = 2;
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
          __preparedStmtOfMarkAsShownDaily.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateReviewProgress(final long id, final long reviewedAt, final long nextReview,
      final int mastery, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateReviewProgress.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, reviewedAt);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, nextReview);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, mastery);
        _argIndex = 4;
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
          __preparedStmtOfUpdateReviewProgress.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<VocabularyEntity>> getAllVocabulary() {
    final String _sql = "SELECT * FROM vocabulary ORDER BY word ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"vocabulary"}, new Callable<List<VocabularyEntity>>() {
      @Override
      @NonNull
      public List<VocabularyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWord = CursorUtil.getColumnIndexOrThrow(_cursor, "word");
          final int _cursorIndexOfDefinition = CursorUtil.getColumnIndexOrThrow(_cursor, "definition");
          final int _cursorIndexOfPronunciation = CursorUtil.getColumnIndexOrThrow(_cursor, "pronunciation");
          final int _cursorIndexOfPartOfSpeech = CursorUtil.getColumnIndexOrThrow(_cursor, "partOfSpeech");
          final int _cursorIndexOfExampleSentence = CursorUtil.getColumnIndexOrThrow(_cursor, "exampleSentence");
          final int _cursorIndexOfSynonyms = CursorUtil.getColumnIndexOrThrow(_cursor, "synonyms");
          final int _cursorIndexOfAntonyms = CursorUtil.getColumnIndexOrThrow(_cursor, "antonyms");
          final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsLearned = CursorUtil.getColumnIndexOrThrow(_cursor, "isLearned");
          final int _cursorIndexOfLearnedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "learnedAt");
          final int _cursorIndexOfReviewCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewCount");
          final int _cursorIndexOfLastReviewedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedAt");
          final int _cursorIndexOfNextReviewAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewAt");
          final int _cursorIndexOfMasteryLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "masteryLevel");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final List<VocabularyEntity> _result = new ArrayList<VocabularyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VocabularyEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpWord;
            _tmpWord = _cursor.getString(_cursorIndexOfWord);
            final String _tmpDefinition;
            _tmpDefinition = _cursor.getString(_cursorIndexOfDefinition);
            final String _tmpPronunciation;
            _tmpPronunciation = _cursor.getString(_cursorIndexOfPronunciation);
            final String _tmpPartOfSpeech;
            _tmpPartOfSpeech = _cursor.getString(_cursorIndexOfPartOfSpeech);
            final String _tmpExampleSentence;
            _tmpExampleSentence = _cursor.getString(_cursorIndexOfExampleSentence);
            final String _tmpSynonyms;
            _tmpSynonyms = _cursor.getString(_cursorIndexOfSynonyms);
            final String _tmpAntonyms;
            _tmpAntonyms = _cursor.getString(_cursorIndexOfAntonyms);
            final String _tmpOrigin;
            _tmpOrigin = _cursor.getString(_cursorIndexOfOrigin);
            final int _tmpDifficulty;
            _tmpDifficulty = _cursor.getInt(_cursorIndexOfDifficulty);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final boolean _tmpIsLearned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLearned);
            _tmpIsLearned = _tmp != 0;
            final Long _tmpLearnedAt;
            if (_cursor.isNull(_cursorIndexOfLearnedAt)) {
              _tmpLearnedAt = null;
            } else {
              _tmpLearnedAt = _cursor.getLong(_cursorIndexOfLearnedAt);
            }
            final int _tmpReviewCount;
            _tmpReviewCount = _cursor.getInt(_cursorIndexOfReviewCount);
            final Long _tmpLastReviewedAt;
            if (_cursor.isNull(_cursorIndexOfLastReviewedAt)) {
              _tmpLastReviewedAt = null;
            } else {
              _tmpLastReviewedAt = _cursor.getLong(_cursorIndexOfLastReviewedAt);
            }
            final Long _tmpNextReviewAt;
            if (_cursor.isNull(_cursorIndexOfNextReviewAt)) {
              _tmpNextReviewAt = null;
            } else {
              _tmpNextReviewAt = _cursor.getLong(_cursorIndexOfNextReviewAt);
            }
            final int _tmpMasteryLevel;
            _tmpMasteryLevel = _cursor.getInt(_cursorIndexOfMasteryLevel);
            final boolean _tmpIsFavorite;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_1 != 0;
            final boolean _tmpShownAsDaily;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfShownAsDaily);
            _tmpShownAsDaily = _tmp_2 != 0;
            final Long _tmpShownAt;
            if (_cursor.isNull(_cursorIndexOfShownAt)) {
              _tmpShownAt = null;
            } else {
              _tmpShownAt = _cursor.getLong(_cursorIndexOfShownAt);
            }
            _item = new VocabularyEntity(_tmpId,_tmpWord,_tmpDefinition,_tmpPronunciation,_tmpPartOfSpeech,_tmpExampleSentence,_tmpSynonyms,_tmpAntonyms,_tmpOrigin,_tmpDifficulty,_tmpCategory,_tmpIsLearned,_tmpLearnedAt,_tmpReviewCount,_tmpLastReviewedAt,_tmpNextReviewAt,_tmpMasteryLevel,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
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
  public Object getWordById(final long id,
      final Continuation<? super VocabularyEntity> $completion) {
    final String _sql = "SELECT * FROM vocabulary WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<VocabularyEntity>() {
      @Override
      @Nullable
      public VocabularyEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWord = CursorUtil.getColumnIndexOrThrow(_cursor, "word");
          final int _cursorIndexOfDefinition = CursorUtil.getColumnIndexOrThrow(_cursor, "definition");
          final int _cursorIndexOfPronunciation = CursorUtil.getColumnIndexOrThrow(_cursor, "pronunciation");
          final int _cursorIndexOfPartOfSpeech = CursorUtil.getColumnIndexOrThrow(_cursor, "partOfSpeech");
          final int _cursorIndexOfExampleSentence = CursorUtil.getColumnIndexOrThrow(_cursor, "exampleSentence");
          final int _cursorIndexOfSynonyms = CursorUtil.getColumnIndexOrThrow(_cursor, "synonyms");
          final int _cursorIndexOfAntonyms = CursorUtil.getColumnIndexOrThrow(_cursor, "antonyms");
          final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsLearned = CursorUtil.getColumnIndexOrThrow(_cursor, "isLearned");
          final int _cursorIndexOfLearnedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "learnedAt");
          final int _cursorIndexOfReviewCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewCount");
          final int _cursorIndexOfLastReviewedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedAt");
          final int _cursorIndexOfNextReviewAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewAt");
          final int _cursorIndexOfMasteryLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "masteryLevel");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final VocabularyEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpWord;
            _tmpWord = _cursor.getString(_cursorIndexOfWord);
            final String _tmpDefinition;
            _tmpDefinition = _cursor.getString(_cursorIndexOfDefinition);
            final String _tmpPronunciation;
            _tmpPronunciation = _cursor.getString(_cursorIndexOfPronunciation);
            final String _tmpPartOfSpeech;
            _tmpPartOfSpeech = _cursor.getString(_cursorIndexOfPartOfSpeech);
            final String _tmpExampleSentence;
            _tmpExampleSentence = _cursor.getString(_cursorIndexOfExampleSentence);
            final String _tmpSynonyms;
            _tmpSynonyms = _cursor.getString(_cursorIndexOfSynonyms);
            final String _tmpAntonyms;
            _tmpAntonyms = _cursor.getString(_cursorIndexOfAntonyms);
            final String _tmpOrigin;
            _tmpOrigin = _cursor.getString(_cursorIndexOfOrigin);
            final int _tmpDifficulty;
            _tmpDifficulty = _cursor.getInt(_cursorIndexOfDifficulty);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final boolean _tmpIsLearned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLearned);
            _tmpIsLearned = _tmp != 0;
            final Long _tmpLearnedAt;
            if (_cursor.isNull(_cursorIndexOfLearnedAt)) {
              _tmpLearnedAt = null;
            } else {
              _tmpLearnedAt = _cursor.getLong(_cursorIndexOfLearnedAt);
            }
            final int _tmpReviewCount;
            _tmpReviewCount = _cursor.getInt(_cursorIndexOfReviewCount);
            final Long _tmpLastReviewedAt;
            if (_cursor.isNull(_cursorIndexOfLastReviewedAt)) {
              _tmpLastReviewedAt = null;
            } else {
              _tmpLastReviewedAt = _cursor.getLong(_cursorIndexOfLastReviewedAt);
            }
            final Long _tmpNextReviewAt;
            if (_cursor.isNull(_cursorIndexOfNextReviewAt)) {
              _tmpNextReviewAt = null;
            } else {
              _tmpNextReviewAt = _cursor.getLong(_cursorIndexOfNextReviewAt);
            }
            final int _tmpMasteryLevel;
            _tmpMasteryLevel = _cursor.getInt(_cursorIndexOfMasteryLevel);
            final boolean _tmpIsFavorite;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_1 != 0;
            final boolean _tmpShownAsDaily;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfShownAsDaily);
            _tmpShownAsDaily = _tmp_2 != 0;
            final Long _tmpShownAt;
            if (_cursor.isNull(_cursorIndexOfShownAt)) {
              _tmpShownAt = null;
            } else {
              _tmpShownAt = _cursor.getLong(_cursorIndexOfShownAt);
            }
            _result = new VocabularyEntity(_tmpId,_tmpWord,_tmpDefinition,_tmpPronunciation,_tmpPartOfSpeech,_tmpExampleSentence,_tmpSynonyms,_tmpAntonyms,_tmpOrigin,_tmpDifficulty,_tmpCategory,_tmpIsLearned,_tmpLearnedAt,_tmpReviewCount,_tmpLastReviewedAt,_tmpNextReviewAt,_tmpMasteryLevel,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
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
  public Flow<VocabularyEntity> observeWordById(final long id) {
    final String _sql = "SELECT * FROM vocabulary WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"vocabulary"}, new Callable<VocabularyEntity>() {
      @Override
      @Nullable
      public VocabularyEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWord = CursorUtil.getColumnIndexOrThrow(_cursor, "word");
          final int _cursorIndexOfDefinition = CursorUtil.getColumnIndexOrThrow(_cursor, "definition");
          final int _cursorIndexOfPronunciation = CursorUtil.getColumnIndexOrThrow(_cursor, "pronunciation");
          final int _cursorIndexOfPartOfSpeech = CursorUtil.getColumnIndexOrThrow(_cursor, "partOfSpeech");
          final int _cursorIndexOfExampleSentence = CursorUtil.getColumnIndexOrThrow(_cursor, "exampleSentence");
          final int _cursorIndexOfSynonyms = CursorUtil.getColumnIndexOrThrow(_cursor, "synonyms");
          final int _cursorIndexOfAntonyms = CursorUtil.getColumnIndexOrThrow(_cursor, "antonyms");
          final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsLearned = CursorUtil.getColumnIndexOrThrow(_cursor, "isLearned");
          final int _cursorIndexOfLearnedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "learnedAt");
          final int _cursorIndexOfReviewCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewCount");
          final int _cursorIndexOfLastReviewedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedAt");
          final int _cursorIndexOfNextReviewAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewAt");
          final int _cursorIndexOfMasteryLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "masteryLevel");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final VocabularyEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpWord;
            _tmpWord = _cursor.getString(_cursorIndexOfWord);
            final String _tmpDefinition;
            _tmpDefinition = _cursor.getString(_cursorIndexOfDefinition);
            final String _tmpPronunciation;
            _tmpPronunciation = _cursor.getString(_cursorIndexOfPronunciation);
            final String _tmpPartOfSpeech;
            _tmpPartOfSpeech = _cursor.getString(_cursorIndexOfPartOfSpeech);
            final String _tmpExampleSentence;
            _tmpExampleSentence = _cursor.getString(_cursorIndexOfExampleSentence);
            final String _tmpSynonyms;
            _tmpSynonyms = _cursor.getString(_cursorIndexOfSynonyms);
            final String _tmpAntonyms;
            _tmpAntonyms = _cursor.getString(_cursorIndexOfAntonyms);
            final String _tmpOrigin;
            _tmpOrigin = _cursor.getString(_cursorIndexOfOrigin);
            final int _tmpDifficulty;
            _tmpDifficulty = _cursor.getInt(_cursorIndexOfDifficulty);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final boolean _tmpIsLearned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLearned);
            _tmpIsLearned = _tmp != 0;
            final Long _tmpLearnedAt;
            if (_cursor.isNull(_cursorIndexOfLearnedAt)) {
              _tmpLearnedAt = null;
            } else {
              _tmpLearnedAt = _cursor.getLong(_cursorIndexOfLearnedAt);
            }
            final int _tmpReviewCount;
            _tmpReviewCount = _cursor.getInt(_cursorIndexOfReviewCount);
            final Long _tmpLastReviewedAt;
            if (_cursor.isNull(_cursorIndexOfLastReviewedAt)) {
              _tmpLastReviewedAt = null;
            } else {
              _tmpLastReviewedAt = _cursor.getLong(_cursorIndexOfLastReviewedAt);
            }
            final Long _tmpNextReviewAt;
            if (_cursor.isNull(_cursorIndexOfNextReviewAt)) {
              _tmpNextReviewAt = null;
            } else {
              _tmpNextReviewAt = _cursor.getLong(_cursorIndexOfNextReviewAt);
            }
            final int _tmpMasteryLevel;
            _tmpMasteryLevel = _cursor.getInt(_cursorIndexOfMasteryLevel);
            final boolean _tmpIsFavorite;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_1 != 0;
            final boolean _tmpShownAsDaily;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfShownAsDaily);
            _tmpShownAsDaily = _tmp_2 != 0;
            final Long _tmpShownAt;
            if (_cursor.isNull(_cursorIndexOfShownAt)) {
              _tmpShownAt = null;
            } else {
              _tmpShownAt = _cursor.getLong(_cursorIndexOfShownAt);
            }
            _result = new VocabularyEntity(_tmpId,_tmpWord,_tmpDefinition,_tmpPronunciation,_tmpPartOfSpeech,_tmpExampleSentence,_tmpSynonyms,_tmpAntonyms,_tmpOrigin,_tmpDifficulty,_tmpCategory,_tmpIsLearned,_tmpLearnedAt,_tmpReviewCount,_tmpLastReviewedAt,_tmpNextReviewAt,_tmpMasteryLevel,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
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
  public Object getWordByName(final String word,
      final Continuation<? super VocabularyEntity> $completion) {
    final String _sql = "SELECT * FROM vocabulary WHERE word = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, word);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<VocabularyEntity>() {
      @Override
      @Nullable
      public VocabularyEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWord = CursorUtil.getColumnIndexOrThrow(_cursor, "word");
          final int _cursorIndexOfDefinition = CursorUtil.getColumnIndexOrThrow(_cursor, "definition");
          final int _cursorIndexOfPronunciation = CursorUtil.getColumnIndexOrThrow(_cursor, "pronunciation");
          final int _cursorIndexOfPartOfSpeech = CursorUtil.getColumnIndexOrThrow(_cursor, "partOfSpeech");
          final int _cursorIndexOfExampleSentence = CursorUtil.getColumnIndexOrThrow(_cursor, "exampleSentence");
          final int _cursorIndexOfSynonyms = CursorUtil.getColumnIndexOrThrow(_cursor, "synonyms");
          final int _cursorIndexOfAntonyms = CursorUtil.getColumnIndexOrThrow(_cursor, "antonyms");
          final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsLearned = CursorUtil.getColumnIndexOrThrow(_cursor, "isLearned");
          final int _cursorIndexOfLearnedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "learnedAt");
          final int _cursorIndexOfReviewCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewCount");
          final int _cursorIndexOfLastReviewedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedAt");
          final int _cursorIndexOfNextReviewAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewAt");
          final int _cursorIndexOfMasteryLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "masteryLevel");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final VocabularyEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpWord;
            _tmpWord = _cursor.getString(_cursorIndexOfWord);
            final String _tmpDefinition;
            _tmpDefinition = _cursor.getString(_cursorIndexOfDefinition);
            final String _tmpPronunciation;
            _tmpPronunciation = _cursor.getString(_cursorIndexOfPronunciation);
            final String _tmpPartOfSpeech;
            _tmpPartOfSpeech = _cursor.getString(_cursorIndexOfPartOfSpeech);
            final String _tmpExampleSentence;
            _tmpExampleSentence = _cursor.getString(_cursorIndexOfExampleSentence);
            final String _tmpSynonyms;
            _tmpSynonyms = _cursor.getString(_cursorIndexOfSynonyms);
            final String _tmpAntonyms;
            _tmpAntonyms = _cursor.getString(_cursorIndexOfAntonyms);
            final String _tmpOrigin;
            _tmpOrigin = _cursor.getString(_cursorIndexOfOrigin);
            final int _tmpDifficulty;
            _tmpDifficulty = _cursor.getInt(_cursorIndexOfDifficulty);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final boolean _tmpIsLearned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLearned);
            _tmpIsLearned = _tmp != 0;
            final Long _tmpLearnedAt;
            if (_cursor.isNull(_cursorIndexOfLearnedAt)) {
              _tmpLearnedAt = null;
            } else {
              _tmpLearnedAt = _cursor.getLong(_cursorIndexOfLearnedAt);
            }
            final int _tmpReviewCount;
            _tmpReviewCount = _cursor.getInt(_cursorIndexOfReviewCount);
            final Long _tmpLastReviewedAt;
            if (_cursor.isNull(_cursorIndexOfLastReviewedAt)) {
              _tmpLastReviewedAt = null;
            } else {
              _tmpLastReviewedAt = _cursor.getLong(_cursorIndexOfLastReviewedAt);
            }
            final Long _tmpNextReviewAt;
            if (_cursor.isNull(_cursorIndexOfNextReviewAt)) {
              _tmpNextReviewAt = null;
            } else {
              _tmpNextReviewAt = _cursor.getLong(_cursorIndexOfNextReviewAt);
            }
            final int _tmpMasteryLevel;
            _tmpMasteryLevel = _cursor.getInt(_cursorIndexOfMasteryLevel);
            final boolean _tmpIsFavorite;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_1 != 0;
            final boolean _tmpShownAsDaily;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfShownAsDaily);
            _tmpShownAsDaily = _tmp_2 != 0;
            final Long _tmpShownAt;
            if (_cursor.isNull(_cursorIndexOfShownAt)) {
              _tmpShownAt = null;
            } else {
              _tmpShownAt = _cursor.getLong(_cursorIndexOfShownAt);
            }
            _result = new VocabularyEntity(_tmpId,_tmpWord,_tmpDefinition,_tmpPronunciation,_tmpPartOfSpeech,_tmpExampleSentence,_tmpSynonyms,_tmpAntonyms,_tmpOrigin,_tmpDifficulty,_tmpCategory,_tmpIsLearned,_tmpLearnedAt,_tmpReviewCount,_tmpLastReviewedAt,_tmpNextReviewAt,_tmpMasteryLevel,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
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
  public Object getRandomUnlearnedWord(final Continuation<? super VocabularyEntity> $completion) {
    final String _sql = "SELECT * FROM vocabulary WHERE isLearned = 0 ORDER BY RANDOM() LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<VocabularyEntity>() {
      @Override
      @Nullable
      public VocabularyEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWord = CursorUtil.getColumnIndexOrThrow(_cursor, "word");
          final int _cursorIndexOfDefinition = CursorUtil.getColumnIndexOrThrow(_cursor, "definition");
          final int _cursorIndexOfPronunciation = CursorUtil.getColumnIndexOrThrow(_cursor, "pronunciation");
          final int _cursorIndexOfPartOfSpeech = CursorUtil.getColumnIndexOrThrow(_cursor, "partOfSpeech");
          final int _cursorIndexOfExampleSentence = CursorUtil.getColumnIndexOrThrow(_cursor, "exampleSentence");
          final int _cursorIndexOfSynonyms = CursorUtil.getColumnIndexOrThrow(_cursor, "synonyms");
          final int _cursorIndexOfAntonyms = CursorUtil.getColumnIndexOrThrow(_cursor, "antonyms");
          final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsLearned = CursorUtil.getColumnIndexOrThrow(_cursor, "isLearned");
          final int _cursorIndexOfLearnedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "learnedAt");
          final int _cursorIndexOfReviewCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewCount");
          final int _cursorIndexOfLastReviewedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedAt");
          final int _cursorIndexOfNextReviewAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewAt");
          final int _cursorIndexOfMasteryLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "masteryLevel");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final VocabularyEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpWord;
            _tmpWord = _cursor.getString(_cursorIndexOfWord);
            final String _tmpDefinition;
            _tmpDefinition = _cursor.getString(_cursorIndexOfDefinition);
            final String _tmpPronunciation;
            _tmpPronunciation = _cursor.getString(_cursorIndexOfPronunciation);
            final String _tmpPartOfSpeech;
            _tmpPartOfSpeech = _cursor.getString(_cursorIndexOfPartOfSpeech);
            final String _tmpExampleSentence;
            _tmpExampleSentence = _cursor.getString(_cursorIndexOfExampleSentence);
            final String _tmpSynonyms;
            _tmpSynonyms = _cursor.getString(_cursorIndexOfSynonyms);
            final String _tmpAntonyms;
            _tmpAntonyms = _cursor.getString(_cursorIndexOfAntonyms);
            final String _tmpOrigin;
            _tmpOrigin = _cursor.getString(_cursorIndexOfOrigin);
            final int _tmpDifficulty;
            _tmpDifficulty = _cursor.getInt(_cursorIndexOfDifficulty);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final boolean _tmpIsLearned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLearned);
            _tmpIsLearned = _tmp != 0;
            final Long _tmpLearnedAt;
            if (_cursor.isNull(_cursorIndexOfLearnedAt)) {
              _tmpLearnedAt = null;
            } else {
              _tmpLearnedAt = _cursor.getLong(_cursorIndexOfLearnedAt);
            }
            final int _tmpReviewCount;
            _tmpReviewCount = _cursor.getInt(_cursorIndexOfReviewCount);
            final Long _tmpLastReviewedAt;
            if (_cursor.isNull(_cursorIndexOfLastReviewedAt)) {
              _tmpLastReviewedAt = null;
            } else {
              _tmpLastReviewedAt = _cursor.getLong(_cursorIndexOfLastReviewedAt);
            }
            final Long _tmpNextReviewAt;
            if (_cursor.isNull(_cursorIndexOfNextReviewAt)) {
              _tmpNextReviewAt = null;
            } else {
              _tmpNextReviewAt = _cursor.getLong(_cursorIndexOfNextReviewAt);
            }
            final int _tmpMasteryLevel;
            _tmpMasteryLevel = _cursor.getInt(_cursorIndexOfMasteryLevel);
            final boolean _tmpIsFavorite;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_1 != 0;
            final boolean _tmpShownAsDaily;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfShownAsDaily);
            _tmpShownAsDaily = _tmp_2 != 0;
            final Long _tmpShownAt;
            if (_cursor.isNull(_cursorIndexOfShownAt)) {
              _tmpShownAt = null;
            } else {
              _tmpShownAt = _cursor.getLong(_cursorIndexOfShownAt);
            }
            _result = new VocabularyEntity(_tmpId,_tmpWord,_tmpDefinition,_tmpPronunciation,_tmpPartOfSpeech,_tmpExampleSentence,_tmpSynonyms,_tmpAntonyms,_tmpOrigin,_tmpDifficulty,_tmpCategory,_tmpIsLearned,_tmpLearnedAt,_tmpReviewCount,_tmpLastReviewedAt,_tmpNextReviewAt,_tmpMasteryLevel,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
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
  public Object getWordOfTheDay(final Continuation<? super VocabularyEntity> $completion) {
    final String _sql = "SELECT * FROM vocabulary WHERE shownAsDaily = 0 ORDER BY RANDOM() LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<VocabularyEntity>() {
      @Override
      @Nullable
      public VocabularyEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWord = CursorUtil.getColumnIndexOrThrow(_cursor, "word");
          final int _cursorIndexOfDefinition = CursorUtil.getColumnIndexOrThrow(_cursor, "definition");
          final int _cursorIndexOfPronunciation = CursorUtil.getColumnIndexOrThrow(_cursor, "pronunciation");
          final int _cursorIndexOfPartOfSpeech = CursorUtil.getColumnIndexOrThrow(_cursor, "partOfSpeech");
          final int _cursorIndexOfExampleSentence = CursorUtil.getColumnIndexOrThrow(_cursor, "exampleSentence");
          final int _cursorIndexOfSynonyms = CursorUtil.getColumnIndexOrThrow(_cursor, "synonyms");
          final int _cursorIndexOfAntonyms = CursorUtil.getColumnIndexOrThrow(_cursor, "antonyms");
          final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsLearned = CursorUtil.getColumnIndexOrThrow(_cursor, "isLearned");
          final int _cursorIndexOfLearnedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "learnedAt");
          final int _cursorIndexOfReviewCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewCount");
          final int _cursorIndexOfLastReviewedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedAt");
          final int _cursorIndexOfNextReviewAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewAt");
          final int _cursorIndexOfMasteryLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "masteryLevel");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final VocabularyEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpWord;
            _tmpWord = _cursor.getString(_cursorIndexOfWord);
            final String _tmpDefinition;
            _tmpDefinition = _cursor.getString(_cursorIndexOfDefinition);
            final String _tmpPronunciation;
            _tmpPronunciation = _cursor.getString(_cursorIndexOfPronunciation);
            final String _tmpPartOfSpeech;
            _tmpPartOfSpeech = _cursor.getString(_cursorIndexOfPartOfSpeech);
            final String _tmpExampleSentence;
            _tmpExampleSentence = _cursor.getString(_cursorIndexOfExampleSentence);
            final String _tmpSynonyms;
            _tmpSynonyms = _cursor.getString(_cursorIndexOfSynonyms);
            final String _tmpAntonyms;
            _tmpAntonyms = _cursor.getString(_cursorIndexOfAntonyms);
            final String _tmpOrigin;
            _tmpOrigin = _cursor.getString(_cursorIndexOfOrigin);
            final int _tmpDifficulty;
            _tmpDifficulty = _cursor.getInt(_cursorIndexOfDifficulty);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final boolean _tmpIsLearned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLearned);
            _tmpIsLearned = _tmp != 0;
            final Long _tmpLearnedAt;
            if (_cursor.isNull(_cursorIndexOfLearnedAt)) {
              _tmpLearnedAt = null;
            } else {
              _tmpLearnedAt = _cursor.getLong(_cursorIndexOfLearnedAt);
            }
            final int _tmpReviewCount;
            _tmpReviewCount = _cursor.getInt(_cursorIndexOfReviewCount);
            final Long _tmpLastReviewedAt;
            if (_cursor.isNull(_cursorIndexOfLastReviewedAt)) {
              _tmpLastReviewedAt = null;
            } else {
              _tmpLastReviewedAt = _cursor.getLong(_cursorIndexOfLastReviewedAt);
            }
            final Long _tmpNextReviewAt;
            if (_cursor.isNull(_cursorIndexOfNextReviewAt)) {
              _tmpNextReviewAt = null;
            } else {
              _tmpNextReviewAt = _cursor.getLong(_cursorIndexOfNextReviewAt);
            }
            final int _tmpMasteryLevel;
            _tmpMasteryLevel = _cursor.getInt(_cursorIndexOfMasteryLevel);
            final boolean _tmpIsFavorite;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_1 != 0;
            final boolean _tmpShownAsDaily;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfShownAsDaily);
            _tmpShownAsDaily = _tmp_2 != 0;
            final Long _tmpShownAt;
            if (_cursor.isNull(_cursorIndexOfShownAt)) {
              _tmpShownAt = null;
            } else {
              _tmpShownAt = _cursor.getLong(_cursorIndexOfShownAt);
            }
            _result = new VocabularyEntity(_tmpId,_tmpWord,_tmpDefinition,_tmpPronunciation,_tmpPartOfSpeech,_tmpExampleSentence,_tmpSynonyms,_tmpAntonyms,_tmpOrigin,_tmpDifficulty,_tmpCategory,_tmpIsLearned,_tmpLearnedAt,_tmpReviewCount,_tmpLastReviewedAt,_tmpNextReviewAt,_tmpMasteryLevel,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
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
  public Flow<List<VocabularyEntity>> getLearnedWords() {
    final String _sql = "SELECT * FROM vocabulary WHERE isLearned = 1 ORDER BY learnedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"vocabulary"}, new Callable<List<VocabularyEntity>>() {
      @Override
      @NonNull
      public List<VocabularyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWord = CursorUtil.getColumnIndexOrThrow(_cursor, "word");
          final int _cursorIndexOfDefinition = CursorUtil.getColumnIndexOrThrow(_cursor, "definition");
          final int _cursorIndexOfPronunciation = CursorUtil.getColumnIndexOrThrow(_cursor, "pronunciation");
          final int _cursorIndexOfPartOfSpeech = CursorUtil.getColumnIndexOrThrow(_cursor, "partOfSpeech");
          final int _cursorIndexOfExampleSentence = CursorUtil.getColumnIndexOrThrow(_cursor, "exampleSentence");
          final int _cursorIndexOfSynonyms = CursorUtil.getColumnIndexOrThrow(_cursor, "synonyms");
          final int _cursorIndexOfAntonyms = CursorUtil.getColumnIndexOrThrow(_cursor, "antonyms");
          final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsLearned = CursorUtil.getColumnIndexOrThrow(_cursor, "isLearned");
          final int _cursorIndexOfLearnedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "learnedAt");
          final int _cursorIndexOfReviewCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewCount");
          final int _cursorIndexOfLastReviewedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedAt");
          final int _cursorIndexOfNextReviewAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewAt");
          final int _cursorIndexOfMasteryLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "masteryLevel");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final List<VocabularyEntity> _result = new ArrayList<VocabularyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VocabularyEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpWord;
            _tmpWord = _cursor.getString(_cursorIndexOfWord);
            final String _tmpDefinition;
            _tmpDefinition = _cursor.getString(_cursorIndexOfDefinition);
            final String _tmpPronunciation;
            _tmpPronunciation = _cursor.getString(_cursorIndexOfPronunciation);
            final String _tmpPartOfSpeech;
            _tmpPartOfSpeech = _cursor.getString(_cursorIndexOfPartOfSpeech);
            final String _tmpExampleSentence;
            _tmpExampleSentence = _cursor.getString(_cursorIndexOfExampleSentence);
            final String _tmpSynonyms;
            _tmpSynonyms = _cursor.getString(_cursorIndexOfSynonyms);
            final String _tmpAntonyms;
            _tmpAntonyms = _cursor.getString(_cursorIndexOfAntonyms);
            final String _tmpOrigin;
            _tmpOrigin = _cursor.getString(_cursorIndexOfOrigin);
            final int _tmpDifficulty;
            _tmpDifficulty = _cursor.getInt(_cursorIndexOfDifficulty);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final boolean _tmpIsLearned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLearned);
            _tmpIsLearned = _tmp != 0;
            final Long _tmpLearnedAt;
            if (_cursor.isNull(_cursorIndexOfLearnedAt)) {
              _tmpLearnedAt = null;
            } else {
              _tmpLearnedAt = _cursor.getLong(_cursorIndexOfLearnedAt);
            }
            final int _tmpReviewCount;
            _tmpReviewCount = _cursor.getInt(_cursorIndexOfReviewCount);
            final Long _tmpLastReviewedAt;
            if (_cursor.isNull(_cursorIndexOfLastReviewedAt)) {
              _tmpLastReviewedAt = null;
            } else {
              _tmpLastReviewedAt = _cursor.getLong(_cursorIndexOfLastReviewedAt);
            }
            final Long _tmpNextReviewAt;
            if (_cursor.isNull(_cursorIndexOfNextReviewAt)) {
              _tmpNextReviewAt = null;
            } else {
              _tmpNextReviewAt = _cursor.getLong(_cursorIndexOfNextReviewAt);
            }
            final int _tmpMasteryLevel;
            _tmpMasteryLevel = _cursor.getInt(_cursorIndexOfMasteryLevel);
            final boolean _tmpIsFavorite;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_1 != 0;
            final boolean _tmpShownAsDaily;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfShownAsDaily);
            _tmpShownAsDaily = _tmp_2 != 0;
            final Long _tmpShownAt;
            if (_cursor.isNull(_cursorIndexOfShownAt)) {
              _tmpShownAt = null;
            } else {
              _tmpShownAt = _cursor.getLong(_cursorIndexOfShownAt);
            }
            _item = new VocabularyEntity(_tmpId,_tmpWord,_tmpDefinition,_tmpPronunciation,_tmpPartOfSpeech,_tmpExampleSentence,_tmpSynonyms,_tmpAntonyms,_tmpOrigin,_tmpDifficulty,_tmpCategory,_tmpIsLearned,_tmpLearnedAt,_tmpReviewCount,_tmpLastReviewedAt,_tmpNextReviewAt,_tmpMasteryLevel,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
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
  public Flow<List<VocabularyEntity>> getFavoriteWords() {
    final String _sql = "SELECT * FROM vocabulary WHERE isFavorite = 1 ORDER BY word ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"vocabulary"}, new Callable<List<VocabularyEntity>>() {
      @Override
      @NonNull
      public List<VocabularyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWord = CursorUtil.getColumnIndexOrThrow(_cursor, "word");
          final int _cursorIndexOfDefinition = CursorUtil.getColumnIndexOrThrow(_cursor, "definition");
          final int _cursorIndexOfPronunciation = CursorUtil.getColumnIndexOrThrow(_cursor, "pronunciation");
          final int _cursorIndexOfPartOfSpeech = CursorUtil.getColumnIndexOrThrow(_cursor, "partOfSpeech");
          final int _cursorIndexOfExampleSentence = CursorUtil.getColumnIndexOrThrow(_cursor, "exampleSentence");
          final int _cursorIndexOfSynonyms = CursorUtil.getColumnIndexOrThrow(_cursor, "synonyms");
          final int _cursorIndexOfAntonyms = CursorUtil.getColumnIndexOrThrow(_cursor, "antonyms");
          final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsLearned = CursorUtil.getColumnIndexOrThrow(_cursor, "isLearned");
          final int _cursorIndexOfLearnedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "learnedAt");
          final int _cursorIndexOfReviewCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewCount");
          final int _cursorIndexOfLastReviewedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedAt");
          final int _cursorIndexOfNextReviewAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewAt");
          final int _cursorIndexOfMasteryLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "masteryLevel");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final List<VocabularyEntity> _result = new ArrayList<VocabularyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VocabularyEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpWord;
            _tmpWord = _cursor.getString(_cursorIndexOfWord);
            final String _tmpDefinition;
            _tmpDefinition = _cursor.getString(_cursorIndexOfDefinition);
            final String _tmpPronunciation;
            _tmpPronunciation = _cursor.getString(_cursorIndexOfPronunciation);
            final String _tmpPartOfSpeech;
            _tmpPartOfSpeech = _cursor.getString(_cursorIndexOfPartOfSpeech);
            final String _tmpExampleSentence;
            _tmpExampleSentence = _cursor.getString(_cursorIndexOfExampleSentence);
            final String _tmpSynonyms;
            _tmpSynonyms = _cursor.getString(_cursorIndexOfSynonyms);
            final String _tmpAntonyms;
            _tmpAntonyms = _cursor.getString(_cursorIndexOfAntonyms);
            final String _tmpOrigin;
            _tmpOrigin = _cursor.getString(_cursorIndexOfOrigin);
            final int _tmpDifficulty;
            _tmpDifficulty = _cursor.getInt(_cursorIndexOfDifficulty);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final boolean _tmpIsLearned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLearned);
            _tmpIsLearned = _tmp != 0;
            final Long _tmpLearnedAt;
            if (_cursor.isNull(_cursorIndexOfLearnedAt)) {
              _tmpLearnedAt = null;
            } else {
              _tmpLearnedAt = _cursor.getLong(_cursorIndexOfLearnedAt);
            }
            final int _tmpReviewCount;
            _tmpReviewCount = _cursor.getInt(_cursorIndexOfReviewCount);
            final Long _tmpLastReviewedAt;
            if (_cursor.isNull(_cursorIndexOfLastReviewedAt)) {
              _tmpLastReviewedAt = null;
            } else {
              _tmpLastReviewedAt = _cursor.getLong(_cursorIndexOfLastReviewedAt);
            }
            final Long _tmpNextReviewAt;
            if (_cursor.isNull(_cursorIndexOfNextReviewAt)) {
              _tmpNextReviewAt = null;
            } else {
              _tmpNextReviewAt = _cursor.getLong(_cursorIndexOfNextReviewAt);
            }
            final int _tmpMasteryLevel;
            _tmpMasteryLevel = _cursor.getInt(_cursorIndexOfMasteryLevel);
            final boolean _tmpIsFavorite;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_1 != 0;
            final boolean _tmpShownAsDaily;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfShownAsDaily);
            _tmpShownAsDaily = _tmp_2 != 0;
            final Long _tmpShownAt;
            if (_cursor.isNull(_cursorIndexOfShownAt)) {
              _tmpShownAt = null;
            } else {
              _tmpShownAt = _cursor.getLong(_cursorIndexOfShownAt);
            }
            _item = new VocabularyEntity(_tmpId,_tmpWord,_tmpDefinition,_tmpPronunciation,_tmpPartOfSpeech,_tmpExampleSentence,_tmpSynonyms,_tmpAntonyms,_tmpOrigin,_tmpDifficulty,_tmpCategory,_tmpIsLearned,_tmpLearnedAt,_tmpReviewCount,_tmpLastReviewedAt,_tmpNextReviewAt,_tmpMasteryLevel,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
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
  public Flow<List<VocabularyEntity>> getWordsByCategory(final String category) {
    final String _sql = "SELECT * FROM vocabulary WHERE category = ? ORDER BY word ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, category);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"vocabulary"}, new Callable<List<VocabularyEntity>>() {
      @Override
      @NonNull
      public List<VocabularyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWord = CursorUtil.getColumnIndexOrThrow(_cursor, "word");
          final int _cursorIndexOfDefinition = CursorUtil.getColumnIndexOrThrow(_cursor, "definition");
          final int _cursorIndexOfPronunciation = CursorUtil.getColumnIndexOrThrow(_cursor, "pronunciation");
          final int _cursorIndexOfPartOfSpeech = CursorUtil.getColumnIndexOrThrow(_cursor, "partOfSpeech");
          final int _cursorIndexOfExampleSentence = CursorUtil.getColumnIndexOrThrow(_cursor, "exampleSentence");
          final int _cursorIndexOfSynonyms = CursorUtil.getColumnIndexOrThrow(_cursor, "synonyms");
          final int _cursorIndexOfAntonyms = CursorUtil.getColumnIndexOrThrow(_cursor, "antonyms");
          final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsLearned = CursorUtil.getColumnIndexOrThrow(_cursor, "isLearned");
          final int _cursorIndexOfLearnedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "learnedAt");
          final int _cursorIndexOfReviewCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewCount");
          final int _cursorIndexOfLastReviewedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedAt");
          final int _cursorIndexOfNextReviewAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewAt");
          final int _cursorIndexOfMasteryLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "masteryLevel");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final List<VocabularyEntity> _result = new ArrayList<VocabularyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VocabularyEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpWord;
            _tmpWord = _cursor.getString(_cursorIndexOfWord);
            final String _tmpDefinition;
            _tmpDefinition = _cursor.getString(_cursorIndexOfDefinition);
            final String _tmpPronunciation;
            _tmpPronunciation = _cursor.getString(_cursorIndexOfPronunciation);
            final String _tmpPartOfSpeech;
            _tmpPartOfSpeech = _cursor.getString(_cursorIndexOfPartOfSpeech);
            final String _tmpExampleSentence;
            _tmpExampleSentence = _cursor.getString(_cursorIndexOfExampleSentence);
            final String _tmpSynonyms;
            _tmpSynonyms = _cursor.getString(_cursorIndexOfSynonyms);
            final String _tmpAntonyms;
            _tmpAntonyms = _cursor.getString(_cursorIndexOfAntonyms);
            final String _tmpOrigin;
            _tmpOrigin = _cursor.getString(_cursorIndexOfOrigin);
            final int _tmpDifficulty;
            _tmpDifficulty = _cursor.getInt(_cursorIndexOfDifficulty);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final boolean _tmpIsLearned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLearned);
            _tmpIsLearned = _tmp != 0;
            final Long _tmpLearnedAt;
            if (_cursor.isNull(_cursorIndexOfLearnedAt)) {
              _tmpLearnedAt = null;
            } else {
              _tmpLearnedAt = _cursor.getLong(_cursorIndexOfLearnedAt);
            }
            final int _tmpReviewCount;
            _tmpReviewCount = _cursor.getInt(_cursorIndexOfReviewCount);
            final Long _tmpLastReviewedAt;
            if (_cursor.isNull(_cursorIndexOfLastReviewedAt)) {
              _tmpLastReviewedAt = null;
            } else {
              _tmpLastReviewedAt = _cursor.getLong(_cursorIndexOfLastReviewedAt);
            }
            final Long _tmpNextReviewAt;
            if (_cursor.isNull(_cursorIndexOfNextReviewAt)) {
              _tmpNextReviewAt = null;
            } else {
              _tmpNextReviewAt = _cursor.getLong(_cursorIndexOfNextReviewAt);
            }
            final int _tmpMasteryLevel;
            _tmpMasteryLevel = _cursor.getInt(_cursorIndexOfMasteryLevel);
            final boolean _tmpIsFavorite;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_1 != 0;
            final boolean _tmpShownAsDaily;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfShownAsDaily);
            _tmpShownAsDaily = _tmp_2 != 0;
            final Long _tmpShownAt;
            if (_cursor.isNull(_cursorIndexOfShownAt)) {
              _tmpShownAt = null;
            } else {
              _tmpShownAt = _cursor.getLong(_cursorIndexOfShownAt);
            }
            _item = new VocabularyEntity(_tmpId,_tmpWord,_tmpDefinition,_tmpPronunciation,_tmpPartOfSpeech,_tmpExampleSentence,_tmpSynonyms,_tmpAntonyms,_tmpOrigin,_tmpDifficulty,_tmpCategory,_tmpIsLearned,_tmpLearnedAt,_tmpReviewCount,_tmpLastReviewedAt,_tmpNextReviewAt,_tmpMasteryLevel,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
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
  public Flow<List<VocabularyEntity>> getWordsByDifficulty(final int difficulty) {
    final String _sql = "SELECT * FROM vocabulary WHERE difficulty = ? ORDER BY word ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, difficulty);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"vocabulary"}, new Callable<List<VocabularyEntity>>() {
      @Override
      @NonNull
      public List<VocabularyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWord = CursorUtil.getColumnIndexOrThrow(_cursor, "word");
          final int _cursorIndexOfDefinition = CursorUtil.getColumnIndexOrThrow(_cursor, "definition");
          final int _cursorIndexOfPronunciation = CursorUtil.getColumnIndexOrThrow(_cursor, "pronunciation");
          final int _cursorIndexOfPartOfSpeech = CursorUtil.getColumnIndexOrThrow(_cursor, "partOfSpeech");
          final int _cursorIndexOfExampleSentence = CursorUtil.getColumnIndexOrThrow(_cursor, "exampleSentence");
          final int _cursorIndexOfSynonyms = CursorUtil.getColumnIndexOrThrow(_cursor, "synonyms");
          final int _cursorIndexOfAntonyms = CursorUtil.getColumnIndexOrThrow(_cursor, "antonyms");
          final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsLearned = CursorUtil.getColumnIndexOrThrow(_cursor, "isLearned");
          final int _cursorIndexOfLearnedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "learnedAt");
          final int _cursorIndexOfReviewCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewCount");
          final int _cursorIndexOfLastReviewedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedAt");
          final int _cursorIndexOfNextReviewAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewAt");
          final int _cursorIndexOfMasteryLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "masteryLevel");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final List<VocabularyEntity> _result = new ArrayList<VocabularyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VocabularyEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpWord;
            _tmpWord = _cursor.getString(_cursorIndexOfWord);
            final String _tmpDefinition;
            _tmpDefinition = _cursor.getString(_cursorIndexOfDefinition);
            final String _tmpPronunciation;
            _tmpPronunciation = _cursor.getString(_cursorIndexOfPronunciation);
            final String _tmpPartOfSpeech;
            _tmpPartOfSpeech = _cursor.getString(_cursorIndexOfPartOfSpeech);
            final String _tmpExampleSentence;
            _tmpExampleSentence = _cursor.getString(_cursorIndexOfExampleSentence);
            final String _tmpSynonyms;
            _tmpSynonyms = _cursor.getString(_cursorIndexOfSynonyms);
            final String _tmpAntonyms;
            _tmpAntonyms = _cursor.getString(_cursorIndexOfAntonyms);
            final String _tmpOrigin;
            _tmpOrigin = _cursor.getString(_cursorIndexOfOrigin);
            final int _tmpDifficulty;
            _tmpDifficulty = _cursor.getInt(_cursorIndexOfDifficulty);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final boolean _tmpIsLearned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLearned);
            _tmpIsLearned = _tmp != 0;
            final Long _tmpLearnedAt;
            if (_cursor.isNull(_cursorIndexOfLearnedAt)) {
              _tmpLearnedAt = null;
            } else {
              _tmpLearnedAt = _cursor.getLong(_cursorIndexOfLearnedAt);
            }
            final int _tmpReviewCount;
            _tmpReviewCount = _cursor.getInt(_cursorIndexOfReviewCount);
            final Long _tmpLastReviewedAt;
            if (_cursor.isNull(_cursorIndexOfLastReviewedAt)) {
              _tmpLastReviewedAt = null;
            } else {
              _tmpLastReviewedAt = _cursor.getLong(_cursorIndexOfLastReviewedAt);
            }
            final Long _tmpNextReviewAt;
            if (_cursor.isNull(_cursorIndexOfNextReviewAt)) {
              _tmpNextReviewAt = null;
            } else {
              _tmpNextReviewAt = _cursor.getLong(_cursorIndexOfNextReviewAt);
            }
            final int _tmpMasteryLevel;
            _tmpMasteryLevel = _cursor.getInt(_cursorIndexOfMasteryLevel);
            final boolean _tmpIsFavorite;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_1 != 0;
            final boolean _tmpShownAsDaily;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfShownAsDaily);
            _tmpShownAsDaily = _tmp_2 != 0;
            final Long _tmpShownAt;
            if (_cursor.isNull(_cursorIndexOfShownAt)) {
              _tmpShownAt = null;
            } else {
              _tmpShownAt = _cursor.getLong(_cursorIndexOfShownAt);
            }
            _item = new VocabularyEntity(_tmpId,_tmpWord,_tmpDefinition,_tmpPronunciation,_tmpPartOfSpeech,_tmpExampleSentence,_tmpSynonyms,_tmpAntonyms,_tmpOrigin,_tmpDifficulty,_tmpCategory,_tmpIsLearned,_tmpLearnedAt,_tmpReviewCount,_tmpLastReviewedAt,_tmpNextReviewAt,_tmpMasteryLevel,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
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
  public Object getWordsForReview(final long currentTime, final int limit,
      final Continuation<? super List<VocabularyEntity>> $completion) {
    final String _sql = "SELECT * FROM vocabulary WHERE nextReviewAt <= ? AND isLearned = 1 ORDER BY nextReviewAt ASC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, currentTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<VocabularyEntity>>() {
      @Override
      @NonNull
      public List<VocabularyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWord = CursorUtil.getColumnIndexOrThrow(_cursor, "word");
          final int _cursorIndexOfDefinition = CursorUtil.getColumnIndexOrThrow(_cursor, "definition");
          final int _cursorIndexOfPronunciation = CursorUtil.getColumnIndexOrThrow(_cursor, "pronunciation");
          final int _cursorIndexOfPartOfSpeech = CursorUtil.getColumnIndexOrThrow(_cursor, "partOfSpeech");
          final int _cursorIndexOfExampleSentence = CursorUtil.getColumnIndexOrThrow(_cursor, "exampleSentence");
          final int _cursorIndexOfSynonyms = CursorUtil.getColumnIndexOrThrow(_cursor, "synonyms");
          final int _cursorIndexOfAntonyms = CursorUtil.getColumnIndexOrThrow(_cursor, "antonyms");
          final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsLearned = CursorUtil.getColumnIndexOrThrow(_cursor, "isLearned");
          final int _cursorIndexOfLearnedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "learnedAt");
          final int _cursorIndexOfReviewCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewCount");
          final int _cursorIndexOfLastReviewedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedAt");
          final int _cursorIndexOfNextReviewAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewAt");
          final int _cursorIndexOfMasteryLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "masteryLevel");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final List<VocabularyEntity> _result = new ArrayList<VocabularyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VocabularyEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpWord;
            _tmpWord = _cursor.getString(_cursorIndexOfWord);
            final String _tmpDefinition;
            _tmpDefinition = _cursor.getString(_cursorIndexOfDefinition);
            final String _tmpPronunciation;
            _tmpPronunciation = _cursor.getString(_cursorIndexOfPronunciation);
            final String _tmpPartOfSpeech;
            _tmpPartOfSpeech = _cursor.getString(_cursorIndexOfPartOfSpeech);
            final String _tmpExampleSentence;
            _tmpExampleSentence = _cursor.getString(_cursorIndexOfExampleSentence);
            final String _tmpSynonyms;
            _tmpSynonyms = _cursor.getString(_cursorIndexOfSynonyms);
            final String _tmpAntonyms;
            _tmpAntonyms = _cursor.getString(_cursorIndexOfAntonyms);
            final String _tmpOrigin;
            _tmpOrigin = _cursor.getString(_cursorIndexOfOrigin);
            final int _tmpDifficulty;
            _tmpDifficulty = _cursor.getInt(_cursorIndexOfDifficulty);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final boolean _tmpIsLearned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLearned);
            _tmpIsLearned = _tmp != 0;
            final Long _tmpLearnedAt;
            if (_cursor.isNull(_cursorIndexOfLearnedAt)) {
              _tmpLearnedAt = null;
            } else {
              _tmpLearnedAt = _cursor.getLong(_cursorIndexOfLearnedAt);
            }
            final int _tmpReviewCount;
            _tmpReviewCount = _cursor.getInt(_cursorIndexOfReviewCount);
            final Long _tmpLastReviewedAt;
            if (_cursor.isNull(_cursorIndexOfLastReviewedAt)) {
              _tmpLastReviewedAt = null;
            } else {
              _tmpLastReviewedAt = _cursor.getLong(_cursorIndexOfLastReviewedAt);
            }
            final Long _tmpNextReviewAt;
            if (_cursor.isNull(_cursorIndexOfNextReviewAt)) {
              _tmpNextReviewAt = null;
            } else {
              _tmpNextReviewAt = _cursor.getLong(_cursorIndexOfNextReviewAt);
            }
            final int _tmpMasteryLevel;
            _tmpMasteryLevel = _cursor.getInt(_cursorIndexOfMasteryLevel);
            final boolean _tmpIsFavorite;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_1 != 0;
            final boolean _tmpShownAsDaily;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfShownAsDaily);
            _tmpShownAsDaily = _tmp_2 != 0;
            final Long _tmpShownAt;
            if (_cursor.isNull(_cursorIndexOfShownAt)) {
              _tmpShownAt = null;
            } else {
              _tmpShownAt = _cursor.getLong(_cursorIndexOfShownAt);
            }
            _item = new VocabularyEntity(_tmpId,_tmpWord,_tmpDefinition,_tmpPronunciation,_tmpPartOfSpeech,_tmpExampleSentence,_tmpSynonyms,_tmpAntonyms,_tmpOrigin,_tmpDifficulty,_tmpCategory,_tmpIsLearned,_tmpLearnedAt,_tmpReviewCount,_tmpLastReviewedAt,_tmpNextReviewAt,_tmpMasteryLevel,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
            _result.add(_item);
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
  public Flow<List<VocabularyEntity>> searchVocabulary(final String query) {
    final String _sql = "SELECT * FROM vocabulary WHERE word LIKE '%' || ? || '%' OR definition LIKE '%' || ? || '%' ORDER BY word ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"vocabulary"}, new Callable<List<VocabularyEntity>>() {
      @Override
      @NonNull
      public List<VocabularyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWord = CursorUtil.getColumnIndexOrThrow(_cursor, "word");
          final int _cursorIndexOfDefinition = CursorUtil.getColumnIndexOrThrow(_cursor, "definition");
          final int _cursorIndexOfPronunciation = CursorUtil.getColumnIndexOrThrow(_cursor, "pronunciation");
          final int _cursorIndexOfPartOfSpeech = CursorUtil.getColumnIndexOrThrow(_cursor, "partOfSpeech");
          final int _cursorIndexOfExampleSentence = CursorUtil.getColumnIndexOrThrow(_cursor, "exampleSentence");
          final int _cursorIndexOfSynonyms = CursorUtil.getColumnIndexOrThrow(_cursor, "synonyms");
          final int _cursorIndexOfAntonyms = CursorUtil.getColumnIndexOrThrow(_cursor, "antonyms");
          final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsLearned = CursorUtil.getColumnIndexOrThrow(_cursor, "isLearned");
          final int _cursorIndexOfLearnedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "learnedAt");
          final int _cursorIndexOfReviewCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewCount");
          final int _cursorIndexOfLastReviewedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedAt");
          final int _cursorIndexOfNextReviewAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewAt");
          final int _cursorIndexOfMasteryLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "masteryLevel");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final List<VocabularyEntity> _result = new ArrayList<VocabularyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VocabularyEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpWord;
            _tmpWord = _cursor.getString(_cursorIndexOfWord);
            final String _tmpDefinition;
            _tmpDefinition = _cursor.getString(_cursorIndexOfDefinition);
            final String _tmpPronunciation;
            _tmpPronunciation = _cursor.getString(_cursorIndexOfPronunciation);
            final String _tmpPartOfSpeech;
            _tmpPartOfSpeech = _cursor.getString(_cursorIndexOfPartOfSpeech);
            final String _tmpExampleSentence;
            _tmpExampleSentence = _cursor.getString(_cursorIndexOfExampleSentence);
            final String _tmpSynonyms;
            _tmpSynonyms = _cursor.getString(_cursorIndexOfSynonyms);
            final String _tmpAntonyms;
            _tmpAntonyms = _cursor.getString(_cursorIndexOfAntonyms);
            final String _tmpOrigin;
            _tmpOrigin = _cursor.getString(_cursorIndexOfOrigin);
            final int _tmpDifficulty;
            _tmpDifficulty = _cursor.getInt(_cursorIndexOfDifficulty);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final boolean _tmpIsLearned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLearned);
            _tmpIsLearned = _tmp != 0;
            final Long _tmpLearnedAt;
            if (_cursor.isNull(_cursorIndexOfLearnedAt)) {
              _tmpLearnedAt = null;
            } else {
              _tmpLearnedAt = _cursor.getLong(_cursorIndexOfLearnedAt);
            }
            final int _tmpReviewCount;
            _tmpReviewCount = _cursor.getInt(_cursorIndexOfReviewCount);
            final Long _tmpLastReviewedAt;
            if (_cursor.isNull(_cursorIndexOfLastReviewedAt)) {
              _tmpLastReviewedAt = null;
            } else {
              _tmpLastReviewedAt = _cursor.getLong(_cursorIndexOfLastReviewedAt);
            }
            final Long _tmpNextReviewAt;
            if (_cursor.isNull(_cursorIndexOfNextReviewAt)) {
              _tmpNextReviewAt = null;
            } else {
              _tmpNextReviewAt = _cursor.getLong(_cursorIndexOfNextReviewAt);
            }
            final int _tmpMasteryLevel;
            _tmpMasteryLevel = _cursor.getInt(_cursorIndexOfMasteryLevel);
            final boolean _tmpIsFavorite;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_1 != 0;
            final boolean _tmpShownAsDaily;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfShownAsDaily);
            _tmpShownAsDaily = _tmp_2 != 0;
            final Long _tmpShownAt;
            if (_cursor.isNull(_cursorIndexOfShownAt)) {
              _tmpShownAt = null;
            } else {
              _tmpShownAt = _cursor.getLong(_cursorIndexOfShownAt);
            }
            _item = new VocabularyEntity(_tmpId,_tmpWord,_tmpDefinition,_tmpPronunciation,_tmpPartOfSpeech,_tmpExampleSentence,_tmpSynonyms,_tmpAntonyms,_tmpOrigin,_tmpDifficulty,_tmpCategory,_tmpIsLearned,_tmpLearnedAt,_tmpReviewCount,_tmpLastReviewedAt,_tmpNextReviewAt,_tmpMasteryLevel,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
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
  public Flow<Integer> getLearnedCount() {
    final String _sql = "SELECT COUNT(*) FROM vocabulary WHERE isLearned = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"vocabulary"}, new Callable<Integer>() {
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
  public Flow<Integer> getTotalCount() {
    final String _sql = "SELECT COUNT(*) FROM vocabulary";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"vocabulary"}, new Callable<Integer>() {
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
  public Flow<Float> getAverageMastery() {
    final String _sql = "SELECT AVG(masteryLevel) FROM vocabulary WHERE isLearned = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"vocabulary"}, new Callable<Float>() {
      @Override
      @Nullable
      public Float call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Float _result;
          if (_cursor.moveToFirst()) {
            final Float _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getFloat(0);
            }
            _result = _tmp;
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
  public Flow<List<String>> getAllCategories() {
    final String _sql = "SELECT DISTINCT category FROM vocabulary";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"vocabulary"}, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            _item = _cursor.getString(0);
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
  public Object getWordsNeedingPractice(final int limit,
      final Continuation<? super List<VocabularyEntity>> $completion) {
    final String _sql = "SELECT * FROM vocabulary WHERE isLearned = 1 ORDER BY masteryLevel ASC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<VocabularyEntity>>() {
      @Override
      @NonNull
      public List<VocabularyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWord = CursorUtil.getColumnIndexOrThrow(_cursor, "word");
          final int _cursorIndexOfDefinition = CursorUtil.getColumnIndexOrThrow(_cursor, "definition");
          final int _cursorIndexOfPronunciation = CursorUtil.getColumnIndexOrThrow(_cursor, "pronunciation");
          final int _cursorIndexOfPartOfSpeech = CursorUtil.getColumnIndexOrThrow(_cursor, "partOfSpeech");
          final int _cursorIndexOfExampleSentence = CursorUtil.getColumnIndexOrThrow(_cursor, "exampleSentence");
          final int _cursorIndexOfSynonyms = CursorUtil.getColumnIndexOrThrow(_cursor, "synonyms");
          final int _cursorIndexOfAntonyms = CursorUtil.getColumnIndexOrThrow(_cursor, "antonyms");
          final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsLearned = CursorUtil.getColumnIndexOrThrow(_cursor, "isLearned");
          final int _cursorIndexOfLearnedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "learnedAt");
          final int _cursorIndexOfReviewCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewCount");
          final int _cursorIndexOfLastReviewedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedAt");
          final int _cursorIndexOfNextReviewAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewAt");
          final int _cursorIndexOfMasteryLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "masteryLevel");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final List<VocabularyEntity> _result = new ArrayList<VocabularyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VocabularyEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpWord;
            _tmpWord = _cursor.getString(_cursorIndexOfWord);
            final String _tmpDefinition;
            _tmpDefinition = _cursor.getString(_cursorIndexOfDefinition);
            final String _tmpPronunciation;
            _tmpPronunciation = _cursor.getString(_cursorIndexOfPronunciation);
            final String _tmpPartOfSpeech;
            _tmpPartOfSpeech = _cursor.getString(_cursorIndexOfPartOfSpeech);
            final String _tmpExampleSentence;
            _tmpExampleSentence = _cursor.getString(_cursorIndexOfExampleSentence);
            final String _tmpSynonyms;
            _tmpSynonyms = _cursor.getString(_cursorIndexOfSynonyms);
            final String _tmpAntonyms;
            _tmpAntonyms = _cursor.getString(_cursorIndexOfAntonyms);
            final String _tmpOrigin;
            _tmpOrigin = _cursor.getString(_cursorIndexOfOrigin);
            final int _tmpDifficulty;
            _tmpDifficulty = _cursor.getInt(_cursorIndexOfDifficulty);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final boolean _tmpIsLearned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLearned);
            _tmpIsLearned = _tmp != 0;
            final Long _tmpLearnedAt;
            if (_cursor.isNull(_cursorIndexOfLearnedAt)) {
              _tmpLearnedAt = null;
            } else {
              _tmpLearnedAt = _cursor.getLong(_cursorIndexOfLearnedAt);
            }
            final int _tmpReviewCount;
            _tmpReviewCount = _cursor.getInt(_cursorIndexOfReviewCount);
            final Long _tmpLastReviewedAt;
            if (_cursor.isNull(_cursorIndexOfLastReviewedAt)) {
              _tmpLastReviewedAt = null;
            } else {
              _tmpLastReviewedAt = _cursor.getLong(_cursorIndexOfLastReviewedAt);
            }
            final Long _tmpNextReviewAt;
            if (_cursor.isNull(_cursorIndexOfNextReviewAt)) {
              _tmpNextReviewAt = null;
            } else {
              _tmpNextReviewAt = _cursor.getLong(_cursorIndexOfNextReviewAt);
            }
            final int _tmpMasteryLevel;
            _tmpMasteryLevel = _cursor.getInt(_cursorIndexOfMasteryLevel);
            final boolean _tmpIsFavorite;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_1 != 0;
            final boolean _tmpShownAsDaily;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfShownAsDaily);
            _tmpShownAsDaily = _tmp_2 != 0;
            final Long _tmpShownAt;
            if (_cursor.isNull(_cursorIndexOfShownAt)) {
              _tmpShownAt = null;
            } else {
              _tmpShownAt = _cursor.getLong(_cursorIndexOfShownAt);
            }
            _item = new VocabularyEntity(_tmpId,_tmpWord,_tmpDefinition,_tmpPronunciation,_tmpPartOfSpeech,_tmpExampleSentence,_tmpSynonyms,_tmpAntonyms,_tmpOrigin,_tmpDifficulty,_tmpCategory,_tmpIsLearned,_tmpLearnedAt,_tmpReviewCount,_tmpLastReviewedAt,_tmpNextReviewAt,_tmpMasteryLevel,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
            _result.add(_item);
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
  public Object getVocabularyProgressSync(
      final Continuation<? super List<VocabularyEntity>> $completion) {
    final String _sql = "SELECT * FROM vocabulary WHERE isLearned = 1 OR isFavorite = 1 OR reviewCount > 0 ORDER BY word ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<VocabularyEntity>>() {
      @Override
      @NonNull
      public List<VocabularyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWord = CursorUtil.getColumnIndexOrThrow(_cursor, "word");
          final int _cursorIndexOfDefinition = CursorUtil.getColumnIndexOrThrow(_cursor, "definition");
          final int _cursorIndexOfPronunciation = CursorUtil.getColumnIndexOrThrow(_cursor, "pronunciation");
          final int _cursorIndexOfPartOfSpeech = CursorUtil.getColumnIndexOrThrow(_cursor, "partOfSpeech");
          final int _cursorIndexOfExampleSentence = CursorUtil.getColumnIndexOrThrow(_cursor, "exampleSentence");
          final int _cursorIndexOfSynonyms = CursorUtil.getColumnIndexOrThrow(_cursor, "synonyms");
          final int _cursorIndexOfAntonyms = CursorUtil.getColumnIndexOrThrow(_cursor, "antonyms");
          final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsLearned = CursorUtil.getColumnIndexOrThrow(_cursor, "isLearned");
          final int _cursorIndexOfLearnedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "learnedAt");
          final int _cursorIndexOfReviewCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewCount");
          final int _cursorIndexOfLastReviewedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedAt");
          final int _cursorIndexOfNextReviewAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewAt");
          final int _cursorIndexOfMasteryLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "masteryLevel");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final List<VocabularyEntity> _result = new ArrayList<VocabularyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VocabularyEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpWord;
            _tmpWord = _cursor.getString(_cursorIndexOfWord);
            final String _tmpDefinition;
            _tmpDefinition = _cursor.getString(_cursorIndexOfDefinition);
            final String _tmpPronunciation;
            _tmpPronunciation = _cursor.getString(_cursorIndexOfPronunciation);
            final String _tmpPartOfSpeech;
            _tmpPartOfSpeech = _cursor.getString(_cursorIndexOfPartOfSpeech);
            final String _tmpExampleSentence;
            _tmpExampleSentence = _cursor.getString(_cursorIndexOfExampleSentence);
            final String _tmpSynonyms;
            _tmpSynonyms = _cursor.getString(_cursorIndexOfSynonyms);
            final String _tmpAntonyms;
            _tmpAntonyms = _cursor.getString(_cursorIndexOfAntonyms);
            final String _tmpOrigin;
            _tmpOrigin = _cursor.getString(_cursorIndexOfOrigin);
            final int _tmpDifficulty;
            _tmpDifficulty = _cursor.getInt(_cursorIndexOfDifficulty);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final boolean _tmpIsLearned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLearned);
            _tmpIsLearned = _tmp != 0;
            final Long _tmpLearnedAt;
            if (_cursor.isNull(_cursorIndexOfLearnedAt)) {
              _tmpLearnedAt = null;
            } else {
              _tmpLearnedAt = _cursor.getLong(_cursorIndexOfLearnedAt);
            }
            final int _tmpReviewCount;
            _tmpReviewCount = _cursor.getInt(_cursorIndexOfReviewCount);
            final Long _tmpLastReviewedAt;
            if (_cursor.isNull(_cursorIndexOfLastReviewedAt)) {
              _tmpLastReviewedAt = null;
            } else {
              _tmpLastReviewedAt = _cursor.getLong(_cursorIndexOfLastReviewedAt);
            }
            final Long _tmpNextReviewAt;
            if (_cursor.isNull(_cursorIndexOfNextReviewAt)) {
              _tmpNextReviewAt = null;
            } else {
              _tmpNextReviewAt = _cursor.getLong(_cursorIndexOfNextReviewAt);
            }
            final int _tmpMasteryLevel;
            _tmpMasteryLevel = _cursor.getInt(_cursorIndexOfMasteryLevel);
            final boolean _tmpIsFavorite;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_1 != 0;
            final boolean _tmpShownAsDaily;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfShownAsDaily);
            _tmpShownAsDaily = _tmp_2 != 0;
            final Long _tmpShownAt;
            if (_cursor.isNull(_cursorIndexOfShownAt)) {
              _tmpShownAt = null;
            } else {
              _tmpShownAt = _cursor.getLong(_cursorIndexOfShownAt);
            }
            _item = new VocabularyEntity(_tmpId,_tmpWord,_tmpDefinition,_tmpPronunciation,_tmpPartOfSpeech,_tmpExampleSentence,_tmpSynonyms,_tmpAntonyms,_tmpOrigin,_tmpDifficulty,_tmpCategory,_tmpIsLearned,_tmpLearnedAt,_tmpReviewCount,_tmpLastReviewedAt,_tmpNextReviewAt,_tmpMasteryLevel,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
            _result.add(_item);
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
  public Object getAllVocabularySync(
      final Continuation<? super List<VocabularyEntity>> $completion) {
    final String _sql = "SELECT * FROM vocabulary ORDER BY word ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<VocabularyEntity>>() {
      @Override
      @NonNull
      public List<VocabularyEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfWord = CursorUtil.getColumnIndexOrThrow(_cursor, "word");
          final int _cursorIndexOfDefinition = CursorUtil.getColumnIndexOrThrow(_cursor, "definition");
          final int _cursorIndexOfPronunciation = CursorUtil.getColumnIndexOrThrow(_cursor, "pronunciation");
          final int _cursorIndexOfPartOfSpeech = CursorUtil.getColumnIndexOrThrow(_cursor, "partOfSpeech");
          final int _cursorIndexOfExampleSentence = CursorUtil.getColumnIndexOrThrow(_cursor, "exampleSentence");
          final int _cursorIndexOfSynonyms = CursorUtil.getColumnIndexOrThrow(_cursor, "synonyms");
          final int _cursorIndexOfAntonyms = CursorUtil.getColumnIndexOrThrow(_cursor, "antonyms");
          final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
          final int _cursorIndexOfDifficulty = CursorUtil.getColumnIndexOrThrow(_cursor, "difficulty");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsLearned = CursorUtil.getColumnIndexOrThrow(_cursor, "isLearned");
          final int _cursorIndexOfLearnedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "learnedAt");
          final int _cursorIndexOfReviewCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewCount");
          final int _cursorIndexOfLastReviewedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastReviewedAt");
          final int _cursorIndexOfNextReviewAt = CursorUtil.getColumnIndexOrThrow(_cursor, "nextReviewAt");
          final int _cursorIndexOfMasteryLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "masteryLevel");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final List<VocabularyEntity> _result = new ArrayList<VocabularyEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VocabularyEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpWord;
            _tmpWord = _cursor.getString(_cursorIndexOfWord);
            final String _tmpDefinition;
            _tmpDefinition = _cursor.getString(_cursorIndexOfDefinition);
            final String _tmpPronunciation;
            _tmpPronunciation = _cursor.getString(_cursorIndexOfPronunciation);
            final String _tmpPartOfSpeech;
            _tmpPartOfSpeech = _cursor.getString(_cursorIndexOfPartOfSpeech);
            final String _tmpExampleSentence;
            _tmpExampleSentence = _cursor.getString(_cursorIndexOfExampleSentence);
            final String _tmpSynonyms;
            _tmpSynonyms = _cursor.getString(_cursorIndexOfSynonyms);
            final String _tmpAntonyms;
            _tmpAntonyms = _cursor.getString(_cursorIndexOfAntonyms);
            final String _tmpOrigin;
            _tmpOrigin = _cursor.getString(_cursorIndexOfOrigin);
            final int _tmpDifficulty;
            _tmpDifficulty = _cursor.getInt(_cursorIndexOfDifficulty);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final boolean _tmpIsLearned;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLearned);
            _tmpIsLearned = _tmp != 0;
            final Long _tmpLearnedAt;
            if (_cursor.isNull(_cursorIndexOfLearnedAt)) {
              _tmpLearnedAt = null;
            } else {
              _tmpLearnedAt = _cursor.getLong(_cursorIndexOfLearnedAt);
            }
            final int _tmpReviewCount;
            _tmpReviewCount = _cursor.getInt(_cursorIndexOfReviewCount);
            final Long _tmpLastReviewedAt;
            if (_cursor.isNull(_cursorIndexOfLastReviewedAt)) {
              _tmpLastReviewedAt = null;
            } else {
              _tmpLastReviewedAt = _cursor.getLong(_cursorIndexOfLastReviewedAt);
            }
            final Long _tmpNextReviewAt;
            if (_cursor.isNull(_cursorIndexOfNextReviewAt)) {
              _tmpNextReviewAt = null;
            } else {
              _tmpNextReviewAt = _cursor.getLong(_cursorIndexOfNextReviewAt);
            }
            final int _tmpMasteryLevel;
            _tmpMasteryLevel = _cursor.getInt(_cursorIndexOfMasteryLevel);
            final boolean _tmpIsFavorite;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_1 != 0;
            final boolean _tmpShownAsDaily;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfShownAsDaily);
            _tmpShownAsDaily = _tmp_2 != 0;
            final Long _tmpShownAt;
            if (_cursor.isNull(_cursorIndexOfShownAt)) {
              _tmpShownAt = null;
            } else {
              _tmpShownAt = _cursor.getLong(_cursorIndexOfShownAt);
            }
            _item = new VocabularyEntity(_tmpId,_tmpWord,_tmpDefinition,_tmpPronunciation,_tmpPartOfSpeech,_tmpExampleSentence,_tmpSynonyms,_tmpAntonyms,_tmpOrigin,_tmpDifficulty,_tmpCategory,_tmpIsLearned,_tmpLearnedAt,_tmpReviewCount,_tmpLastReviewedAt,_tmpNextReviewAt,_tmpMasteryLevel,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
