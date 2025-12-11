package com.prody.prashant.di;

import com.prody.prashant.data.local.dao.VocabularyDao;
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
public final class AppModule_ProvideVocabularyDaoFactory implements Factory<VocabularyDao> {
  private final Provider<ProdyDatabase> databaseProvider;

  public AppModule_ProvideVocabularyDaoFactory(Provider<ProdyDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public VocabularyDao get() {
    return provideVocabularyDao(databaseProvider.get());
  }

  public static AppModule_ProvideVocabularyDaoFactory create(
      Provider<ProdyDatabase> databaseProvider) {
    return new AppModule_ProvideVocabularyDaoFactory(databaseProvider);
  }

  public static VocabularyDao provideVocabularyDao(ProdyDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideVocabularyDao(database));
  }
}
