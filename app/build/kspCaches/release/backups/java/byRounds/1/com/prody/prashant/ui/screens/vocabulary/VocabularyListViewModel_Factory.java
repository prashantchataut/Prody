package com.prody.prashant.ui.screens.vocabulary;

import com.prody.prashant.data.local.dao.VocabularyDao;
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
public final class VocabularyListViewModel_Factory implements Factory<VocabularyListViewModel> {
  private final Provider<VocabularyDao> vocabularyDaoProvider;

  public VocabularyListViewModel_Factory(Provider<VocabularyDao> vocabularyDaoProvider) {
    this.vocabularyDaoProvider = vocabularyDaoProvider;
  }

  @Override
  public VocabularyListViewModel get() {
    return newInstance(vocabularyDaoProvider.get());
  }

  public static VocabularyListViewModel_Factory create(
      Provider<VocabularyDao> vocabularyDaoProvider) {
    return new VocabularyListViewModel_Factory(vocabularyDaoProvider);
  }

  public static VocabularyListViewModel newInstance(VocabularyDao vocabularyDao) {
    return new VocabularyListViewModel(vocabularyDao);
  }
}
