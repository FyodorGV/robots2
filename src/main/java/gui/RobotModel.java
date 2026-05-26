package gui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Модель робота. Состояние, управление
 */
public class RobotModel {

    private double x = 100;
    private double y = 100;
    private double direction = 0;
    private double targetX = 150;
    private double targetY = 100;

    private static final double MAX_VELOCITY = 0.1;
    private static final double MAX_ANGULAR_VELOCITY = 0.01;

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public final String PROP_X = "x";
    public final String PROP_Y = "y";
    public final String PROP_DIRECTION = "direction";
    public final String PROP_TARGET_X = "targetX";
    public final String PROP_TARGET_Y = "targetY";

    public double getX() { return x; }
    public double getY() { return y; }
    public double getDirection() { return direction; }
    public double getTargetX() { return targetX; }
    public double getTargetY() { return targetY; }

    /**
     * Угол до цели в радианах
     */
    public double getAngleToTarget() {
        double dx = targetX - x;
        double dy = targetY - y;
        return Math.atan2(dy, dx);
    }

    /**
     * Разница углов
     */
    public double getAngleDiff() {
        double angleToTarget = getAngleToTarget();
        double diff = angleToTarget - direction;
        return normalizeAngleDifference(diff);
    }


    /**
     * Цель с оповещением
     */
    public void setTarget(double x, double y) {
        double oldX = this.targetX;
        double oldY = this.targetY;
        this.targetX = x;
        this.targetY = y;
        pcs.firePropertyChange(PROP_TARGET_X, oldX, x);
        pcs.firePropertyChange(PROP_TARGET_Y, oldY, y);
    }

    /**
     *  Регистрация слушателей
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }


    /**
     * Обновление состояния 10 мс
     */
    public void update(int durationMs) {
        double oldX = x;
        double oldY = y;
        double oldDir = direction;
        double distance = distanceToTarget();
        if (distance < 0.5) {
            return;
        }

        double velocity = MAX_VELOCITY;
        double angleToTarget = getAngleToTarget();
        double angularVelocity = calculateAngularVelocity(angleToTarget, direction);

        if (angularVelocity != 0 && isTargetInsideTurnCircle(velocity, angularVelocity)) {
            angularVelocity = 0;
        }

        moveRobot(velocity, angularVelocity, durationMs);

        if (oldX != x || oldY != y) {
            pcs.firePropertyChange(PROP_X, oldX, x);
            pcs.firePropertyChange(PROP_Y, oldY, y);
        }
        if (oldDir != direction) {
            pcs.firePropertyChange(PROP_DIRECTION, oldDir, direction);
        }
    }

    /**
     * Угловая скорость с кратчайшим поворотом
     */
    private double calculateAngularVelocity(double targetAngle, double currentAngle) {
        double angleDiff = targetAngle - currentAngle;

        angleDiff = Math.atan2(Math.sin(angleDiff), Math.cos(angleDiff));

        if (Math.abs(angleDiff) < 0.01) {
            return 0.0;
        }

        if (angleDiff > 0) {
            return MAX_ANGULAR_VELOCITY;
        } else {
            return -MAX_ANGULAR_VELOCITY;
        }
    }

    /**
     * Разница между радиусом и расстоянием до цели
     */
    private boolean isTargetInsideTurnCircle(double velocity, double angularVelocity) {
        if (Math.abs(angularVelocity) < 1e-10) return false;

        double radius = Math.abs(velocity / angularVelocity);

        double centerX1 = x - radius * Math.sin(direction);
        double centerY1 = y + radius * Math.cos(direction);

        double centerX2 = x + radius * Math.sin(direction);
        double centerY2 = y - radius * Math.cos(direction);

        double distSq1 = Math.pow(targetX - centerX1, 2) + Math.pow(targetY - centerY1, 2);
        double distSq2 = Math.pow(targetX - centerX2, 2) + Math.pow(targetY - centerY2, 2);

        double radiusSq = radius * radius;

        return (distSq1 < radiusSq) || (distSq2 < radiusSq);
    }

    /**
     * Перемещение робота с линейной и угловой скоростями
     */
    private void moveRobot(double velocity, double angularVelocity, double duration) {
        velocity = limits(velocity, 0, MAX_VELOCITY);
        angularVelocity = limits(angularVelocity, -MAX_ANGULAR_VELOCITY, MAX_ANGULAR_VELOCITY);

        double newX = x + velocity / angularVelocity *
                (Math.sin(direction + angularVelocity * duration) - Math.sin(direction));


        if (!Double.isFinite(newX)) {
            newX = x + velocity * duration * Math.cos(direction);
        }

        double newY = y - velocity / angularVelocity *
                (Math.cos(direction + angularVelocity * duration) - Math.cos(direction));
        if (!Double.isFinite(newY)) {
            newY = y + velocity * duration * Math.sin(direction);
        }

        x = newX;
        y = newY;
        double newDirection = normalizeRadians(direction + angularVelocity * duration);
        direction = newDirection;
    }

    /**
     * Расстояние
     */
    private double distanceToTarget() {
        double dx = targetX - x;
        double dy = targetY - y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Нормализация [0, 2п)
     */
    private double normalizeRadians(double angle) {
        angle = angle % (2 * Math.PI);
        if (angle < 0) {
            angle += 2 * Math.PI;
        }
        return angle;
    }

    /**
     * Нормализация [-П, П]
     */
    private double normalizeAngleDifference(double angle) {
        while (angle > Math.PI) {
            angle -= 2 * Math.PI;
        }
        while (angle <= -Math.PI) {
            angle += 2 * Math.PI;
        }
        return angle;
    }

    /**
     * Ограничение значения
     */
    private double limits(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
}