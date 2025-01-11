package org.herbshouse.logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Utils {

  public static final double EPS = 0.000000000001d;

  public static final double PI = 3.1415926535897932384626433832795d;

  public static final int NR_DECIMALS_FORMAT_DEGREES = 10;

  public static final int BIG_PRECISION = 12;

  public static double linearInterpolation(double x, double x1, double y1, double x2, double y2) {
    double value = Double.MAX_VALUE;
    if (Math.abs(x2 - x1) > EPS) {
      value = (((x - x2) * (y2 - y1)) / (x2 - x1)) + y2;
    }
    return value;
  }

  public static double[] generateCircle(Point2D location,
      double radius,
      int nrSegments,
      double beginAngle
  ) {
    List<Point2D> points = new ArrayList<>();
    double angleIncrement = (2 * Math.PI) / nrSegments;
    double angleTemp = beginAngle;
    int safetyCounter = 0;
    do {
      Point2D point = moveToDirection(location, radius, angleTemp);
      angleTemp = normAngle(angleTemp + angleIncrement);
      points.add(point);
      safetyCounter++;
    } while (safetyCounter < nrSegments);

    double[] area = new double[points.size() * 2];
    int i = 0;
    for (Point2D p : points) {
      area[i++] = p.x;
      area[i++] = p.y;
    }
    return area;
  }

  public static String getPCIdentifier() throws IOException {
    @SuppressWarnings("StringBufferReplaceableByString")
    StringBuilder cpuid = new StringBuilder();
    cpuid.append("CPU: ");
    cpuid.append(System.getenv("PROCESSOR_REVISION"));
    cpuid.append(",");
    cpuid.append(System.getenv("PROCESSOR_IDENTIFIER"));
    cpuid.append("|MOTHERBOARD: ");
    cpuid.append(getMotherboardSN());
    return cpuid.toString();
  }

  public static double distance(double x1, double y1, double x2, double y2) {
    return formatNDecimals(Math.sqrt(((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2))),
        BIG_PRECISION);
  }

  public static double distance(Point2D a, Point2D b) {
    return distance(a.x, a.y, b.x, b.y);
  }

  public static Point2D moveToDirection(Point2D loc, double distance, double angle) {
    return moveToDirection(loc.x, loc.y, distance, angle);
  }

  private static Point2D moveToDirection(double x1, double y1, double distance, double angle) {
    Point2D p = new Point2D(0, 0);
    double ang = angle;
    double degAlpha = Math.toDegrees(normAngle(angle));

    if ((degAlpha >= 0) && (degAlpha < 90)) {
      p.x = x1 + (distance * Math.cos(ang));
      p.y = y1 + (distance * Math.sin(ang));
    } else if ((degAlpha > 90) && (degAlpha < 180)) {
      ang = Math.toRadians(180 - degAlpha);
      p.x = x1 - (distance * Math.cos(ang));
      p.y = y1 + (distance * Math.sin(ang));
    } else if ((degAlpha > 180) && (degAlpha < 270)) {
      ang = Math.toRadians(270 - degAlpha);
      p.x = x1 - (distance * Math.sin(ang));
      p.y = y1 - (distance * Math.cos(ang));
    } else if ((degAlpha > 270) && (degAlpha < 360)) {
      ang = Math.toRadians(360 - degAlpha);
      p.x = x1 + (distance * Math.cos(ang));
      p.y = y1 - (distance * Math.sin(ang));
    }

    if (equalsEPS(degAlpha, 90)) {
      p.x = x1;
      p.y = y1 + distance;
    } else if (equalsEPS(degAlpha, 180)) {
      p.x = x1 - distance;
      p.y = y1;
    } else if (equalsEPS(degAlpha, 270)) {
      p.x = x1;
      p.y = y1 - distance;
    } else if (equalsEPS(degAlpha, 0)) {
      p.x = x1 + distance;
      p.y = y1;
    }
    return p;
  }

  public static Point2D pointRotation(Point2D fixPoint, Point2D point, double angle) {
    double distance = distance(fixPoint.x, fixPoint.y, point.x, point.y);
    double angCurrent = angleOfPath(fixPoint.x, fixPoint.y, point.x, point.y);
    angCurrent = normAngle(angCurrent - normAngle(angle));
    return moveToDirection(fixPoint.x, fixPoint.y, distance, angCurrent);
  }

  public static double normAngle(double angle) {
    double degAngle = Math.toDegrees(angle);
    degAngle = formatNDecimals(degAngle, NR_DECIMALS_FORMAT_DEGREES);
    if (Math.abs(degAngle) >= (360 - EPS)) {
      degAngle = degAngle % 360;
      if (degAngle < 0) {
        degAngle = degAngle + ((((double) ((int) degAngle) / 360) + 1) * 360);
      }
    } else if (degAngle < 0) {
      degAngle = degAngle + ((((double) ((int) degAngle) / 360) + 1) * 360);
    }
    return Math.toRadians(degAngle);
  }

  public static double formatNDecimals(double nr, int precision) {
    long doublePrecision = (long) Math.pow(10, precision);
    long n = Math.round(nr * doublePrecision * 10.0d) / 10;
    return (double) n / (double) doublePrecision;
  }

  public static boolean equalsEPS(double a, double b) {
    return Math.abs(a - b) <= EPS;
  }

  public static String getMotherboardSN() throws IOException {
    StringBuilder result = new StringBuilder();
    File file = File.createTempFile("realhowto", ".vbs");
    file.deleteOnExit();
    FileWriter fw = new FileWriter(file);
    String vbs = """
        Set objWMIService = GetObject("winmgmts:\\\\.\\root\\cimv2")
        Set colItems = objWMIService.ExecQuery _\s
           ("Select * from Win32_BaseBoard")\s
        For Each objItem in colItems\s
            Wscript.Echo objItem.SerialNumber\s
            exit for  ' do the first cpu only!\s
        Next\s
        """;

    fw.write(vbs);
    fw.close();

    ProcessBuilder processBuilder = new ProcessBuilder("cscript //NoLogo", file.getPath());
    processBuilder.redirectErrorStream(true);
    Process process = processBuilder.start();
    try (BufferedReader input = new BufferedReader(
        new InputStreamReader(process.getInputStream()))) {
      String line;
      while ((line = input.readLine()) != null) {
        result.append(line);
      }
    }
    return result.toString().trim();
  }

  public static double angleOfPath(double x1, double y1, double x2, double y2) {
    double path = getPath(x1, y1, x2, y2);
    double alpha;
    if ((path == Double.NEGATIVE_INFINITY) || (path == Double.POSITIVE_INFINITY)) {
      if (path == Double.POSITIVE_INFINITY) {
        alpha = Math.toRadians(90);
      } else {
        alpha = Math.toRadians(270);
      }
    } else {
      alpha = Math.atan(path);
      if (x1 > x2) {
        alpha += PI;
      }
    }
    alpha = normAngle(alpha);
    return alpha;
  }

  public static double getPath(double x1, double y1, double x2, double y2) {
    double ret;
    if (Math.abs(x2 - x1) >= EPS) {
      ret = (y2 - y1) / (x2 - x1);
    } else {
      if (y2 > y1) {
        ret = Double.POSITIVE_INFINITY;
      } else {
        ret = Double.NEGATIVE_INFINITY;
      }
    }
    return ret;
  }

  public static double angleOfPath(Point2D p1, Point2D p2) {
    return angleOfPath(p1.x, p1.y, p2.x, p2.y);
  }

  public static void sleep(int i) {
    try {
      Thread.sleep(i);
    } catch (Throwable e) {
      // nothing to do
    }
  }

  public static boolean isColliding(AbstractMovableObject obj1, AbstractMovableObject obj2) {
    int sumSize = (obj1.getSize() + obj2.getSize()) / 2 + 2;
    return Utils.distance(obj1.getLocation(), obj2.getLocation()) < sumSize;
  }

}
