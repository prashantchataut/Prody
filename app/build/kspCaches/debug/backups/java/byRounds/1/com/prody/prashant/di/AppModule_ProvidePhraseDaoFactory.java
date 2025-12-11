package com.prody.prashant.di;

import com.prody.prashant.data.local.dao.PhraseDao;
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
public final class AppModule_ProvidePhraseDaoFactory implements Factory<PhraseDao> {
  private final Provider<ProdyDatabase> databaseProvider;

  public AppModule_ProvidePhraseDaoFactory(Provider<ProdyDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public PhraseDao get() {
    return providePhraseDao(databaseProvider.get());
  }

  public static AppModule_ProvidePhraseDaoFactory create(Provider<ProdyDatabase> databaseProvider) {
    return new AppModule_ProvidePhraseDaoFactory(databaseProvider);
  }

  public static PhraseDao providePhraseDao(ProdyDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.providePhraseDao(database));
  }
}
