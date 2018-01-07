package com.maragues.planner.test.rules;

import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.functions.Function;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.Callable;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * This rule registers SchedulerHooks for RxJava and RxAndroid to ensure that subscriptions
 * always subscribeOn and observeOn Schedulers.immediate().
 * Warning, this rule will resetProcedureStatus RxAndroidPlugins and RxJavaPlugins after each test
 * so
 * if the application code uses RxJava plugins this may affect the behaviour of the testing method.
 * <p>
 *
 * See https://medium.com/@fabioCollini/testing-asynchronous-rxjava-code-using-mockito-8ad831a16877#.ahj5h7jmg
 * See https://github.com/fabioCollini/TestingRxJavaUsingMockito/blob/master/app/src/test/java/it/codingjam/testingrxjava/TestSchedulerRule.java
 */
public class ImmediateRxSchedulersOverrideRule implements TestRule {

  private final Scheduler SCHEDULER_INSTANCE = Schedulers.trampoline();

  private final Function<Scheduler, Scheduler> schedulerFunction = scheduler -> SCHEDULER_INSTANCE;

  private final Function<Callable<Scheduler>, Scheduler> schedulerFunctionLazy = schedulerCallable -> SCHEDULER_INSTANCE;

  @Override
  public Statement apply(final Statement base, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        RxAndroidPlugins.reset();
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerFunctionLazy);

        RxJavaPlugins.reset();
        RxJavaPlugins.setIoSchedulerHandler(schedulerFunction);
        RxJavaPlugins.setNewThreadSchedulerHandler(schedulerFunction);
        RxJavaPlugins.setComputationSchedulerHandler(schedulerFunction);

        try {
          base.evaluate();
        } finally {
          RxAndroidPlugins.reset();
          RxJavaPlugins.reset();
        }
      }
    };
  }
}