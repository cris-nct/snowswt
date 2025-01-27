package org.herbshouse.logic.snow.attack.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.herbshouse.audio.AudioPlayOrder;
import org.herbshouse.audio.AudioPlayType;
import org.herbshouse.audio.AudioPlayer;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.AbstractPhaseAttackData;
import org.herbshouse.logic.snow.attack.AttackStrategy;
import org.herbshouse.logic.snow.attack.PhaseProcessor;
import org.herbshouse.logic.snow.data.SnowflakeData;

public abstract class AbstractAttackPhaseStrategy<T extends AbstractPhaseAttackData> implements
    AttackStrategy<T> {

  private final List<PhaseProcessor<T>> phases = new ArrayList<>();
  private final AudioPlayer audioPlayer;
  private PhaseProcessor<T> currentPhaseProcessor;
  private volatile boolean allArrivedToDestination;
  private boolean started = false;
  private boolean finished = false;

  public AbstractAttackPhaseStrategy(AudioPlayer audioPlayer) {
    this.audioPlayer = audioPlayer;
  }

  public void playAudio(String filename) {
    if (!audioPlayer.isPlaying("sounds/" + filename)) {
      AudioPlayOrder order = new AudioPlayOrder("sounds/" + filename);
      order.setType(AudioPlayType.BACKGROUND);
      order.setVolume(0.9f);
      audioPlayer.play(order);
    }
  }

  public void stopAudio(String filename) {
    audioPlayer.stop("sounds/" + filename);
  }

  @SafeVarargs
  public final void addPhases(PhaseProcessor<T>... phases) {
    this.phases.addAll(Arrays.stream(phases).toList());
  }

  @Override
  public void beforeStart(List<Snowflake> snowflakeList) {
    this.currentPhaseProcessor = phases.getFirst();
    this.initPhase(snowflakeList);
    started = true;
  }

  private void initPhase(List<Snowflake> snowflakeList) {
    for (Snowflake snowflake : snowflakeList) {
      initPhase(snowflake);
    }
  }

  private void initPhase(Snowflake snowflake) {
    getData(snowflake).setPhase(currentPhaseProcessor.getCurrentPhaseIndex());
    currentPhaseProcessor.startPhase(snowflake);
  }

  @Override
  public boolean isStarted() {
    return started;
  }

  @Override
  public T getData(Snowflake snowflake) {
    SnowflakeData data = snowflake.getData(getDataClass().getSimpleName());
    if (data == null) {
      try {
        data = getDataClass().getDeclaredConstructor().newInstance();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      snowflake.setData(data.getClass().getSimpleName(), data);
      this.beforeStart(List.of(snowflake));
    }
    return (T) data;
  }

  public abstract Class<T> getDataClass();

  @Override
  public Point2D computeNextLocation(Snowflake snowflake, Snowflake prevSnowFlake) {
    return currentPhaseProcessor.computeLocation(snowflake);
  }

  @Override
  public void afterUpdate(List<Snowflake> snowflakeList) {
    allArrivedToDestination = true;
    for (Snowflake snowflake : snowflakeList) {
      T data = getData(snowflake);
      if (snowflake.isFreezed() || data.getLocationToFollow() == null) {
        continue;
      }
      allArrivedToDestination = currentPhaseProcessor.isFinished(snowflake);
      if (!allArrivedToDestination) {
        break;
      }
    }
    if (allArrivedToDestination) {
      currentPhaseProcessor.endPhase();
      currentPhaseProcessor = currentPhaseProcessor.getNextPhaseProcessor();
      if (currentPhaseProcessor == null) {
        afterEnd();
      } else {
        initPhase(snowflakeList);
      }
    }
  }

  public PhaseProcessor<T> getCurrentPhaseProcessor() {
    return currentPhaseProcessor;
  }

  @Override
  public void afterEnd() {
    finished = true;
  }

  @Override
  public boolean isFinished() {
    return finished;
  }
}
