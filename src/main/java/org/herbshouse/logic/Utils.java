
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


  /**
   * Map type from a string.
   * 
   * By example:
   * 
   * <code>
   *    GraphicalElementTypes type = mapTypeFromString("STACK", GraphicalElementTypes.class);
   *    
   *    --> In this case  type will be GraphicalElementTypes.STACK
   *    
   * </code>
   * 
   * @param <T>
   * @param str
   * @param enumType
   * @return <T> or null
   */
  public static <T extends Enum<T>> T mapTypeFromString(String str, Class<T> enumType) {
    try {
      T ret = Enum.valueOf(enumType, str);
      return ret;
    } catch (Throwable e) {
      // Logger.error(0, "", e);
    }
    return null;
  }

  /**
   * E.g use:
   * 
   * Vehicles vhType = getEnumerationFromEnum(1, Vehicles.class);
   * 
   * vhType will be Vehicles.RS
   * 
   * 
   * @param <T>
   *          an enumeration
   * @param index
   *          a number from 0 to number of enumerations from enumType - 1
   * @param enumType
   *          an enum type
   * @return the enumeration with index <index> from enum enumType
   */
  public static <T extends Enum<T>> T getEnumerationFromEnum(int index, Class<T> enumType) {
    try {
      EnumSet<T> list = EnumSet.allOf(enumType);
      Iterator<T> iterator = list.iterator();

      int counter = 0;
      while (iterator.hasNext()) {
        T obj = iterator.next();
        if (counter == index) {
          return obj;
        }
        counter++;
      }

      return null;
    } catch (Throwable e) {
      // Logger.error(0, "", e);
    }
    return null;
  }

  /**
   * @param val
   * @return true if val can be converted to double
   */
  public static boolean isDouble(String val) {
    try {
      Double.parseDouble(val);
      return true;
    } catch (Throwable e) {
      return false;
    }
  }

  /**
   * @param val
   * @return true if the value can be converted to long
   */
  public static boolean isLong(String val) {
    try {
      Long.parseLong(val);
      return true;
    } catch (Throwable e) {
      return false;
    }
  }

  public static String getPCIdentifier() {
    StringBuffer cpuChecksum = new StringBuffer();
    cpuChecksum.append("CPU: ");
    cpuChecksum.append(System.getenv("PROCESSOR_REVISION"));
    cpuChecksum.append(",");
    cpuChecksum.append(System.getenv("PROCESSOR_IDENTIFIER"));
    cpuChecksum.append("|MOTHERBOARD: ");
    cpuChecksum.append(getMotherboardSN());
    return cpuChecksum.toString();
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
