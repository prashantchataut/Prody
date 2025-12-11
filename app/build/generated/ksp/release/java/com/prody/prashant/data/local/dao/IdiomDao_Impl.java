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
import com.prody.prashant.data.local.entity.IdiomEntity;
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
public final class IdiomDao_Impl implements IdiomDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<IdiomEntity> __insertionAdapterOfIdiomEntity;

  private final EntityDeletionOrUpdateAdapter<IdiomEntity> __deletionAdapterOfIdiomEntity;

  private final EntityDeletionOrUpdateAdapter<IdiomEntity> __updateAdapterOfIdiomEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateFavoriteStatus;

  private final SharedSQLiteStatement __preparedStmtOfMarkAsShownDaily;

  public IdiomDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfIdiomEntity = new EntityInsertionAdapter<IdiomEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `idioms` (`id`,`phrase`,`meaning`,`origin`,`exampleSentence`,`category`,`isFavorite`,`shownAsDaily`,`shownAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IdiomEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getPhrase());
        statement.bindString(3, entity.getMeaning());
        statement.bindString(4, entity.getOrigin());
        statement.bindString(5, entity.getExampleSentence());
        statement.bindString(6, entity.getCategory());
        final int _tmp = entity.isFavorite() ? 1 : 0;
        statement.bindLong(7, _tmp);
        final int _tmp_1 = entity.getShownAsDaily() ? 1 : 0;
        statement.bindLong(8, _tmp_1);
        if (entity.getShownAt() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getShownAt());
        }
      }
    };
    this.__deletionAdapterOfIdiomEntity = new EntityDeletionOrUpdateAdapter<IdiomEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `idioms` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IdiomEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfIdiomEntity = new EntityDeletionOrUpdateAdapter<IdiomEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `idioms` SET `id` = ?,`phrase` = ?,`meaning` = ?,`origin` = ?,`exampleSentence` = ?,`category` = ?,`isFavorite` = ?,`shownAsDaily` = ?,`shownAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final IdiomEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getPhrase());
        statement.bindString(3, entity.getMeaning());
        statement.bindString(4, entity.getOrigin());
        statement.bindString(5, entity.getExampleSentence());
        statement.bindString(6, entity.getCategory());
        final int _tmp = entity.isFavorite() ? 1 : 0;
        statement.bindLong(7, _tmp);
        final int _tmp_1 = entity.getShownAsDaily() ? 1 : 0;
        statement.bindLong(8, _tmp_1);
        if (entity.getShownAt() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getShownAt());
        }
        statement.bindLong(10, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateFavoriteStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE idioms SET isFavorite = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkAsShownDaily = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE idioms SET shownAsDaily = 1, shownAt = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertIdiom(final IdiomEntity idiom, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfIdiomEntity.insertAndReturnId(idiom);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertIdioms(final List<IdiomEntity> idioms,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfIdiomEntity.insert(idioms);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteIdiom(final IdiomEntity idiom, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfIdiomEntity.handle(idiom);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateIdiom(final IdiomEntity idiom, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfIdiomEntity.handle(idiom);
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
  public Flow<List<IdiomEntity>> getAllIdioms() {
    final String _sql = "SELECT * FROM idioms ORDER BY phrase ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"idioms"}, new Callable<List<IdiomEntity>>() {
      @Override
      @NonNull
      public List<IdiomEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhrase = CursorUtil.getColumnIndexOrThrow(_cursor, "phrase");
          final int _cursorIndexOfMeaning = CursorUtil.getColumnIndexOrThrow(_cursor, "meaning");
          final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
          final int _cursorIndexOfExampleSentence = CursorUtil.getColumnIndexOrThrow(_cursor, "exampleSentence");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final List<IdiomEntity> _result = new ArrayList<IdiomEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IdiomEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPhrase;
            _tmpPhrase = _cursor.getString(_cursorIndexOfPhrase);
            final String _tmpMeaning;
            _tmpMeaning = _cursor.getString(_cursorIndexOfMeaning);
            final String _tmpOrigin;
            _tmpOrigin = _cursor.getString(_cursorIndexOfOrigin);
            final String _tmpExampleSentence;
            _tmpExampleSentence = _cursor.getString(_cursorIndexOfExampleSentence);
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
            _item = new IdiomEntity(_tmpId,_tmpPhrase,_tmpMeaning,_tmpOrigin,_tmpExampleSentence,_tmpCategory,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
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
  public Object getIdiomById(final long id, final Continuation<? super IdiomEntity> $completion) {
    final String _sql = "SELECT * FROM idioms WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<IdiomEntity>() {
      @Override
      @Nullable
      public IdiomEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhrase = CursorUtil.getColumnIndexOrThrow(_cursor, "phrase");
          final int _cursorIndexOfMeaning = CursorUtil.getColumnIndexOrThrow(_cursor, "meaning");
          final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
          final int _cursorIndexOfExampleSentence = CursorUtil.getColumnIndexOrThrow(_cursor, "exampleSentence");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final IdiomEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPhrase;
            _tmpPhrase = _cursor.getString(_cursorIndexOfPhrase);
            final String _tmpMeaning;
            _tmpMeaning = _cursor.getString(_cursorIndexOfMeaning);
            final String _tmpOrigin;
            _tmpOrigin = _cursor.getString(_cursorIndexOfOrigin);
            final String _tmpExampleSentence;
            _tmpExampleSentence = _cursor.getString(_cursorIndexOfExampleSentence);
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
            _result = new IdiomEntity(_tmpId,_tmpPhrase,_tmpMeaning,_tmpOrigin,_tmpExampleSentence,_tmpCategory,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
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
  public Object getIdiomOfTheDay(final Continuation<? super IdiomEntity> $completion) {
    final String _sql = "SELECT * FROM idioms WHERE shownAsDaily = 0 ORDER BY RANDOM() LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<IdiomEntity>() {
      @Override
      @Nullable
      public IdiomEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhrase = CursorUtil.getColumnIndexOrThrow(_cursor, "phrase");
          final int _cursorIndexOfMeaning = CursorUtil.getColumnIndexOrThrow(_cursor, "meaning");
          final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
          final int _cursorIndexOfExampleSentence = CursorUtil.getColumnIndexOrThrow(_cursor, "exampleSentence");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final IdiomEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPhrase;
            _tmpPhrase = _cursor.getString(_cursorIndexOfPhrase);
            final String _tmpMeaning;
            _tmpMeaning = _cursor.getString(_cursorIndexOfMeaning);
            final String _tmpOrigin;
            _tmpOrigin = _cursor.getString(_cursorIndexOfOrigin);
            final String _tmpExampleSentence;
            _tmpExampleSentence = _cursor.getString(_cursorIndexOfExampleSentence);
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
            _result = new IdiomEntity(_tmpId,_tmpPhrase,_tmpMeaning,_tmpOrigin,_tmpExampleSentence,_tmpCategory,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
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
  public Flow<List<IdiomEntity>> getFavoriteIdioms() {
    final String _sql = "SELECT * FROM idioms WHERE isFavorite = 1 ORDER BY phrase ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"idioms"}, new Callable<List<IdiomEntity>>() {
      @Override
      @NonNull
      public List<IdiomEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhrase = CursorUtil.getColumnIndexOrThrow(_cursor, "phrase");
          final int _cursorIndexOfMeaning = CursorUtil.getColumnIndexOrThrow(_cursor, "meaning");
          final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
          final int _cursorIndexOfExampleSentence = CursorUtil.getColumnIndexOrThrow(_cursor, "exampleSentence");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final List<IdiomEntity> _result = new ArrayList<IdiomEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IdiomEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPhrase;
            _tmpPhrase = _cursor.getString(_cursorIndexOfPhrase);
            final String _tmpMeaning;
            _tmpMeaning = _cursor.getString(_cursorIndexOfMeaning);
            final String _tmpOrigin;
            _tmpOrigin = _cursor.getString(_cursorIndexOfOrigin);
            final String _tmpExampleSentence;
            _tmpExampleSentence = _cursor.getString(_cursorIndexOfExampleSentence);
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
            _item = new IdiomEntity(_tmpId,_tmpPhrase,_tmpMeaning,_tmpOrigin,_tmpExampleSentence,_tmpCategory,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
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
  public Flow<List<IdiomEntity>> getIdiomsByCategory(final String category) {
    final String _sql = "SELECT * FROM idioms WHERE category = ? ORDER BY phrase ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, category);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"idioms"}, new Callable<List<IdiomEntity>>() {
      @Override
      @NonNull
      public List<IdiomEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhrase = CursorUtil.getColumnIndexOrThrow(_cursor, "phrase");
          final int _cursorIndexOfMeaning = CursorUtil.getColumnIndexOrThrow(_cursor, "meaning");
          final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
          final int _cursorIndexOfExampleSentence = CursorUtil.getColumnIndexOrThrow(_cursor, "exampleSentence");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final List<IdiomEntity> _result = new ArrayList<IdiomEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IdiomEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPhrase;
            _tmpPhrase = _cursor.getString(_cursorIndexOfPhrase);
            final String _tmpMeaning;
            _tmpMeaning = _cursor.getString(_cursorIndexOfMeaning);
            final String _tmpOrigin;
            _tmpOrigin = _cursor.getString(_cursorIndexOfOrigin);
            final String _tmpExampleSentence;
            _tmpExampleSentence = _cursor.getString(_cursorIndexOfExampleSentence);
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
            _item = new IdiomEntity(_tmpId,_tmpPhrase,_tmpMeaning,_tmpOrigin,_tmpExampleSentence,_tmpCategory,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
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
  public Flow<List<IdiomEntity>> searchIdioms(final String query) {
    final String _sql = "SELECT * FROM idioms WHERE phrase LIKE '%' || ? || '%' OR meaning LIKE '%' || ? || '%'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"idioms"}, new Callable<List<IdiomEntity>>() {
      @Override
      @NonNull
      public List<IdiomEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPhrase = CursorUtil.getColumnIndexOrThrow(_cursor, "phrase");
          final int _cursorIndexOfMeaning = CursorUtil.getColumnIndexOrThrow(_cursor, "meaning");
          final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
          final int _cursorIndexOfExampleSentence = CursorUtil.getColumnIndexOrThrow(_cursor, "exampleSentence");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final List<IdiomEntity> _result = new ArrayList<IdiomEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final IdiomEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPhrase;
            _tmpPhrase = _cursor.getString(_cursorIndexOfPhrase);
            final String _tmpMeaning;
            _tmpMeaning = _cursor.getString(_cursorIndexOfMeaning);
            final String _tmpOrigin;
            _tmpOrigin = _cursor.getString(_cursorIndexOfOrigin);
            final String _tmpExampleSentence;
            _tmpExampleSentence = _cursor.getString(_cursorIndexOfExampleSentence);
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
            _item = new IdiomEntity(_tmpId,_tmpPhrase,_tmpMeaning,_tmpOrigin,_tmpExampleSentence,_tmpCategory,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
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
