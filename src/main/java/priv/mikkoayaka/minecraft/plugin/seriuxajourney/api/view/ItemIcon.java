package priv.mikkoayaka.minecraft.plugin.seriuxajourney.api.view;

import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ItemIcon implements Clickable {
    private final boolean needRefresh;
    public ItemIcon(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }
    private ItemStack itemStackCache;
    @NonNull
    public ItemStack getIcon() {
        if(needRefresh) return createIcon();
        if(itemStackCache == null)itemStackCache = createIcon();
        return itemStackCache;
    }
    @NonNull
    protected abstract ItemStack createIcon();

    protected ItemStack fastCreateItemStack(Material material,int count, String displayName, String... lores) {
        List<String> loreList;
        if(lores == null || lores.length == 0) loreList = new ArrayList<>();
        else loreList = Arrays.asList(lores);
        ItemStack itemStack = new ItemStack(material,count);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(loreList);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
