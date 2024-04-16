package com.group.eBookManagementSystem.GUI;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
@Getter
@Setter
public class SingletonWindow extends JFrame {

    private static SingletonWindow singletonWindow = null;
    // define a constructor
    private SingletonWindow() {

    }

    // use synchronized to ensure thread safety
    // create a new instance of the class if one doesn't exist
    // otherwise return the existing instance
    public static synchronized SingletonWindow getInstance(){
        if(singletonWindow == null){
            singletonWindow = new SingletonWindow();
        }
        return singletonWindow;
    }

}
