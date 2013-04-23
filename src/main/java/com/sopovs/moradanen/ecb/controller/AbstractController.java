package com.sopovs.moradanen.ecb.controller;


public abstract class AbstractController {
    protected final String previousUrl(String url, int showNum, int startFrom) {
        if (startFrom - showNum < 0) {
            return "";
        }
        return url + "?startFrom=" + (startFrom - showNum)
                + ((showNum != getDefaultShowNum()) ? "&showNum=" + showNum : "");
    }

    protected final String nextUrl(String url, int showNum, int startFrom, int countAll) {
        if (startFrom + showNum >= countAll) {
            return "";
        }
        return url + "?startFrom=" + (startFrom + showNum)
                + ((showNum != getDefaultShowNum()) ? "&showNum=" + showNum : "");
    }

    protected int getDefaultShowNum() {
        return 50;
    }

}