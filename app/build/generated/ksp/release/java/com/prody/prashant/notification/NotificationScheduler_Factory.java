package com.prody.prashant.notification;

import android.content.Context;
import com.prody.prashant.data.local.dao.FutureMessageDao;
import com.prody.prashant.data.local.preferences.PreferencesManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class NotificationScheduler_Factory implements Factory<NotificationScheduler> {
  private final Provider<Context> contextProvider;

  private final Provider<PreferencesManager> preferencesManagerProvider;

  private final Provider<FutureMessageDao> futureMessageDaoProvider;

  public NotificationScheduler_Factory(Provider<Context> contextProvider,
      Provider<PreferencesManager> preferencesManagerProvider,
      Provider<FutureMessageDao> futureMessageDaoProvider) {
    this.contextProvider = contextProvider;
    this.preferencesManagerProvider = preferencesManagerProvider;
    this.futureMessageDaoProvider = futureMessageDaoProvider;
  }

  @Override
  public NotificationScheduler get() {
    return newInstance(contextProvider.get(), preferencesManagerProvider.get(), futureMessageDaoProvider.get());
  }

  public static NotificationScheduler_Factory create(Provider<Context> contextProvider,
      Provider<PreferencesManager> preferencesManagerProvider,
      Provider<FutureMessageDao> futureMessageDaoProvider) {
    return new NotificationScheduler_Factory(contextProvider, preferencesManagerProvider, futureMessageDaoProvider);
  }

  public static NotificationScheduler newInstance(Context context,
      PreferencesManager preferencesManager, FutureMessageDao futureMessageDao) {
    return new NotificationScheduler(context, preferencesManager, futureMessageDao);
  }
}
