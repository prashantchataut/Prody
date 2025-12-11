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
public final class JournalDetailViewModel_Factory implements Factory<JournalDetailViewModel> {
  private final Provider<JournalDao> journalDaoProvider;

  public JournalDetailViewModel_Factory(Provider<JournalDao> journalDaoProvider) {
    this.journalDaoProvider = journalDaoProvider;
  }

  @Override
  public JournalDetailViewModel get() {
    return newInstance(journalDaoProvider.get());
  }

  public static JournalDetailViewModel_Factory create(Provider<JournalDao> journalDaoProvider) {
    return new JournalDetailViewModel_Factory(journalDaoProvider);
  }

  public static JournalDetailViewModel newInstance(JournalDao journalDao) {
    return new JournalDetailViewModel(journalDao);
  }
}
