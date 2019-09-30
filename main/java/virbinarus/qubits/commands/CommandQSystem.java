package virbinarus.qubits.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.TimeArgument;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import virbinarus.qubits.internal.qsystems.IQSystems;
import virbinarus.qubits.internal.qsystems.QSystemProvider;

import java.util.UUID;

public class CommandQSystem {
    static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("qsys")
                .then(
                        Commands.literal("count").requires(cs -> cs.hasPermissionLevel(1))
                                .executes(ctx -> {
                                    World world = ctx.getSource().getWorld();
                                    IQSystems qSystems = world.getCapability(QSystemProvider.Q_SYSTEMS).orElse(null);
                                    ctx.getSource().sendFeedback(new TranslationTextComponent(Integer.toString(qSystems.getSystemCount())), true);
                                    return 0;
                                })
                ).then(
                        Commands.literal("add").then(
                                Commands.argument("uuid", IntegerArgumentType.integer(0, 128))
                                        .requires(cs -> cs.hasPermissionLevel(1))
                                        .executes(ctx -> {
                                            World world = ctx.getSource().getWorld();
                                            IQSystems qSystems = world.getCapability(QSystemProvider.Q_SYSTEMS).orElse(null);
                                            UUID uuid = new UUID(0L, IntegerArgumentType.getInteger(ctx, "uuid"));
                                            qSystems.add_q_system(uuid);
                                            return 0;
                                        })
                        )
                );
    }
}
