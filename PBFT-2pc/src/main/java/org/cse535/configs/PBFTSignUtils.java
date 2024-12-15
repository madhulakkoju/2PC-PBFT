package org.cse535.configs;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


public class PBFTSignUtils {

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        // Use RSA algorithm for key generation
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);  // Key size: 2048 bits
        return keyPairGenerator.generateKeyPair();
    }

    // Method to generate a KeyPair (public and private) from a predefined text
    public static KeyPair generateKeyPairFromText(String predefinedText){

        try {
            // Use SHA-256 to derive a private key from the predefined text
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] privateKeyBytes = digest.digest(predefinedText.getBytes());

            // Generate private key from the derived bytes
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

            // Generate public key from the private key
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(privateKey.getEncoded()));

            return new KeyPair(publicKey, privateKey);

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return new KeyPair(null,null);
    }


    // Method to generate an RSA KeyPair deterministically from predefined text
    public static KeyPair generateKeyPairFromText2(String predefinedText) {
        try {

            // Use SecureRandom and seed it with the hash of the predefined text
            byte[] seed = predefinedText.getBytes();  // Get the byte array of predefined text
            SecureRandom random = new SecureRandom(seed);  // Seed SecureRandom with the predefined text

            // Create RSA KeyPairGenerator and initialize with key size (e.g., 2048 bits)
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048, random);  // Initialize with 2048-bit key size

            // Generate the RSA key pair
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

//            System.out.println("Server: "+predefinedText+"\n");
//            System.out.println("Public Key: "+keyPair.getPublic());
//            System.out.println("Private Key: " + keyPair.getPrivate());

            return keyPair;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return new KeyPair(null, null);
    }

    // Method to sign a message using the private key
    public static String signMessage(String message, PrivateKey privateKey) {
        // Use SHA256withRSA signature algorithm
        try {

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(message.getBytes());

            // Sign the message and encode it in Base64 for easier transfer
            byte[] signedMessage = signature.sign();
            return Base64.getEncoder().encodeToString(signedMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    // Method to verify a signed message using the public key
    public static boolean verifySignature(String message, String signedMessage, PublicKey publicKey) {
        try {
            // Decode the signed message from Base64
            byte[] signedMessageBytes = Base64.getDecoder().decode(signedMessage);

            // Use SHA256withRSA signature algorithm to verify the signature
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(message.getBytes());

            // Verify the signature
            return signature.verify(signedMessageBytes);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



    public static void main(String[] args){
        KeyPair pair = generateKeyPairFromText2("S1");

        System.out.println(pair.getPrivate().toString());
        System.out.println(pair.getPublic().toString());


        System.out.println(signMessage("fasak", pair.getPrivate()));


        System.out.println(signMessage("fasak", pair.getPrivate()));


        System.out.println(signMessage("fasak", pair.getPrivate()));


        String a = signMessage("fasak", pair.getPrivate());

        System.out.println(verifySignature("fasak", a, pair.getPublic() ));


    }
}
