package com.EvilNotch.lib.content;

import com.EvilNotch.lib.util.minecraft.EntityUtil;
import com.EvilNotch.lib.util.registry.SpawnListEntryAdvanced;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.command.server.CommandSummon;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CmdSpawn extends CommandSummon{

	@Override
	public String getName() {
		return "spawnMob";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "commands.mobspawnage.usage";
	}
	 /**
     * Return the required permission level for this command.
     */
    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
	       if (args.length < 1)
	        {
	            throw new WrongUsageException("commands.summon.usage", new Object[0]);
	        }
	        else
	        {
	            String s = args[0];
	            BlockPos blockpos = sender.getPosition();
	            Vec3d vec3d = sender.getPositionVector();
	            double d0 = vec3d.x;
	            double d1 = vec3d.y;
	            double d2 = vec3d.z;

	            if (args.length >= 4)
	            {
	                d0 = parseDouble(d0, args[1], true);
	                d1 = parseDouble(d1, args[2], false);
	                d2 = parseDouble(d2, args[3], true);
	                blockpos = new BlockPos(d0, d1, d2);
	            }

	            World world = sender.getEntityWorld();

	            if (!world.isBlockLoaded(blockpos))
	            {
	                throw new CommandException("commands.summon.outOfWorld", new Object[0]);
	            }
	            else
	            {
	                NBTTagCompound nbttagcompound = null;
	                boolean flag = false;

	                if (args.length >= 5)
	                {
	                    String s1 = buildString(args, 4);

	                    try
	                    {
	                        nbttagcompound = JsonToNBT.getTagFromJson(s1);
	                        flag = true;
	                    }
	                    catch (NBTException nbtexception)
	                    {
	                        throw new CommandException("commands.summon.tagError", new Object[] {nbtexception.getMessage()});
	                    }
	                }
	                if(nbttagcompound != null)
	                	nbttagcompound.setString("id", s);
	                
	                ResourceLocation loc = new ResourceLocation(s);
	                boolean sucess = EntityUtil.spawnEntityEntry(world, new SpawnListEntryAdvanced(loc,-1,-1,-1, nbttagcompound),d0,d1,d2);
	                if(!sucess)
	                {
	                	throw new CommandException("commands.summon.tagError", new Object[0]);
	                }
	            }
	        }
	}

}
