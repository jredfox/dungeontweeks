package com.EvilNotch.dungeontweeks.Api;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.EvilNotch.dungeontweeks.main.MainJava;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class FieldAcess {
		
		//MCPMAPPINGS API hashmaps cached here only on pre-init
	    public static File dirmappings = null;
		public static HashMap<String,String> fieldToOb = new HashMap();
		public static HashMap<String,String> obToField = new HashMap();
		
		public static HashMap<String,String> methodToOb = new HashMap();
		public static HashMap<String,String> obToMethod = new HashMap();
		
		public static HashMap<String,String> paramToOb = new HashMap();
		public static HashMap<String,String> obToParam = new HashMap();
		
		public static void CacheMCP(File dir)
		{
			dirmappings = new File(dir,"rplib/mcp/" + MinecraftForge.MC_VERSION);
			if(!dirmappings.exists())
				dirmappings.mkdirs();
			String strfield = "/assets/rplib/mcp/" + MinecraftForge.MC_VERSION + "/fields.csv";
			String strmethod = "/assets/rplib/mcp/" + MinecraftForge.MC_VERSION + "/methods.csv";
			String strparams = "/assets/rplib/mcp/" + MinecraftForge.MC_VERSION + "/params.csv";
			File dirFields = new File(dirmappings,"fields.csv");
			File dirMethods = new File(dirmappings,"methods.csv");
			File dirParams = new File(dirmappings,"params.csv");
			MainJava.moveFileFromJar(MainJava.class, strfield,dirFields, false);
			MainJava.moveFileFromJar(MainJava.class, strmethod, dirMethods, false);
			MainJava.moveFileFromJar(MainJava.class, strparams, dirParams, false);
			CSVE field = new CSVE(dirFields);
			CSVE method = new CSVE(dirMethods);
			CSVE par = new CSVE(dirParams);
			
			for(CSV csv : field.list)
			{
				fieldToOb.put(csv.list.get(1), csv.list.get(0));
				obToField.put(csv.list.get(0), csv.list.get(1));
			}
			for(CSV csv : method.list)
			{
				methodToOb.put(csv.list.get(1), csv.list.get(0));
				obToMethod.put(csv.list.get(0), csv.list.get(1));
			}
			for(CSV csv : par.list)
			{
				paramToOb.put(csv.list.get(1), csv.list.get(0));
				obToParam.put(csv.list.get(0), csv.list.get(1));
			}
			
		}
}
