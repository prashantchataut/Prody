package com.prody.prashant;

import androidx.hilt.work.HiltWorkerFactory;
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
public final class ProdyApplication_MembersInjector implements MembersInjector<ProdyApplication> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  public ProdyApplication_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
  }

  public static MembersInjector<ProdyApplication> create(
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new ProdyApplication_MembersInjector(workerFactoryProvider);
  }

  @Override
  public void injectMembers(ProdyApplication instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  @InjectedFieldSignature("com.prody.prashant.ProdyApplication.workerFactory")
  public static void injectWorkerFactory(ProdyApplication instance,
      HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}
