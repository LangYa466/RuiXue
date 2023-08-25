/*
 * LiquidBounce Base
 * God SkidBounce
 * Conding
 */
package net.ccbluex.liquidbounce.features.module.modules.combat;

import net.ccbluex.liquidbounce.api.enums.WEnumHand;
import net.ccbluex.liquidbounce.api.minecraft.item.IItemStack;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.MotionEvent;
import net.ccbluex.liquidbounce.event.Render3DEvent;
import net.ccbluex.liquidbounce.event.TickEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.CrossVersionUtilsKt;
import net.ccbluex.liquidbounce.utils.InventoryUtils;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.ccbluex.liquidbounce.utils.item.ArmorComparator;
import net.ccbluex.liquidbounce.utils.item.ArmorPiece;
import net.ccbluex.liquidbounce.utils.item.ItemUtils;
import net.ccbluex.liquidbounce.utils.timer.TimeUtils;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.ccbluex.liquidbounce.utils.CrossVersionUtilsKt.createOpenInventoryPacket;

@ModuleInfo(name = "AutoArmor", description = "Automatically equips the best armor in your inventory.",chinesename = "自动装备", category = ModuleCategory.COMBAT)
public class AutoArmor extends Module {
    private final BoolValue autoreplace = new BoolValue("AutoReplace", true);
    private final List<Item> whitelist = Arrays.asList(Items.SLIME_BALL, Items.BLAZE_ROD);
    public static final ArmorComparator ARMOR_COMPARATOR = new ArmorComparator();
    private final IntegerValue minDelayValue = new IntegerValue("MinDelay", 100, 0, 400) {

        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            final int maxDelay = maxDelayValue.get();

            if (maxDelay < newValue) set(maxDelay);
        }
    };
    private final IntegerValue maxDelayValue = new IntegerValue("MaxDelay", 200, 0, 400) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            final int minDelay = minDelayValue.get();

            if (minDelay > newValue) set(minDelay);
        }
    };
    private final BoolValue invOpenValue = new BoolValue("InvOpen", false);
    private final BoolValue simulateInventory = new BoolValue("SimulateInventory", true);
    private final BoolValue noMoveValue = new BoolValue("NoMove", false);
    private final IntegerValue itemDelayValue = new IntegerValue("ItemDelay", 0, 0, 5000);
    private final BoolValue hotbarValue = new BoolValue("Hotbar", true);
    private final BoolValue GrimBypassValue = new BoolValue("GrimBypass", true);

    private long delay;

    private boolean locked = false;

    @EventTarget
    public void onRender3D(final Render3DEvent event) {
        if (!InventoryUtils.CLICK_TIMER.hasTimePassed(delay) || mc.getThePlayer() == null ||
                (mc.getThePlayer().getOpenContainer() != null && mc.getThePlayer().getOpenContainer().getWindowId() != 0))
            return;

        // Find best armor
        final Map<Integer, List<ArmorPiece>> armorPieces = IntStream.range(0, 36)
                .filter(i -> {
                    final IItemStack itemStack = mc.getThePlayer().getInventory().getStackInSlot(i);

                    return itemStack != null && classProvider.isItemArmor(itemStack.getItem())
                            && (i < 9 || System.currentTimeMillis() - itemStack.getItemDelay() >= itemDelayValue.get());
                })
                .mapToObj(i -> new ArmorPiece(mc.getThePlayer().getInventory().getStackInSlot(i), i))
                .collect(Collectors.groupingBy(ArmorPiece::getArmorType));

        final ArmorPiece[] bestArmor = new ArmorPiece[4];

        for (final Map.Entry<Integer, List<ArmorPiece>> armorEntry : armorPieces.entrySet()) {
            bestArmor[armorEntry.getKey()] = armorEntry.getValue().stream()
                    .max(ARMOR_COMPARATOR).orElse(null);
        }

        // Swap armor
        for (int i = 0; i < 4; i++) {
            final ArmorPiece armorPiece = bestArmor[i];

            if (armorPiece == null)
                continue;

            int armorSlot = 3 - i;

            final ArmorPiece oldArmor = new ArmorPiece(mc.getThePlayer().getInventory().armorItemInSlot(armorSlot), -1);

            if (ItemUtils.isStackEmpty(oldArmor.getItemStack()) || !classProvider.isItemArmor(oldArmor.getItemStack().getItem()) ||
                    ARMOR_COMPARATOR.compare(oldArmor, armorPiece) < 0) {
                if (!ItemUtils.isStackEmpty(oldArmor.getItemStack()) && move(8 - (3 - armorSlot), true)) {
                    locked = true;
                    return;
                }

                if (ItemUtils.isStackEmpty(mc.getThePlayer().getInventory().armorItemInSlot(armorSlot)) && move(armorPiece.getSlot(), false)) {
                    locked = true;
                    return;
                }
            }
        }

        locked = false;
    }

    public boolean isLocked() {
        return !getState() || locked;
    }

    /**
     * Shift+Left clicks the specified item
     *
     * @param item        Slot of the item to click
     * @param isArmorSlot
     * @return True if it is unable to move the item
     */
    private boolean move(int item, boolean isArmorSlot) {
        if (!isArmorSlot && item < 9 && hotbarValue.get() && !classProvider.isGuiInventory(mc.getCurrentScreen())) {
            mc.getNetHandler().addToSendQueue(classProvider.createCPacketHeldItemChange(item));
            mc.getNetHandler().addToSendQueue(CrossVersionUtilsKt.createUseItemPacket(mc.getThePlayer().getInventoryContainer().getSlot(item).getStack(), WEnumHand.MAIN_HAND));
            mc.getNetHandler().addToSendQueue(classProvider.createCPacketHeldItemChange(mc.getThePlayer().getInventory().getCurrentItem()));

            delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get());

            return true;
        } else if (!(noMoveValue.get() && MovementUtils.isMoving()) && (!invOpenValue.get() || classProvider.isGuiInventory(mc.getCurrentScreen())) && item != -1) {
            final boolean openInventory = simulateInventory.get() && !classProvider.isGuiInventory(mc.getCurrentScreen());
            final boolean bypass = GrimBypassValue.get();

            if (openInventory && !bypass)
                mc.getNetHandler().addToSendQueue(createOpenInventoryPacket());

            boolean full = isArmorSlot;

            if (full) {
                for (IItemStack iItemStack : mc.getThePlayer().getInventory().getMainInventory()) {
                    if (ItemUtils.isStackEmpty(iItemStack)) {
                        full = false;
                        break;
                    }
                }
            }

            if (full) {
                mc.getPlayerController().windowClick(mc.getThePlayer().getInventoryContainer().getWindowId(), item, 1, 4, mc.getThePlayer());
            } else {
                mc.getPlayerController().windowClick(mc.getThePlayer().getInventoryContainer().getWindowId(), (isArmorSlot ? item : (item < 9 ? item + 36 : item)), 0, 1, mc.getThePlayer());
            }

            delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get());

            if (openInventory || bypass)
                mc.getNetHandler().addToSendQueue(classProvider.createCPacketCloseWindow());

            return true;
        }

        return false;
    }
    @EventTarget
    public void onUpdate(MotionEvent event) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;

        if(player != null && player.isEntityAlive()) {
            if(autoreplace.get()) {
                for(int i = 0; i < 4; i++) {
                    ItemStack armorStack = player.inventory.armorInventory.get(i);

                    if(armorStack.isEmpty() || !(armorStack.getItem() instanceof ItemArmor)) {
                        continue;
                    }

                    int armorPieceDamage = armorStack.getMaxDamage() - armorStack.getItemDamage();
                    float armorPieceToughness = ((ItemArmor) armorStack.getItem()).damageReduceAmount;

                    for(int j = 9; j < 45; j++) {
                        ItemStack itemStack = player.inventoryContainer.getSlot(j).getStack();

                        if(itemStack.isEmpty() || !(itemStack.getItem() instanceof ItemArmor)) {
                            continue;
                        }

                        ItemArmor itemArmor = (ItemArmor) itemStack.getItem();
                        int itemPieceDamage = itemStack.getMaxDamage() - itemStack.getItemDamage();
                        float itemPieceToughness = itemArmor.damageReduceAmount;

                        if(itemArmor.armorType == EntityEquipmentSlot.HEAD && i != 3){
                            continue;
                        }
                        if(itemArmor.armorType == EntityEquipmentSlot.CHEST && i != 2){
                            continue;
                        }
                        if(itemArmor.armorType == EntityEquipmentSlot.LEGS && i != 1){
                            continue;
                        }
                        if(itemArmor.armorType == EntityEquipmentSlot.FEET && i != 0){
                            continue;
                        }

                        if(itemPieceDamage > armorPieceDamage || (itemPieceDamage == armorPieceDamage && itemPieceToughness > armorPieceToughness)) {

                            // Check if the item in the slot is in the whitelist
                            if(!whitelist.contains(itemStack.getItem())) {
                                continue;
                            }

                            player.inventory.currentItem = 8 - i;
                            Minecraft.getMinecraft().displayGuiScreen(new GuiInventory(player));
                            Minecraft.getMinecraft().playerController.windowClick(player.openContainer.windowId, j, 0, ClickType.PICKUP, player);
                            Minecraft.getMinecraft().playerController.windowClick(player.openContainer.windowId, 3 - i, 0, ClickType.PICKUP, player);
                            Minecraft.getMinecraft().playerController.windowClick(player.openContainer.windowId, j, 0, ClickType.PICKUP, player);

                            return;
                        }
                    }
                }
            }
        }
    }
}