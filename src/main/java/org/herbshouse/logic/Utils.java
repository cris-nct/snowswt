
package org.herbshouse.logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.EnumSet;
import java.util.Iterator;

public class Utils {
  public static final double EPS = 0.000000000001d;
  public static double linearInterpolation(double x, double x1, double y1, double x2, double y2) {
    double value = Double.MAX_VALUE;
    if (Math.abs(x2 - x1) > EPS) {
      value = (((x - x2) * (y2 - y1)) / (x2 - x1)) + y2;
    }
    return value;
  }

  public static String getPCIdentifier() {
    StringBuffer cpuid = new StringBuffer();
    cpuid.append("CPU: ");
    cpuid.append(System.getenv("PROCESSOR_REVISION"));
    cpuid.append(",");
    cpuid.append(System.getenv("PROCESSOR_IDENTIFIER"));
    cpuid.append("|MOTHERBOARD: ");
    cpuid.append(getMotherboardSN());
    return cpuid.toString();
  }

  public static String getMotherboardSN() {
    String result = "";
    try {
      File file = File.createTempFile("realhowto", ".vbs");
      file.deleteOnExit();
      FileWriter fw = new FileWriter(file);

      String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
          + "Set colItems = objWMIService.ExecQuery _ \n"
          + "   (\"Select * from Win32_BaseBoard\") \n" + "For Each objItem in colItems \n"
          + "    Wscript.Echo objItem.SerialNumber \n"
          + "    exit for  ' do the first cpu only! \n" + "Next \n";

      fw.write(vbs);
      fw.close();
      Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
      BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
      String line;
      while ((line = input.readLine()) != null) {
        result += line;
      }
      input.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result.trim();
  }

  public static void sleep(int i) {
    try {
      Thread.sleep(i);
    } catch (Throwable e) {
      // nothing to do
    }
  }
}
