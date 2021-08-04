package blockchain.Block.BlockCreator;

import blockchain.Block.BlockProduct.BlockManager;
import blockchain.Block.GUI.DisplayBlockChain;


public class MakeBlock implements BlockGenerator {

    @Override
    public void createBlock() {

        BlockManager blockManager = new BlockManager(5);

        //Once Blocks have been created then display them to Screen
        DisplayBlockChain dbc = new DisplayBlockChain(blockManager);
        blockManager.notifyObservers();

        blockManager.persistBlocks();


//        System.out.println("Saving Blocks to File out.bin");
//        blockManager.persistBlocks();
//
//        //Test Blocks are emptied
//        System.out.println("TESTING\n\t-Clearing out the block list");
//        System.out.print(blockManager);
//        System.out.println("\t-Block List is EMPTY!");
//
//
//        //Now Recover the blocks
//        System.out.println("Recover the List from file");
//        blockManager.recoverBlock();
//        System.out.print("\t-Printing the Recovered Block List");
//        System.out.println(blockManager);
    }
}
