package org.herbshouse.logic.snow.attack.impl.phase.dancing;

import java.util.List;
import org.herbshouse.audio.AudioPlayType;
import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.snow.Snowflake;
import org.herbshouse.logic.snow.attack.AttackStrategy;
import org.herbshouse.logic.snow.data.AttackDataDancing;

/**
 * Move snowflakes on a circle around mouse position.
 */
public class DancingPhase1 extends DancingAbstract {

  protected DancingPhase1(AttackStrategy<AttackDataDancing> strategy) {
    super(strategy);
  }

  @Override
  public void startPhase(Snowflake snowflake) {
    AttackDataDancing attackData = getStrategy().getData(snowflake);
    Point2D dest = Utils.moveToDirection(getStrategy().getFlagsConfiguration().getMouseLoc(), 250,
        Math.toRadians(Math.random() * 360));
    attackData.setLocationToFollow(dest);
    getStrategy().playAudio("dancing.wav", AudioPlayType.BACKGROUND, 1f);
  }

  @Override
  public void endPhase(List<Snowflake> snowflakeList) {

  }

  @Override
  public int getCurrentPhaseIndex() {
    return 1;
  }

}
