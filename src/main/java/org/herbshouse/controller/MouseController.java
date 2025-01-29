package org.herbshouse.controller;

public interface MouseController {

  void mouseMove(int x, int y);

  void mouseDown(int button, int x, int y);

  void mouseScrolled(int count);

}
