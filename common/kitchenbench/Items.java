package kitchenbench;

import kitchenbench.enderio.ItemEnderface;
import kitchenbench.item.ArmorMelon;
import kitchenbench.item.ItemGoldenRoastApple;
import kitchenbench.item.ItemRoastApple;
import kitchenbench.item.ItemRoastPotato;

public class Items {

  public ItemRoastPotato roastPotatoItem;

  public ItemRoastApple roastAppleItem;

  public ItemGoldenRoastApple goldenRoastAppleItem;

  public ItemEnderface enderfaceItem;

  public ArmorMelon melonArmor;

  Items() {
  }

  void init(int startId, CommonProxy proxy) {
    int nextItemId = startId;

    melonArmor = ArmorMelon.create(nextItemId, proxy);
    nextItemId = melonArmor.getLastId() + 1;

    roastPotatoItem = ItemRoastPotato.create(nextItemId);
    nextItemId++;

    roastAppleItem = ItemRoastApple.create(nextItemId);
    nextItemId++;

    goldenRoastAppleItem = ItemGoldenRoastApple.create(nextItemId);
    nextItemId++;

    enderfaceItem = ItemEnderface.create(nextItemId);
    nextItemId++;
  }
}
