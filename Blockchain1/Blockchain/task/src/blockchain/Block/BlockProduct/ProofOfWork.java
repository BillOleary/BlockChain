package blockchain.Block.BlockProduct;

import java.io.Serializable;
import java.util.Scanner;

public class ProofOfWork implements Serializable {
    long serialVersionUID  = SerializableUID.UID.getSerialUID();
    static int readNumberOfZeros = -1;


    public static int getReadNumberOfZeros() {
        return readNumberOfZeros;
    }

    public static int getLeadingZeros() {
        System.out.println("Enter how many zeros the hash must start with: ");
        Scanner readIn = new Scanner(System.in);
        int number;
        do {
            readNumberOfZeros = readIn.nextInt();
        } while (readNumberOfZeros < 0);
       return readNumberOfZeros;
    }

}
