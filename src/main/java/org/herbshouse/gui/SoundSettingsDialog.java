package org.herbshouse.gui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.herbshouse.controller.GraphicalSoundConfig;

public class SoundSettingsDialog extends Dialog {

  private static final int DIALOG_WIDTH = 350;
  private static final int DIALOG_HEIGHT = 300;
  private static final Color RED_COLOR = SWTResourceManager.getColor(new RGB(255, 150, 150));

  private GraphicalSoundConfig soundConfig;
  private Text frequency1Txt;
  private Text frequency2Txt;
  private Text durationTxt;
  private Spinner speedSpn;
  private Spinner circularSoundSpn;
  private Spinner channelsSpn;
  private Button multiRowsBtn;

  public SoundSettingsDialog(Shell parent, GraphicalSoundConfig soundConfig) {
    super(parent);
    this.soundConfig = soundConfig;
  }

  @Override
  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setText("Configure sound...");
    newShell.setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
    newShell.setLocation((GuiUtils.SCREEN_BOUNDS.width - DIALOG_WIDTH) / 2, (GuiUtils.SCREEN_BOUNDS.height - DIALOG_HEIGHT) / 2);
  }

  @Override
  protected Control createDialogArea(Composite parent) {
    parent.setLayout(new GridLayout(2, false));

    frequency1Txt = this.addTextProperty(parent, "Frequency 1(Hz):");
    frequency1Txt.addModifyListener(_ ->
        checkValid(frequency1Txt, 1, 20000, "Frequency should be a number between 1 and 20000.")
    );
    frequency1Txt.setText(String.valueOf(soundConfig.getFrequency1()));

    frequency2Txt = this.addTextProperty(parent, "Frequency 2(Hz):");
    frequency2Txt.addModifyListener(_ ->
        checkValid(frequency2Txt, 1, 20000, "Frequency should be a number between 1 and 20000.")
    );
    frequency2Txt.setText(String.valueOf(soundConfig.getFrequency2()));

    durationTxt = this.addTextProperty(parent, "Duration(Sec):");
    durationTxt.addModifyListener(_ ->
        checkValid(durationTxt, 1, 3600, "Duration should be a number between 1 and 3600.")
    );
    durationTxt.setText(String.valueOf(soundConfig.getDuration()));

    speedSpn = this.addSpinnerProperty(parent, "Speed:", 1, 10);
    speedSpn.setSelection(soundConfig.getSpeed());

    circularSoundSpn = this.addSpinnerProperty(parent, "Circular sound level:", 1, 10);
    circularSoundSpn.setSelection(soundConfig.getCircularSoundLevel());

    channelsSpn = this.addSpinnerProperty(parent, "Channels:", 1, 2);
    channelsSpn.setSelection(soundConfig.getChannels());

    multiRowsBtn = this.addButtonProperty(parent, "Multiple rows:");
    multiRowsBtn.setSelection(soundConfig.isMultiRowsRendering());

    return super.createDialogArea(parent);
  }

  private Text addTextProperty(Composite settingsShell, String labelText) {
    CLabel label = new CLabel(settingsShell, SWT.NONE);
    label.setText(labelText);
    label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

    Text text = new Text(settingsShell, SWT.NONE);
    text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    return text;
  }

  private Spinner addSpinnerProperty(Composite settingsShell, String labelText, int min, int max) {
    CLabel label = new CLabel(settingsShell, SWT.NONE);
    label.setText(labelText);
    label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

    Spinner spinner = new Spinner(settingsShell, SWT.READ_ONLY);
    spinner.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    spinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    spinner.setIncrement(1);
    spinner.setMinimum(min);
    spinner.setMaximum(max);

    return spinner;
  }

  private Button addButtonProperty(Composite settingsShell, String labelText) {
    CLabel label = new CLabel(settingsShell, SWT.NONE);
    label.setText(labelText);
    label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

    Button btn = new Button(settingsShell, SWT.CHECK);
    btn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    return btn;
  }

  @Override
  protected void okPressed() {
    soundConfig = new GraphicalSoundConfig(Integer.parseInt(frequency1Txt.getText()),
        Integer.parseInt(frequency2Txt.getText()),
        Integer.parseInt(durationTxt.getText()),
        speedSpn.getSelection(),
        multiRowsBtn.getSelection(),
        circularSoundSpn.getSelection(),
        channelsSpn.getSelection()
    );
    super.okPressed();
  }

  private boolean isValid(String text, int min, int max) {
    try {
      int value = Integer.parseInt(text);
      return value >= min && value <= max;
    } catch (RuntimeException _) {
    }
    return false;
  }

  private void checkValid(Text control, @SuppressWarnings("SameParameterValue") int min, int max, String errorMessage) {
    if (isValid(control.getText(), min, max)) {
      control.setToolTipText(null);
      control.setBackground(null);
      if (getButton(Dialog.OK) != null) {
        getButton(Dialog.OK).setEnabled(true);
      }
    } else {
      control.setBackground(RED_COLOR);
      control.setToolTipText(errorMessage);
      if (getButton(Dialog.OK) != null) {
        getButton(Dialog.OK).setEnabled(false);
      }
    }
  }

  public GraphicalSoundConfig getSoundConfig() {
    return soundConfig;
  }

}
