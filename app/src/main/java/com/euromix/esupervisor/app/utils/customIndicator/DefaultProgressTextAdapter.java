package com.euromix.esupervisor.app.utils.customIndicator;

public class DefaultProgressTextAdapter implements CustomProgressIndicator.ProgressTextAdapter{

    @Override
    public String formatText(double currentProgress) {
        return String.valueOf((int) currentProgress);
    }
}
