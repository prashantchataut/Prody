package com.prody.prashant.di;

import com.prody.prashant.data.local.dao.UserDao;
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
public final class AppModule_ProvideUserDaoFactory implements Factory<UserDao> {
  private final Provider<ProdyDatabase> databaseProvider;

  public AppModule_ProvideUserDaoFactory(Provider<ProdyDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public UserDao get() {
    return provideUserDao(databaseProvider.get());
  }

  public static AppModule_ProvideUserDaoFactory create(Provider<ProdyDatabase> databaseProvider) {
    return new AppModule_ProvideUserDaoFactory(databaseProvider);
  }

  public static UserDao provideUserDao(ProdyDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideUserDao(database));
  }
}
