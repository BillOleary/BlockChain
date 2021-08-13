package blockchain.Block.BlockCreator;

import blockchain.Block.BlockProduct.BlockManager;
import blockchain.Block.GUI.DisplayBlockChain;

import javax.swing.*;
import java.util.Objects;


public class MakeBlock implements BlockGenerator {

    final BlockManager blockManager;

    public MakeBlock(BlockManager blockManager) {
        this.blockManager = blockManager;
    }

    public void generateNBlocks(int numberOfBlocksToCreate) {
        if (Objects.nonNull(blockManager)) {
            blockManager.createTheBlocks(numberOfBlocksToCreate);
        }
    }

    @Override
    public void createBlock() {

        //Once Blocks have been created then display them to Screen
        DisplayBlockChain dbc = new DisplayBlockChain(blockManager);
        blockManager.notifyObservers();

        blockManager.persistBlocks();
    }

}
