package org.herbshouse.logic.snow;

import org.herbshouse.gui.GuiUtils;
import org.herbshouse.logic.AbstractMovableObject;
import org.herbshouse.logic.snow.attack.attack1.AttackData1;
import org.herbshouse.logic.snow.attack.attack2.AttackData2;
import org.herbshouse.logic.snow.attack.attack3.AttackData3;
import org.herbshouse.logic.snow.attack.attack4.YinYangData;

public class Snowflake extends AbstractMovableObject {

  private AttackData1 attackData1 = new AttackData1();
  private AttackData2 attackData2 = new AttackData2();
  private AttackData3 attackData3 = new AttackData3();
  private YinYangData attackData4 = new YinYangData();
  private final HappyWindSnowFlakeData happyWindData = new HappyWindSnowFlakeData();

  private final SnowTail snowTail;
  private boolean freezed = false;

  public Snowflake() {
    snowTail = new SnowTail(this);
  }

  public AttackData3 getAttackData3() {
    return attackData3;
  }

  public YinYangData getAttackData4() {
    return attackData4;
  }

  public SnowTail getSnowTail() {
    return snowTail;
  }

  public AttackData2 getAttackData2() {
    return attackData2;
  }

  public AttackData1 getAttackData1() {
    return attackData1;
  }

  public HappyWindSnowFlakeData getHappyWindData() {
    return happyWindData;
  }

  public void freeze() {
    if (!freezed) {
      freezed = true;
      setColor(GuiUtils.FREEZE_COLOR);
    }
  }

  public boolean isFreezed() {
    return freezed;
  }

  public void cleanupAttackData() {
    attackData1 = new AttackData1();
    attackData2 = new AttackData2();
    attackData3 = new AttackData3();
    attackData4 = new YinYangData();
  }

}