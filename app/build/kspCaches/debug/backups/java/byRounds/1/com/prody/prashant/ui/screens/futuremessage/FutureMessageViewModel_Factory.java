package com.prody.prashant.ui.screens.futuremessage;

import com.prody.prashant.data.local.dao.FutureMessageDao;
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
public final class FutureMessageViewModel_Factory implements Factory<FutureMessageViewModel> {
  private final Provider<FutureMessageDao> futureMessageDaoProvider;

  public FutureMessageViewModel_Factory(Provider<FutureMessageDao> futureMessageDaoProvider) {
    this.futureMessageDaoProvider = futureMessageDaoProvider;
  }

  @Override
  public FutureMessageViewModel get() {
    return newInstance(futureMessageDaoProvider.get());
  }

  public static FutureMessageViewModel_Factory create(
      Provider<FutureMessageDao> futureMessageDaoProvider) {
    return new FutureMessageViewModel_Factory(futureMessageDaoProvider);
  }

  public static FutureMessageViewModel newInstance(FutureMessageDao futureMessageDao) {
    return new FutureMessageViewModel(futureMessageDao);
  }
}
