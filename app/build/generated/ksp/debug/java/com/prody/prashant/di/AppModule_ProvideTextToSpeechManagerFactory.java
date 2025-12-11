package com.prody.prashant.di;

import android.content.Context;
import com.prody.prashant.util.TextToSpeechManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideTextToSpeechManagerFactory implements Factory<TextToSpeechManager> {
  private final Provider<Context> contextProvider;

  public AppModule_ProvideTextToSpeechManagerFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public TextToSpeechManager get() {
    return provideTextToSpeechManager(contextProvider.get());
  }

  public static AppModule_ProvideTextToSpeechManagerFactory create(
      Provider<Context> contextProvider) {
    return new AppModule_ProvideTextToSpeechManagerFactory(contextProvider);
  }

  public static TextToSpeechManager provideTextToSpeechManager(Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideTextToSpeechManager(context));
  }
}
