package me.leoner.jmelody.command;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Arrays;

@Slf4j
public class CommandRegister {

    public static void register(final JDA jda) {
        log.info("Starting command register.");

//        clear(jda);

        var commands = Arrays.stream(CommandEnum.values())
                .map(CommandEnum::getCommand)
                .map(CommandRegister::mapCommand)
                .toList();

        jda.getGuilds().forEach(guild -> {
            guild.updateCommands()
                    .addCommands(commands)
                    .queue();
            log.info("Guild <{}: {} /> commands registered successfully.", guild.getName(), guild.getId());
        });
    }

    private static void clear(JDA jda) {
        jda.updateCommands().queue();
        log.info("Global commands has been reset.");

        jda.getGuilds().forEach(guild -> {
            guild.updateCommands().queue();
            log.info("Guild <{}: {} /> commands has been reset.", guild.getName(), guild.getId());
        });
    }

    private static SlashCommandData mapCommand(final CommandAbstract command) {
        var slashCommand = Commands.slash(
                command.getName(),
                command.getDescription()
        );

        if (command.hasOption()) {
            slashCommand.addOptions(command.getOptions());
        }

        log.info("Commmand {} registered successfully.", command);

        return slashCommand;
    }
}
