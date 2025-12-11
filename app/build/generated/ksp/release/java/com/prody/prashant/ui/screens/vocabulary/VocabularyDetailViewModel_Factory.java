package com.prody.prashant.ui.screens.vocabulary;

import com.prody.prashant.data.local.dao.VocabularyDao;
import com.prody.prashant.util.TextToSpeechManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class VocabularyDetailViewModel_Factory implements Factory<VocabularyDetailViewModel> {
  private final Provider<VocabularyDao> vocabularyDaoProvider;

  private final Provider<TextToSpeechManager> ttsManagerProvider;

  public VocabularyDetailViewModel_Factory(Provider<VocabularyDao> vocabularyDaoProvider,
      Provider<TextToSpeechManager> ttsManagerProvider) {
    this.vocabularyDaoProvider = vocabularyDaoProvider;
    this.ttsManagerProvider = ttsManagerProvider;
  }

  @Override
  public VocabularyDetailViewModel get() {
    return newInstance(vocabularyDaoProvider.get(), ttsManagerProvider.get());
  }

  public static VocabularyDetailViewModel_Factory create(
      Provider<VocabularyDao> vocabularyDaoProvider,
      Provider<TextToSpeechManager> ttsManagerProvider) {
    return new VocabularyDetailViewModel_Factory(vocabularyDaoProvider, ttsManagerProvider);
  }

  public static VocabularyDetailViewModel newInstance(VocabularyDao vocabularyDao,
      TextToSpeechManager ttsManager) {
    return new VocabularyDetailViewModel(vocabularyDao, ttsManager);
  }
}
