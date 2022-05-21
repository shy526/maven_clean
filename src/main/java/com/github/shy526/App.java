package com.github.shy526;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
        System.out.println("工作目录--->" + work.getAbsolutePath());
        List<File> fileList = Arrays.asList(work.listFiles());
        deleteLast(fileList, "lastUpdated");
        deleteLast(fileList, "war");
        final List<File> jarList = steamFilter(fileList, "jar").collect(Collectors.toList());
        if (jarList.size()<=1){
            return;
        }
        jarList.forEach(System.out::println);
    }

    private static void deleteLast(List<File> workList, String last) {
        steamFilter(workList, last)
                .forEach(file -> System.out.println("删除---->" + file.getAbsolutePath() + "---->>" + file.delete()));
    }

    private static Stream<File> steamFilter(List<File> workList, String last) {
        return workList.stream()
                .filter(file -> last.equals(file.getName().substring(file.getName().lastIndexOf(".") + 1)));
    }
}
