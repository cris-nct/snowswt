package org.herbshouse.gui.imageBuilder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.herbshouse.controller.FlagsConfiguration;
import org.herbshouse.controller.LogicController;
import org.herbshouse.gui.GuiUtils;
import org.herbshouse.gui.SWTResourceManager;

class LegendDrawer {

  private final LogicController controller;

  public LegendDrawer(LogicController controller) {
    this.controller = controller;
  }

  public void draw(GC gc, int realFPS, int currentAttackPhase) {
    if (gc == null) {
      throw new IllegalArgumentException("Unproper usage of SwtImageBuilder");
    }
    FlagsConfiguration config = controller.getFlagsConfiguration();
    //Draw legend
    gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_GREEN));
    gc.setFont(SWTResourceManager.getFont("Arial", 12, SWT.BOLD));
    StringBuilder legendBuilder = new StringBuilder();
    this.addTextToLegend(legendBuilder, "Normal wind(space)", config.isNormalWind());
    this.addTextToLegend(legendBuilder, "Happy wind(X)", config.isHappyWind());
    this.addTextToLegend(legendBuilder, "Debug(D)", config.isDebug());
    this.addTextToLegend(legendBuilder, "Flip image(L)", config.isFlipImage());
    this.addTextToLegend(legendBuilder, "Big balls(B)", config.isBigBalls());
    this.addTextToLegend(legendBuilder, "Freeze snowflakes(P)", config.isFreezeSnowflakes());
    this.addTextToLegend(legendBuilder, "Snowflakes tail(T)", config.isObjectsTail());
    this.addTextToLegend(legendBuilder, "Attack mode (A & 1..4)", config.isAttack());
    if (config.isAttack()) {
      this.addTextToLegend(legendBuilder, "Attack type", config.getAttackType());
      this.addTextToLegend(legendBuilder, "Attack phase", currentAttackPhase);
    }
    this.addTextToLegend(legendBuilder, "Individual movement(I)", config.isIndividualMovements());
    this.addTextToLegend(legendBuilder, "Mercedes snowflakes(M)", config.isMercedesSnowflakes());
    this.addTextToLegend(legendBuilder, "Snow level(+/-)", config.getSnowingLevel());
    this.addTextToLegend(legendBuilder, "Black holes(H)", config.isBlackHoles());
    this.addTextToLegend(legendBuilder, "Enemies(E)", config.isEnemies());
    this.addTextToLegend(legendBuilder, "Youtube(Y)", config.isYoutube());
    if (config.isYoutube()) {
      legendBuilder.append(", Next video(N)");
    }
    this.addTextToLegend(legendBuilder, "Fractals(F)", config.isFractals());
    if (config.isFractals()) {
      legendBuilder.append("\nFractals default tree(F1)\n");
      legendBuilder.append("Fractals fir tree(F2)\n");
      legendBuilder.append("Fractals default random(F3)\n");
      legendBuilder.append("Fractals default fir(F4)");
    }
    legendBuilder.append("\n-------");
    this.addTextToLegend(legendBuilder, "Your points", controller.getUserInfo().getPoints());
    legendBuilder.append("\n-------\n");
    legendBuilder.append("Fire(left button)\n");
    legendBuilder.append("Reset simulation(R)\n");
    legendBuilder.append("-------");
    this.addTextToLegend(legendBuilder, "Desired FPS", controller.getDesiredFps());
    this.addTextToLegend(legendBuilder, "Real FPS", realFPS);
    legendBuilder.append("\nExit(Q)");
    gc.drawText(legendBuilder.toString(), GuiUtils.SCREEN_BOUNDS.width - 240, 10, true);

  }

  private void addTextToLegend(StringBuilder builder, String text, boolean value) {
    if (!builder.isEmpty()) {
      builder.append("\r\n");
    }
    builder.append(text);
    builder.append(": ");
    builder.append(value ? "ON" : "OFF");
  }

  private void addTextToLegend(StringBuilder builder, String text, int value) {
    if (!builder.isEmpty()) {
      builder.append("\r\n");
    }
    builder.append(text);
    builder.append(": ");
    builder.append(value);
  }

}
