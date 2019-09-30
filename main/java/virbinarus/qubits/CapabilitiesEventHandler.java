package virbinarus.qubits;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import virbinarus.qubits.block.GlobalBooleanBlock;
import virbinarus.qubits.globalboolean.GlobalBooleanProvider;
import virbinarus.qubits.internal.qsystems.QSystemProvider;


public class CapabilitiesEventHandler {
    @SubscribeEvent
    public void attachCapabilities(AttachCapabilitiesEvent<World> event) {
        event.addCapability(new ResourceLocation(QubitsMod.MOD_ID, "q_systems"), new QSystemProvider());
        event.addCapability(new ResourceLocation(QubitsMod.MOD_ID, "global_boolean"), new GlobalBooleanProvider());
    }

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        System.out.println("OOO");
        World worldIn = event.getWorld();
        BlockPos pos = event.getPos();
        BlockState state = worldIn.getBlockState(pos);
        Block block = state.getBlock();
        System.out.println(block.getRegistryName());
        if (block instanceof GlobalBooleanBlock) {
            System.out.println("OOOO");
            ((GlobalBooleanBlock) block).onRightClickBlock(state, worldIn, pos);
            event.setUseBlock(Event.Result.ALLOW);
            event.setResult(Event.Result.ALLOW);
        }
    }
}
