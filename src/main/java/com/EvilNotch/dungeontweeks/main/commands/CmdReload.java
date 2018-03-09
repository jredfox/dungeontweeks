package com.EvilNotch.dungeontweeks.main.commands;

import com.EvilNotch.dungeontweeks.main.world.worldgen.mobs.DungeonMobs;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CmdReload extends CommandBase{

	 /**
     * Gets the name of the command
     */
	@Override
    public String getName()
    {
        return "dungeonTweaksReload";
    }

    /**
     * Return the required permission level for this command.
     */
    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }
    /**
    * Gets the usage string for the command.
    */
    @Override
    public String getUsage(ICommandSender sender)
    {
       return "commands.dungeontweeks.usage";
    }

   @Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
	   DungeonMobs.cacheMobs();
	   sender.sendMessage(new TextComponentString("Dungeon Tweaks Confgurations Reloaded") );
	   DungeonMobs.printDungeonMobs();
	}

}
