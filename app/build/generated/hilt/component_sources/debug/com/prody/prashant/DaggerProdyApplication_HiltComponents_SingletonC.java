package com.prody.prashant;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.hilt.work.WorkerAssistedFactory;
import androidx.hilt.work.WorkerFactoryModule_ProvideFactoryFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.work.ListenableWorker;
import com.prody.prashant.data.local.dao.FutureMessageDao;
import com.prody.prashant.data.local.dao.IdiomDao;
import com.prody.prashant.data.local.dao.JournalDao;
import com.prody.prashant.data.local.dao.PhraseDao;
import com.prody.prashant.data.local.dao.ProverbDao;
import com.prody.prashant.data.local.dao.QuoteDao;
import com.prody.prashant.data.local.dao.UserDao;
import com.prody.prashant.data.local.dao.VocabularyDao;
import com.prody.prashant.data.local.database.ProdyDatabase;
import com.prody.prashant.data.local.preferences.PreferencesManager;
import com.prody.prashant.di.AppModule_ProvideDatabaseFactory;
import com.prody.prashant.di.AppModule_ProvideFutureMessageDaoFactory;
import com.prody.prashant.di.AppModule_ProvideIdiomDaoFactory;
import com.prody.prashant.di.AppModule_ProvideJournalDaoFactory;
import com.prody.prashant.di.AppModule_ProvidePhraseDaoFactory;
import com.prody.prashant.di.AppModule_ProvidePreferencesManagerFactory;
import com.prody.prashant.di.AppModule_ProvideProverbDaoFactory;
import com.prody.prashant.di.AppModule_ProvideQuoteDaoFactory;
import com.prody.prashant.di.AppModule_ProvideTextToSpeechManagerFactory;
import com.prody.prashant.di.AppModule_ProvideUserDaoFactory;
import com.prody.prashant.di.AppModule_ProvideVocabularyDaoFactory;
import com.prody.prashant.notification.BootReceiver;
import com.prody.prashant.notification.BootReceiver_MembersInjector;
import com.prody.prashant.notification.NotificationScheduler;
import com.prody.prashant.ui.screens.futuremessage.FutureMessageViewModel;
import com.prody.prashant.ui.screens.futuremessage.FutureMessageViewModel_HiltModules;
import com.prody.prashant.ui.screens.futuremessage.WriteMessageViewModel;
import com.prody.prashant.ui.screens.futuremessage.WriteMessageViewModel_HiltModules;
import com.prody.prashant.ui.screens.home.HomeViewModel;
import com.prody.prashant.ui.screens.home.HomeViewModel_HiltModules;
import com.prody.prashant.ui.screens.journal.JournalDetailViewModel;
import com.prody.prashant.ui.screens.journal.JournalDetailViewModel_HiltModules;
import com.prody.prashant.ui.screens.journal.JournalViewModel;
import com.prody.prashant.ui.screens.journal.JournalViewModel_HiltModules;
import com.prody.prashant.ui.screens.journal.NewJournalEntryViewModel;
import com.prody.prashant.ui.screens.journal.NewJournalEntryViewModel_HiltModules;
import com.prody.prashant.ui.screens.meditation.MeditationTimerViewModel;
import com.prody.prashant.ui.screens.meditation.MeditationTimerViewModel_HiltModules;
import com.prody.prashant.ui.screens.onboarding.OnboardingViewModel;
import com.prody.prashant.ui.screens.onboarding.OnboardingViewModel_HiltModules;
import com.prody.prashant.ui.screens.profile.ProfileViewModel;
import com.prody.prashant.ui.screens.profile.ProfileViewModel_HiltModules;
import com.prody.prashant.ui.screens.profile.SettingsViewModel;
import com.prody.prashant.ui.screens.profile.SettingsViewModel_HiltModules;
import com.prody.prashant.ui.screens.quotes.QuotesViewModel;
import com.prody.prashant.ui.screens.quotes.QuotesViewModel_HiltModules;
import com.prody.prashant.ui.screens.stats.StatsViewModel;
import com.prody.prashant.ui.screens.stats.StatsViewModel_HiltModules;
import com.prody.prashant.ui.screens.vocabulary.VocabularyDetailViewModel;
import com.prody.prashant.ui.screens.vocabulary.VocabularyDetailViewModel_HiltModules;
import com.prody.prashant.ui.screens.vocabulary.VocabularyListViewModel;
import com.prody.prashant.ui.screens.vocabulary.VocabularyListViewModel_HiltModules;
import com.prody.prashant.util.TextToSpeechManager;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerProdyApplication_HiltComponents_SingletonC {
  private DaggerProdyApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public ProdyApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements ProdyApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public ProdyApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements ProdyApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public ProdyApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements ProdyApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public ProdyApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements ProdyApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public ProdyApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements ProdyApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public ProdyApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements ProdyApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public ProdyApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements ProdyApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public ProdyApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends ProdyApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends ProdyApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends ProdyApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends ProdyApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
      injectMainActivity2(mainActivity);
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(14).put(LazyClassKeyProvider.com_prody_prashant_ui_screens_futuremessage_FutureMessageViewModel, FutureMessageViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_prody_prashant_ui_screens_home_HomeViewModel, HomeViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_prody_prashant_ui_screens_journal_JournalDetailViewModel, JournalDetailViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_prody_prashant_ui_screens_journal_JournalViewModel, JournalViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_prody_prashant_ui_screens_meditation_MeditationTimerViewModel, MeditationTimerViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_prody_prashant_ui_screens_journal_NewJournalEntryViewModel, NewJournalEntryViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_prody_prashant_ui_screens_onboarding_OnboardingViewModel, OnboardingViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_prody_prashant_ui_screens_profile_ProfileViewModel, ProfileViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_prody_prashant_ui_screens_quotes_QuotesViewModel, QuotesViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_prody_prashant_ui_screens_profile_SettingsViewModel, SettingsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_prody_prashant_ui_screens_stats_StatsViewModel, StatsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_prody_prashant_ui_screens_vocabulary_VocabularyDetailViewModel, VocabularyDetailViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_prody_prashant_ui_screens_vocabulary_VocabularyListViewModel, VocabularyListViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_prody_prashant_ui_screens_futuremessage_WriteMessageViewModel, WriteMessageViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectPreferencesManager(instance, singletonCImpl.providePreferencesManagerProvider.get());
      MainActivity_MembersInjector.injectNotificationScheduler(instance, singletonCImpl.notificationSchedulerProvider.get());
      return instance;
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_prody_prashant_ui_screens_profile_ProfileViewModel = "com.prody.prashant.ui.screens.profile.ProfileViewModel";

      static String com_prody_prashant_ui_screens_stats_StatsViewModel = "com.prody.prashant.ui.screens.stats.StatsViewModel";

      static String com_prody_prashant_ui_screens_quotes_QuotesViewModel = "com.prody.prashant.ui.screens.quotes.QuotesViewModel";

      static String com_prody_prashant_ui_screens_journal_JournalViewModel = "com.prody.prashant.ui.screens.journal.JournalViewModel";

      static String com_prody_prashant_ui_screens_vocabulary_VocabularyListViewModel = "com.prody.prashant.ui.screens.vocabulary.VocabularyListViewModel";

      static String com_prody_prashant_ui_screens_journal_NewJournalEntryViewModel = "com.prody.prashant.ui.screens.journal.NewJournalEntryViewModel";

      static String com_prody_prashant_ui_screens_journal_JournalDetailViewModel = "com.prody.prashant.ui.screens.journal.JournalDetailViewModel";

      static String com_prody_prashant_ui_screens_home_HomeViewModel = "com.prody.prashant.ui.screens.home.HomeViewModel";

      static String com_prody_prashant_ui_screens_profile_SettingsViewModel = "com.prody.prashant.ui.screens.profile.SettingsViewModel";

      static String com_prody_prashant_ui_screens_meditation_MeditationTimerViewModel = "com.prody.prashant.ui.screens.meditation.MeditationTimerViewModel";

      static String com_prody_prashant_ui_screens_vocabulary_VocabularyDetailViewModel = "com.prody.prashant.ui.screens.vocabulary.VocabularyDetailViewModel";

      static String com_prody_prashant_ui_screens_futuremessage_FutureMessageViewModel = "com.prody.prashant.ui.screens.futuremessage.FutureMessageViewModel";

      static String com_prody_prashant_ui_screens_futuremessage_WriteMessageViewModel = "com.prody.prashant.ui.screens.futuremessage.WriteMessageViewModel";

      static String com_prody_prashant_ui_screens_onboarding_OnboardingViewModel = "com.prody.prashant.ui.screens.onboarding.OnboardingViewModel";

      @KeepFieldType
      ProfileViewModel com_prody_prashant_ui_screens_profile_ProfileViewModel2;

      @KeepFieldType
      StatsViewModel com_prody_prashant_ui_screens_stats_StatsViewModel2;

      @KeepFieldType
      QuotesViewModel com_prody_prashant_ui_screens_quotes_QuotesViewModel2;

      @KeepFieldType
      JournalViewModel com_prody_prashant_ui_screens_journal_JournalViewModel2;

      @KeepFieldType
      VocabularyListViewModel com_prody_prashant_ui_screens_vocabulary_VocabularyListViewModel2;

      @KeepFieldType
      NewJournalEntryViewModel com_prody_prashant_ui_screens_journal_NewJournalEntryViewModel2;

      @KeepFieldType
      JournalDetailViewModel com_prody_prashant_ui_screens_journal_JournalDetailViewModel2;

      @KeepFieldType
      HomeViewModel com_prody_prashant_ui_screens_home_HomeViewModel2;

      @KeepFieldType
      SettingsViewModel com_prody_prashant_ui_screens_profile_SettingsViewModel2;

      @KeepFieldType
      MeditationTimerViewModel com_prody_prashant_ui_screens_meditation_MeditationTimerViewModel2;

      @KeepFieldType
      VocabularyDetailViewModel com_prody_prashant_ui_screens_vocabulary_VocabularyDetailViewModel2;

      @KeepFieldType
      FutureMessageViewModel com_prody_prashant_ui_screens_futuremessage_FutureMessageViewModel2;

      @KeepFieldType
      WriteMessageViewModel com_prody_prashant_ui_screens_futuremessage_WriteMessageViewModel2;

      @KeepFieldType
      OnboardingViewModel com_prody_prashant_ui_screens_onboarding_OnboardingViewModel2;
    }
  }

  private static final class ViewModelCImpl extends ProdyApplication_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<FutureMessageViewModel> futureMessageViewModelProvider;

    private Provider<HomeViewModel> homeViewModelProvider;

    private Provider<JournalDetailViewModel> journalDetailViewModelProvider;

    private Provider<JournalViewModel> journalViewModelProvider;

    private Provider<MeditationTimerViewModel> meditationTimerViewModelProvider;

    private Provider<NewJournalEntryViewModel> newJournalEntryViewModelProvider;

    private Provider<OnboardingViewModel> onboardingViewModelProvider;

    private Provider<ProfileViewModel> profileViewModelProvider;

    private Provider<QuotesViewModel> quotesViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private Provider<StatsViewModel> statsViewModelProvider;

    private Provider<VocabularyDetailViewModel> vocabularyDetailViewModelProvider;

    private Provider<VocabularyListViewModel> vocabularyListViewModelProvider;

    private Provider<WriteMessageViewModel> writeMessageViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.futureMessageViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.journalDetailViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.journalViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.meditationTimerViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.newJournalEntryViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.onboardingViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.profileViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.quotesViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 9);
      this.statsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 10);
      this.vocabularyDetailViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 11);
      this.vocabularyListViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 12);
      this.writeMessageViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 13);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(14).put(LazyClassKeyProvider.com_prody_prashant_ui_screens_futuremessage_FutureMessageViewModel, ((Provider) futureMessageViewModelProvider)).put(LazyClassKeyProvider.com_prody_prashant_ui_screens_home_HomeViewModel, ((Provider) homeViewModelProvider)).put(LazyClassKeyProvider.com_prody_prashant_ui_screens_journal_JournalDetailViewModel, ((Provider) journalDetailViewModelProvider)).put(LazyClassKeyProvider.com_prody_prashant_ui_screens_journal_JournalViewModel, ((Provider) journalViewModelProvider)).put(LazyClassKeyProvider.com_prody_prashant_ui_screens_meditation_MeditationTimerViewModel, ((Provider) meditationTimerViewModelProvider)).put(LazyClassKeyProvider.com_prody_prashant_ui_screens_journal_NewJournalEntryViewModel, ((Provider) newJournalEntryViewModelProvider)).put(LazyClassKeyProvider.com_prody_prashant_ui_screens_onboarding_OnboardingViewModel, ((Provider) onboardingViewModelProvider)).put(LazyClassKeyProvider.com_prody_prashant_ui_screens_profile_ProfileViewModel, ((Provider) profileViewModelProvider)).put(LazyClassKeyProvider.com_prody_prashant_ui_screens_quotes_QuotesViewModel, ((Provider) quotesViewModelProvider)).put(LazyClassKeyProvider.com_prody_prashant_ui_screens_profile_SettingsViewModel, ((Provider) settingsViewModelProvider)).put(LazyClassKeyProvider.com_prody_prashant_ui_screens_stats_StatsViewModel, ((Provider) statsViewModelProvider)).put(LazyClassKeyProvider.com_prody_prashant_ui_screens_vocabulary_VocabularyDetailViewModel, ((Provider) vocabularyDetailViewModelProvider)).put(LazyClassKeyProvider.com_prody_prashant_ui_screens_vocabulary_VocabularyListViewModel, ((Provider) vocabularyListViewModelProvider)).put(LazyClassKeyProvider.com_prody_prashant_ui_screens_futuremessage_WriteMessageViewModel, ((Provider) writeMessageViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_prody_prashant_ui_screens_journal_JournalDetailViewModel = "com.prody.prashant.ui.screens.journal.JournalDetailViewModel";

      static String com_prody_prashant_ui_screens_vocabulary_VocabularyDetailViewModel = "com.prody.prashant.ui.screens.vocabulary.VocabularyDetailViewModel";

      static String com_prody_prashant_ui_screens_journal_JournalViewModel = "com.prody.prashant.ui.screens.journal.JournalViewModel";

      static String com_prody_prashant_ui_screens_stats_StatsViewModel = "com.prody.prashant.ui.screens.stats.StatsViewModel";

      static String com_prody_prashant_ui_screens_quotes_QuotesViewModel = "com.prody.prashant.ui.screens.quotes.QuotesViewModel";

      static String com_prody_prashant_ui_screens_futuremessage_WriteMessageViewModel = "com.prody.prashant.ui.screens.futuremessage.WriteMessageViewModel";

      static String com_prody_prashant_ui_screens_onboarding_OnboardingViewModel = "com.prody.prashant.ui.screens.onboarding.OnboardingViewModel";

      static String com_prody_prashant_ui_screens_vocabulary_VocabularyListViewModel = "com.prody.prashant.ui.screens.vocabulary.VocabularyListViewModel";

      static String com_prody_prashant_ui_screens_journal_NewJournalEntryViewModel = "com.prody.prashant.ui.screens.journal.NewJournalEntryViewModel";

      static String com_prody_prashant_ui_screens_meditation_MeditationTimerViewModel = "com.prody.prashant.ui.screens.meditation.MeditationTimerViewModel";

      static String com_prody_prashant_ui_screens_profile_SettingsViewModel = "com.prody.prashant.ui.screens.profile.SettingsViewModel";

      static String com_prody_prashant_ui_screens_futuremessage_FutureMessageViewModel = "com.prody.prashant.ui.screens.futuremessage.FutureMessageViewModel";

      static String com_prody_prashant_ui_screens_home_HomeViewModel = "com.prody.prashant.ui.screens.home.HomeViewModel";

      static String com_prody_prashant_ui_screens_profile_ProfileViewModel = "com.prody.prashant.ui.screens.profile.ProfileViewModel";

      @KeepFieldType
      JournalDetailViewModel com_prody_prashant_ui_screens_journal_JournalDetailViewModel2;

      @KeepFieldType
      VocabularyDetailViewModel com_prody_prashant_ui_screens_vocabulary_VocabularyDetailViewModel2;

      @KeepFieldType
      JournalViewModel com_prody_prashant_ui_screens_journal_JournalViewModel2;

      @KeepFieldType
      StatsViewModel com_prody_prashant_ui_screens_stats_StatsViewModel2;

      @KeepFieldType
      QuotesViewModel com_prody_prashant_ui_screens_quotes_QuotesViewModel2;

      @KeepFieldType
      WriteMessageViewModel com_prody_prashant_ui_screens_futuremessage_WriteMessageViewModel2;

      @KeepFieldType
      OnboardingViewModel com_prody_prashant_ui_screens_onboarding_OnboardingViewModel2;

      @KeepFieldType
      VocabularyListViewModel com_prody_prashant_ui_screens_vocabulary_VocabularyListViewModel2;

      @KeepFieldType
      NewJournalEntryViewModel com_prody_prashant_ui_screens_journal_NewJournalEntryViewModel2;

      @KeepFieldType
      MeditationTimerViewModel com_prody_prashant_ui_screens_meditation_MeditationTimerViewModel2;

      @KeepFieldType
      SettingsViewModel com_prody_prashant_ui_screens_profile_SettingsViewModel2;

      @KeepFieldType
      FutureMessageViewModel com_prody_prashant_ui_screens_futuremessage_FutureMessageViewModel2;

      @KeepFieldType
      HomeViewModel com_prody_prashant_ui_screens_home_HomeViewModel2;

      @KeepFieldType
      ProfileViewModel com_prody_prashant_ui_screens_profile_ProfileViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.prody.prashant.ui.screens.futuremessage.FutureMessageViewModel 
          return (T) new FutureMessageViewModel(singletonCImpl.provideFutureMessageDaoProvider.get());

          case 1: // com.prody.prashant.ui.screens.home.HomeViewModel 
          return (T) new HomeViewModel(singletonCImpl.provideUserDaoProvider.get(), singletonCImpl.provideVocabularyDaoProvider.get(), singletonCImpl.provideQuoteDaoProvider.get(), singletonCImpl.provideProverbDaoProvider.get(), singletonCImpl.provideIdiomDaoProvider.get(), singletonCImpl.provideJournalDaoProvider.get(), singletonCImpl.providePreferencesManagerProvider.get());

          case 2: // com.prody.prashant.ui.screens.journal.JournalDetailViewModel 
          return (T) new JournalDetailViewModel(singletonCImpl.provideJournalDaoProvider.get());

          case 3: // com.prody.prashant.ui.screens.journal.JournalViewModel 
          return (T) new JournalViewModel(singletonCImpl.provideJournalDaoProvider.get());

          case 4: // com.prody.prashant.ui.screens.meditation.MeditationTimerViewModel 
          return (T) new MeditationTimerViewModel(singletonCImpl.provideQuoteDaoProvider.get(), singletonCImpl.provideUserDaoProvider.get());

          case 5: // com.prody.prashant.ui.screens.journal.NewJournalEntryViewModel 
          return (T) new NewJournalEntryViewModel(singletonCImpl.provideJournalDaoProvider.get(), singletonCImpl.provideUserDaoProvider.get());

          case 6: // com.prody.prashant.ui.screens.onboarding.OnboardingViewModel 
          return (T) new OnboardingViewModel(singletonCImpl.providePreferencesManagerProvider.get(), singletonCImpl.provideVocabularyDaoProvider.get(), singletonCImpl.provideQuoteDaoProvider.get(), singletonCImpl.provideProverbDaoProvider.get(), singletonCImpl.provideIdiomDaoProvider.get(), singletonCImpl.providePhraseDaoProvider.get(), singletonCImpl.provideUserDaoProvider.get());

          case 7: // com.prody.prashant.ui.screens.profile.ProfileViewModel 
          return (T) new ProfileViewModel(singletonCImpl.provideUserDaoProvider.get());

          case 8: // com.prody.prashant.ui.screens.quotes.QuotesViewModel 
          return (T) new QuotesViewModel(singletonCImpl.provideQuoteDaoProvider.get(), singletonCImpl.provideProverbDaoProvider.get(), singletonCImpl.provideIdiomDaoProvider.get(), singletonCImpl.providePhraseDaoProvider.get());

          case 9: // com.prody.prashant.ui.screens.profile.SettingsViewModel 
          return (T) new SettingsViewModel(singletonCImpl.providePreferencesManagerProvider.get());

          case 10: // com.prody.prashant.ui.screens.stats.StatsViewModel 
          return (T) new StatsViewModel(singletonCImpl.provideUserDaoProvider.get());

          case 11: // com.prody.prashant.ui.screens.vocabulary.VocabularyDetailViewModel 
          return (T) new VocabularyDetailViewModel(singletonCImpl.provideVocabularyDaoProvider.get(), singletonCImpl.provideTextToSpeechManagerProvider.get());

          case 12: // com.prody.prashant.ui.screens.vocabulary.VocabularyListViewModel 
          return (T) new VocabularyListViewModel(singletonCImpl.provideVocabularyDaoProvider.get());

          case 13: // com.prody.prashant.ui.screens.futuremessage.WriteMessageViewModel 
          return (T) new WriteMessageViewModel(singletonCImpl.provideFutureMessageDaoProvider.get(), singletonCImpl.provideUserDaoProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends ProdyApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends ProdyApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends ProdyApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<PreferencesManager> providePreferencesManagerProvider;

    private Provider<ProdyDatabase> provideDatabaseProvider;

    private Provider<FutureMessageDao> provideFutureMessageDaoProvider;

    private Provider<NotificationScheduler> notificationSchedulerProvider;

    private Provider<UserDao> provideUserDaoProvider;

    private Provider<VocabularyDao> provideVocabularyDaoProvider;

    private Provider<QuoteDao> provideQuoteDaoProvider;

    private Provider<ProverbDao> provideProverbDaoProvider;

    private Provider<IdiomDao> provideIdiomDaoProvider;

    private Provider<JournalDao> provideJournalDaoProvider;

    private Provider<PhraseDao> providePhraseDaoProvider;

    private Provider<TextToSpeechManager> provideTextToSpeechManagerProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private HiltWorkerFactory hiltWorkerFactory() {
      return WorkerFactoryModule_ProvideFactoryFactory.provideFactory(Collections.<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>>emptyMap());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.providePreferencesManagerProvider = DoubleCheck.provider(new SwitchingProvider<PreferencesManager>(singletonCImpl, 1));
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<ProdyDatabase>(singletonCImpl, 3));
      this.provideFutureMessageDaoProvider = DoubleCheck.provider(new SwitchingProvider<FutureMessageDao>(singletonCImpl, 2));
      this.notificationSchedulerProvider = DoubleCheck.provider(new SwitchingProvider<NotificationScheduler>(singletonCImpl, 0));
      this.provideUserDaoProvider = DoubleCheck.provider(new SwitchingProvider<UserDao>(singletonCImpl, 4));
      this.provideVocabularyDaoProvider = DoubleCheck.provider(new SwitchingProvider<VocabularyDao>(singletonCImpl, 5));
      this.provideQuoteDaoProvider = DoubleCheck.provider(new SwitchingProvider<QuoteDao>(singletonCImpl, 6));
      this.provideProverbDaoProvider = DoubleCheck.provider(new SwitchingProvider<ProverbDao>(singletonCImpl, 7));
      this.provideIdiomDaoProvider = DoubleCheck.provider(new SwitchingProvider<IdiomDao>(singletonCImpl, 8));
      this.provideJournalDaoProvider = DoubleCheck.provider(new SwitchingProvider<JournalDao>(singletonCImpl, 9));
      this.providePhraseDaoProvider = DoubleCheck.provider(new SwitchingProvider<PhraseDao>(singletonCImpl, 10));
      this.provideTextToSpeechManagerProvider = DoubleCheck.provider(new SwitchingProvider<TextToSpeechManager>(singletonCImpl, 11));
    }

    @Override
    public void injectProdyApplication(ProdyApplication prodyApplication) {
      injectProdyApplication2(prodyApplication);
    }

    @Override
    public void injectBootReceiver(BootReceiver bootReceiver) {
      injectBootReceiver2(bootReceiver);
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private ProdyApplication injectProdyApplication2(ProdyApplication instance) {
      ProdyApplication_MembersInjector.injectWorkerFactory(instance, hiltWorkerFactory());
      return instance;
    }

    private BootReceiver injectBootReceiver2(BootReceiver instance2) {
      BootReceiver_MembersInjector.injectNotificationScheduler(instance2, notificationSchedulerProvider.get());
      return instance2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.prody.prashant.notification.NotificationScheduler 
          return (T) new NotificationScheduler(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.providePreferencesManagerProvider.get(), singletonCImpl.provideFutureMessageDaoProvider.get());

          case 1: // com.prody.prashant.data.local.preferences.PreferencesManager 
          return (T) AppModule_ProvidePreferencesManagerFactory.providePreferencesManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // com.prody.prashant.data.local.dao.FutureMessageDao 
          return (T) AppModule_ProvideFutureMessageDaoFactory.provideFutureMessageDao(singletonCImpl.provideDatabaseProvider.get());

          case 3: // com.prody.prashant.data.local.database.ProdyDatabase 
          return (T) AppModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 4: // com.prody.prashant.data.local.dao.UserDao 
          return (T) AppModule_ProvideUserDaoFactory.provideUserDao(singletonCImpl.provideDatabaseProvider.get());

          case 5: // com.prody.prashant.data.local.dao.VocabularyDao 
          return (T) AppModule_ProvideVocabularyDaoFactory.provideVocabularyDao(singletonCImpl.provideDatabaseProvider.get());

          case 6: // com.prody.prashant.data.local.dao.QuoteDao 
          return (T) AppModule_ProvideQuoteDaoFactory.provideQuoteDao(singletonCImpl.provideDatabaseProvider.get());

          case 7: // com.prody.prashant.data.local.dao.ProverbDao 
          return (T) AppModule_ProvideProverbDaoFactory.provideProverbDao(singletonCImpl.provideDatabaseProvider.get());

          case 8: // com.prody.prashant.data.local.dao.IdiomDao 
          return (T) AppModule_ProvideIdiomDaoFactory.provideIdiomDao(singletonCImpl.provideDatabaseProvider.get());

          case 9: // com.prody.prashant.data.local.dao.JournalDao 
          return (T) AppModule_ProvideJournalDaoFactory.provideJournalDao(singletonCImpl.provideDatabaseProvider.get());

          case 10: // com.prody.prashant.data.local.dao.PhraseDao 
          return (T) AppModule_ProvidePhraseDaoFactory.providePhraseDao(singletonCImpl.provideDatabaseProvider.get());

          case 11: // com.prody.prashant.util.TextToSpeechManager 
          return (T) AppModule_ProvideTextToSpeechManagerFactory.provideTextToSpeechManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
