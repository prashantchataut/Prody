package com.prody.prashant.di;

import com.prody.prashant.data.local.dao.IdiomDao;
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
public final class AppModule_ProvideIdiomDaoFactory implements Factory<IdiomDao> {
  private final Provider<ProdyDatabase> databaseProvider;

  public AppModule_ProvideIdiomDaoFactory(Provider<ProdyDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public IdiomDao get() {
    return provideIdiomDao(databaseProvider.get());
  }

  public static AppModule_ProvideIdiomDaoFactory create(Provider<ProdyDatabase> databaseProvider) {
    return new AppModule_ProvideIdiomDaoFactory(databaseProvider);
  }

  public static IdiomDao provideIdiomDao(ProdyDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideIdiomDao(database));
  }
}
