package com.smartstudent;

import com.smartstudent.ui.common.LoginScreen;
import com.smartstudent.util.UITheme;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        UITheme.applyGlobalLook();
        SwingUtilities.invokeLater(LoginScreen::new);
    }
}
