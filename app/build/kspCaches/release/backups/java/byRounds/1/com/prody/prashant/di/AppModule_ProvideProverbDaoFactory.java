package com.prody.prashant.di;

import com.prody.prashant.data.local.dao.ProverbDao;
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
public final class AppModule_ProvideProverbDaoFactory implements Factory<ProverbDao> {
  private final Provider<ProdyDatabase> databaseProvider;

  public AppModule_ProvideProverbDaoFactory(Provider<ProdyDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public ProverbDao get() {
    return provideProverbDao(databaseProvider.get());
  }

  public static AppModule_ProvideProverbDaoFactory create(
      Provider<ProdyDatabase> databaseProvider) {
    return new AppModule_ProvideProverbDaoFactory(databaseProvider);
  }

  public static ProverbDao provideProverbDao(ProdyDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideProverbDao(database));
  }
}
