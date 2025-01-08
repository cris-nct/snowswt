package org.herbshouse;

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

        TestController controller = new TestController();
        controller.setUserInfo(userInfo);
        controller.registerListener(snowGenerator);
        controller.registerListener(enemyGenerator);

        snowGenerator.start();
        enemyGenerator.start();

        Utils.sleep(10000);

        controller.shutdown();
        snowGenerator.shutdown();
        enemyGenerator.shutdown();
    }

}
