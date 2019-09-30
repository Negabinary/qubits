package virbinarus.qubits.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;

public class CommandQLocalState {
    static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("qlocal")
                .requires(cs->cs.hasPermissionLevel(1))
                .executes(ctx -> {
                    ctx.getSource().sendFeedback(new TranslationTextComponent("commands.qubits.qlocal"), true);
                return 0;
                });
    }
}
