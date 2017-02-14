package com.multipong.utility;

public class GOUtility {

    private static Boolean yesOrNo = false;

    public static Boolean imTheGo() {
        return yesOrNo;
    }

    public static void setId(Boolean b) {
        GOUtility.yesOrNo = b;
    }
}
