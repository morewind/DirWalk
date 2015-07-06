/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.seawind.dirwalk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Admin
 */
public class DirWalk {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        ArrayList<String> urlToCheck;
        urlToCheck = LoadUrlsFromFile();
        CheckUrls(urlToCheck);
    }

    public static void CheckUrls(ArrayList<String> urlToCheck) throws InterruptedException {
        StatusWindow statusWindow = new StatusWindow();
        statusWindow.setMaxNumDirectories(urlToCheck.size());
        statusWindow.setVisible(true);
        int checkedUrls = 0;
        int nonExistUrls = 0;
        for (String next : urlToCheck) {
            File dir = new File(next);
            boolean exist = dir.isDirectory();
            if (exist) {
            } else {
                nonExistUrls++;
                System.out.println("Directory: " + next + " exist: " + exist);
            }
            //System.out.println("Directory: " + next + " exist: " + exist);
            checkedUrls++;
            statusWindow.setCurrDirectory(checkedUrls);
            statusWindow.setCheckedUrls(checkedUrls);
            statusWindow.setNonExistUrls(nonExistUrls);
            //Thread.sleep(100);
        }
        statusWindow.setVisible(false);
        statusWindow.dispose();
    }

    private static ArrayList<String> LoadUrlsFromFile() throws FileNotFoundException {
        ArrayList<String> loadedUrls = new ArrayList<>();
        JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
        File file;
        fc.setFileFilter(filter);
        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
        } else {
            file = null;
            System.exit(0);
        }
        //Scanner urls = new Scanner(file);
        Scanner urls = new Scanner(new FileInputStream(file), "UTF-8");
        while (urls.hasNextLine()) {
            String url;
            url = urls.nextLine();
            if (url.contains("?")) {
            } else {
                loadedUrls.add(url);
            }
        }
        return loadedUrls;
    }

}
