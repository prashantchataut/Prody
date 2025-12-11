package com.prody.prashant.ui.screens.profile;

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
public final class ProfileViewModel_Factory implements Factory<ProfileViewModel> {
  private final Provider<UserDao> userDaoProvider;

  public ProfileViewModel_Factory(Provider<UserDao> userDaoProvider) {
    this.userDaoProvider = userDaoProvider;
  }

  @Override
  public ProfileViewModel get() {
    return newInstance(userDaoProvider.get());
  }

  public static ProfileViewModel_Factory create(Provider<UserDao> userDaoProvider) {
    return new ProfileViewModel_Factory(userDaoProvider);
  }

  public static ProfileViewModel newInstance(UserDao userDao) {
    return new ProfileViewModel(userDao);
  }
}
