package com.ooyala.util;

import com.ooyala.model.CampaignCombination;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.TreeMap;

/**
 * Created by schinnas on 5/13/17.
 */
public class FileUtil {

    public void writeToAFile(TreeMap<Long, CampaignCombination> impressionToMaxRevenue, String fileName) {

        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(impressionToMaxRevenue);
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public TreeMap<Long, CampaignCombination> readFile(String fileName) {

        TreeMap<Long, CampaignCombination> map = null;
        try {
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            map = (TreeMap<Long, CampaignCombination>) ois.readObject();
            ois.close();
            fis.close();
            return map;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
            return null;
        }

    }
}
