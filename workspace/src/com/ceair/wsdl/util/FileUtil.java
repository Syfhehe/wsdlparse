package com.ceair.wsdl.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FileUtil {

    private static final int DEFAULT_BUFFER_SIZE = 1024;

    public static String file2String(File file, String encoding) {

        InputStreamReader reader = null;
        StringWriter writer = new StringWriter();

        try {
            if (encoding == null || "".equals(encoding.trim())) {
                reader = new InputStreamReader(new FileInputStream(file), encoding);
            } else {
                reader = new InputStreamReader(new FileInputStream(file));
            }

            char[] buffer = new char[DEFAULT_BUFFER_SIZE];
            int n = 0;
            while (-1 != (n = reader.read(buffer))) {
                writer.write(buffer, 0, n);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return writer.toString();
    }

    public static String string2File(String res, String filePath) {
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;

        String fName = filePath.trim();
        String fNameNew = null;
        String temp[] = fName.replaceAll("\\\\", "/").split("/");
        if (temp.length > 1) {
            fName = temp[temp.length - 1];
            System.out.println("fName" + fName);
            File distFile = new File(filePath);
            if (distFile.exists()) {
                fNameNew = fName.replace(".wsdl", "_temp.wsdl");
            }
            filePath = filePath.replace(fName, fNameNew);
        }

        try {
            File distFile = new File(filePath);
            if (!distFile.getParentFile().exists())
                distFile.getParentFile().mkdirs();
            bufferedReader = new BufferedReader(new StringReader(res));
            bufferedWriter = new BufferedWriter(new FileWriter(distFile));
            char buf[] = new char[1024];
            int len;
            while ((len = bufferedReader.read(buf)) != -1) {
                bufferedWriter.write(buf, 0, len);
            }
            bufferedWriter.flush();
            bufferedReader.close();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return filePath;
    }

    /**
     * 删除文件，�?�以是�?�个文件或文件夹
     * 
     * @param fileName 待删除的文件�??
     * @return 文件删除�?功返回true,�?�则返回false
     */
    public static boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("删除文件失败：" + fileName + "文件�?存在");
            return false;
        } else {
            if (file.isFile()) {

                return deleteFile(fileName);
            } else {
                return deleteDirectory(fileName);
            }
        }
    }

    /**
     * 删除�?�个文件
     * 
     * @param fileName 被删除文件的文件�??
     * @return �?�个文件删除�?功返回true,�?�则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.isFile() && file.exists()) {
            file.delete();
            System.out.println("删除�?�个文件" + fileName + "�?功�?");
            return true;
        } else {
            System.out.println("删除�?�个文件" + fileName + "失败�?");
            return false;
        }
    }

    /**
     * 删除目录（文件夹）以�?�目录下的文件
     * 
     * @param dir 被删除目录的文件路径
     * @return 目录删除�?功返回true,�?�则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir�?以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator)) {
            dir = dir + File.separator;
        }
        File dirFile = new File(dir);
        // 如果dir对应的文件�?存在，或者�?是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            System.out.println("删除目录失败" + dir + "目录�?存在�?");
            return false;
        }
        boolean flag = true;
        // 删除文件夹下的所有文件(包括�?目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除�?文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
            // 删除�?目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }

        if (!flag) {
            System.out.println("删除目录失败");
            return false;
        }

        // 删除当�?目录
        if (dirFile.delete()) {
            System.out.println("删除目录" + dir + "�?功�?");
            return true;
        } else {
            System.out.println("删除目录" + dir + "失败�?");
            return false;
        }
    }

    public static List<String> getFileList(String strPath, ArrayList<String> filelist) {
        

        File dir = new File(strPath);
        File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                if (files[i].isDirectory()) { // 判断是文件还是文件夹
                    getFileList(files[i].getAbsolutePath(), filelist); // 获取文件绝对路径
                } else if (fileName.endsWith(".wsdl")) { // 判断文件名是否以.avi结尾
                    String strFileName = files[i].getAbsolutePath();
                    System.out.println("---" + strFileName);
                    filelist.add(strFileName);
                } else {
                    continue;
                }
            }

        }
        return filelist;
    }

}
