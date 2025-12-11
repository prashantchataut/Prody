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
import com.prody.prashant.data.local.entity.JournalEntryEntity;
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
public final class JournalDao_Impl implements JournalDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<JournalEntryEntity> __insertionAdapterOfJournalEntryEntity;

  private final EntityDeletionOrUpdateAdapter<JournalEntryEntity> __deletionAdapterOfJournalEntryEntity;

  private final EntityDeletionOrUpdateAdapter<JournalEntryEntity> __updateAdapterOfJournalEntryEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteEntryById;

  private final SharedSQLiteStatement __preparedStmtOfUpdateBookmarkStatus;

  private final SharedSQLiteStatement __preparedStmtOfUpdateBuddhaResponse;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllEntries;

  public JournalDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfJournalEntryEntity = new EntityInsertionAdapter<JournalEntryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `journal_entries` (`id`,`content`,`mood`,`moodIntensity`,`buddhaResponse`,`tags`,`isBookmarked`,`wordCount`,`createdAt`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final JournalEntryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getContent());
        statement.bindString(3, entity.getMood());
        statement.bindLong(4, entity.getMoodIntensity());
        if (entity.getBuddhaResponse() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getBuddhaResponse());
        }
        statement.bindString(6, entity.getTags());
        final int _tmp = entity.isBookmarked() ? 1 : 0;
        statement.bindLong(7, _tmp);
        statement.bindLong(8, entity.getWordCount());
        statement.bindLong(9, entity.getCreatedAt());
        statement.bindLong(10, entity.getUpdatedAt());
      }
    };
    this.__deletionAdapterOfJournalEntryEntity = new EntityDeletionOrUpdateAdapter<JournalEntryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `journal_entries` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final JournalEntryEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfJournalEntryEntity = new EntityDeletionOrUpdateAdapter<JournalEntryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `journal_entries` SET `id` = ?,`content` = ?,`mood` = ?,`moodIntensity` = ?,`buddhaResponse` = ?,`tags` = ?,`isBookmarked` = ?,`wordCount` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final JournalEntryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getContent());
        statement.bindString(3, entity.getMood());
        statement.bindLong(4, entity.getMoodIntensity());
        if (entity.getBuddhaResponse() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getBuddhaResponse());
        }
        statement.bindString(6, entity.getTags());
        final int _tmp = entity.isBookmarked() ? 1 : 0;
        statement.bindLong(7, _tmp);
        statement.bindLong(8, entity.getWordCount());
        statement.bindLong(9, entity.getCreatedAt());
        statement.bindLong(10, entity.getUpdatedAt());
        statement.bindLong(11, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteEntryById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM journal_entries WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateBookmarkStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE journal_entries SET isBookmarked = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateBuddhaResponse = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE journal_entries SET buddhaResponse = ?, updatedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllEntries = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM journal_entries";
        return _query;
      }
    };
  }

  @Override
  public Object insertEntry(final JournalEntryEntity entry,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfJournalEntryEntity.insertAndReturnId(entry);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertEntries(final List<JournalEntryEntity> entries,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfJournalEntryEntity.insert(entries);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteEntry(final JournalEntryEntity entry,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfJournalEntryEntity.handle(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateEntry(final JournalEntryEntity entry,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfJournalEntryEntity.handle(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteEntryById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteEntryById.acquire();
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
          __preparedStmtOfDeleteEntryById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateBookmarkStatus(final long id, final boolean isBookmarked,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateBookmarkStatus.acquire();
        int _argIndex = 1;
        final int _tmp = isBookmarked ? 1 : 0;
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
          __preparedStmtOfUpdateBookmarkStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateBuddhaResponse(final long id, final String response, final long updatedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateBuddhaResponse.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, response);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, updatedAt);
        _argIndex = 3;
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
          __preparedStmtOfUpdateBuddhaResponse.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllEntries(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllEntries.acquire();
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
          __preparedStmtOfDeleteAllEntries.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<JournalEntryEntity>> getAllEntries() {
    final String _sql = "SELECT * FROM journal_entries ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"journal_entries"}, new Callable<List<JournalEntryEntity>>() {
      @Override
      @NonNull
      public List<JournalEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfMoodIntensity = CursorUtil.getColumnIndexOrThrow(_cursor, "moodIntensity");
          final int _cursorIndexOfBuddhaResponse = CursorUtil.getColumnIndexOrThrow(_cursor, "buddhaResponse");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfIsBookmarked = CursorUtil.getColumnIndexOrThrow(_cursor, "isBookmarked");
          final int _cursorIndexOfWordCount = CursorUtil.getColumnIndexOrThrow(_cursor, "wordCount");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<JournalEntryEntity> _result = new ArrayList<JournalEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final JournalEntryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpMood;
            _tmpMood = _cursor.getString(_cursorIndexOfMood);
            final int _tmpMoodIntensity;
            _tmpMoodIntensity = _cursor.getInt(_cursorIndexOfMoodIntensity);
            final String _tmpBuddhaResponse;
            if (_cursor.isNull(_cursorIndexOfBuddhaResponse)) {
              _tmpBuddhaResponse = null;
            } else {
              _tmpBuddhaResponse = _cursor.getString(_cursorIndexOfBuddhaResponse);
            }
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
            final boolean _tmpIsBookmarked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsBookmarked);
            _tmpIsBookmarked = _tmp != 0;
            final int _tmpWordCount;
            _tmpWordCount = _cursor.getInt(_cursorIndexOfWordCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new JournalEntryEntity(_tmpId,_tmpContent,_tmpMood,_tmpMoodIntensity,_tmpBuddhaResponse,_tmpTags,_tmpIsBookmarked,_tmpWordCount,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getEntryById(final long id,
      final Continuation<? super JournalEntryEntity> $completion) {
    final String _sql = "SELECT * FROM journal_entries WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<JournalEntryEntity>() {
      @Override
      @Nullable
      public JournalEntryEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfMoodIntensity = CursorUtil.getColumnIndexOrThrow(_cursor, "moodIntensity");
          final int _cursorIndexOfBuddhaResponse = CursorUtil.getColumnIndexOrThrow(_cursor, "buddhaResponse");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfIsBookmarked = CursorUtil.getColumnIndexOrThrow(_cursor, "isBookmarked");
          final int _cursorIndexOfWordCount = CursorUtil.getColumnIndexOrThrow(_cursor, "wordCount");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final JournalEntryEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpMood;
            _tmpMood = _cursor.getString(_cursorIndexOfMood);
            final int _tmpMoodIntensity;
            _tmpMoodIntensity = _cursor.getInt(_cursorIndexOfMoodIntensity);
            final String _tmpBuddhaResponse;
            if (_cursor.isNull(_cursorIndexOfBuddhaResponse)) {
              _tmpBuddhaResponse = null;
            } else {
              _tmpBuddhaResponse = _cursor.getString(_cursorIndexOfBuddhaResponse);
            }
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
            final boolean _tmpIsBookmarked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsBookmarked);
            _tmpIsBookmarked = _tmp != 0;
            final int _tmpWordCount;
            _tmpWordCount = _cursor.getInt(_cursorIndexOfWordCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new JournalEntryEntity(_tmpId,_tmpContent,_tmpMood,_tmpMoodIntensity,_tmpBuddhaResponse,_tmpTags,_tmpIsBookmarked,_tmpWordCount,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<JournalEntryEntity> observeEntryById(final long id) {
    final String _sql = "SELECT * FROM journal_entries WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"journal_entries"}, new Callable<JournalEntryEntity>() {
      @Override
      @Nullable
      public JournalEntryEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfMoodIntensity = CursorUtil.getColumnIndexOrThrow(_cursor, "moodIntensity");
          final int _cursorIndexOfBuddhaResponse = CursorUtil.getColumnIndexOrThrow(_cursor, "buddhaResponse");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfIsBookmarked = CursorUtil.getColumnIndexOrThrow(_cursor, "isBookmarked");
          final int _cursorIndexOfWordCount = CursorUtil.getColumnIndexOrThrow(_cursor, "wordCount");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final JournalEntryEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpMood;
            _tmpMood = _cursor.getString(_cursorIndexOfMood);
            final int _tmpMoodIntensity;
            _tmpMoodIntensity = _cursor.getInt(_cursorIndexOfMoodIntensity);
            final String _tmpBuddhaResponse;
            if (_cursor.isNull(_cursorIndexOfBuddhaResponse)) {
              _tmpBuddhaResponse = null;
            } else {
              _tmpBuddhaResponse = _cursor.getString(_cursorIndexOfBuddhaResponse);
            }
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
            final boolean _tmpIsBookmarked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsBookmarked);
            _tmpIsBookmarked = _tmp != 0;
            final int _tmpWordCount;
            _tmpWordCount = _cursor.getInt(_cursorIndexOfWordCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new JournalEntryEntity(_tmpId,_tmpContent,_tmpMood,_tmpMoodIntensity,_tmpBuddhaResponse,_tmpTags,_tmpIsBookmarked,_tmpWordCount,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<JournalEntryEntity>> getBookmarkedEntries() {
    final String _sql = "SELECT * FROM journal_entries WHERE isBookmarked = 1 ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"journal_entries"}, new Callable<List<JournalEntryEntity>>() {
      @Override
      @NonNull
      public List<JournalEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfMoodIntensity = CursorUtil.getColumnIndexOrThrow(_cursor, "moodIntensity");
          final int _cursorIndexOfBuddhaResponse = CursorUtil.getColumnIndexOrThrow(_cursor, "buddhaResponse");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfIsBookmarked = CursorUtil.getColumnIndexOrThrow(_cursor, "isBookmarked");
          final int _cursorIndexOfWordCount = CursorUtil.getColumnIndexOrThrow(_cursor, "wordCount");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<JournalEntryEntity> _result = new ArrayList<JournalEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final JournalEntryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpMood;
            _tmpMood = _cursor.getString(_cursorIndexOfMood);
            final int _tmpMoodIntensity;
            _tmpMoodIntensity = _cursor.getInt(_cursorIndexOfMoodIntensity);
            final String _tmpBuddhaResponse;
            if (_cursor.isNull(_cursorIndexOfBuddhaResponse)) {
              _tmpBuddhaResponse = null;
            } else {
              _tmpBuddhaResponse = _cursor.getString(_cursorIndexOfBuddhaResponse);
            }
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
            final boolean _tmpIsBookmarked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsBookmarked);
            _tmpIsBookmarked = _tmp != 0;
            final int _tmpWordCount;
            _tmpWordCount = _cursor.getInt(_cursorIndexOfWordCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new JournalEntryEntity(_tmpId,_tmpContent,_tmpMood,_tmpMoodIntensity,_tmpBuddhaResponse,_tmpTags,_tmpIsBookmarked,_tmpWordCount,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<JournalEntryEntity>> getEntriesByMood(final String mood) {
    final String _sql = "SELECT * FROM journal_entries WHERE mood = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, mood);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"journal_entries"}, new Callable<List<JournalEntryEntity>>() {
      @Override
      @NonNull
      public List<JournalEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfMoodIntensity = CursorUtil.getColumnIndexOrThrow(_cursor, "moodIntensity");
          final int _cursorIndexOfBuddhaResponse = CursorUtil.getColumnIndexOrThrow(_cursor, "buddhaResponse");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfIsBookmarked = CursorUtil.getColumnIndexOrThrow(_cursor, "isBookmarked");
          final int _cursorIndexOfWordCount = CursorUtil.getColumnIndexOrThrow(_cursor, "wordCount");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<JournalEntryEntity> _result = new ArrayList<JournalEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final JournalEntryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpMood;
            _tmpMood = _cursor.getString(_cursorIndexOfMood);
            final int _tmpMoodIntensity;
            _tmpMoodIntensity = _cursor.getInt(_cursorIndexOfMoodIntensity);
            final String _tmpBuddhaResponse;
            if (_cursor.isNull(_cursorIndexOfBuddhaResponse)) {
              _tmpBuddhaResponse = null;
            } else {
              _tmpBuddhaResponse = _cursor.getString(_cursorIndexOfBuddhaResponse);
            }
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
            final boolean _tmpIsBookmarked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsBookmarked);
            _tmpIsBookmarked = _tmp != 0;
            final int _tmpWordCount;
            _tmpWordCount = _cursor.getInt(_cursorIndexOfWordCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new JournalEntryEntity(_tmpId,_tmpContent,_tmpMood,_tmpMoodIntensity,_tmpBuddhaResponse,_tmpTags,_tmpIsBookmarked,_tmpWordCount,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<JournalEntryEntity>> getEntriesByDateRange(final long startDate,
      final long endDate) {
    final String _sql = "SELECT * FROM journal_entries WHERE createdAt BETWEEN ? AND ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endDate);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"journal_entries"}, new Callable<List<JournalEntryEntity>>() {
      @Override
      @NonNull
      public List<JournalEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfMoodIntensity = CursorUtil.getColumnIndexOrThrow(_cursor, "moodIntensity");
          final int _cursorIndexOfBuddhaResponse = CursorUtil.getColumnIndexOrThrow(_cursor, "buddhaResponse");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfIsBookmarked = CursorUtil.getColumnIndexOrThrow(_cursor, "isBookmarked");
          final int _cursorIndexOfWordCount = CursorUtil.getColumnIndexOrThrow(_cursor, "wordCount");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<JournalEntryEntity> _result = new ArrayList<JournalEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final JournalEntryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpMood;
            _tmpMood = _cursor.getString(_cursorIndexOfMood);
            final int _tmpMoodIntensity;
            _tmpMoodIntensity = _cursor.getInt(_cursorIndexOfMoodIntensity);
            final String _tmpBuddhaResponse;
            if (_cursor.isNull(_cursorIndexOfBuddhaResponse)) {
              _tmpBuddhaResponse = null;
            } else {
              _tmpBuddhaResponse = _cursor.getString(_cursorIndexOfBuddhaResponse);
            }
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
            final boolean _tmpIsBookmarked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsBookmarked);
            _tmpIsBookmarked = _tmp != 0;
            final int _tmpWordCount;
            _tmpWordCount = _cursor.getInt(_cursorIndexOfWordCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new JournalEntryEntity(_tmpId,_tmpContent,_tmpMood,_tmpMoodIntensity,_tmpBuddhaResponse,_tmpTags,_tmpIsBookmarked,_tmpWordCount,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<JournalEntryEntity>> searchEntries(final String query) {
    final String _sql = "SELECT * FROM journal_entries WHERE content LIKE '%' || ? || '%' OR tags LIKE '%' || ? || '%' ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"journal_entries"}, new Callable<List<JournalEntryEntity>>() {
      @Override
      @NonNull
      public List<JournalEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfMoodIntensity = CursorUtil.getColumnIndexOrThrow(_cursor, "moodIntensity");
          final int _cursorIndexOfBuddhaResponse = CursorUtil.getColumnIndexOrThrow(_cursor, "buddhaResponse");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfIsBookmarked = CursorUtil.getColumnIndexOrThrow(_cursor, "isBookmarked");
          final int _cursorIndexOfWordCount = CursorUtil.getColumnIndexOrThrow(_cursor, "wordCount");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<JournalEntryEntity> _result = new ArrayList<JournalEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final JournalEntryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpMood;
            _tmpMood = _cursor.getString(_cursorIndexOfMood);
            final int _tmpMoodIntensity;
            _tmpMoodIntensity = _cursor.getInt(_cursorIndexOfMoodIntensity);
            final String _tmpBuddhaResponse;
            if (_cursor.isNull(_cursorIndexOfBuddhaResponse)) {
              _tmpBuddhaResponse = null;
            } else {
              _tmpBuddhaResponse = _cursor.getString(_cursorIndexOfBuddhaResponse);
            }
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
            final boolean _tmpIsBookmarked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsBookmarked);
            _tmpIsBookmarked = _tmp != 0;
            final int _tmpWordCount;
            _tmpWordCount = _cursor.getInt(_cursorIndexOfWordCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new JournalEntryEntity(_tmpId,_tmpContent,_tmpMood,_tmpMoodIntensity,_tmpBuddhaResponse,_tmpTags,_tmpIsBookmarked,_tmpWordCount,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<Integer> getEntryCount() {
    final String _sql = "SELECT COUNT(*) FROM journal_entries";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"journal_entries"}, new Callable<Integer>() {
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
  public Object getTodayEntryCount(final long startOfDay,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM journal_entries WHERE createdAt >= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startOfDay);
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
  public Flow<Integer> getTotalWordCount() {
    final String _sql = "SELECT SUM(wordCount) FROM journal_entries";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"journal_entries"}, new Callable<Integer>() {
      @Override
      @Nullable
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
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
  public Flow<List<MoodCount>> getMoodDistribution() {
    final String _sql = "SELECT mood, COUNT(*) as count FROM journal_entries GROUP BY mood ORDER BY count DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"journal_entries"}, new Callable<List<MoodCount>>() {
      @Override
      @NonNull
      public List<MoodCount> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMood = 0;
          final int _cursorIndexOfCount = 1;
          final List<MoodCount> _result = new ArrayList<MoodCount>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MoodCount _item;
            final String _tmpMood;
            _tmpMood = _cursor.getString(_cursorIndexOfMood);
            final int _tmpCount;
            _tmpCount = _cursor.getInt(_cursorIndexOfCount);
            _item = new MoodCount(_tmpMood,_tmpCount);
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
  public Flow<List<JournalEntryEntity>> getRecentEntries(final int limit) {
    final String _sql = "SELECT * FROM journal_entries ORDER BY createdAt DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"journal_entries"}, new Callable<List<JournalEntryEntity>>() {
      @Override
      @NonNull
      public List<JournalEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfMoodIntensity = CursorUtil.getColumnIndexOrThrow(_cursor, "moodIntensity");
          final int _cursorIndexOfBuddhaResponse = CursorUtil.getColumnIndexOrThrow(_cursor, "buddhaResponse");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfIsBookmarked = CursorUtil.getColumnIndexOrThrow(_cursor, "isBookmarked");
          final int _cursorIndexOfWordCount = CursorUtil.getColumnIndexOrThrow(_cursor, "wordCount");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<JournalEntryEntity> _result = new ArrayList<JournalEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final JournalEntryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpMood;
            _tmpMood = _cursor.getString(_cursorIndexOfMood);
            final int _tmpMoodIntensity;
            _tmpMoodIntensity = _cursor.getInt(_cursorIndexOfMoodIntensity);
            final String _tmpBuddhaResponse;
            if (_cursor.isNull(_cursorIndexOfBuddhaResponse)) {
              _tmpBuddhaResponse = null;
            } else {
              _tmpBuddhaResponse = _cursor.getString(_cursorIndexOfBuddhaResponse);
            }
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
            final boolean _tmpIsBookmarked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsBookmarked);
            _tmpIsBookmarked = _tmp != 0;
            final int _tmpWordCount;
            _tmpWordCount = _cursor.getInt(_cursorIndexOfWordCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new JournalEntryEntity(_tmpId,_tmpContent,_tmpMood,_tmpMoodIntensity,_tmpBuddhaResponse,_tmpTags,_tmpIsBookmarked,_tmpWordCount,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<String>> getAllMoods() {
    final String _sql = "SELECT DISTINCT mood FROM journal_entries";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"journal_entries"}, new Callable<List<String>>() {
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
  public Object getAllEntriesSync(
      final Continuation<? super List<JournalEntryEntity>> $completion) {
    final String _sql = "SELECT * FROM journal_entries ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<JournalEntryEntity>>() {
      @Override
      @NonNull
      public List<JournalEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfMood = CursorUtil.getColumnIndexOrThrow(_cursor, "mood");
          final int _cursorIndexOfMoodIntensity = CursorUtil.getColumnIndexOrThrow(_cursor, "moodIntensity");
          final int _cursorIndexOfBuddhaResponse = CursorUtil.getColumnIndexOrThrow(_cursor, "buddhaResponse");
          final int _cursorIndexOfTags = CursorUtil.getColumnIndexOrThrow(_cursor, "tags");
          final int _cursorIndexOfIsBookmarked = CursorUtil.getColumnIndexOrThrow(_cursor, "isBookmarked");
          final int _cursorIndexOfWordCount = CursorUtil.getColumnIndexOrThrow(_cursor, "wordCount");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<JournalEntryEntity> _result = new ArrayList<JournalEntryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final JournalEntryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final String _tmpMood;
            _tmpMood = _cursor.getString(_cursorIndexOfMood);
            final int _tmpMoodIntensity;
            _tmpMoodIntensity = _cursor.getInt(_cursorIndexOfMoodIntensity);
            final String _tmpBuddhaResponse;
            if (_cursor.isNull(_cursorIndexOfBuddhaResponse)) {
              _tmpBuddhaResponse = null;
            } else {
              _tmpBuddhaResponse = _cursor.getString(_cursorIndexOfBuddhaResponse);
            }
            final String _tmpTags;
            _tmpTags = _cursor.getString(_cursorIndexOfTags);
            final boolean _tmpIsBookmarked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsBookmarked);
            _tmpIsBookmarked = _tmp != 0;
            final int _tmpWordCount;
            _tmpWordCount = _cursor.getInt(_cursorIndexOfWordCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new JournalEntryEntity(_tmpId,_tmpContent,_tmpMood,_tmpMoodIntensity,_tmpBuddhaResponse,_tmpTags,_tmpIsBookmarked,_tmpWordCount,_tmpCreatedAt,_tmpUpdatedAt);
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
