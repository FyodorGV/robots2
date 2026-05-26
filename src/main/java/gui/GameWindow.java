package gui;

import javax.swing.*;
import java.awt.*;

/**
 * Окно игры с роботом. Содержит визуализатор и связывается с контроллером.
 */
public class GameWindow extends JInternalFrame implements Saveble
{
    private final GameVisualizer gameVisualizer;
    private final Controller controller;
    private final RobotModel model;

    public GameWindow() 
    {
        super("Игровое поле", true, true, true, true);
        model = new RobotModel();
        gameVisualizer = new GameVisualizer(model);
        controller = new Controller(model, gameVisualizer);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(gameVisualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    public RobotModel getModel() {
        return model;
    }

    @Override
    public String getPrefix() {
        return "game";
    }
}
