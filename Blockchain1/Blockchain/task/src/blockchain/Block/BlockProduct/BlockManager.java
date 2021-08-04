package blockchain.Block.BlockProduct;

import blockchain.Block.GUI.BlockObserver;
import blockchain.Block.Persistance.FromFile;
import blockchain.Block.Persistance.PersistBlocks;
import blockchain.Block.Persistance.ToFile;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

/*
*24 June 2021
* This class aims to provide the requirements for a BlockChain.
* Things to add Later
* add
* delete
* update
* remove
* find
*
 */
public class BlockManager implements Serializable, Subject {

    long serialVersionUID  = SerializableUID.UID.getSerialUID();
    //State Variables for the BlockManager
    //Create a List of Block elements which will form our chain
    int blocksSizeToCreate = 0;
    int currentBlockSize = 0;
    List<Block> blockList = new ArrayList<>();
    //newest block is the last block to be added
    Block newBlock;
    //Used for recovery of Block Chain data from file on first run.
    boolean fileNotRecovered = true;
    int currentRecoveredListSize;
    List<BlockObserver> listOfBlockObservers = new ArrayList<>();


    //Generate a UniBLock
    //Invariant - Test for Block0
    //          - Test for proof of Work!
    public BlockManager() {
        testForNewBlock();
    }


    //Constructor - Good for generating n sized Block's
    //Shit for uniBlocks!!!!!!!!!!
    public BlockManager(int blocksSizeToCreate) {
        this();     //default constructor
        this.blocksSizeToCreate = blocksSizeToCreate;
        for (int index = 1; index < blocksSizeToCreate; index++) {
            testForNewBlock();
        }
    }

    private void testForNewBlock() {
        //If the file with block data exists then load it
        File blockDataPersistFile = new File("out.bin");
        if (fileNotRecovered &&
                blockDataPersistFile.exists() &&
                blockDataPersistFile.length() > 0L) {
            recoverBlock();
            //how many files were recovered/read from the list
            //will use this when we append the new files to the list
            //Now Print it to Screen
            blockList.forEach(System.out::println);
            //Now Test the Block to make sure it is valid
//            System.out.println("BlockChain is Valid \u2192 " +
//                    blockList.stream().
//                            map(Block::blockIsValid).
//                            reduce(true, (a, b) -> a && b));
            fileNotRecovered = false;
        }

//        long starTime = System.nanoTime();
        //Invariant
        if (blockList.size() == 0) {
            //Edge case if we are on the first write to file then keep the count to 0 or else
            //nothing gets written to file (actually null values get dumped in to the file).
            //currentRecoveredListSize = 0;
            blockList.add(new Block(null));

        } else {
            Block previousBlock = blockList.get(blockList.size() - 1);
            blockList.add(new Block(previousBlock));
        }
//        long stopTime = System.nanoTime();
//        System.out.println("Block was generated for " + (stopTime - starTime) / 1_000_000_000 + " seconds\n");
        currentBlockSize = blockList.size();
        //Pointer to the most current Block on the chain.
        newBlock = blockList.get(currentBlockSize - 1);
    }

    /*
    * When you want to save the items to file call the method below
     */
    public void persistBlocks() {
        List<Block> subListToWrite = blockList.stream().skip(currentRecoveredListSize).collect(Collectors.toList());
        new ToFile().persistBlockChain(subListToWrite);
        blockList.clear();
    }

    /*
    * When recovery is required call this method
     */
    public void recoverBlock() {
        PersistBlocks persistBlocks = new FromFile();
        blockList = persistBlocks.persistBlockChain(blockList).
                collect(Collectors.toList());
    }

    @Override
    public String toString() {
        StringBuilder allBlocks = new StringBuilder();
        for (Block blocks : blockList) {
            allBlocks.append(blocks);
            allBlocks.append("\n");
        }
        return allBlocks.toString();
    }

    /**************************Implement the Subject Interface**************/
    @Override
    public void registerObservers(BlockObserver observer) {
        this.listOfBlockObservers.add(observer);
    }

    @Override
    public void removeObservers(BlockObserver blockObserver) {
        listOfBlockObservers.remove(blockObserver);
    }

    @Override
    public void notifyObservers() {
        //Send out the iterator of block objects to the GUI
        //Good practice to send out an iterator rather than
        //the crown jewels of block-lists
        for (BlockObserver list : listOfBlockObservers) {
            list.update(blockList.listIterator());
        }
    }
}
