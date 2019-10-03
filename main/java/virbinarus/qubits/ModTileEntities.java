package virbinarus.qubits;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import virbinarus.qubits.block.QubitBlock;
import virbinarus.qubits.block.QubitBlockTileEntity;

import java.util.function.Supplier;

@ObjectHolder(QubitsMod.MOD_ID)
public class ModTileEntities {

    @ObjectHolder("qubit_block_tile_entity")
    public static final TileEntityType<?> QUBIT_BLOCK_TILE_ENTITY = null;
}
