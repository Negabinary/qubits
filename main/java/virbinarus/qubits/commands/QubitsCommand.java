package virbinarus.qubits.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;

public class QubitsCommand {
    public QubitsCommand(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                LiteralArgumentBuilder.<CommandSource>literal("qubits")
                .then(CommandQLocalState.register())
                .then(CommandQSystem.register())
        );
    }
}
