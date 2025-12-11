package com.prody.prashant.ui.screens.meditation;

import com.prody.prashant.data.local.dao.QuoteDao;
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
public final class MeditationTimerViewModel_Factory implements Factory<MeditationTimerViewModel> {
  private final Provider<QuoteDao> quoteDaoProvider;

  private final Provider<UserDao> userDaoProvider;

  public MeditationTimerViewModel_Factory(Provider<QuoteDao> quoteDaoProvider,
      Provider<UserDao> userDaoProvider) {
    this.quoteDaoProvider = quoteDaoProvider;
    this.userDaoProvider = userDaoProvider;
  }

  @Override
  public MeditationTimerViewModel get() {
    return newInstance(quoteDaoProvider.get(), userDaoProvider.get());
  }

  public static MeditationTimerViewModel_Factory create(Provider<QuoteDao> quoteDaoProvider,
      Provider<UserDao> userDaoProvider) {
    return new MeditationTimerViewModel_Factory(quoteDaoProvider, userDaoProvider);
  }

  public static MeditationTimerViewModel newInstance(QuoteDao quoteDao, UserDao userDao) {
    return new MeditationTimerViewModel(quoteDao, userDao);
  }
}
