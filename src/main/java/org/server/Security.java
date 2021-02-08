package org.server;

import javax.crypto.Cipher;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class Security {
    private static final String ALGORITHM = "RSA";
    private Socket socket;
    public Security(Socket socket) {
        this.socket = socket;
    }

    public  byte[] encrypt(byte[] publicKey, byte[] inputData) {
        try {
            PublicKey key = KeyFactory.getInstance(ALGORITHM)
                    .generatePublic(new X509EncodedKeySpec(publicKey));
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(inputData);
        } catch (Exception e) {
            e.getCause();
        }
        return null;
    }

    public  byte[] decrypt(byte[] privateKey, byte[] inputData) {
        try {
            PrivateKey key = KeyFactory.getInstance(ALGORITHM)
                    .generatePrivate(new PKCS8EncodedKeySpec(privateKey));
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(inputData);
        } catch (Exception e) {
            e.getCause();
        }
        return null;
    }

    public  KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(512, random);
            return keyGen.generateKeyPair();
        } catch (Exception e) {
            e.getCause();
        }
        return null;
    }

    public byte[] getEncryptedDataFromUser() {
        try {
            DataInputStream dIn = new DataInputStream(socket.getInputStream());
            int length = dIn.readInt();
            byte[] encryptedData = new byte[length];
            if(length>0) {
                dIn.readFully(encryptedData, 0, encryptedData.length); // read the message
            }
            return encryptedData;
        }catch (Exception e){
            e.getCause();
        }
        return null;
    }

    public void sendPublicKeyToClient(byte[] publicKey) {
        try {
            DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
            dOut.writeInt(publicKey.length);
            dOut.write(publicKey);
        }catch (Exception e){
            e.getCause();
        }
    }
}
