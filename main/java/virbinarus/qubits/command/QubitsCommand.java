package virbinarus.qubits.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import virbinarus.qubits.block.QubitBlockTileEntity;
import virbinarus.qubits.internal.Qubit;

public class QubitsCommand {
    public QubitsCommand(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                LiteralArgumentBuilder.<CommandSource>literal("qubit").then(
                        Commands.literal("get").then(
                                Commands.argument("pos", BlockPosArgument.blockPos())
                                        .requires(cs -> cs.hasPermissionLevel(1))
                                        .executes(ctx -> {
                                            World world = ctx.getSource().getWorld();
                                            BlockPos pos = BlockPosArgument.getBlockPos(ctx, "pos");
                                            QubitBlockTileEntity tileEntity = (QubitBlockTileEntity) world.getTileEntity(pos);
                                            ctx.getSource().sendFeedback(new TranslationTextComponent(tileEntity.qubit.write().getString()), true);
                                            return 0;
                                        })
                        )
                ).then(
                        Commands.literal("not").then(
                                Commands.argument("pos", BlockPosArgument.blockPos())
                                        .requires(cs -> cs.hasPermissionLevel(1))
                                        .executes(ctx -> {
                                            World world = ctx.getSource().getWorld();
                                            BlockPos pos = BlockPosArgument.getBlockPos(ctx, "pos");
                                            QubitBlockTileEntity tileEntity = (QubitBlockTileEntity) world.getTileEntity(pos);
                                            tileEntity.qubit.applyNot(new Qubit[]{}, world);
                                            return 0;
                                        })

                        )
                ).then(
                        Commands.literal("h").then(
                                Commands.argument("pos", BlockPosArgument.blockPos())
                                        .requires(cs -> cs.hasPermissionLevel(1))
                                        .executes(ctx -> {
                                            World world = ctx.getSource().getWorld();
                                            BlockPos pos = BlockPosArgument.getBlockPos(ctx, "pos");
                                            QubitBlockTileEntity tileEntity = (QubitBlockTileEntity) world.getTileEntity(pos);
                                            tileEntity.qubit.applyH(new Qubit[]{}, world);
                                            return 0;
                                        })

                        )
                ).then(
                        Commands.literal("merge").then(
                                Commands.argument("pos1", BlockPosArgument.blockPos()).then(
                                        Commands.argument("pos2", BlockPosArgument.blockPos())
                                                .requires(cs -> cs.hasPermissionLevel(1))
                                                .executes(ctx -> {
                                                    World world = ctx.getSource().getWorld();
                                                    BlockPos pos1 = BlockPosArgument.getBlockPos(ctx, "pos1");
                                                    BlockPos pos2 = BlockPosArgument.getBlockPos(ctx, "pos2");
                                                    ((QubitBlockTileEntity) world.getTileEntity(pos1)).qubit.getQubitSystem(world).merge(
                                                            ((QubitBlockTileEntity) world.getTileEntity(pos2)).qubit.getQubitSystem(world),
                                                            world
                                                    );
                                                    return 0;
                                                })
                                )
                        )
                )
        );
    }
}