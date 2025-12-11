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
import com.prody.prashant.data.local.entity.PhraseEntity;
import java.lang.Class;
import java.lang.Exception;
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
public final class PhraseDao_Impl implements PhraseDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PhraseEntity> __insertionAdapterOfPhraseEntity;

  private final EntityDeletionOrUpdateAdapter<PhraseEntity> __deletionAdapterOfPhraseEntity;

  private final EntityDeletionOrUpdateAdapter<PhraseEntity> __updateAdapterOfPhraseEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateFavoriteStatus;

  private final SharedSQLiteStatement __preparedStmtOfMarkAsShownDaily;

  public PhraseDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPhraseEntity = new EntityInsertionAdapter<PhraseEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `phrases` (`id`,`phrase`,`meaning`,`usage`,`exampleSentence`,`formality`,`category`,`isFavorite`,`shownAsDaily`,`shownAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PhraseEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getPhrase());
        statement.bindString(3, entity.getMeaning());
        statement.bindString(4, entity.getUsage());
        statement.bindString(5, entity.getExampleSentence());
        statement.bindString(6, entity.getFormality());
        statement.bindString(7, entity.getCategory());
        final int _tmp = entity.isFavorite() ? 1 : 0;
        statement.bindLong(8, _tmp);
        final int _tmp_1 = entity.getShownAsDaily() ? 1 : 0;
        statement.bindLong(9, _tmp_1);
        if (entity.getShownAt() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getShownAt());
        }
      }
    };
    this.__deletionAdapterOfPhraseEntity = new EntityDeletionOrUpdateAdapter<PhraseEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `phrases` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PhraseEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfPhraseEntity = new EntityDeletionOrUpdateAdapter<PhraseEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `phrases` SET `id` = ?,`phrase` = ?,`meaning` = ?,`usage` = ?,`exampleSentence` = ?,`formality` = ?,`category` = ?,`isFavorite` = ?,`shownAsDaily` = ?,`shownAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PhraseEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getPhrase());
        statement.bindString(3, entity.getMeaning());
        statement.bindString(4, entity.getUsage());
        statement.bindString(5, entity.getExampleSentence());
        statement.bindString(6, entity.getFormality());
        statement.bindString(7, entity.getCategory());
        final int _tmp = entity.isFavorite() ? 1 : 0;
        statement.bindLong(8, _tmp);
        final int _tmp_1 = entity.getShownAsDaily() ? 1 : 0;
        statement.bindLong(9, _tmp_1);
        if (entity.getShownAt() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getShownAt());
        }
        statement.bindLong(11, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateFavoriteStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE phrases SET isFavorite = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkAsShownDaily = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE phrases SET shownAsDaily = 1, shownAt = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertPhrase(final PhraseEntity phrase,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfPhraseEntity.insertAndReturnId(phrase);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertPhrases(final List<PhraseEntity> phrases,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPhraseEntity.insert(phrases);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deletePhrase(final PhraseEntity phrase,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfPhraseEntity.handle(phrase);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updatePhrase(final PhraseEntity phrase,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfPhraseEntity.handle(phrase);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
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
  public Flow<List<PhraseEntity>> getAllPhrases() {
    final String _sql = "SELECT * FROM phrases ORDER BY phrase ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"phrases"}, new Callable<List<PhraseEntity>>() {
      @Override
      @NonNull
      public List<PhraseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhrase = CursorUtil.getColumnIndexOrThrow(_cursor, "phrase");
          final int _cursorIndexOfMeaning = CursorUtil.getColumnIndexOrThrow(_cursor, "meaning");
          final int _cursorIndexOfUsage = CursorUtil.getColumnIndexOrThrow(_cursor, "usage");
          final int _cursorIndexOfExampleSentence = CursorUtil.getColumnIndexOrThrow(_cursor, "exampleSentence");
          final int _cursorIndexOfFormality = CursorUtil.getColumnIndexOrThrow(_cursor, "formality");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final List<PhraseEntity> _result = new ArrayList<PhraseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PhraseEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPhrase;
            _tmpPhrase = _cursor.getString(_cursorIndexOfPhrase);
            final String _tmpMeaning;
            _tmpMeaning = _cursor.getString(_cursorIndexOfMeaning);
            final String _tmpUsage;
            _tmpUsage = _cursor.getString(_cursorIndexOfUsage);
            final String _tmpExampleSentence;
            _tmpExampleSentence = _cursor.getString(_cursorIndexOfExampleSentence);
            final String _tmpFormality;
            _tmpFormality = _cursor.getString(_cursorIndexOfFormality);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final boolean _tmpShownAsDaily;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfShownAsDaily);
            _tmpShownAsDaily = _tmp_1 != 0;
            final Long _tmpShownAt;
            if (_cursor.isNull(_cursorIndexOfShownAt)) {
              _tmpShownAt = null;
            } else {
              _tmpShownAt = _cursor.getLong(_cursorIndexOfShownAt);
            }
            _item = new PhraseEntity(_tmpId,_tmpPhrase,_tmpMeaning,_tmpUsage,_tmpExampleSentence,_tmpFormality,_tmpCategory,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
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
  public Object getPhraseById(final long id, final Continuation<? super PhraseEntity> $completion) {
    final String _sql = "SELECT * FROM phrases WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PhraseEntity>() {
      @Override
      @Nullable
      public PhraseEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhrase = CursorUtil.getColumnIndexOrThrow(_cursor, "phrase");
          final int _cursorIndexOfMeaning = CursorUtil.getColumnIndexOrThrow(_cursor, "meaning");
          final int _cursorIndexOfUsage = CursorUtil.getColumnIndexOrThrow(_cursor, "usage");
          final int _cursorIndexOfExampleSentence = CursorUtil.getColumnIndexOrThrow(_cursor, "exampleSentence");
          final int _cursorIndexOfFormality = CursorUtil.getColumnIndexOrThrow(_cursor, "formality");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final PhraseEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPhrase;
            _tmpPhrase = _cursor.getString(_cursorIndexOfPhrase);
            final String _tmpMeaning;
            _tmpMeaning = _cursor.getString(_cursorIndexOfMeaning);
            final String _tmpUsage;
            _tmpUsage = _cursor.getString(_cursorIndexOfUsage);
            final String _tmpExampleSentence;
            _tmpExampleSentence = _cursor.getString(_cursorIndexOfExampleSentence);
            final String _tmpFormality;
            _tmpFormality = _cursor.getString(_cursorIndexOfFormality);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final boolean _tmpShownAsDaily;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfShownAsDaily);
            _tmpShownAsDaily = _tmp_1 != 0;
            final Long _tmpShownAt;
            if (_cursor.isNull(_cursorIndexOfShownAt)) {
              _tmpShownAt = null;
            } else {
              _tmpShownAt = _cursor.getLong(_cursorIndexOfShownAt);
            }
            _result = new PhraseEntity(_tmpId,_tmpPhrase,_tmpMeaning,_tmpUsage,_tmpExampleSentence,_tmpFormality,_tmpCategory,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
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
  public Object getPhraseOfTheDay(final Continuation<? super PhraseEntity> $completion) {
    final String _sql = "SELECT * FROM phrases WHERE shownAsDaily = 0 ORDER BY RANDOM() LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PhraseEntity>() {
      @Override
      @Nullable
      public PhraseEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhrase = CursorUtil.getColumnIndexOrThrow(_cursor, "phrase");
          final int _cursorIndexOfMeaning = CursorUtil.getColumnIndexOrThrow(_cursor, "meaning");
          final int _cursorIndexOfUsage = CursorUtil.getColumnIndexOrThrow(_cursor, "usage");
          final int _cursorIndexOfExampleSentence = CursorUtil.getColumnIndexOrThrow(_cursor, "exampleSentence");
          final int _cursorIndexOfFormality = CursorUtil.getColumnIndexOrThrow(_cursor, "formality");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final PhraseEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPhrase;
            _tmpPhrase = _cursor.getString(_cursorIndexOfPhrase);
            final String _tmpMeaning;
            _tmpMeaning = _cursor.getString(_cursorIndexOfMeaning);
            final String _tmpUsage;
            _tmpUsage = _cursor.getString(_cursorIndexOfUsage);
            final String _tmpExampleSentence;
            _tmpExampleSentence = _cursor.getString(_cursorIndexOfExampleSentence);
            final String _tmpFormality;
            _tmpFormality = _cursor.getString(_cursorIndexOfFormality);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final boolean _tmpShownAsDaily;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfShownAsDaily);
            _tmpShownAsDaily = _tmp_1 != 0;
            final Long _tmpShownAt;
            if (_cursor.isNull(_cursorIndexOfShownAt)) {
              _tmpShownAt = null;
            } else {
              _tmpShownAt = _cursor.getLong(_cursorIndexOfShownAt);
            }
            _result = new PhraseEntity(_tmpId,_tmpPhrase,_tmpMeaning,_tmpUsage,_tmpExampleSentence,_tmpFormality,_tmpCategory,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
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
  public Flow<List<PhraseEntity>> getFavoritePhrases() {
    final String _sql = "SELECT * FROM phrases WHERE isFavorite = 1 ORDER BY phrase ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"phrases"}, new Callable<List<PhraseEntity>>() {
      @Override
      @NonNull
      public List<PhraseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhrase = CursorUtil.getColumnIndexOrThrow(_cursor, "phrase");
          final int _cursorIndexOfMeaning = CursorUtil.getColumnIndexOrThrow(_cursor, "meaning");
          final int _cursorIndexOfUsage = CursorUtil.getColumnIndexOrThrow(_cursor, "usage");
          final int _cursorIndexOfExampleSentence = CursorUtil.getColumnIndexOrThrow(_cursor, "exampleSentence");
          final int _cursorIndexOfFormality = CursorUtil.getColumnIndexOrThrow(_cursor, "formality");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final List<PhraseEntity> _result = new ArrayList<PhraseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PhraseEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPhrase;
            _tmpPhrase = _cursor.getString(_cursorIndexOfPhrase);
            final String _tmpMeaning;
            _tmpMeaning = _cursor.getString(_cursorIndexOfMeaning);
            final String _tmpUsage;
            _tmpUsage = _cursor.getString(_cursorIndexOfUsage);
            final String _tmpExampleSentence;
            _tmpExampleSentence = _cursor.getString(_cursorIndexOfExampleSentence);
            final String _tmpFormality;
            _tmpFormality = _cursor.getString(_cursorIndexOfFormality);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final boolean _tmpShownAsDaily;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfShownAsDaily);
            _tmpShownAsDaily = _tmp_1 != 0;
            final Long _tmpShownAt;
            if (_cursor.isNull(_cursorIndexOfShownAt)) {
              _tmpShownAt = null;
            } else {
              _tmpShownAt = _cursor.getLong(_cursorIndexOfShownAt);
            }
            _item = new PhraseEntity(_tmpId,_tmpPhrase,_tmpMeaning,_tmpUsage,_tmpExampleSentence,_tmpFormality,_tmpCategory,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
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
  public Flow<List<PhraseEntity>> getPhrasesByCategory(final String category) {
    final String _sql = "SELECT * FROM phrases WHERE category = ? ORDER BY phrase ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, category);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"phrases"}, new Callable<List<PhraseEntity>>() {
      @Override
      @NonNull
      public List<PhraseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhrase = CursorUtil.getColumnIndexOrThrow(_cursor, "phrase");
          final int _cursorIndexOfMeaning = CursorUtil.getColumnIndexOrThrow(_cursor, "meaning");
          final int _cursorIndexOfUsage = CursorUtil.getColumnIndexOrThrow(_cursor, "usage");
          final int _cursorIndexOfExampleSentence = CursorUtil.getColumnIndexOrThrow(_cursor, "exampleSentence");
          final int _cursorIndexOfFormality = CursorUtil.getColumnIndexOrThrow(_cursor, "formality");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final List<PhraseEntity> _result = new ArrayList<PhraseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PhraseEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPhrase;
            _tmpPhrase = _cursor.getString(_cursorIndexOfPhrase);
            final String _tmpMeaning;
            _tmpMeaning = _cursor.getString(_cursorIndexOfMeaning);
            final String _tmpUsage;
            _tmpUsage = _cursor.getString(_cursorIndexOfUsage);
            final String _tmpExampleSentence;
            _tmpExampleSentence = _cursor.getString(_cursorIndexOfExampleSentence);
            final String _tmpFormality;
            _tmpFormality = _cursor.getString(_cursorIndexOfFormality);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final boolean _tmpShownAsDaily;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfShownAsDaily);
            _tmpShownAsDaily = _tmp_1 != 0;
            final Long _tmpShownAt;
            if (_cursor.isNull(_cursorIndexOfShownAt)) {
              _tmpShownAt = null;
            } else {
              _tmpShownAt = _cursor.getLong(_cursorIndexOfShownAt);
            }
            _item = new PhraseEntity(_tmpId,_tmpPhrase,_tmpMeaning,_tmpUsage,_tmpExampleSentence,_tmpFormality,_tmpCategory,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
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
  public Flow<List<PhraseEntity>> getPhrasesByFormality(final String formality) {
    final String _sql = "SELECT * FROM phrases WHERE formality = ? ORDER BY phrase ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, formality);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"phrases"}, new Callable<List<PhraseEntity>>() {
      @Override
      @NonNull
      public List<PhraseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhrase = CursorUtil.getColumnIndexOrThrow(_cursor, "phrase");
          final int _cursorIndexOfMeaning = CursorUtil.getColumnIndexOrThrow(_cursor, "meaning");
          final int _cursorIndexOfUsage = CursorUtil.getColumnIndexOrThrow(_cursor, "usage");
          final int _cursorIndexOfExampleSentence = CursorUtil.getColumnIndexOrThrow(_cursor, "exampleSentence");
          final int _cursorIndexOfFormality = CursorUtil.getColumnIndexOrThrow(_cursor, "formality");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final List<PhraseEntity> _result = new ArrayList<PhraseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PhraseEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPhrase;
            _tmpPhrase = _cursor.getString(_cursorIndexOfPhrase);
            final String _tmpMeaning;
            _tmpMeaning = _cursor.getString(_cursorIndexOfMeaning);
            final String _tmpUsage;
            _tmpUsage = _cursor.getString(_cursorIndexOfUsage);
            final String _tmpExampleSentence;
            _tmpExampleSentence = _cursor.getString(_cursorIndexOfExampleSentence);
            final String _tmpFormality;
            _tmpFormality = _cursor.getString(_cursorIndexOfFormality);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final boolean _tmpShownAsDaily;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfShownAsDaily);
            _tmpShownAsDaily = _tmp_1 != 0;
            final Long _tmpShownAt;
            if (_cursor.isNull(_cursorIndexOfShownAt)) {
              _tmpShownAt = null;
            } else {
              _tmpShownAt = _cursor.getLong(_cursorIndexOfShownAt);
            }
            _item = new PhraseEntity(_tmpId,_tmpPhrase,_tmpMeaning,_tmpUsage,_tmpExampleSentence,_tmpFormality,_tmpCategory,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
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
  public Flow<List<PhraseEntity>> searchPhrases(final String query) {
    final String _sql = "SELECT * FROM phrases WHERE phrase LIKE '%' || ? || '%' OR meaning LIKE '%' || ? || '%'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"phrases"}, new Callable<List<PhraseEntity>>() {
      @Override
      @NonNull
      public List<PhraseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhrase = CursorUtil.getColumnIndexOrThrow(_cursor, "phrase");
          final int _cursorIndexOfMeaning = CursorUtil.getColumnIndexOrThrow(_cursor, "meaning");
          final int _cursorIndexOfUsage = CursorUtil.getColumnIndexOrThrow(_cursor, "usage");
          final int _cursorIndexOfExampleSentence = CursorUtil.getColumnIndexOrThrow(_cursor, "exampleSentence");
          final int _cursorIndexOfFormality = CursorUtil.getColumnIndexOrThrow(_cursor, "formality");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final List<PhraseEntity> _result = new ArrayList<PhraseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PhraseEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPhrase;
            _tmpPhrase = _cursor.getString(_cursorIndexOfPhrase);
            final String _tmpMeaning;
            _tmpMeaning = _cursor.getString(_cursorIndexOfMeaning);
            final String _tmpUsage;
            _tmpUsage = _cursor.getString(_cursorIndexOfUsage);
            final String _tmpExampleSentence;
            _tmpExampleSentence = _cursor.getString(_cursorIndexOfExampleSentence);
            final String _tmpFormality;
            _tmpFormality = _cursor.getString(_cursorIndexOfFormality);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final boolean _tmpIsFavorite;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp != 0;
            final boolean _tmpShownAsDaily;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfShownAsDaily);
            _tmpShownAsDaily = _tmp_1 != 0;
            final Long _tmpShownAt;
            if (_cursor.isNull(_cursorIndexOfShownAt)) {
              _tmpShownAt = null;
            } else {
              _tmpShownAt = _cursor.getLong(_cursorIndexOfShownAt);
            }
            _item = new PhraseEntity(_tmpId,_tmpPhrase,_tmpMeaning,_tmpUsage,_tmpExampleSentence,_tmpFormality,_tmpCategory,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
