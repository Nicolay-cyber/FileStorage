package com.dnn.clientapp;

import io.netty.channel.ChannelHandlerContext;

import javax.swing.*;

public class LoginWindow {
    public LoginWindow(ChannelHandlerContext ctx) {
        JTextField loginField = new JTextField();
        JTextField passwordField = new JPasswordField();
        Object[] message = {
                "Login:", loginField,
                "Password:", passwordField,
        };
        UIManager.put("OptionPane.noButtonText", "Register");
        UIManager.put("OptionPane.yesButtonText", "Log in");
        int option = JOptionPane.showConfirmDialog(null, message, "Login", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            if (loginField.getText().equals("") || passwordField.getText().equals("")) {
                new LoginWindow(ctx);
            }
            ctx.writeAndFlush(new Request(
                            "Check user",
                            loginField.getText() + " " + passwordField.getText()
                    )
            );
        } else if (option == JOptionPane.NO_OPTION) {
            if (loginField.getText().equals("") || passwordField.getText().equals("")) {
                new LoginWindow(ctx);
            }
            ctx.writeAndFlush(new Request(
                            "Register user",
                            loginField.getText() + " " + passwordField.getText()
                    )
            );
        } else {
            System.exit(1);
        }


    }
}
