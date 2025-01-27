package org.herbshouse.logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Utils {

  public static final double EPS = 1e-12;
  public static final int BIG_PRECISION = 12;

  private Utils() {
  }

  public static double linearInterpolation(double x, double x1, double y1, double x2, double y2) {
    if (Math.abs(x2 - x1) <= EPS) {
      return Double.MAX_VALUE;
    }
    return (((x - x2) * (y2 - y1)) / (x2 - x1)) + y2;
  }

  public static double[] generateCircle(Point2D location, double radius, int nrSegments,
      double beginAngle) {
    double angleIncrement = (2 * Math.PI) / nrSegments;
    List<Point2D> points = new ArrayList<>(nrSegments);

    for (int i = 0; i < nrSegments; i++) {
      double angle = beginAngle + i * angleIncrement;
      points.add(moveToDirection(location, radius, angle));
    }

    double[] area = new double[points.size() * 2];
    for (int i = 0; i < points.size(); i++) {
      area[2 * i] = points.get(i).x;
      area[2 * i + 1] = points.get(i).y;
    }
    return area;
  }

  public static String getPCIdentifier() throws IOException {
    return String.format("CPU: %s,%s|MOTHERBOARD: %s",
        System.getenv("PROCESSOR_REVISION"),
        System.getenv("PROCESSOR_IDENTIFIER"),
        getMotherboardSN());
  }

  public static double distance(double x1, double y1, double x2, double y2) {
    return formatNDecimals(Math.hypot(x1 - x2, y1 - y2), BIG_PRECISION);
  }

  public static double distance(Point2D a, Point2D b) {
    return distance(a.x, a.y, b.x, b.y);
  }

  public static Point2D moveToDirection(Point2D loc, double distance, double angle) {
    return moveToDirection(loc.x, loc.y, distance, angle);
  }

  private static Point2D moveToDirection(double x1, double y1, double distance, double angle) {
    double x = x1 + distance * Math.cos(angle);
    double y = y1 + distance * Math.sin(angle);
    return new Point2D(x, y);
  }

  public static Point2D pointRotation(Point2D fixPoint, Point2D point, double angle) {
    double distance = distance(fixPoint, point);
    double currentAngle = angleOfLine(fixPoint, point);
    double newAngle = normAngle(currentAngle - angle);
    return moveToDirection(fixPoint, distance, newAngle);
  }

  public static double normAngle(double angle) {
    return Math.toRadians((Math.toDegrees(angle) + 360) % 360);
  }

  public static double formatNDecimals(double number, int precision) {
    double scale = Math.pow(10, precision);
    return Math.round(number * scale) / scale;
  }

  public static boolean equalsEPS(double a, double b) {
    return Math.abs(a - b) <= EPS;
  }

  public static String getMotherboardSN() throws IOException {
    File file = File.createTempFile("realhowto", ".vbs");
    file.deleteOnExit();
    String vbs = """
         Set objWMIService = GetObject("winmgmts:\\\\.\\root\\cimv2")
         Set colItems = objWMIService.ExecQuery _\s
            ("Select * from Win32_BaseBoard")\s
         For Each objItem in colItems\s
             Wscript.Echo objItem.SerialNumber\s
             exit for  ' do the first cpu only!\s
         Next\s
        \s""";

    try (FileWriter fw = new FileWriter(file)) {
      fw.write(vbs);
    }

    ProcessBuilder processBuilder = new ProcessBuilder("cscript", "//NoLogo", file.getPath());
    processBuilder.redirectErrorStream(true);
    Process process = processBuilder.start();

    StringBuilder result = new StringBuilder();
    try (BufferedReader input = new BufferedReader(
        new InputStreamReader(process.getInputStream()))) {
      String line;
      while ((line = input.readLine()) != null) {
        result.append(line);
      }
    }
    return result.toString().trim();
  }

  public static double angleOfLine(double x1, double y1, double x2, double y2) {
    double path = getPath(x1, y1, x2, y2);
    if (path == Double.NEGATIVE_INFINITY) {
      return Math.toRadians(270);
    } else if (path == Double.POSITIVE_INFINITY) {
      return Math.toRadians(90);
    } else {
      double alpha = Math.atan(path);
      if (x1 > x2) {
        alpha += Math.PI;
      }
      return normAngle(alpha);
    }
  }

  public static double getPath(double x1, double y1, double x2, double y2) {
    if (Math.abs(x2 - x1) >= EPS) {
      return (y2 - y1) / (x2 - x1);
    }
    return (y2 > y1) ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
  }

  public static double angleOfLine(Point2D p1, Point2D p2) {
    return angleOfLine(p1.x, p1.y, p2.x, p2.y);
  }

  public static void sleep(int milliseconds) {
    try {
      Thread.sleep(milliseconds);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt(); // restore interrupted status
    }
  }

  public static void sleep(int millis, int nanos) {
    try {
      Thread.sleep(millis, nanos);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt(); // restore interrupted status
    }
  }

  public static boolean isColliding(AbstractMovableObject obj1, AbstractMovableObject obj2) {
    double sumSize = (obj1.getSize() + obj2.getSize()) / 2.0 + 2;
    return distance(obj1.getLocation(), obj2.getLocation()) < sumSize;
  }

}