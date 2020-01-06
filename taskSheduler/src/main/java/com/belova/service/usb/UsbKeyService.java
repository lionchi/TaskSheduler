package com.belova.service.usb;

import com.belova.entity.User;

import java.io.File;
import java.io.IOException;

public interface UsbKeyService {
    void addUsbKey(String serialNumber, String path, User user);

    boolean checkKey(File file, String login);

    boolean deleteKey(User user);
}
