package blockchain.Block.Persistance;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FromFile implements PersistBlocks {
    @Override
    public <Block> Stream<Block> persistBlockChain(List<Block> list) {

        FileInputStream fis = null;
        ObjectInputStream ois = null;
        List<Block> blockList = new ArrayList<>();

        try {
            fis = new FileInputStream("out.bin");
            ois = new ObjectInputStream(fis);

            blockList =  ((List<Block>) ois.readObject());

        } catch (IOException | ClassNotFoundException ioXception) {
            ioXception.printStackTrace();
        } finally {
            try {
                assert ois != null;
                ois.close();
                fis.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        return blockList.stream();
    }
}

