package org.herbshouse.controller;

import java.util.List;
import org.herbshouse.logic.AbstractMovableObject;
import org.herbshouse.logic.GeneratorListener;
import org.herbshouse.logic.UserInfo;

public interface MainController {

  boolean canStart();

  void flipImage();

  void pause();

  UserInfo getUserInfo();

  FlagsConfiguration getFlagsConfiguration();

  void registerListener(GeneratorListener<?> listener);

  List<GeneratorListener<? extends AbstractMovableObject>> getListeners();

  int getDesiredFps();

  void reset();

  void shutdown();

}
