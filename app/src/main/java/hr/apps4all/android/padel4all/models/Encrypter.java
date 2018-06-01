package hr.apps4all.android.padel4all.models;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Bruno on 2.2.2018..
 */

public class Encrypter {

    private static final String ALGORITHM = "SHA256";

    public static String encrypt(String value) {
        MessageDigest digest = null;
        try{
             digest = MessageDigest.getInstance("SHA-256");
        }catch (NoSuchAlgorithmException ignorable){
        }

        byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
        return new String(hash);
    }

}
