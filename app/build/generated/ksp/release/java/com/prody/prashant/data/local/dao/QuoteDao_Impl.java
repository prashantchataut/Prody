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
import com.prody.prashant.data.local.entity.QuoteEntity;
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
public final class QuoteDao_Impl implements QuoteDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<QuoteEntity> __insertionAdapterOfQuoteEntity;

  private final EntityDeletionOrUpdateAdapter<QuoteEntity> __deletionAdapterOfQuoteEntity;

  private final EntityDeletionOrUpdateAdapter<QuoteEntity> __updateAdapterOfQuoteEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateFavoriteStatus;

  private final SharedSQLiteStatement __preparedStmtOfMarkAsShownDaily;

  public QuoteDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfQuoteEntity = new EntityInsertionAdapter<QuoteEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `quotes` (`id`,`content`,`author`,`source`,`category`,`tags`,`isFavorite`,`shownAsDaily`,`shownAt`,`reflectionPrompt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final QuoteEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getContent());
        statement.bindString(3, entity.getAuthor());
        statement.bindString(4, entity.getSource());
        statement.bindString(5, entity.getCategory());
        statement.bindString(6, entity.getTags());
        final int _tmp = entity.isFavorite() ? 1 : 0;
        statement.bindLong(7, _tmp);
        final int _tmp_1 = entity.getShownAsDaily() ? 1 : 0;
        statement.bindLong(8, _tmp_1);
        if (entity.getShownAt() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getShownAt());
        }
        statement.bindString(10, entity.getReflectionPrompt());
      }
    };
    this.__deletionAdapterOfQuoteEntity = new EntityDeletionOrUpdateAdapter<QuoteEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `quotes` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final QuoteEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfQuoteEntity = new EntityDeletionOrUpdateAdapter<QuoteEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `quotes` SET `id` = ?,`content` = ?,`author` = ?,`source` = ?,`category` = ?,`tags` = ?,`isFavorite` = ?,`shownAsDaily` = ?,`shownAt` = ?,`reflectionPrompt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final QuoteEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getContent());
        statement.bindString(3, entity.getAuthor());
        statement.bindString(4, entity.getSource());
        statement.bindString(5, entity.getCategory());
        statement.bindString(6, entity.getTags());
        final int _tmp = entity.isFavorite() ? 1 : 0;
        statement.bindLong(7, _tmp);
        final int _tmp_1 = entity.getShownAsDaily() ? 1 : 0;
        statement.bindLong(8, _tmp_1);
        if (entity.getShownAt() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getShownAt());
        }
        statement.bindString(10, entity.getReflectionPrompt());
        statement.bindLong(11, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateFavoriteStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE quotes SET isFavorite = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkAsShownDaily = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE quotes SET shownAsDaily = 1, shownAt = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertQuote(final QuoteEntity quote, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfQuoteEntity.insertAndReturnId(quote);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertQuotes(final List<QuoteEntity> quotes,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfQuoteEntity.insert(quotes);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteQuote(final QuoteEntity quote, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfQuoteEntity.handle(quote);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateQuote(final QuoteEntity quote, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfQuoteEntity.handle(quote);
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
  public Flow<List<QuoteEntity>> getAllQuotes() {
    final String _sql = "SELECT * FROM quotes ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"quotes"}, new Callable<List<QuoteEntity>>() {
      @Override
      @NonNull
      public List<QuoteEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfAuthor = CursorUtil.getColumnIndexOrThrow(_cursor, "author");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final int _cursorIndexOfReflectionPrompt = CursorUtil.getColumnIndexOrThrow(_cursor, "reflectionPrompt");
          final List<QuoteEntity> _result = new ArrayList<QuoteEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final QuoteEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpAuthor;
            _tmpAuthor = _cursor.getString(_cursorIndexOfAuthor);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
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
            final String _tmpReflectionPrompt;
            _tmpReflectionPrompt = _cursor.getString(_cursorIndexOfReflectionPrompt);
            _item = new QuoteEntity(_tmpId,_tmpContent,_tmpAuthor,_tmpSource,_tmpCategory,_tmpTags,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt,_tmpReflectionPrompt);
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
  public Object getQuoteById(final long id, final Continuation<? super QuoteEntity> $completion) {
    final String _sql = "SELECT * FROM quotes WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<QuoteEntity>() {
      @Override
      @Nullable
      public QuoteEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfAuthor = CursorUtil.getColumnIndexOrThrow(_cursor, "author");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final int _cursorIndexOfReflectionPrompt = CursorUtil.getColumnIndexOrThrow(_cursor, "reflectionPrompt");
          final QuoteEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpAuthor;
            _tmpAuthor = _cursor.getString(_cursorIndexOfAuthor);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
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
            final String _tmpReflectionPrompt;
            _tmpReflectionPrompt = _cursor.getString(_cursorIndexOfReflectionPrompt);
            _result = new QuoteEntity(_tmpId,_tmpContent,_tmpAuthor,_tmpSource,_tmpCategory,_tmpTags,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt,_tmpReflectionPrompt);
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
  public Object getQuoteOfTheDay(final Continuation<? super QuoteEntity> $completion) {
    final String _sql = "SELECT * FROM quotes WHERE shownAsDaily = 0 ORDER BY RANDOM() LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<QuoteEntity>() {
      @Override
      @Nullable
      public QuoteEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfAuthor = CursorUtil.getColumnIndexOrThrow(_cursor, "author");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final int _cursorIndexOfReflectionPrompt = CursorUtil.getColumnIndexOrThrow(_cursor, "reflectionPrompt");
          final QuoteEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpAuthor;
            _tmpAuthor = _cursor.getString(_cursorIndexOfAuthor);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
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
            final String _tmpReflectionPrompt;
            _tmpReflectionPrompt = _cursor.getString(_cursorIndexOfReflectionPrompt);
            _result = new QuoteEntity(_tmpId,_tmpContent,_tmpAuthor,_tmpSource,_tmpCategory,_tmpTags,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt,_tmpReflectionPrompt);
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
  public Flow<List<QuoteEntity>> getFavoriteQuotes() {
    final String _sql = "SELECT * FROM quotes WHERE isFavorite = 1 ORDER BY id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"quotes"}, new Callable<List<QuoteEntity>>() {
      @Override
      @NonNull
      public List<QuoteEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfAuthor = CursorUtil.getColumnIndexOrThrow(_cursor, "author");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final int _cursorIndexOfReflectionPrompt = CursorUtil.getColumnIndexOrThrow(_cursor, "reflectionPrompt");
          final List<QuoteEntity> _result = new ArrayList<QuoteEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final QuoteEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpAuthor;
            _tmpAuthor = _cursor.getString(_cursorIndexOfAuthor);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
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
            final String _tmpReflectionPrompt;
            _tmpReflectionPrompt = _cursor.getString(_cursorIndexOfReflectionPrompt);
            _item = new QuoteEntity(_tmpId,_tmpContent,_tmpAuthor,_tmpSource,_tmpCategory,_tmpTags,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt,_tmpReflectionPrompt);
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
  public Flow<List<QuoteEntity>> getQuotesByCategory(final String category) {
    final String _sql = "SELECT * FROM quotes WHERE category = ? ORDER BY RANDOM()";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, category);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"quotes"}, new Callable<List<QuoteEntity>>() {
      @Override
      @NonNull
      public List<QuoteEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfAuthor = CursorUtil.getColumnIndexOrThrow(_cursor, "author");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final int _cursorIndexOfReflectionPrompt = CursorUtil.getColumnIndexOrThrow(_cursor, "reflectionPrompt");
          final List<QuoteEntity> _result = new ArrayList<QuoteEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final QuoteEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpAuthor;
            _tmpAuthor = _cursor.getString(_cursorIndexOfAuthor);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
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
            final String _tmpReflectionPrompt;
            _tmpReflectionPrompt = _cursor.getString(_cursorIndexOfReflectionPrompt);
            _item = new QuoteEntity(_tmpId,_tmpContent,_tmpAuthor,_tmpSource,_tmpCategory,_tmpTags,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt,_tmpReflectionPrompt);
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
  public Flow<List<QuoteEntity>> getQuotesByAuthor(final String author) {
    final String _sql = "SELECT * FROM quotes WHERE author = ? ORDER BY id ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, author);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"quotes"}, new Callable<List<QuoteEntity>>() {
      @Override
      @NonNull
      public List<QuoteEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfAuthor = CursorUtil.getColumnIndexOrThrow(_cursor, "author");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final int _cursorIndexOfReflectionPrompt = CursorUtil.getColumnIndexOrThrow(_cursor, "reflectionPrompt");
          final List<QuoteEntity> _result = new ArrayList<QuoteEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final QuoteEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpAuthor;
            _tmpAuthor = _cursor.getString(_cursorIndexOfAuthor);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
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
            final String _tmpReflectionPrompt;
            _tmpReflectionPrompt = _cursor.getString(_cursorIndexOfReflectionPrompt);
            _item = new QuoteEntity(_tmpId,_tmpContent,_tmpAuthor,_tmpSource,_tmpCategory,_tmpTags,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt,_tmpReflectionPrompt);
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
  public Flow<List<QuoteEntity>> searchQuotes(final String query) {
    final String _sql = "SELECT * FROM quotes WHERE content LIKE '%' || ? || '%' OR author LIKE '%' || ? || '%'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"quotes"}, new Callable<List<QuoteEntity>>() {
      @Override
      @NonNull
      public List<QuoteEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfAuthor = CursorUtil.getColumnIndexOrThrow(_cursor, "author");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final int _cursorIndexOfShownAsDaily = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAsDaily");
          final int _cursorIndexOfShownAt = CursorUtil.getColumnIndexOrThrow(_cursor, "shownAt");
          final int _cursorIndexOfReflectionPrompt = CursorUtil.getColumnIndexOrThrow(_cursor, "reflectionPrompt");
          final List<QuoteEntity> _result = new ArrayList<QuoteEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final QuoteEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpAuthor;
            _tmpAuthor = _cursor.getString(_cursorIndexOfAuthor);
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
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
            final String _tmpReflectionPrompt;
            _tmpReflectionPrompt = _cursor.getString(_cursorIndexOfReflectionPrompt);
            _item = new QuoteEntity(_tmpId,_tmpContent,_tmpAuthor,_tmpSource,_tmpCategory,_tmpTags,_tmpIsFavorite,_tmpShownAsDaily,_tmpShownAt,_tmpReflectionPrompt);
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
  public Flow<List<String>> getAllAuthors() {
    final String _sql = "SELECT DISTINCT author FROM quotes ORDER BY author ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"quotes"}, new Callable<List<String>>() {
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
  public Flow<List<String>> getAllCategories() {
    final String _sql = "SELECT DISTINCT category FROM quotes";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"quotes"}, new Callable<List<String>>() {
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
