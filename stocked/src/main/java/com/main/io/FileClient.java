package com.main.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileClient {

    public static List<String> readTextFile(String fileDir) {
        List<String> symbols = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new java.io.FileReader(fileDir))) {
            String str;
            while ((str = in.readLine()) != null) symbols.add(str);
        } catch (IOException e) {
            System.out.println("IO error reading file");
        }
        return symbols;
    }

}
