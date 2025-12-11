package com.prody.prashant.util;

import android.content.Context;
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
public final class TextToSpeechManager_Factory implements Factory<TextToSpeechManager> {
  private final Provider<Context> contextProvider;

  public TextToSpeechManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public TextToSpeechManager get() {
    return newInstance(contextProvider.get());
  }

  public static TextToSpeechManager_Factory create(Provider<Context> contextProvider) {
    return new TextToSpeechManager_Factory(contextProvider);
  }

  public static TextToSpeechManager newInstance(Context context) {
    return new TextToSpeechManager(context);
  }
}
