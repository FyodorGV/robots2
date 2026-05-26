package gui;

import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.swing.*;
import java.beans.PropertyVetoException;

/**
 * Класс для сохранения и загрузки состояния окон в файл
 */
public class SessionManager {

    private final String path;  /** Полный путь к файлу конфигурации */

    /**
     * Создает объект для работы с файлом состояния
     */
    public SessionManager(String surname) {
        this.path = System.getProperty("user.home") + "/" + surname + "/state.cfg";
        new File(path).getParentFile().mkdirs();
    }

    /**
     * Сохраняет состояние главного и всех внутренних окон с данными в файл
     */
    public void saveAll(Saveble mainFrame, JInternalFrame[] internalFrames) {
        Map<String, String> allData = new HashMap<>();
        if (mainFrame instanceof JFrame frame) {
            Map<String, String> view = saveCommonState(frame, mainFrame, allData);
        }
        for (JInternalFrame frame : internalFrames) {
            if (frame instanceof Saveble saveableFrame) {
                Map<String, String> view = saveCommonState(frame, saveableFrame, allData);
            }
        }
        Properties props = new Properties();
        props.putAll(allData);
        try (FileOutputStream out = new FileOutputStream(path)) {
            props.store(out, "состояние окон");
        } catch (IOException e) {}
    }

    /**
     * Метод для сохранения общих всойств
     */
    private Map<String, String> saveCommonState(Component component, Saveble saveable, Map<String, String> allData){
        Map<String, String> view = new PrefixedMap(allData, saveable.getPrefix());
        view.put("x", String.valueOf(component.getX()));
        view.put("y", String.valueOf(component.getY()));
        view.put("width", String.valueOf(component.getWidth()));
        view.put("height", String.valueOf(component.getHeight()));
        if (component instanceof JInternalFrame frame) {
            view.put("isIcon", String.valueOf(frame.isIcon()));
        }else if (component instanceof JFrame frame) {
            view.put("extendedState", String.valueOf(frame.getExtendedState()));
        }
        return view;
    }

    /**
     * Метод для загрузки общих свойств
     */
    private void loadCommonState(Component component, Map<String, String> view){
        try {
            String x = view.get("x");
            String y = view.get("y");
            String w = view.get("width");
            String h = view.get("height");

            if (x != null && y != null && w != null && h != null) {
                component.setBounds(Integer.parseInt(x), Integer.parseInt(y),
                        Integer.parseInt(w), Integer.parseInt(h));
            }
        } catch (NumberFormatException e){}
    }

    /**
     * Загружает и применяет состояние главного и всех внутренних окон из файла
     */
    public void loadAll(Saveble mainFrame, JInternalFrame[] internalFrames) {
        File file = new File(path);
        if (!file.exists()) return;

        Map<String, String> allData = new HashMap<>();

        try (FileInputStream in = new FileInputStream(file)) {
            Properties props = new Properties();
            props.load(in);
            for (String key : props.stringPropertyNames()) {
                allData.put(key, props.getProperty(key));
            }
        } catch (IOException e) {
            return;
        }

        if (mainFrame instanceof JFrame frame) {
            Map<String, String> view = new PrefixedMap(allData, mainFrame.getPrefix());
            loadCommonState(frame, view);
            try {
                String state = view.get("extendedState");
                if (state != null) frame.setExtendedState(Integer.parseInt(state));
            } catch (NumberFormatException e) {}
        }
        for (JInternalFrame frame : internalFrames) {
            if (frame instanceof Saveble saveableFrame) {
                Map<String, String> view = new PrefixedMap(allData, saveableFrame.getPrefix());
                loadCommonState(frame, view);
                try {
                    String isIcon = view.get("isIcon");
                    if (isIcon != null) frame.setIcon(Boolean.parseBoolean(isIcon));
                }
                catch (NumberFormatException | PropertyVetoException e) {}
            }
        }
    }
}