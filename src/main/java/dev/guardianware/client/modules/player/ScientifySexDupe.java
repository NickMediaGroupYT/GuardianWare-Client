package dev.guardianware.client.modules.player;

import dev.guardianware.api.manager.module.Module;
import dev.guardianware.api.manager.module.RegisterModule;
import dev.guardianware.client.events.EventPacketSend;
import dev.guardianware.client.values.impl.ValueBoolean;
import dev.guardianware.client.values.impl.ValueNumber;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;


import java.util.*;


@RegisterModule(name="ScientifySexDupe", description="trident funny dupe", category=Module.Category.PLAYER)
public class ScientifySexDupe extends Module {
    public ValueNumber delay = new ValueNumber("Delay","Delay","",5,0,10);
    public ValueBoolean dropTridents = new ValueBoolean("Drop Tridents","Drop Tridents","",false);
    public ValueBoolean durabilityManagement = new ValueBoolean("DuraManagement","DuraManagement","",true);

    private final Queue<Packet<?>> delayedPackets = new LinkedList<>();

    @EventHandler
    public void onSendPacket(EventPacketSend event) {

        if (event.getPacket() instanceof ClientCommandC2SPacket
                || event.getPacket() instanceof PlayerMoveC2SPacket
                || event.getPacket() instanceof CloseHandledScreenC2SPacket)
            return;

        if (!(event.getPacket() instanceof ClickSlotC2SPacket)
                && !(event.getPacket() instanceof PlayerActionC2SPacket))
        {
            return;
        }
        if (!cancel)
            return;

        MutableText packetStr = Text.literal(event.getPacket().toString()).formatted(Formatting.WHITE);

        event.cancel();
    }

    @Override
    public void onEnable()
    {
        if (mc.player == null)
            return;

        for (int i = 0; i < 9; i++)
        {
            if (mc.player.getInventory().getStack((i)).getItem() == Items.TRIDENT)
            {
                Integer currentHotbarDamage = mc.player.getInventory().getStack((i)).getDamage();

            }
        }

        PlayerInteractItemC2SPacket pckt = new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, 10, -57.0f, 66.29f);

        Int2ObjectMap<ItemStack> modifiedStacks = new Int2ObjectOpenHashMap<>();

        modifiedStacks.put(3,  mc.player.getInventory().getStack(mc.player.getInventory().selectedSlot));
        modifiedStacks.put(36,  mc.player.getInventory().getStack(mc.player.getInventory().selectedSlot));

        ClickSlotC2SPacket packet = new ClickSlotC2SPacket(0, 15, 0, 0, SlotActionType.SWAP,
                new ItemStack(Items.AIR), modifiedStacks);

        scheduledTasks.clear();
        dupe();

    }

    private void dupe()
    {
        int delayInt = (delay.getValue()).intValue()*100;

        System.out.println(delayInt);

        int lowestHotbarSlot = 0;
        int lowestHotbarDamage = 1000;
        for (int i = 0; i < 9; i++)
        {
            if (mc.player.getInventory().getStack((i)).getItem() == Items.TRIDENT)
            {
                Integer currentHotbarDamage = mc.player.getInventory().getStack((i)).getDamage();
                if(lowestHotbarDamage > currentHotbarDamage) { lowestHotbarSlot = i; lowestHotbarDamage = currentHotbarDamage;}

            }
        }

        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
        cancel = true;

        int finalLowestHotbarSlot = lowestHotbarSlot;
        scheduleTask(() -> {
            cancel = false;

            if(durabilityManagement.getValue()) {
                if(finalLowestHotbarSlot != 0) {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, (44), 0, SlotActionType.SWAP, mc.player);
                    if(dropTridents.getValue())mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 44, 0, SlotActionType.THROW, mc.player);
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, (36 + finalLowestHotbarSlot), 0, SlotActionType.SWAP, mc.player);
                }
            }

            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 3, 0, SlotActionType.SWAP, mc.player);

            PlayerActionC2SPacket packet2 = new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN, 0);
            mc.getNetworkHandler().sendPacket(packet2);

            if(dropTridents.getValue()) mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 44, 0, SlotActionType.THROW, mc.player);

            cancel = true;
            scheduleTask2(this::dupe, delayInt);
        }, delayInt);
    }


    private boolean cancel = true;

    private final List<Pair<Long, Runnable>> scheduledTasks = new ArrayList<>();
    private final List<Pair<Long, Runnable>> scheduledTasks2 = new ArrayList<>();

    public void scheduleTask(Runnable task, long delayMillis) {
        long executeTime = System.currentTimeMillis() + delayMillis;
        scheduledTasks.add(new Pair<>(executeTime, task));
    }
    public void scheduleTask2(Runnable task, long delayMillis) {
        long executeTime = System.currentTimeMillis() + delayMillis;
        scheduledTasks2.add(new Pair<>(executeTime, task));
    }

    @Override
    public void onTick() {
        long currentTime = System.currentTimeMillis();
        {
            Iterator<Pair<Long, Runnable>> iterator = scheduledTasks.iterator();

            while (iterator.hasNext()) {
                Pair<Long, Runnable> entry = iterator.next();
                if (entry.getLeft() <= currentTime) {
                    entry.getRight().run();
                    iterator.remove(); // Remove executed task from the list
                }
            }
        }
        {
            Iterator<Pair<Long, Runnable>> iterator = scheduledTasks2.iterator();

            while (iterator.hasNext()) {
                Pair<Long, Runnable> entry = iterator.next();
                if (entry.getLeft() <= currentTime) {
                    entry.getRight().run();
                    iterator.remove(); // Remove executed task from the list
                }
            }
        }
    }

}
