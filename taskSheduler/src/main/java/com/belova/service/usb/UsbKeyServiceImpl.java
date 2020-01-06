package com.belova.service.usb;

import com.belova.common.supporting.MD5;
import com.belova.entity.UsbKey;
import com.belova.entity.User;
import com.belova.service.user.UserServiceImpl;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.*;
import java.util.ArrayList;

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
    public void addUsbKey(String serialNumber, String path, User user){
        UsbKey usbKey = new UsbKey();
        String sequence_key = MD5.crypt(generatedKey(serialNumber));
        usbKey.setKey(sequence_key);
        String fileData = sequence_key;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path + "//"+"configuration");
            fos.write(fileData.getBytes());
            fos.flush();
            fos.close();
            String command = "C:\\WINDOWS\\System32\\ATTRIB.EXE +H +R "+path + "//"+"configuration";
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (path != null && !path.equals("")) {
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                 FileInputStream fileInputStream = new FileInputStream(new File(path+ "//"+"configuration"))) {
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
        return builder.toString().equals(usbKey.getKey());
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

    private String generatedKey(String SN) {
       String[] letter = SN.split("");
       SN = "";
       for(int i=0; i<letter.length; i++) {
           RandomStringGenerator randomStringGenerator = new RandomStringGenerator.Builder()
                   .withinRange('0', 'z')
                   .filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
                   .build();
           String rand = randomStringGenerator.generate(2);
           SN+=letter[i]+rand;
       }
       return SN;
    }
}

