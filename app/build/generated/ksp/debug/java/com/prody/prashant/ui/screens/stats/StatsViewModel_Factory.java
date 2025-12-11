package com.prody.prashant.ui.screens.stats;

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
public final class StatsViewModel_Factory implements Factory<StatsViewModel> {
  private final Provider<UserDao> userDaoProvider;

  public StatsViewModel_Factory(Provider<UserDao> userDaoProvider) {
    this.userDaoProvider = userDaoProvider;
  }

  @Override
  public StatsViewModel get() {
    return newInstance(userDaoProvider.get());
  }

  public static StatsViewModel_Factory create(Provider<UserDao> userDaoProvider) {
    return new StatsViewModel_Factory(userDaoProvider);
  }

  public static StatsViewModel newInstance(UserDao userDao) {
    return new StatsViewModel(userDao);
  }
}
