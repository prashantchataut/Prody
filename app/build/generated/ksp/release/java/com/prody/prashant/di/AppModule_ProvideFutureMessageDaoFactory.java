package com.prody.prashant.di;

import com.prody.prashant.data.local.dao.FutureMessageDao;
import com.prody.prashant.data.local.database.ProdyDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AppModule_ProvideFutureMessageDaoFactory implements Factory<FutureMessageDao> {
  private final Provider<ProdyDatabase> databaseProvider;

  public AppModule_ProvideFutureMessageDaoFactory(Provider<ProdyDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public FutureMessageDao get() {
    return provideFutureMessageDao(databaseProvider.get());
  }

  public static AppModule_ProvideFutureMessageDaoFactory create(
      Provider<ProdyDatabase> databaseProvider) {
    return new AppModule_ProvideFutureMessageDaoFactory(databaseProvider);
  }

  public static FutureMessageDao provideFutureMessageDao(ProdyDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideFutureMessageDao(database));
  }
}
