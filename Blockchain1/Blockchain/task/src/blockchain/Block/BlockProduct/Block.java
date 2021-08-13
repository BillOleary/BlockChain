package blockchain.Block.BlockProduct;

import blockchain.Block.HashUtility.StringHash;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;

public class Block implements Serializable, Comparable<Block> {

    long serialVersionUID  = SerializableUID.UID.getSerialUID();
    //Create the data fields you want your Block to have
    private int blockId;                    //The ID of the block to be created.
    private long dateTime;                  //Date and Time for the creation of the current Block.
    private String previousBlockHash;       //Hash for the previous Block.
    private String currentBlockHash = "";   //Hash for the current Block.
    private int magicNumber = 0;            //Crypto Secure Random Number Generator.
    private Block previousBlock = null;     //A Link to the previous Block.
    //Get the number of 0 prefix values to constrain each hash value.
    private int numberOfZeros;
    private int timeToCalculateBlock = 0;

    //A manual way to create a single new block
    public Block(int blockId,
                 long dateTime,
                 String previousBlockHash,
                 String currentBlockHash,
                 int magicNumber,
                 int zerosInFront,
                 int timeToCalculateBlock,
                 Block previousBlock
    ) {
        this.blockId = blockId;
        this.dateTime = dateTime;
        this.previousBlockHash = previousBlockHash;
        this.currentBlockHash = currentBlockHash;
        this.magicNumber = magicNumber;
        numberOfZeros = zerosInFront;
        this.timeToCalculateBlock = timeToCalculateBlock;
        this.previousBlock = previousBlock;
    }

    //Create a new block given a previous Block
    Block(Block previousBlock) {
        //Set up your Invariants
        // - First Block has a hash of 0
        // - Block must contain 'n' zeros ; n < Size of the digest
        dateTime = new Date().getTime();
        if (previousBlock == null) {
            this.previousBlockHash = "0";        //Invariant
            this.blockId = 1;
        } else {
            this.previousBlock = previousBlock;
            this.previousBlockHash = previousBlock.getCurrentBlockHash();
            this.blockId = previousBlock.getCurrentBlockNumber() + 1;
        }

        /*
        * Proof of Work checks for the number of zeros to generate.  This is saved for each block which is
        * created.
        * Initially the static variable is set to -1 i.e. generate -1 0's for the prefix.  This is an illegal
        * state which forces the code to prompt the user for a valid value i.e. >= 0.
         */
        if (ProofOfWork.readNumberOfZeros < 0) {
            numberOfZeros = ProofOfWork.getLeadingZeros();
        } else {
            numberOfZeros = ProofOfWork.getReadNumberOfZeros();
        }
        //Get a Valid magic Number for which we have index 0..n with 0 value
        try {
            long startTime = System.nanoTime();
            do {
                magicNumber = SecureRandom.getInstanceStrong().nextInt();
                currentBlockHash = StringHash.generateBlockHash(stringOfAllFields());
            } while (!blockIsValid());
            System.out.println(currentBlockHash.length());
            timeToCalculateBlock = (int) ((System.nanoTime() - startTime) / 1_000_000_000);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        //Print out the Block after it is created
        System.out.print(this);

    }

    public int getCurrentBlockNumber() {
        return blockId;
    }

    public long getDateTime() {
        return dateTime;
    }

    public int getMagicNumber() {
        return magicNumber;
    }

    public String getPreviousBlockHash() {
        return previousBlockHash;
    }

    public String getCurrentBlockHash() {
        if (currentBlockHash == null) {
            throw new NullPointerException();
        }
        return currentBlockHash;
    }

    public int getNumberOfZeros() {
        return numberOfZeros;
    }

    public int getTimeToCalculateBlock() {
        return timeToCalculateBlock;
    }

    /*****************************Block Testing Methods**************************************/
    /*
    * Create a string version of all the fields.  This will be used as a key to
    * generate a digest via the StringHash Class method "generateBlockHash(String ...)"
    * In this case we ignore the time take to calculate the block field as it is for
    * a decorating feature rather than a requirement.
    *
     * */
    public String stringOfAllFields() {
        return String.valueOf(getCurrentBlockNumber()) +
                getDateTime() +
                getMagicNumber() +
                getPreviousBlockHash();
    }

    /*
    * Based on Requirements makes sure the generated Block is valid, i.e. proof of Work is correct.
    * Test of Validity is based on
    *  - number of prefix zeros of the previous block.
    *  - number of prefix zeros of the current block.
    *  - the current hash of the 'previous' block is the sames and the previous hash of the 'current' block.
    *  - previous block is NOT Null
    *
     */
    public boolean blockIsValid() {
        boolean isValidBlock = false;
        //Use the regex to find a string with at least n or more zeros - regex -> 'X{n,}'
        if (this.getCurrentBlockHash().length() == StringHash.getBlockLengthAsString() ||
                this.previousBlock != null &&
                this.getPreviousBlockHash().length() == StringHash.getBlockLengthAsString()) {
            //test for the number of zeros for 'this' block.  This value may be different to the previous block as the
            //value may be set by the system automatically to adjust the block validity.
            String regexCheckFor_N_OrMoreZeros = "0{" + this.getNumberOfZeros() + ",}.+";
            try {
                //Compare the correct number of 0's and make sure the current hash for the previous block
                //matches the previous hash of the current block
                boolean currentBlockStartZeros = getCurrentBlockHash().matches(regexCheckFor_N_OrMoreZeros);
                boolean previousBlockStartZeros =
                        getPreviousBlockHash().matches("0") ||
                                getPreviousBlockHash().matches("0{" + previousBlock.getNumberOfZeros() + ",}.+");
                isValidBlock =
                        currentBlockStartZeros &&
                                previousBlockStartZeros &&
                                (previousBlock == null ||
                                        previousBlock.getCurrentBlockHash().compareTo(this.previousBlockHash) == 0);

            } catch (NullPointerException npXception) {
                npXception.printStackTrace();
            }
        }
        return isValidBlock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Block)) return false;

        Block block = (Block) o;

        if (blockId != block.blockId) return false;
        if (getDateTime() != block.getDateTime()) return false;
        if (getMagicNumber() != block.getMagicNumber()) return false;
        if (!getPreviousBlockHash().equals(block.getPreviousBlockHash())) return false;
        return getCurrentBlockHash().equals(block.getCurrentBlockHash());
    }

    @Override
    public int hashCode() {
        int result = blockId;
        result = 31 * result + (int) (getDateTime() ^ (getDateTime() >>> 32));
        result = 31 * result + getPreviousBlockHash().hashCode();
        result = 31 * result + getCurrentBlockHash().hashCode();
        result = 31 * result + (getMagicNumber() ^ (getMagicNumber() >>> 32));
        return result;
    }

    //Print out the Block State using the toString function.
    @Override
    public String toString() {
        return "\nBlock:\n" +
                //Print out the current State of the Block
                "Id: " + getCurrentBlockNumber() + "\n" +
                "Timestamp: " + getDateTime() + "\n" +
                "Magic number: " + getMagicNumber() + "\n" +
                "Hash of the previous block:\n" + getPreviousBlockHash() + "\n" +
                "Hash of the block:\n" + getCurrentBlockHash() + "\n" +
                "Block was generating for " + getTimeToCalculateBlock() + " seconds\n" +
                "Number of Leading Zeros " + getNumberOfZeros() + "\n";
    }

    @Override
    public int compareTo(Block block) {
        return this.equals(block) ? 0 : -1;
    }
}
