package com.github.shy526;

import com.sun.deploy.util.StringUtils;
import sun.rmi.runtime.Log;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        File re = new File("D:\\javaEx\\apache-maven-3.5.3\\re");
        fileList(re);
    }

    public static void fileList(File file) {
        //lastUpdated repositories xml pom  jar
        if (file == null || !file.exists() || file.isFile()) {
            return;
        }
        File[] files = file.listFiles();
        if (files == null || files.length <= 0) {
            //空文件删除
            System.out.println("空文件---->" + file.getAbsolutePath() + "--------------->" + file.delete());
            return;
        }
        List<File> fileList = Arrays.asList(files);
        List<File> collect = fileList.stream().filter(File::isDirectory).collect(Collectors.toList());
        if (collect.size() > 0) {
            collect.forEach(App::fileList);
        } else {
            workProcess(file);
        }

    }

    public static void workProcess(File work) {
        List<File> fileList = Arrays.asList(work.listFiles());
        deleteLast(fileList, "lastUpdated");
        deleteLast(fileList, "war");
        final List<File> jarList = steamFilter(fileList, "jar").collect(Collectors.toList());
        if (jarList.size() <= 1) {
            return;
        }
        fileList.stream().filter(item -> "resolver-status.properties".equals(item.getName())).findFirst()
                .ifPresent(item -> {
                    Properties properties = new Properties();
                    try {
                        properties.load(new FileReader(item));
                        long lastTime = Long.MIN_VALUE;
                        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                            String str = (String) entry.getValue();
                            if (str == null || "".equals(str)) {
                                continue;
                            }
                            lastTime = Math.min(lastTime, Long.parseLong(str));
                        }
                        for (File file : fileList) {
                            if (!file.exists()) {
                                return;
                            }
                            BasicFileAttributes attr = null;
                            attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                            long updateTime = attr.lastModifiedTime().toMillis();
                            if (updateTime > lastTime) {
                                continue;
                            }
                            deleteFile(file);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });


    }

    private static void deleteLast(List<File> workList, String last) {
        steamFilter(workList, last).forEach(App::deleteFile);
    }

    private static Stream<File> steamFilter(List<File> workList, String last) {
        return workList.stream()
                .filter(file -> last.equals(file.getName().substring(file.getName().lastIndexOf(".") + 1)));
    }

    private static void deleteFile(File file) {
        if (!file.exists()) {
            return;
        }
        System.out.println(file.getAbsolutePath() + "---->>" + file.delete());
    }
}
