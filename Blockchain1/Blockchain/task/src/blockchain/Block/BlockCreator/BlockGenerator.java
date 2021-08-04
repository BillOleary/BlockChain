package blockchain.Block.BlockCreator;

/*
*
 */

import java.io.Serializable;

public interface BlockGenerator extends Serializable {

    void createBlock();

}
