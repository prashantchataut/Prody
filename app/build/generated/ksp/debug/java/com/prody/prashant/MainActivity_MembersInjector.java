package com.prody.prashant;

import com.prody.prashant.data.local.preferences.PreferencesManager;
import com.prody.prashant.notification.NotificationScheduler;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<PreferencesManager> preferencesManagerProvider;

  private final Provider<NotificationScheduler> notificationSchedulerProvider;

  public MainActivity_MembersInjector(Provider<PreferencesManager> preferencesManagerProvider,
      Provider<NotificationScheduler> notificationSchedulerProvider) {
    this.preferencesManagerProvider = preferencesManagerProvider;
    this.notificationSchedulerProvider = notificationSchedulerProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<PreferencesManager> preferencesManagerProvider,
      Provider<NotificationScheduler> notificationSchedulerProvider) {
    return new MainActivity_MembersInjector(preferencesManagerProvider, notificationSchedulerProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectPreferencesManager(instance, preferencesManagerProvider.get());
    injectNotificationScheduler(instance, notificationSchedulerProvider.get());
  }

  @InjectedFieldSignature("com.prody.prashant.MainActivity.preferencesManager")
  public static void injectPreferencesManager(MainActivity instance,
      PreferencesManager preferencesManager) {
    instance.preferencesManager = preferencesManager;
  }

  @InjectedFieldSignature("com.prody.prashant.MainActivity.notificationScheduler")
  public static void injectNotificationScheduler(MainActivity instance,
      NotificationScheduler notificationScheduler) {
    instance.notificationScheduler = notificationScheduler;
  }
}
