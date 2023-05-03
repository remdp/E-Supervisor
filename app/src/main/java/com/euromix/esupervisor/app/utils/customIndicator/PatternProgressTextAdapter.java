package com.euromix.esupervisor.app.utils.customIndicator;

import androidx.annotation.NonNull;

public class PatternProgressTextAdapter implements CustomProgressIndicator.ProgressTextAdapter{

    private String pattern;

    public PatternProgressTextAdapter(String pattern) {
        this.pattern = pattern;
    }

    @NonNull
    @Override
    public String formatText(double currentProgress) {
        return String.format(pattern, currentProgress);
    }

}
