package org.herbshouse.gui;

import org.eclipse.swt.graphics.Transform;

public interface GuiListener {

  void substractAreaFromShell(int[] polygon);

  void resetShellSurface();

  Transform getTransform();

  void setAttackPhase(int currentAttackPhase);
}
