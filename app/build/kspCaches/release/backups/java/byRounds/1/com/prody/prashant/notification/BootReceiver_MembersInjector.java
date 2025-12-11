package com.prody.prashant.notification;

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
public final class BootReceiver_MembersInjector implements MembersInjector<BootReceiver> {
  private final Provider<NotificationScheduler> notificationSchedulerProvider;

  public BootReceiver_MembersInjector(
      Provider<NotificationScheduler> notificationSchedulerProvider) {
    this.notificationSchedulerProvider = notificationSchedulerProvider;
  }

  public static MembersInjector<BootReceiver> create(
      Provider<NotificationScheduler> notificationSchedulerProvider) {
    return new BootReceiver_MembersInjector(notificationSchedulerProvider);
  }

  @Override
  public void injectMembers(BootReceiver instance) {
    injectNotificationScheduler(instance, notificationSchedulerProvider.get());
  }

  @InjectedFieldSignature("com.prody.prashant.notification.BootReceiver.notificationScheduler")
  public static void injectNotificationScheduler(BootReceiver instance,
      NotificationScheduler notificationScheduler) {
    instance.notificationScheduler = notificationScheduler;
  }
}
