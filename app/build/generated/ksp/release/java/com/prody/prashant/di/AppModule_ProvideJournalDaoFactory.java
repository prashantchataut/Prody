package com.prody.prashant.di;

import com.prody.prashant.data.local.dao.JournalDao;
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
public final class AppModule_ProvideJournalDaoFactory implements Factory<JournalDao> {
  private final Provider<ProdyDatabase> databaseProvider;

  public AppModule_ProvideJournalDaoFactory(Provider<ProdyDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public JournalDao get() {
    return provideJournalDao(databaseProvider.get());
  }

  public static AppModule_ProvideJournalDaoFactory create(
      Provider<ProdyDatabase> databaseProvider) {
    return new AppModule_ProvideJournalDaoFactory(databaseProvider);
  }

  public static JournalDao provideJournalDao(ProdyDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideJournalDao(database));
  }
}
