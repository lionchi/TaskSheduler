package com.belova.service;

import com.belova.entity.User;

import java.io.File;
import java.io.IOException;

public interface UsbKeyService {
    void addUsbKey(String path, User user) throws IOException;

    boolean checkKey(File file, String login);

    boolean deleteKey(User user);
}
