package com.belova.service;

import com.belova.common.MD5;
import com.belova.entity.UsbKey;
import com.belova.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.*;

@Service
@Transactional
public class UsbKeyServiceImpl implements UsbKeyService {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private UserServiceImpl userService;
    @Value("${file.name}")
    private String fileName;

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void addUsbKey(String serialNumber, String path, User user) {
        UsbKey usbKey = new UsbKey();
        usbKey.setSerialNumber(serialNumber);
        if (path != null && !path.equals("")) {
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                 FileInputStream fileInputStream = new FileInputStream(new File(path))) {
                int len;
                byte[] data = new byte[1024];

                while ((len = fileInputStream.read(data, 0, data.length)) != -1) {
                    byteArrayOutputStream.write(data, 0, len);
                }
                byteArrayOutputStream.flush();
                usbKey.setBytes(byteArrayOutputStream.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        usbKey.setUser(user);
        entityManager.persist(usbKey);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public boolean checkKey(File file, String login) {
        User userByLogin = userService.findUserByLogin(login);

        File[] matchingFiles = file.listFiles((dir, name) -> name.equals(fileName));
        StringBuilder builder = new StringBuilder();
        if (matchingFiles != null && matchingFiles.length > 0) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(matchingFiles[0])))) {
                int i;
                while ((i = in.read()) != -1) {
                    builder.append((char) i);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        UsbKey usbKey = entityManager.find(UsbKey.class, userByLogin.getUsbKey().getId());
        return builder.toString().equals(MD5.crypt(usbKey.getSerialNumber()));
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public boolean deleteKey(User user) {
        if (user.getUsbKey()!=null) {
            UsbKey merge = entityManager.merge(user.getUsbKey());
            entityManager.remove(merge);
            return true;
        } else return false;
    }
}
