package com.prody.prashant.ui.screens.futuremessage;

import com.prody.prashant.data.local.dao.FutureMessageDao;
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
public final class WriteMessageViewModel_Factory implements Factory<WriteMessageViewModel> {
  private final Provider<FutureMessageDao> futureMessageDaoProvider;

  private final Provider<UserDao> userDaoProvider;

  public WriteMessageViewModel_Factory(Provider<FutureMessageDao> futureMessageDaoProvider,
      Provider<UserDao> userDaoProvider) {
    this.futureMessageDaoProvider = futureMessageDaoProvider;
    this.userDaoProvider = userDaoProvider;
  }

  @Override
  public WriteMessageViewModel get() {
    return newInstance(futureMessageDaoProvider.get(), userDaoProvider.get());
  }

  public static WriteMessageViewModel_Factory create(
      Provider<FutureMessageDao> futureMessageDaoProvider, Provider<UserDao> userDaoProvider) {
    return new WriteMessageViewModel_Factory(futureMessageDaoProvider, userDaoProvider);
  }

  public static WriteMessageViewModel newInstance(FutureMessageDao futureMessageDao,
      UserDao userDao) {
    return new WriteMessageViewModel(futureMessageDao, userDao);
  }
}
