package kitchenbench;

import kitchenbench.enderio.BlockEnderIO;
import kitchenbench.oven.BlockOven;

public class Blocks {

  public BlockOven ovenBlock;

  public BlockEnderIO enderIoBlock;

  Blocks() {
  }

  void init(int startId, CommonProxy proxy) {
    int nextBlockId = startId;

    ovenBlock = BlockOven.create(nextBlockId, proxy);
    nextBlockId++;

    enderIoBlock = BlockEnderIO.create(nextBlockId);
    nextBlockId++;
  }

}
