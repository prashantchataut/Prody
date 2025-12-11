package com.prody.prashant.ui.screens.home;

import com.prody.prashant.data.local.dao.IdiomDao;
import com.prody.prashant.data.local.dao.JournalDao;
import com.prody.prashant.data.local.dao.ProverbDao;
import com.prody.prashant.data.local.dao.QuoteDao;
import com.prody.prashant.data.local.dao.UserDao;
import com.prody.prashant.data.local.dao.VocabularyDao;
import com.prody.prashant.data.local.preferences.PreferencesManager;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<UserDao> userDaoProvider;

  private final Provider<VocabularyDao> vocabularyDaoProvider;

  private final Provider<QuoteDao> quoteDaoProvider;

  private final Provider<ProverbDao> proverbDaoProvider;

  private final Provider<IdiomDao> idiomDaoProvider;

  private final Provider<JournalDao> journalDaoProvider;

  private final Provider<PreferencesManager> preferencesManagerProvider;

  public HomeViewModel_Factory(Provider<UserDao> userDaoProvider,
      Provider<VocabularyDao> vocabularyDaoProvider, Provider<QuoteDao> quoteDaoProvider,
      Provider<ProverbDao> proverbDaoProvider, Provider<IdiomDao> idiomDaoProvider,
      Provider<JournalDao> journalDaoProvider,
      Provider<PreferencesManager> preferencesManagerProvider) {
    this.userDaoProvider = userDaoProvider;
    this.vocabularyDaoProvider = vocabularyDaoProvider;
    this.quoteDaoProvider = quoteDaoProvider;
    this.proverbDaoProvider = proverbDaoProvider;
    this.idiomDaoProvider = idiomDaoProvider;
    this.journalDaoProvider = journalDaoProvider;
    this.preferencesManagerProvider = preferencesManagerProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(userDaoProvider.get(), vocabularyDaoProvider.get(), quoteDaoProvider.get(), proverbDaoProvider.get(), idiomDaoProvider.get(), journalDaoProvider.get(), preferencesManagerProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<UserDao> userDaoProvider,
      Provider<VocabularyDao> vocabularyDaoProvider, Provider<QuoteDao> quoteDaoProvider,
      Provider<ProverbDao> proverbDaoProvider, Provider<IdiomDao> idiomDaoProvider,
      Provider<JournalDao> journalDaoProvider,
      Provider<PreferencesManager> preferencesManagerProvider) {
    return new HomeViewModel_Factory(userDaoProvider, vocabularyDaoProvider, quoteDaoProvider, proverbDaoProvider, idiomDaoProvider, journalDaoProvider, preferencesManagerProvider);
  }

  public static HomeViewModel newInstance(UserDao userDao, VocabularyDao vocabularyDao,
      QuoteDao quoteDao, ProverbDao proverbDao, IdiomDao idiomDao, JournalDao journalDao,
      PreferencesManager preferencesManager) {
    return new HomeViewModel(userDao, vocabularyDao, quoteDao, proverbDao, idiomDao, journalDao, preferencesManager);
  }
}
