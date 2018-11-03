package com.belova.service;

import com.belova.entity.User;

import java.io.File;

public interface UsbKeyService {
    void addUsbKey(String serialNumber, String path, User user);

    boolean checkKey(File file, String login);

    boolean deleteKey(User user);
}
