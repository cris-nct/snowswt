package org.herbshouse;

import org.herbshouse.controller.DefaultLogicController;
import org.herbshouse.controller.ViewController;
import org.herbshouse.logic.UserInfo;
import org.herbshouse.logic.Utils;
import org.herbshouse.logic.enemies.EnemyGenerator;
import org.herbshouse.logic.snow.SnowGenerator;

public class Tests {

  public static void main(String[] args) {
    UserInfo userInfo = new UserInfo();

    SnowGenerator snowGenerator = new SnowGenerator();
    snowGenerator.skipInitialAnimation();

    EnemyGenerator enemyGenerator = new EnemyGenerator();

    DefaultLogicController controller = new DefaultLogicController();
    controller.setUserInfo(userInfo);
    controller.setDesiredFPS(60);
    controller.registerListener(snowGenerator);
    controller.registerListener(enemyGenerator);

    enemyGenerator.setViewController(new ViewController() {
      @Override
      public void substractAreaFromShell(int[] polygon) {

      }

      @Override
      public void resetScreenSurface() {

      }
    });
    snowGenerator.start();
    enemyGenerator.start();

    Utils.sleep(10000);

    controller.shutdown();
    snowGenerator.shutdown();
    enemyGenerator.shutdown();
  }

}
