package ru.shift.view;

public interface ViewListener {
    void onConnectRequested(String host, String port);
    void onNameSubmitted(String name);
    void onMessageSent(String text);
    void onExitRequested();
}
