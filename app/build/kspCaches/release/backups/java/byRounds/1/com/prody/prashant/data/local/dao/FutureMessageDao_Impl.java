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
import com.prody.prashant.data.local.entity.FutureMessageEntity;
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
public final class FutureMessageDao_Impl implements FutureMessageDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<FutureMessageEntity> __insertionAdapterOfFutureMessageEntity;

  private final EntityDeletionOrUpdateAdapter<FutureMessageEntity> __deletionAdapterOfFutureMessageEntity;

  private final EntityDeletionOrUpdateAdapter<FutureMessageEntity> __updateAdapterOfFutureMessageEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteMessageById;

  private final SharedSQLiteStatement __preparedStmtOfMarkAsDelivered;

  private final SharedSQLiteStatement __preparedStmtOfMarkAsRead;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllMessages;

  public FutureMessageDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfFutureMessageEntity = new EntityInsertionAdapter<FutureMessageEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `future_messages` (`id`,`title`,`content`,`deliveryDate`,`isDelivered`,`isRead`,`category`,`attachedGoal`,`createdAt`,`deliveredAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FutureMessageEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getContent());
        statement.bindLong(4, entity.getDeliveryDate());
        final int _tmp = entity.isDelivered() ? 1 : 0;
        statement.bindLong(5, _tmp);
        final int _tmp_1 = entity.isRead() ? 1 : 0;
        statement.bindLong(6, _tmp_1);
        statement.bindString(7, entity.getCategory());
        if (entity.getAttachedGoal() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getAttachedGoal());
        }
        statement.bindLong(9, entity.getCreatedAt());
        if (entity.getDeliveredAt() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getDeliveredAt());
        }
      }
    };
    this.__deletionAdapterOfFutureMessageEntity = new EntityDeletionOrUpdateAdapter<FutureMessageEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `future_messages` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FutureMessageEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfFutureMessageEntity = new EntityDeletionOrUpdateAdapter<FutureMessageEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `future_messages` SET `id` = ?,`title` = ?,`content` = ?,`deliveryDate` = ?,`isDelivered` = ?,`isRead` = ?,`category` = ?,`attachedGoal` = ?,`createdAt` = ?,`deliveredAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FutureMessageEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getContent());
        statement.bindLong(4, entity.getDeliveryDate());
        final int _tmp = entity.isDelivered() ? 1 : 0;
        statement.bindLong(5, _tmp);
        final int _tmp_1 = entity.isRead() ? 1 : 0;
        statement.bindLong(6, _tmp_1);
        statement.bindString(7, entity.getCategory());
        if (entity.getAttachedGoal() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getAttachedGoal());
        }
        statement.bindLong(9, entity.getCreatedAt());
        if (entity.getDeliveredAt() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getDeliveredAt());
        }
        statement.bindLong(11, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteMessageById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM future_messages WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkAsDelivered = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE future_messages SET isDelivered = 1, deliveredAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkAsRead = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE future_messages SET isRead = 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllMessages = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM future_messages";
        return _query;
      }
    };
  }

  @Override
  public Object insertMessage(final FutureMessageEntity message,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfFutureMessageEntity.insertAndReturnId(message);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertMessages(final List<FutureMessageEntity> messages,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfFutureMessageEntity.insert(messages);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteMessage(final FutureMessageEntity message,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfFutureMessageEntity.handle(message);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateMessage(final FutureMessageEntity message,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfFutureMessageEntity.handle(message);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteMessageById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteMessageById.acquire();
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
          __preparedStmtOfDeleteMessageById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markAsDelivered(final long id, final long deliveredAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkAsDelivered.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, deliveredAt);
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
          __preparedStmtOfMarkAsDelivered.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markAsRead(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkAsRead.acquire();
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
          __preparedStmtOfMarkAsRead.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllMessages(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllMessages.acquire();
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
          __preparedStmtOfDeleteAllMessages.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<FutureMessageEntity>> getAllMessages() {
    final String _sql = "SELECT * FROM future_messages ORDER BY deliveryDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"future_messages"}, new Callable<List<FutureMessageEntity>>() {
      @Override
      @NonNull
      public List<FutureMessageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfDeliveryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveryDate");
          final int _cursorIndexOfIsDelivered = CursorUtil.getColumnIndexOrThrow(_cursor, "isDelivered");
          final int _cursorIndexOfIsRead = CursorUtil.getColumnIndexOrThrow(_cursor, "isRead");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfAttachedGoal = CursorUtil.getColumnIndexOrThrow(_cursor, "attachedGoal");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfDeliveredAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveredAt");
          final List<FutureMessageEntity> _result = new ArrayList<FutureMessageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FutureMessageEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final long _tmpDeliveryDate;
            _tmpDeliveryDate = _cursor.getLong(_cursorIndexOfDeliveryDate);
            final boolean _tmpIsDelivered;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDelivered);
            _tmpIsDelivered = _tmp != 0;
            final boolean _tmpIsRead;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsRead);
            _tmpIsRead = _tmp_1 != 0;
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpAttachedGoal;
            if (_cursor.isNull(_cursorIndexOfAttachedGoal)) {
              _tmpAttachedGoal = null;
            } else {
              _tmpAttachedGoal = _cursor.getString(_cursorIndexOfAttachedGoal);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpDeliveredAt;
            if (_cursor.isNull(_cursorIndexOfDeliveredAt)) {
              _tmpDeliveredAt = null;
            } else {
              _tmpDeliveredAt = _cursor.getLong(_cursorIndexOfDeliveredAt);
            }
            _item = new FutureMessageEntity(_tmpId,_tmpTitle,_tmpContent,_tmpDeliveryDate,_tmpIsDelivered,_tmpIsRead,_tmpCategory,_tmpAttachedGoal,_tmpCreatedAt,_tmpDeliveredAt);
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
  public Object getMessageById(final long id,
      final Continuation<? super FutureMessageEntity> $completion) {
    final String _sql = "SELECT * FROM future_messages WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<FutureMessageEntity>() {
      @Override
      @Nullable
      public FutureMessageEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfDeliveryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveryDate");
          final int _cursorIndexOfIsDelivered = CursorUtil.getColumnIndexOrThrow(_cursor, "isDelivered");
          final int _cursorIndexOfIsRead = CursorUtil.getColumnIndexOrThrow(_cursor, "isRead");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfAttachedGoal = CursorUtil.getColumnIndexOrThrow(_cursor, "attachedGoal");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfDeliveredAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveredAt");
          final FutureMessageEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final long _tmpDeliveryDate;
            _tmpDeliveryDate = _cursor.getLong(_cursorIndexOfDeliveryDate);
            final boolean _tmpIsDelivered;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDelivered);
            _tmpIsDelivered = _tmp != 0;
            final boolean _tmpIsRead;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsRead);
            _tmpIsRead = _tmp_1 != 0;
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpAttachedGoal;
            if (_cursor.isNull(_cursorIndexOfAttachedGoal)) {
              _tmpAttachedGoal = null;
            } else {
              _tmpAttachedGoal = _cursor.getString(_cursorIndexOfAttachedGoal);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpDeliveredAt;
            if (_cursor.isNull(_cursorIndexOfDeliveredAt)) {
              _tmpDeliveredAt = null;
            } else {
              _tmpDeliveredAt = _cursor.getLong(_cursorIndexOfDeliveredAt);
            }
            _result = new FutureMessageEntity(_tmpId,_tmpTitle,_tmpContent,_tmpDeliveryDate,_tmpIsDelivered,_tmpIsRead,_tmpCategory,_tmpAttachedGoal,_tmpCreatedAt,_tmpDeliveredAt);
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
  public Flow<List<FutureMessageEntity>> getPendingMessages() {
    final String _sql = "SELECT * FROM future_messages WHERE isDelivered = 0 ORDER BY deliveryDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"future_messages"}, new Callable<List<FutureMessageEntity>>() {
      @Override
      @NonNull
      public List<FutureMessageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfDeliveryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveryDate");
          final int _cursorIndexOfIsDelivered = CursorUtil.getColumnIndexOrThrow(_cursor, "isDelivered");
          final int _cursorIndexOfIsRead = CursorUtil.getColumnIndexOrThrow(_cursor, "isRead");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfAttachedGoal = CursorUtil.getColumnIndexOrThrow(_cursor, "attachedGoal");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfDeliveredAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveredAt");
          final List<FutureMessageEntity> _result = new ArrayList<FutureMessageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FutureMessageEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final long _tmpDeliveryDate;
            _tmpDeliveryDate = _cursor.getLong(_cursorIndexOfDeliveryDate);
            final boolean _tmpIsDelivered;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDelivered);
            _tmpIsDelivered = _tmp != 0;
            final boolean _tmpIsRead;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsRead);
            _tmpIsRead = _tmp_1 != 0;
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpAttachedGoal;
            if (_cursor.isNull(_cursorIndexOfAttachedGoal)) {
              _tmpAttachedGoal = null;
            } else {
              _tmpAttachedGoal = _cursor.getString(_cursorIndexOfAttachedGoal);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpDeliveredAt;
            if (_cursor.isNull(_cursorIndexOfDeliveredAt)) {
              _tmpDeliveredAt = null;
            } else {
              _tmpDeliveredAt = _cursor.getLong(_cursorIndexOfDeliveredAt);
            }
            _item = new FutureMessageEntity(_tmpId,_tmpTitle,_tmpContent,_tmpDeliveryDate,_tmpIsDelivered,_tmpIsRead,_tmpCategory,_tmpAttachedGoal,_tmpCreatedAt,_tmpDeliveredAt);
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
  public Flow<List<FutureMessageEntity>> getDeliveredMessages() {
    final String _sql = "SELECT * FROM future_messages WHERE isDelivered = 1 ORDER BY deliveredAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"future_messages"}, new Callable<List<FutureMessageEntity>>() {
      @Override
      @NonNull
      public List<FutureMessageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfDeliveryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveryDate");
          final int _cursorIndexOfIsDelivered = CursorUtil.getColumnIndexOrThrow(_cursor, "isDelivered");
          final int _cursorIndexOfIsRead = CursorUtil.getColumnIndexOrThrow(_cursor, "isRead");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfAttachedGoal = CursorUtil.getColumnIndexOrThrow(_cursor, "attachedGoal");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfDeliveredAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveredAt");
          final List<FutureMessageEntity> _result = new ArrayList<FutureMessageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FutureMessageEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final long _tmpDeliveryDate;
            _tmpDeliveryDate = _cursor.getLong(_cursorIndexOfDeliveryDate);
            final boolean _tmpIsDelivered;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDelivered);
            _tmpIsDelivered = _tmp != 0;
            final boolean _tmpIsRead;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsRead);
            _tmpIsRead = _tmp_1 != 0;
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpAttachedGoal;
            if (_cursor.isNull(_cursorIndexOfAttachedGoal)) {
              _tmpAttachedGoal = null;
            } else {
              _tmpAttachedGoal = _cursor.getString(_cursorIndexOfAttachedGoal);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpDeliveredAt;
            if (_cursor.isNull(_cursorIndexOfDeliveredAt)) {
              _tmpDeliveredAt = null;
            } else {
              _tmpDeliveredAt = _cursor.getLong(_cursorIndexOfDeliveredAt);
            }
            _item = new FutureMessageEntity(_tmpId,_tmpTitle,_tmpContent,_tmpDeliveryDate,_tmpIsDelivered,_tmpIsRead,_tmpCategory,_tmpAttachedGoal,_tmpCreatedAt,_tmpDeliveredAt);
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
  public Flow<List<FutureMessageEntity>> getUnreadDeliveredMessages() {
    final String _sql = "SELECT * FROM future_messages WHERE isDelivered = 1 AND isRead = 0 ORDER BY deliveredAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"future_messages"}, new Callable<List<FutureMessageEntity>>() {
      @Override
      @NonNull
      public List<FutureMessageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfDeliveryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveryDate");
          final int _cursorIndexOfIsDelivered = CursorUtil.getColumnIndexOrThrow(_cursor, "isDelivered");
          final int _cursorIndexOfIsRead = CursorUtil.getColumnIndexOrThrow(_cursor, "isRead");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfAttachedGoal = CursorUtil.getColumnIndexOrThrow(_cursor, "attachedGoal");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfDeliveredAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveredAt");
          final List<FutureMessageEntity> _result = new ArrayList<FutureMessageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FutureMessageEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final long _tmpDeliveryDate;
            _tmpDeliveryDate = _cursor.getLong(_cursorIndexOfDeliveryDate);
            final boolean _tmpIsDelivered;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDelivered);
            _tmpIsDelivered = _tmp != 0;
            final boolean _tmpIsRead;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsRead);
            _tmpIsRead = _tmp_1 != 0;
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpAttachedGoal;
            if (_cursor.isNull(_cursorIndexOfAttachedGoal)) {
              _tmpAttachedGoal = null;
            } else {
              _tmpAttachedGoal = _cursor.getString(_cursorIndexOfAttachedGoal);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpDeliveredAt;
            if (_cursor.isNull(_cursorIndexOfDeliveredAt)) {
              _tmpDeliveredAt = null;
            } else {
              _tmpDeliveredAt = _cursor.getLong(_cursorIndexOfDeliveredAt);
            }
            _item = new FutureMessageEntity(_tmpId,_tmpTitle,_tmpContent,_tmpDeliveryDate,_tmpIsDelivered,_tmpIsRead,_tmpCategory,_tmpAttachedGoal,_tmpCreatedAt,_tmpDeliveredAt);
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
  public Object getMessagesReadyForDelivery(final long currentTime,
      final Continuation<? super List<FutureMessageEntity>> $completion) {
    final String _sql = "SELECT * FROM future_messages WHERE deliveryDate <= ? AND isDelivered = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, currentTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<FutureMessageEntity>>() {
      @Override
      @NonNull
      public List<FutureMessageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfDeliveryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveryDate");
          final int _cursorIndexOfIsDelivered = CursorUtil.getColumnIndexOrThrow(_cursor, "isDelivered");
          final int _cursorIndexOfIsRead = CursorUtil.getColumnIndexOrThrow(_cursor, "isRead");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfAttachedGoal = CursorUtil.getColumnIndexOrThrow(_cursor, "attachedGoal");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfDeliveredAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveredAt");
          final List<FutureMessageEntity> _result = new ArrayList<FutureMessageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FutureMessageEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final long _tmpDeliveryDate;
            _tmpDeliveryDate = _cursor.getLong(_cursorIndexOfDeliveryDate);
            final boolean _tmpIsDelivered;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDelivered);
            _tmpIsDelivered = _tmp != 0;
            final boolean _tmpIsRead;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsRead);
            _tmpIsRead = _tmp_1 != 0;
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpAttachedGoal;
            if (_cursor.isNull(_cursorIndexOfAttachedGoal)) {
              _tmpAttachedGoal = null;
            } else {
              _tmpAttachedGoal = _cursor.getString(_cursorIndexOfAttachedGoal);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpDeliveredAt;
            if (_cursor.isNull(_cursorIndexOfDeliveredAt)) {
              _tmpDeliveredAt = null;
            } else {
              _tmpDeliveredAt = _cursor.getLong(_cursorIndexOfDeliveredAt);
            }
            _item = new FutureMessageEntity(_tmpId,_tmpTitle,_tmpContent,_tmpDeliveryDate,_tmpIsDelivered,_tmpIsRead,_tmpCategory,_tmpAttachedGoal,_tmpCreatedAt,_tmpDeliveredAt);
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
  public Flow<List<FutureMessageEntity>> getMessagesByCategory(final String category) {
    final String _sql = "SELECT * FROM future_messages WHERE category = ? ORDER BY deliveryDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, category);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"future_messages"}, new Callable<List<FutureMessageEntity>>() {
      @Override
      @NonNull
      public List<FutureMessageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfDeliveryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveryDate");
          final int _cursorIndexOfIsDelivered = CursorUtil.getColumnIndexOrThrow(_cursor, "isDelivered");
          final int _cursorIndexOfIsRead = CursorUtil.getColumnIndexOrThrow(_cursor, "isRead");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfAttachedGoal = CursorUtil.getColumnIndexOrThrow(_cursor, "attachedGoal");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfDeliveredAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveredAt");
          final List<FutureMessageEntity> _result = new ArrayList<FutureMessageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FutureMessageEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final long _tmpDeliveryDate;
            _tmpDeliveryDate = _cursor.getLong(_cursorIndexOfDeliveryDate);
            final boolean _tmpIsDelivered;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDelivered);
            _tmpIsDelivered = _tmp != 0;
            final boolean _tmpIsRead;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsRead);
            _tmpIsRead = _tmp_1 != 0;
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpAttachedGoal;
            if (_cursor.isNull(_cursorIndexOfAttachedGoal)) {
              _tmpAttachedGoal = null;
            } else {
              _tmpAttachedGoal = _cursor.getString(_cursorIndexOfAttachedGoal);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpDeliveredAt;
            if (_cursor.isNull(_cursorIndexOfDeliveredAt)) {
              _tmpDeliveredAt = null;
            } else {
              _tmpDeliveredAt = _cursor.getLong(_cursorIndexOfDeliveredAt);
            }
            _item = new FutureMessageEntity(_tmpId,_tmpTitle,_tmpContent,_tmpDeliveryDate,_tmpIsDelivered,_tmpIsRead,_tmpCategory,_tmpAttachedGoal,_tmpCreatedAt,_tmpDeliveredAt);
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
  public Flow<Integer> getPendingCount() {
    final String _sql = "SELECT COUNT(*) FROM future_messages WHERE isDelivered = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"future_messages"}, new Callable<Integer>() {
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
  public Flow<Integer> getUnreadCount() {
    final String _sql = "SELECT COUNT(*) FROM future_messages WHERE isDelivered = 1 AND isRead = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"future_messages"}, new Callable<Integer>() {
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
    final String _sql = "SELECT COUNT(*) FROM future_messages";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"future_messages"}, new Callable<Integer>() {
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
  public Flow<List<FutureMessageEntity>> getMessagesByDateRange(final long startDate,
      final long endDate) {
    final String _sql = "SELECT * FROM future_messages WHERE deliveryDate BETWEEN ? AND ? ORDER BY deliveryDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endDate);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"future_messages"}, new Callable<List<FutureMessageEntity>>() {
      @Override
      @NonNull
      public List<FutureMessageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfDeliveryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveryDate");
          final int _cursorIndexOfIsDelivered = CursorUtil.getColumnIndexOrThrow(_cursor, "isDelivered");
          final int _cursorIndexOfIsRead = CursorUtil.getColumnIndexOrThrow(_cursor, "isRead");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfAttachedGoal = CursorUtil.getColumnIndexOrThrow(_cursor, "attachedGoal");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfDeliveredAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveredAt");
          final List<FutureMessageEntity> _result = new ArrayList<FutureMessageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FutureMessageEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final long _tmpDeliveryDate;
            _tmpDeliveryDate = _cursor.getLong(_cursorIndexOfDeliveryDate);
            final boolean _tmpIsDelivered;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDelivered);
            _tmpIsDelivered = _tmp != 0;
            final boolean _tmpIsRead;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsRead);
            _tmpIsRead = _tmp_1 != 0;
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpAttachedGoal;
            if (_cursor.isNull(_cursorIndexOfAttachedGoal)) {
              _tmpAttachedGoal = null;
            } else {
              _tmpAttachedGoal = _cursor.getString(_cursorIndexOfAttachedGoal);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpDeliveredAt;
            if (_cursor.isNull(_cursorIndexOfDeliveredAt)) {
              _tmpDeliveredAt = null;
            } else {
              _tmpDeliveredAt = _cursor.getLong(_cursorIndexOfDeliveredAt);
            }
            _item = new FutureMessageEntity(_tmpId,_tmpTitle,_tmpContent,_tmpDeliveryDate,_tmpIsDelivered,_tmpIsRead,_tmpCategory,_tmpAttachedGoal,_tmpCreatedAt,_tmpDeliveredAt);
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
  public Object getNextDeliveryTime(final Continuation<? super Long> $completion) {
    final String _sql = "SELECT MIN(deliveryDate) FROM future_messages WHERE isDelivered = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Long>() {
      @Override
      @Nullable
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final Long _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(0);
            }
            _result = _tmp;
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
  public Object getAllMessagesSync(
      final Continuation<? super List<FutureMessageEntity>> $completion) {
    final String _sql = "SELECT * FROM future_messages ORDER BY deliveryDate ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<FutureMessageEntity>>() {
      @Override
      @NonNull
      public List<FutureMessageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfDeliveryDate = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveryDate");
          final int _cursorIndexOfIsDelivered = CursorUtil.getColumnIndexOrThrow(_cursor, "isDelivered");
          final int _cursorIndexOfIsRead = CursorUtil.getColumnIndexOrThrow(_cursor, "isRead");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfAttachedGoal = CursorUtil.getColumnIndexOrThrow(_cursor, "attachedGoal");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfDeliveredAt = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveredAt");
          final List<FutureMessageEntity> _result = new ArrayList<FutureMessageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FutureMessageEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final long _tmpDeliveryDate;
            _tmpDeliveryDate = _cursor.getLong(_cursorIndexOfDeliveryDate);
            final boolean _tmpIsDelivered;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDelivered);
            _tmpIsDelivered = _tmp != 0;
            final boolean _tmpIsRead;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsRead);
            _tmpIsRead = _tmp_1 != 0;
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            final String _tmpAttachedGoal;
            if (_cursor.isNull(_cursorIndexOfAttachedGoal)) {
              _tmpAttachedGoal = null;
            } else {
              _tmpAttachedGoal = _cursor.getString(_cursorIndexOfAttachedGoal);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpDeliveredAt;
            if (_cursor.isNull(_cursorIndexOfDeliveredAt)) {
              _tmpDeliveredAt = null;
            } else {
              _tmpDeliveredAt = _cursor.getLong(_cursorIndexOfDeliveredAt);
            }
            _item = new FutureMessageEntity(_tmpId,_tmpTitle,_tmpContent,_tmpDeliveryDate,_tmpIsDelivered,_tmpIsRead,_tmpCategory,_tmpAttachedGoal,_tmpCreatedAt,_tmpDeliveredAt);
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
