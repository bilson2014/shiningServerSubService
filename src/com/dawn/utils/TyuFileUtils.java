 package com.dawn.utils;
 
 import java.awt.image.BufferedImage;
 import java.io.BufferedInputStream;
 import java.io.BufferedOutputStream;
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.util.ArrayList;
 import java.util.zip.ZipEntry;
 import java.util.zip.ZipInputStream;
 import javax.imageio.ImageIO;
 import net.coobird.thumbnailator.Thumbnails;
 import net.coobird.thumbnailator.Thumbnails.Builder;
 
 public class TyuFileUtils
 {
   public static final String ERR_TAG = "FileOption.java";
   public static String local_diary_dir = "local_diary";
   public static String net_diary_dir = "net_diary";
 
   public static File createImageShortCut(File inFile) {
     int base_width = 250;
     if (inFile.exists()) {
       File outFile = new File(inFile.getParentFile().getAbsolutePath(), 
         "sc25_" + inFile.getName());
       try {
         BufferedImage originalImage = ImageIO.read(inFile);
         float w = originalImage.getWidth();
 
         float sc = 1.0F;
         if (w > 250.0F) {
           sc = 250.0F / w;
         }
         Thumbnails.of(new File[] { inFile }).scale(sc).toFile(outFile);
 
         return outFile;
       }
       catch (IOException e) {
         e.printStackTrace();
       }
     }
 
     return null;
   }
 
   public static void copyFile(File sourceFile, File targetFile)
     throws IOException
   {
     FileInputStream input = new FileInputStream(sourceFile);
     BufferedInputStream inBuff = new BufferedInputStream(input);
 
     FileOutputStream output = new FileOutputStream(targetFile);
     BufferedOutputStream outBuff = new BufferedOutputStream(output);
 
     byte[] b = new byte[5120];
     int len;
     while ((len = inBuff.read(b)) != -1)
     {
       //int len;
       outBuff.write(b, 0, len);
     }
 
     outBuff.flush();
 
     inBuff.close();
     outBuff.close();
     output.close();
     input.close();
   }
 
   public static void copyFile(String sFile, String tFile)
     throws IOException
   {
     File sourceFile = new File(sFile);
     File targetFile = new File(tFile);
 
     copyFile(sourceFile, targetFile);
   }
 
   public static void copyDirectiory(String sourceDir, String targetDir)
     throws IOException
   {
     new File(targetDir).mkdirs();
 
     File[] file = new File(sourceDir).listFiles();
 
     for (int i = 0; i < file.length; ++i) {
       if (file[i].isFile())
       {
         File sourceFile = file[i];
 
         File targetFile = new File(
           new File(targetDir).getAbsolutePath() + File.separator + 
           file[i].getName());
         copyFile(sourceFile, targetFile);
       }
       if (!file[i].isDirectory())
         continue;
       String dir1 = sourceDir + "/" + file[i].getName();
 
       String dir2 = targetDir + "/" + file[i].getName();
       copyDirectiory(dir1, dir2);
     }
   }
 
   public static void delFile(String strFileName)
   {
     File myFile = new File(strFileName);
     if (myFile.exists())
       myFile.delete();
   }
 
   public static boolean isExist(String strFileName)
   {
     File myFile = new File(strFileName);
     return myFile.exists();
   }
 
   public static void getFiles(String folder, String ext, ArrayList<String> files)
   {
     File[] file = new File(folder).listFiles();
     for (File file2 : file)
       if (file2.getName().endsWith(ext))
         files.add(file2.getName());
   }
 
   public static void deleteDirectory(String DirectoryName)
   {
     File file = new File(DirectoryName);
 
     if (file.isDirectory()) {
       File[] files = file.listFiles();
       for (int i = 0; (i < files.length) && (files != null); ++i)
       {
         files[i].delete();
       }
       file.delete();
     }
   }
 
   public static void deleteDirectoryContent(String DirectoryName)
   {
     File file = new File(DirectoryName);
 
     if (file.isDirectory()) {
       File[] files = file.listFiles();
       for (int i = 0; (i < files.length) && (files != null); ++i)
         files[i].delete();
     }
   }
 
   public static byte[] readFile(String aFileName)
   {
     File file = new File(aFileName);
     int file_len = (int)file.length();
 
     byte[] bs = new byte[file_len + 100];
     int read_len = 0;
     try {
       BufferedInputStream bis = new BufferedInputStream(
         new FileInputStream(file));
       read_len = bis.read(bs);
       bis.close();
       bis = null;
     }
     catch (IOException e) {
       e.printStackTrace();
     }
     if (read_len == file_len) {
       return bs;
     }
     bs = (byte[])null;
     return bs;
   }
 
   public static String readDataFromZip(File aFile)
   {
     String res = null;
     ByteArrayOutputStream baos = new ByteArrayOutputStream();
 
     byte[] buffer = new byte[1024];
     int len = 0;
     try {
       FileInputStream input = new FileInputStream(aFile);
       while ((len = input.read(buffer)) >= 0)
       {
         baos.write(buffer, 0, len);
       }
 
       ZipInputStream zipin = new ZipInputStream(
         new ByteArrayInputStream(baos.toByteArray()));
       baos.close();
       ZipEntry entry;
       if ((entry = zipin.getNextEntry()) != null)
       {
         baos = new ByteArrayOutputStream();
         while ((len = zipin.read(buffer)) >= 0) {
           baos.write(buffer, 0, len);
         }
         res = new String(baos.toByteArray(), 0, baos.size(), 
           "GBK");
 
         baos.close();
       }
 
       zipin.close();
       input.close();
     }
     catch (Exception e) {
       e.printStackTrace();
     }
     return res;
   }
 
   public static void dumpToFile(String aFileName, String data)
   {
     try {
       if ((data == null) || (aFileName == null))
         return;
       File file = new File(aFileName);
       if (file.exists())
         file.delete();
       else {
         file.getParentFile().mkdirs();
       }
 
       OutputStream outStream = new FileOutputStream(aFileName);
 
       BufferedOutputStream bout = null;
 
       bout = new BufferedOutputStream(outStream);
 
       byte[] b = data.getBytes();
 
       int len = b.length;
 
       bout.write(b, 0, len);
 
       bout.close();
     }
     catch (Exception e)
     {
       e.printStackTrace();
     }
   }
 
   public static String loadFromFile(String aFileName)
   {
     String res = null;
     File file = new File(aFileName);
     if (!file.exists())
       return null;
     try
     {
       InputStream input = new FileInputStream(aFileName);
       ByteArrayOutputStream baos = new ByteArrayOutputStream();
       byte[] b = new byte[1024];
 
       int len = input.read(b);
 
       while (len != -1) {
         baos.write(b, 0, len);
         len = input.read(b);
       }
       res = baos.toString("utf-8");
       baos.close();
     }
     catch (Exception e) {
       e.printStackTrace();
     }
 
     return res;
   }
 }
