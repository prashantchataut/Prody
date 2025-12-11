package com.prody.prashant.ui.screens.onboarding;

import com.prody.prashant.data.local.dao.IdiomDao;
import com.prody.prashant.data.local.dao.PhraseDao;
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
public final class OnboardingViewModel_Factory implements Factory<OnboardingViewModel> {
  private final Provider<PreferencesManager> preferencesManagerProvider;

  private final Provider<VocabularyDao> vocabularyDaoProvider;

  private final Provider<QuoteDao> quoteDaoProvider;

  private final Provider<ProverbDao> proverbDaoProvider;

  private final Provider<IdiomDao> idiomDaoProvider;

  private final Provider<PhraseDao> phraseDaoProvider;

  private final Provider<UserDao> userDaoProvider;

  public OnboardingViewModel_Factory(Provider<PreferencesManager> preferencesManagerProvider,
      Provider<VocabularyDao> vocabularyDaoProvider, Provider<QuoteDao> quoteDaoProvider,
      Provider<ProverbDao> proverbDaoProvider, Provider<IdiomDao> idiomDaoProvider,
      Provider<PhraseDao> phraseDaoProvider, Provider<UserDao> userDaoProvider) {
    this.preferencesManagerProvider = preferencesManagerProvider;
    this.vocabularyDaoProvider = vocabularyDaoProvider;
    this.quoteDaoProvider = quoteDaoProvider;
    this.proverbDaoProvider = proverbDaoProvider;
    this.idiomDaoProvider = idiomDaoProvider;
    this.phraseDaoProvider = phraseDaoProvider;
    this.userDaoProvider = userDaoProvider;
  }

  @Override
  public OnboardingViewModel get() {
    return newInstance(preferencesManagerProvider.get(), vocabularyDaoProvider.get(), quoteDaoProvider.get(), proverbDaoProvider.get(), idiomDaoProvider.get(), phraseDaoProvider.get(), userDaoProvider.get());
  }

  public static OnboardingViewModel_Factory create(
      Provider<PreferencesManager> preferencesManagerProvider,
      Provider<VocabularyDao> vocabularyDaoProvider, Provider<QuoteDao> quoteDaoProvider,
      Provider<ProverbDao> proverbDaoProvider, Provider<IdiomDao> idiomDaoProvider,
      Provider<PhraseDao> phraseDaoProvider, Provider<UserDao> userDaoProvider) {
    return new OnboardingViewModel_Factory(preferencesManagerProvider, vocabularyDaoProvider, quoteDaoProvider, proverbDaoProvider, idiomDaoProvider, phraseDaoProvider, userDaoProvider);
  }

  public static OnboardingViewModel newInstance(PreferencesManager preferencesManager,
      VocabularyDao vocabularyDao, QuoteDao quoteDao, ProverbDao proverbDao, IdiomDao idiomDao,
      PhraseDao phraseDao, UserDao userDao) {
    return new OnboardingViewModel(preferencesManager, vocabularyDao, quoteDao, proverbDao, idiomDao, phraseDao, userDao);
  }
}
