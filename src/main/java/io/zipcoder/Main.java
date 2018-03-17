package io.zipcoder;

import org.apache.commons.io.IOUtils;

import java.util.List;


public class Main {

    public String readRawDataToString() throws Exception{
        ClassLoader classLoader = getClass().getClassLoader();
        String result = IOUtils.toString(classLoader.getResourceAsStream("RawData.txt"));
        return result;
    }

    public static void main(String[] args) throws Exception{
        String output = (new Main()).readRawDataToString();

        ItemParser itemParser = new ItemParser();
        itemParser.parseStringIntoItemArray(output);

        System.out.println(itemParser.formatData());
    }
}