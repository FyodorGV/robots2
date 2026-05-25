package gui;

/**
 * Интерефейс для окон, сохраняющих свое состояние
 */
public interface Saveble {
    /**
     * Возвращает префикс для ключей окна
     */
    String getPrefix();
}