package com.example.burncalories.view;

public class CardItem {
    private String viewText;
    private int mTextResource;
    private int mTitleResource;
    private float complete;
    private float current;

    public CardItem(String viewText,int title, int text) {
        this.viewText = viewText;
        mTitleResource = title;
        mTextResource = text;
        complete = 2.0f;
        current = 1.0f;
    }

    public int getText() {
        return mTextResource;
    }

    public int getTitle() {
        return mTitleResource;
    }

    public String getViewText() {
        return viewText;
    }

    public float getComplete() {
        return complete;
    }

    public void setComplete(float complete) {
        this.complete = complete;
    }

    public float getCurrent() {
        return current;
    }

    public void setCurrent(float current) {
        this.current = current;
    }
}
