package com.EvilNotch.dungeontweeks.main.commands;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
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
   /**
    * Get a list of options for when the user presses the TAB key
    */
   @Override
   public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
   {
       if (args.length == 1)
       {
           return getListOfStringsMatchingLastWord(args, EntityList.getEntityNameList());
       }
       else
       {
           return args.length > 1 && args.length <= 4 ? getTabCompletionCoordinate(args, 1, targetPos) : Collections.emptyList();
       }
   }

   @Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
	   sender.sendMessage(new TextComponentString("output custom command") );
	}

}
