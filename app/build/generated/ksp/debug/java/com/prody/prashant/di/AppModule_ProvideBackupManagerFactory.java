package com.prody.prashant.di;

import android.content.Context;
import com.prody.prashant.data.backup.BackupManager;
import com.prody.prashant.data.local.dao.FutureMessageDao;
import com.prody.prashant.data.local.dao.JournalDao;
import com.prody.prashant.data.local.dao.UserDao;
import com.prody.prashant.data.local.dao.VocabularyDao;
import com.prody.prashant.data.local.preferences.PreferencesManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class AppModule_ProvideBackupManagerFactory implements Factory<BackupManager> {
  private final Provider<Context> contextProvider;

  private final Provider<JournalDao> journalDaoProvider;

  private final Provider<FutureMessageDao> futureMessageDaoProvider;

  private final Provider<VocabularyDao> vocabularyDaoProvider;

  private final Provider<UserDao> userDaoProvider;

  private final Provider<PreferencesManager> preferencesManagerProvider;

  public AppModule_ProvideBackupManagerFactory(Provider<Context> contextProvider,
      Provider<JournalDao> journalDaoProvider, Provider<FutureMessageDao> futureMessageDaoProvider,
      Provider<VocabularyDao> vocabularyDaoProvider, Provider<UserDao> userDaoProvider,
      Provider<PreferencesManager> preferencesManagerProvider) {
    this.contextProvider = contextProvider;
    this.journalDaoProvider = journalDaoProvider;
    this.futureMessageDaoProvider = futureMessageDaoProvider;
    this.vocabularyDaoProvider = vocabularyDaoProvider;
    this.userDaoProvider = userDaoProvider;
    this.preferencesManagerProvider = preferencesManagerProvider;
  }

  @Override
  public BackupManager get() {
    return provideBackupManager(contextProvider.get(), journalDaoProvider.get(), futureMessageDaoProvider.get(), vocabularyDaoProvider.get(), userDaoProvider.get(), preferencesManagerProvider.get());
  }

  public static AppModule_ProvideBackupManagerFactory create(Provider<Context> contextProvider,
      Provider<JournalDao> journalDaoProvider, Provider<FutureMessageDao> futureMessageDaoProvider,
      Provider<VocabularyDao> vocabularyDaoProvider, Provider<UserDao> userDaoProvider,
      Provider<PreferencesManager> preferencesManagerProvider) {
    return new AppModule_ProvideBackupManagerFactory(contextProvider, journalDaoProvider, futureMessageDaoProvider, vocabularyDaoProvider, userDaoProvider, preferencesManagerProvider);
  }

  public static BackupManager provideBackupManager(Context context, JournalDao journalDao,
      FutureMessageDao futureMessageDao, VocabularyDao vocabularyDao, UserDao userDao,
      PreferencesManager preferencesManager) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideBackupManager(context, journalDao, futureMessageDao, vocabularyDao, userDao, preferencesManager));
  }
}
