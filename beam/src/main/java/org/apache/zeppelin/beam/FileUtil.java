package org.apache.zeppelin.beam;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author admin
 *
 */
public class FileUtil {
  public static void stringToFile(String content, String file) {
    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new FileWriter(file));
      writer.write(content);

    } catch (IOException e) {
    } finally {
      try {
        if (writer != null)
          writer.close();
      } catch (IOException e) {
      }
    }
  }

  public static void write(byte[] data, String file) {
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(new File(file));
      fos.write(data);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      closeStream(fos);
    }

  }

  public static String readUTF8(String file) {
    return new String(read(file));
  }

  public static byte[] read(String file) {
    byte[] out = null;
    FileInputStream fis = null;
    try {
      File f = new File(file);
      fis = new FileInputStream(f);
      out = new byte[(int) f.length()];
      int i = fis.read(out);
      if (i != out.length) {
        new RuntimeException("can not read whole file :  " + file + " its size is " + f.length()
            + " , but reading only " + i);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      closeStream(fis);
    }

    return out;
  }

  public static void closeStream(Closeable s) {
    if (s != null) {
      try {
        s.close();
      } catch (IOException e) {

        e.printStackTrace();
      }
    }
  }

  public static boolean isFileEmpty(String file) {
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(file));
      if (br.readLine() == null) {
        return true;
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      closeStream(br);
    }

    return false;
  }

  public static List<String> loadCSV(String path, boolean skipHeader) {
    FileInputStream fstream = null;
    BufferedReader br = null;
    try {
      fstream = new FileInputStream(path);
      br = new BufferedReader(new InputStreamReader(fstream));

      boolean skip = skipHeader;
      List<String> temps = new ArrayList<String>();
      String strLine;
      while ((strLine = br.readLine()) != null) {
        if (skip) {
          skip = false;
          continue;
        }
        temps.add(strLine);
      }
      return temps;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      FileUtil.closeStream(br);
    }
    return null;
  }

  public static void createPath(String pathName) throws IOException {
    Files.createDirectories(new File(pathName).toPath());
  }

  public static void copy(String file, String toFile) throws IOException {
    FileInputStream in = new FileInputStream(file);
    FileOutputStream out = new FileOutputStream(toFile);
    FileChannel src = in.getChannel();
    FileChannel dest = out.getChannel();
    dest.transferFrom(src, 0, src.size());
    FileUtil.closeStream(in);
    FileUtil.closeStream(out);
  }

  public static String copyToPath(String dbFile, String appPath) throws IOException {
    createPath(appPath);
    File originalFile = new File(dbFile);
    String imageName = originalFile.getName();
    String newName = imageName;
    String newPath = appPath + File.separator + newName;
    copy(dbFile, newPath);
    return newPath;
  }

  public static boolean pathExists(String path) {
    File f = new File((path.trim()));
    return f.exists() && f.canRead();
  }

  public static String fileExtension(String fileName) {
    String extension = "";

    int i = fileName.lastIndexOf('.');
    int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

    if (i > p) {
      extension = fileName.substring(i + 1);
    }
    return extension;
  }

}
