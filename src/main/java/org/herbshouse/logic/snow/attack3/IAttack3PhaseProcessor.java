package org.herbshouse.logic.snow.attack3;

import org.herbshouse.logic.Point2D;
import org.herbshouse.logic.snow.Snowflake;

public interface IAttack3PhaseProcessor {

    Point2D computeLocation(Snowflake snowflake);

    int getCurrentPhaseIndex();

    void initNextPhase(Snowflake snowflake);

    IAttack3PhaseProcessor getNextPhaseProcessor();

}
