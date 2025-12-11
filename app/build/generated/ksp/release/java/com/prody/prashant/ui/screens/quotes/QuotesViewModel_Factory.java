package com.prody.prashant.ui.screens.quotes;

import com.prody.prashant.data.local.dao.IdiomDao;
import com.prody.prashant.data.local.dao.PhraseDao;
import com.prody.prashant.data.local.dao.ProverbDao;
import com.prody.prashant.data.local.dao.QuoteDao;
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
public final class QuotesViewModel_Factory implements Factory<QuotesViewModel> {
  private final Provider<QuoteDao> quoteDaoProvider;

  private final Provider<ProverbDao> proverbDaoProvider;

  private final Provider<IdiomDao> idiomDaoProvider;

  private final Provider<PhraseDao> phraseDaoProvider;

  public QuotesViewModel_Factory(Provider<QuoteDao> quoteDaoProvider,
      Provider<ProverbDao> proverbDaoProvider, Provider<IdiomDao> idiomDaoProvider,
      Provider<PhraseDao> phraseDaoProvider) {
    this.quoteDaoProvider = quoteDaoProvider;
    this.proverbDaoProvider = proverbDaoProvider;
    this.idiomDaoProvider = idiomDaoProvider;
    this.phraseDaoProvider = phraseDaoProvider;
  }

  @Override
  public QuotesViewModel get() {
    return newInstance(quoteDaoProvider.get(), proverbDaoProvider.get(), idiomDaoProvider.get(), phraseDaoProvider.get());
  }

  public static QuotesViewModel_Factory create(Provider<QuoteDao> quoteDaoProvider,
      Provider<ProverbDao> proverbDaoProvider, Provider<IdiomDao> idiomDaoProvider,
      Provider<PhraseDao> phraseDaoProvider) {
    return new QuotesViewModel_Factory(quoteDaoProvider, proverbDaoProvider, idiomDaoProvider, phraseDaoProvider);
  }

  public static QuotesViewModel newInstance(QuoteDao quoteDao, ProverbDao proverbDao,
      IdiomDao idiomDao, PhraseDao phraseDao) {
    return new QuotesViewModel(quoteDao, proverbDao, idiomDao, phraseDao);
  }
}
