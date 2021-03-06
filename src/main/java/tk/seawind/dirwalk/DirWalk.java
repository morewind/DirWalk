/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tk.seawind.dirwalk;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Admin
 */
public class DirWalk {

    private static final Logger log = Logger.getLogger(DirWalk.class.getName());

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        InitLogger();
        log.log(Level.INFO, "Application started");
        ArrayList<String> urlToCheck;
        ArrayList<String> urlToSave;
        File file = GetFileName();
        log.log(Level.INFO, "Filename entered");
        urlToCheck = LoadUrlsFromFile(file);
        log.log(Level.INFO, "Urls to check loaded");
        CreateBackup(file);
        log.log(Level.INFO, "Backup created");
        urlToSave = CheckUrls(urlToCheck);
        urlToCheck.clear();
        saveUrl(urlToSave, file);
        log.log(Level.INFO, "Checked urls saved");
    }

    private static void InitLogger() {
        Handler logHandler = null;
        try {
            logHandler = new FileHandler("./dirwalk.log");
            logHandler.setFormatter(new SimpleFormatter());
        } catch (IOException | SecurityException ex) {
            log.log(Level.SEVERE, null, ex);
        }
        log.addHandler(logHandler);
    }

    public static ArrayList<String> CheckUrls(ArrayList<String> urlToCheck) {
        ArrayList<String> urlToSave = new ArrayList<>();
        final StatusWindow statusWindow = new StatusWindow();
        statusWindow.setMaxNumDirectories(urlToCheck.size());
        statusWindow.setVisible(true);

        ActionListener al = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        };
        statusWindow.setListener(al);
        int checkedUrls = 0;
        int nonExistUrls = 0;

        for (String url : urlToCheck) {
            File dir = new File(url);
            if (url.contains("!") || (url.trim().isEmpty())) {
                urlToSave.add(url);
            } else {
                if (urlIsValidDirectory(url)) {
                    boolean exist = dir.isDirectory();
                    if (exist) {
                        urlToSave.add(url);
                    } else {
                        nonExistUrls++;
                    }
                }
            }
            checkedUrls++;
            statusWindow.setCheckedUrls(checkedUrls);
            statusWindow.setNonExistUrls(nonExistUrls);
        }
        //statusWindow.setVisible(false);
        //statusWindow.dispose();
        return urlToSave;
    }

    private static ArrayList<String> LoadUrlsFromFile(File file) {
        ArrayList<String> loadedUrls = new ArrayList<>();
        try {
            Scanner urls = new Scanner(new FileInputStream(file), "UTF-8");
            while (urls.hasNextLine()) {
                String url;
                url = urls.nextLine();
                loadedUrls.add(url);
            }
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Файл " + file + " не найден или невозможно прочитать!");
            log.log(Level.SEVERE, null, ex);
            System.exit(0);
        }
        return loadedUrls;
    }

    private static void CreateBackup(File file) {
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        File newFile = new File(file + " - " + date);
        try {
            Files.copy(file.toPath(), newFile.toPath());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Произошла ошибка при создании архивной копии!");
            log.log(Level.SEVERE, null, ex);
            System.exit(0);
        }
    }

    private static void saveUrl(ArrayList<String> urlToSave, File file) {
        try (PrintWriter out = new PrintWriter(file, "UTF-8")) {
            for (String next : urlToSave) {
                out.println(next);
            }
            out.flush();
            out.close();
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Файл " + file + " не найден или невозможно прочитать!");
            log.log(Level.SEVERE, null, ex);
            System.exit(0);
        } catch (UnsupportedEncodingException ex) {
            JOptionPane.showMessageDialog(null, "Произошла ошибка при выборе кодировки файла для записи!");
            log.log(Level.SEVERE, null, ex);
            System.exit(0);
        }
    }

    private static boolean urlIsValidDirectory(String url) {
        return url.contains("\\");
    }

    private static File GetFileName() {
        JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("search.pro", "pro");
        File file;
        fc.setFileFilter(filter);
        fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
        } else {
            file = null;
            JOptionPane.showMessageDialog(null, "Загрузка файла отменена!");
            System.exit(0);
        }
        return file;
    }

}
