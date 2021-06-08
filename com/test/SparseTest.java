package com.test;

import java.io.*;
import java.net.*;

public class SparseTest {
  private static String Tag;

  public static void runTest(final String[] args) {
    if (args.length >= 2) {
      try {
        final String fileDir = new File(SparseTest.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath() + "\\";
        final String fileName = args[0];
        if (new File(fileDir + fileName).exists()) {
          System.out.println(SparseTest.Tag + "processing file " + new File(fileDir + fileName).getPath());
          final int BLOCK_SIZE = (args.length > 2) ? Integer.parseInt(args[2]) : 16;
          final int CLUSTER_SIZE = 512 * BLOCK_SIZE;
          int fileID = 1;
          long curCID = Integer.parseInt(args[1]);
          long fileLength = 0 L;
          System.out.println(SparseTest.Tag + "Using Block Size=" + CLUSTER_SIZE + "bytes and StartSector=" + curCID);
          if (!new File(fileDir + "out").exists()) {
            new File(fileDir + "out").mkdirs();
          }
          final FileInputStream fis = new FileInputStream(fileDir + fileName);
          final FileWriter xos = new FileWriter(fileDir + "\\out\\rawprogram_unsparse.xml");
          xos.write("<?xml version=\"1.0\" ?>\n<data>\n  <!--NOTE: This is an ** Autogenerated file **-->\n  <!--NOTE: Sector size is 512bytes-->");
          FileOutputStream fos = null;
          final byte[] buffer = new byte[CLUSTER_SIZE];
          int read = 0;
          while ((read = fis.read(buffer)) > 0) {
            boolean blockHasData = false;
            for (final byte b: buffer) {
              if (b != 0) {
                blockHasData = true;
                break;
              }
            }
            if (blockHasData) {
              if (fos == null) {
                fos = new FileOutputStream(fileDir + "\\out\\userdata_" + fileID + ".img");
                fileLength = 0 L;
                System.out.print(SparseTest.Tag + "Writing to file :" + fileDir + "\\out\\userdata_" + fileID + ".img");
              }
              fos.write(buffer);
              fileLength += BLOCK_SIZE;
            } else if (fos != null) {
              xos.append((CharSequence)("\n<program SECTOR_SIZE_IN_BYTES=\"512\" file_sector_offset=\"0\" filename=\"userdata_" + fileID + ".img\" label=\"userdata\" num_partition_sectors=\"" + fileLength + "\" physical_partition_number=\"0\" start_sector=\"" + (curCID - fileLength) + "\" />"));
              System.out.println(" " + fileLength + " bytes written successfully");
              fos.close();
              fos = null;
              ++fileID;
            }
            curCID += BLOCK_SIZE;
          }
          if (fos != null) {
            xos.append((CharSequence)("\n<program SECTOR_SIZE_IN_BYTES=\"512\" file_sector_offset=\"0\" filename=\"userdata_" + fileID + ".img\" label=\"userdata\" num_partition_sectors=\"" + fileLength + "\" physical_partition_number=\"0\" start_sector=\"" + (curCID - fileLength) + "\" />"));
            System.out.println(" " + fileLength + " bytes written successfully");
            fos.close();
          }
          xos.append((CharSequence)
            "\n</data>");
          xos.close();
          fis.close();
        } else {
          System.out.println(SparseTest.Tag + " Unable to find File with name = " + new File(fileDir + fileName).getAbsolutePath());
        }
      } catch (IOException e) {
        e.printStackTrace();
      } catch (URISyntaxException e2) {
        e2.printStackTrace();
      }
    } else {
      System.out.println(SparseTest.Tag + " needs 2 argument in order filename startSector");
    }
  }

  static {
    SparseTest.Tag = "BhawnaInTech: ";
  }
}
