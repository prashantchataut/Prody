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
import com.prody.prashant.data.local.entity.ProverbEntity;
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
public final class ProverbDao_Impl implements ProverbDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ProverbEntity> __insertionAdapterOfProverbEntity;

  private final EntityDeletionOrUpdateAdapter<ProverbEntity> __deletionAdapterOfProverbEntity;

  private final EntityDeletionOrUpdateAdapter<ProverbEntity> __updateAdapterOfProverbEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateFavoriteStatus;

  private final SharedSQLiteStatement __preparedStmtOfMarkAsShownDaily;

  public ProverbDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfProverbEntity = new EntityInsertionAdapter<ProverbEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `proverbs` (`id`,`content`,`meaning`,`origin`,`usage`,`category`,`isFavorite`,`shownAsDaily`,`shownAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ProverbEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getContent());
        statement.bindString(3, entity.getMeaning());
        statement.bindString(4, entity.getOrigin());
        statement.bindString(5, entity.getUsage());
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
    this.__deletionAdapterOfProverbEntity = new EntityDeletionOrUpdateAdapter<ProverbEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `proverbs` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ProverbEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfProverbEntity = new EntityDeletionOrUpdateAdapter<ProverbEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `proverbs` SET `id` = ?,`content` = ?,`meaning` = ?,`origin` = ?,`usage` = ?,`category` = ?,`isFavorite` = ?,`shownAsDaily` = ?,`shownAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ProverbEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getContent());
        statement.bindString(3, entity.getMeaning());
        statement.bindString(4, entity.getOrigin());
        statement.bindString(5, entity.getUsage());
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
        final String _query = "UPDATE proverbs SET isFavorite = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkAsShownDaily = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE proverbs SET shownAsDaily = 1, shownAt = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertProverb(final ProverbEntity proverb,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfProverbEntity.insertAndReturnId(proverb);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertProverbs(final List<ProverbEntity> proverbs,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfProverbEntity.insert(proverbs);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteProverb(final ProverbEntity proverb,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfProverbEntity.handle(proverb);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateProverb(final ProverbEntity proverb,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfProverbEntity.handle(proverb);
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
  public Flow<List<ProverbEntity>> getAllProverbs() {
    final String _sql = "SELECT * FROM proverbs ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"proverbs"}, new Callable<List<ProverbEntity>>() {
      @Override
      @NonNull
      public List<ProverbEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfMeaning = CursorUtil.getColumnIndexOrThrow(_cursor, "meaning");
          final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
          final int _cursorIndexOfUsage = CursorUtil.getColumnIndexOrThrow(_cursor, "usage");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final List<ProverbEntity> _result = new ArrayList<ProverbEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProverbEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpMeaning;
            _tmpMeaning = _cursor.getString(_cursorIndexOfMeaning);
            final String _tmpOrigin;
            _tmpOrigin = _cursor.getString(_cursorIndexOfOrigin);
            final String _tmpUsage;
            _tmpUsage = _cursor.getString(_cursorIndexOfUsage);
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
            _item = new ProverbEntity(_tmpId,_tmpContent,_tmpMeaning,_tmpOrigin,_tmpUsage,_tmpCategory,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
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
  public Object getProverbById(final long id,
      final Continuation<? super ProverbEntity> $completion) {
    final String _sql = "SELECT * FROM proverbs WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ProverbEntity>() {
      @Override
      @Nullable
      public ProverbEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfMeaning = CursorUtil.getColumnIndexOrThrow(_cursor, "meaning");
          final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
          final int _cursorIndexOfUsage = CursorUtil.getColumnIndexOrThrow(_cursor, "usage");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final ProverbEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpMeaning;
            _tmpMeaning = _cursor.getString(_cursorIndexOfMeaning);
            final String _tmpOrigin;
            _tmpOrigin = _cursor.getString(_cursorIndexOfOrigin);
            final String _tmpUsage;
            _tmpUsage = _cursor.getString(_cursorIndexOfUsage);
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
            _result = new ProverbEntity(_tmpId,_tmpContent,_tmpMeaning,_tmpOrigin,_tmpUsage,_tmpCategory,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
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
  public Object getProverbOfTheDay(final Continuation<? super ProverbEntity> $completion) {
    final String _sql = "SELECT * FROM proverbs WHERE shownAsDaily = 0 ORDER BY RANDOM() LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ProverbEntity>() {
      @Override
      @Nullable
      public ProverbEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfMeaning = CursorUtil.getColumnIndexOrThrow(_cursor, "meaning");
          final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
          final int _cursorIndexOfUsage = CursorUtil.getColumnIndexOrThrow(_cursor, "usage");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final ProverbEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpMeaning;
            _tmpMeaning = _cursor.getString(_cursorIndexOfMeaning);
            final String _tmpOrigin;
            _tmpOrigin = _cursor.getString(_cursorIndexOfOrigin);
            final String _tmpUsage;
            _tmpUsage = _cursor.getString(_cursorIndexOfUsage);
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
            _result = new ProverbEntity(_tmpId,_tmpContent,_tmpMeaning,_tmpOrigin,_tmpUsage,_tmpCategory,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
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
  public Flow<List<ProverbEntity>> getFavoriteProverbs() {
    final String _sql = "SELECT * FROM proverbs WHERE isFavorite = 1 ORDER BY id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"proverbs"}, new Callable<List<ProverbEntity>>() {
      @Override
      @NonNull
      public List<ProverbEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfMeaning = CursorUtil.getColumnIndexOrThrow(_cursor, "meaning");
          final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
          final int _cursorIndexOfUsage = CursorUtil.getColumnIndexOrThrow(_cursor, "usage");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final List<ProverbEntity> _result = new ArrayList<ProverbEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProverbEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpMeaning;
            _tmpMeaning = _cursor.getString(_cursorIndexOfMeaning);
            final String _tmpOrigin;
            _tmpOrigin = _cursor.getString(_cursorIndexOfOrigin);
            final String _tmpUsage;
            _tmpUsage = _cursor.getString(_cursorIndexOfUsage);
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
            _item = new ProverbEntity(_tmpId,_tmpContent,_tmpMeaning,_tmpOrigin,_tmpUsage,_tmpCategory,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
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
  public Flow<List<ProverbEntity>> getProverbsByCategory(final String category) {
    final String _sql = "SELECT * FROM proverbs WHERE category = ? ORDER BY RANDOM()";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, category);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"proverbs"}, new Callable<List<ProverbEntity>>() {
      @Override
      @NonNull
      public List<ProverbEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfMeaning = CursorUtil.getColumnIndexOrThrow(_cursor, "meaning");
          final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
          final int _cursorIndexOfUsage = CursorUtil.getColumnIndexOrThrow(_cursor, "usage");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final List<ProverbEntity> _result = new ArrayList<ProverbEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProverbEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpMeaning;
            _tmpMeaning = _cursor.getString(_cursorIndexOfMeaning);
            final String _tmpOrigin;
            _tmpOrigin = _cursor.getString(_cursorIndexOfOrigin);
            final String _tmpUsage;
            _tmpUsage = _cursor.getString(_cursorIndexOfUsage);
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
            _item = new ProverbEntity(_tmpId,_tmpContent,_tmpMeaning,_tmpOrigin,_tmpUsage,_tmpCategory,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
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
  public Flow<List<ProverbEntity>> getProverbsByOrigin(final String origin) {
    final String _sql = "SELECT * FROM proverbs WHERE origin = ? ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, origin);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"proverbs"}, new Callable<List<ProverbEntity>>() {
      @Override
      @NonNull
      public List<ProverbEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfMeaning = CursorUtil.getColumnIndexOrThrow(_cursor, "meaning");
          final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
          final int _cursorIndexOfUsage = CursorUtil.getColumnIndexOrThrow(_cursor, "usage");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final List<ProverbEntity> _result = new ArrayList<ProverbEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProverbEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpMeaning;
            _tmpMeaning = _cursor.getString(_cursorIndexOfMeaning);
            final String _tmpOrigin;
            _tmpOrigin = _cursor.getString(_cursorIndexOfOrigin);
            final String _tmpUsage;
            _tmpUsage = _cursor.getString(_cursorIndexOfUsage);
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
            _item = new ProverbEntity(_tmpId,_tmpContent,_tmpMeaning,_tmpOrigin,_tmpUsage,_tmpCategory,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
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
  public Flow<List<ProverbEntity>> searchProverbs(final String query) {
    final String _sql = "SELECT * FROM proverbs WHERE content LIKE '%' || ? || '%' OR meaning LIKE '%' || ? || '%'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"proverbs"}, new Callable<List<ProverbEntity>>() {
      @Override
      @NonNull
      public List<ProverbEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfMeaning = CursorUtil.getColumnIndexOrThrow(_cursor, "meaning");
          final int _cursorIndexOfOrigin = CursorUtil.getColumnIndexOrThrow(_cursor, "origin");
          final int _cursorIndexOfUsage = CursorUtil.getColumnIndexOrThrow(_cursor, "usage");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final List<ProverbEntity> _result = new ArrayList<ProverbEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ProverbEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpMeaning;
            _tmpMeaning = _cursor.getString(_cursorIndexOfMeaning);
            final String _tmpOrigin;
            _tmpOrigin = _cursor.getString(_cursorIndexOfOrigin);
            final String _tmpUsage;
            _tmpUsage = _cursor.getString(_cursorIndexOfUsage);
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
            _item = new ProverbEntity(_tmpId,_tmpContent,_tmpMeaning,_tmpOrigin,_tmpUsage,_tmpCategory,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt);
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
