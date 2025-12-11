package com.prody.prashant.di;

import com.prody.prashant.data.local.dao.QuoteDao;
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
public final class AppModule_ProvideQuoteDaoFactory implements Factory<QuoteDao> {
  private final Provider<ProdyDatabase> databaseProvider;

  public AppModule_ProvideQuoteDaoFactory(Provider<ProdyDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public QuoteDao get() {
    return provideQuoteDao(databaseProvider.get());
  }

  public static AppModule_ProvideQuoteDaoFactory create(Provider<ProdyDatabase> databaseProvider) {
    return new AppModule_ProvideQuoteDaoFactory(databaseProvider);
  }

  public static QuoteDao provideQuoteDao(ProdyDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideQuoteDao(database));
  }
}
