package org.herbshouse.logic;

import org.eclipse.swt.widgets.Display;
import org.herbshouse.gui.GuiListener;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGenerator<T extends AbstractMovableObject>
        extends Thread implements GeneratorListener<T>, GuiListener {

    private final List<GuiListener> guiListeners = new ArrayList<>();

    @Override
    public void registerListener(GuiListener gui) {
        guiListeners.add(gui);
    }

    @Override
    public void substractAreaFromShell(int[] polygon) {
        Display.getDefault().asyncExec(() -> guiListeners.forEach(gui -> gui.substractAreaFromShell(polygon)));
    }

    @Override
    public void resetShellSurface() {
        Display.getDefault().asyncExec(() -> guiListeners.forEach(GuiListener::resetShellSurface));
    }

}
