package gui;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Контроллер робота.
 * Обрабатывает действия пользователя и управляет моделью.
 */
public class Controller implements MouseListener {

    private final RobotModel model;
    private final GameVisualizer visualizer;
    private final Timer timer;

    /**
     * Создаём контроллер для управления роботом
     */
    public Controller(RobotModel model, GameVisualizer visualizer) {
        this.model = model;
        this.visualizer = visualizer;

        // Передаём себя как слушателя мыши в представление
        visualizer.setMouseListener(this);

        // Запускаем таймер для обновления модели
        timer = new Timer("robot-updater", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                model.update(10);
            }
        }, 0, 10);
    }
    /**
     * Обработка клика мыши — установка новой цели
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        model.setTarget(e.getX(), e.getY());
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

}
