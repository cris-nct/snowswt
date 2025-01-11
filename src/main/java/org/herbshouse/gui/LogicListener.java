package org.herbshouse.gui;

import org.eclipse.swt.graphics.Transform;

public interface LogicListener {

  void substractAreaFromShell(int[] polygon);

  void resetScreenSurface();

  Transform getTransform();

  void setAttackPhase(int currentAttackPhase);
}
