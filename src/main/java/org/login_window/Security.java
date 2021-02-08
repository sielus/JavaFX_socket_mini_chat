package org.login_window;

import javax.crypto.Cipher;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

public class Security {
    private Socket socket;
    private byte[] publicKey;
    private byte[] userDataToEncrypt;

    public Security(Socket socket, String userData) {
        this.socket = socket;
        this.userDataToEncrypt = userData.getBytes();
    }

    public byte[] getPublicKey() {
        try {
            DataInputStream inputPublicKeyStreamer = new DataInputStream(socket.getInputStream());
            int length = inputPublicKeyStreamer.readInt();
            byte[] publicKey = new byte[length];
            if (length > 0) {
                inputPublicKeyStreamer.readFully(publicKey, 0, publicKey.length);
            }
            return this.publicKey = publicKey;
        } catch (Exception e) {
            e.getCause();
        }
        return null;
    }

    public byte[] encrypt() {
        try {
            PublicKey key = KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(publicKey));
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(userDataToEncrypt);
            return encryptedBytes;
        } catch (Exception e) {
            e.getCause();
        }
        return null;
    }

    public void sendEncryptedData() {
        try {
            byte[] encryptedUser = encrypt();
            DataOutputStream enryptedDataOutputStream = new DataOutputStream(socket.getOutputStream());
            enryptedDataOutputStream.writeInt(encryptedUser.length);
            enryptedDataOutputStream.write(encryptedUser);
        } catch (Exception e) {
            e.getCause();
        }
    }

    public String getServerRequest() {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            return bufferedReader.readLine();
        } catch (Exception e) {
            e.getCause();
        }
        return null;
    }
}
