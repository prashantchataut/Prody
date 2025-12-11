package com.prody.prashant.ui.screens.journal;

import com.prody.prashant.data.local.dao.JournalDao;
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
public final class JournalViewModel_Factory implements Factory<JournalViewModel> {
  private final Provider<JournalDao> journalDaoProvider;

  public JournalViewModel_Factory(Provider<JournalDao> journalDaoProvider) {
    this.journalDaoProvider = journalDaoProvider;
  }

  @Override
  public JournalViewModel get() {
    return newInstance(journalDaoProvider.get());
  }

  public static JournalViewModel_Factory create(Provider<JournalDao> journalDaoProvider) {
    return new JournalViewModel_Factory(journalDaoProvider);
  }

  public static JournalViewModel newInstance(JournalDao journalDao) {
    return new JournalViewModel(journalDao);
  }
}
