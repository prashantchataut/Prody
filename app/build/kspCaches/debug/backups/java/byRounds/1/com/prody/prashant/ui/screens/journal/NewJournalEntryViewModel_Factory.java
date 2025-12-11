package com.prody.prashant.ui.screens.journal;

import com.prody.prashant.data.local.dao.JournalDao;
import com.prody.prashant.data.local.dao.UserDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
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
public final class NewJournalEntryViewModel_Factory implements Factory<NewJournalEntryViewModel> {
  private final Provider<JournalDao> journalDaoProvider;

  private final Provider<UserDao> userDaoProvider;

  public NewJournalEntryViewModel_Factory(Provider<JournalDao> journalDaoProvider,
      Provider<UserDao> userDaoProvider) {
    this.journalDaoProvider = journalDaoProvider;
    this.userDaoProvider = userDaoProvider;
  }

  @Override
  public NewJournalEntryViewModel get() {
    return newInstance(journalDaoProvider.get(), userDaoProvider.get());
  }

  public static NewJournalEntryViewModel_Factory create(Provider<JournalDao> journalDaoProvider,
      Provider<UserDao> userDaoProvider) {
    return new NewJournalEntryViewModel_Factory(journalDaoProvider, userDaoProvider);
  }

  public static NewJournalEntryViewModel newInstance(JournalDao journalDao, UserDao userDao) {
    return new NewJournalEntryViewModel(journalDao, userDao);
  }
}
