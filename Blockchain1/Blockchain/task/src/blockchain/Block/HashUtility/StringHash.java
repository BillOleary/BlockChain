package blockchain.Block.HashUtility;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
 *   This Class generates a hash for the current Block.
 *   @param : currentBlockString
 *
 */
public class StringHash {

    public static String generateBlockHash(String currentBlockString) throws NoSuchAlgorithmException {

        byte[] digestedBytes;
        StringBuilder hexString = new StringBuilder();

        MessageDigest digestInstance = MessageDigest.getInstance("SHA-256");
        digestedBytes = digestInstance.digest(currentBlockString.getBytes(StandardCharsets.UTF_8));

        //Given the bytes of data in the array - convert to HEX
        for (byte digestedByte : digestedBytes) {
            String hex = Integer.toHexString(0xff & digestedByte);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
