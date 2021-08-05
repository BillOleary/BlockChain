package blockchain;

import blockchain.Block.BlockCreator.BlockGenerator;
import blockchain.Block.BlockCreator.MakeBlock;

public class Main {

    public static void main(String[] args) {

        BlockGenerator blocky = new MakeBlock();
        blocky.createBlock();

    }
}
