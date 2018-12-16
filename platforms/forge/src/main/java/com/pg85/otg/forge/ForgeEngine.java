package com.pg85.otg.forge;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.pg85.otg.LocalMaterialData;
import com.pg85.otg.LocalWorld;
import com.pg85.otg.OTG;
import com.pg85.otg.OTGEngine;
import com.pg85.otg.configuration.BiomeConfig;
import com.pg85.otg.configuration.standard.PluginStandardValues;
import com.pg85.otg.exception.InvalidConfigException;
import com.pg85.otg.logging.LogMarker;
import com.pg85.otg.util.minecraftTypes.DefaultMaterial;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ForgeEngine extends OTGEngine
{
	// OTG+

    public void onSave(World world)
    {
    	//OTG.log(LogMarker.INFO, "ForgeEngine onSave");
    	ForgeWorld forgeWorld = (ForgeWorld) getWorld(world);
    	if(forgeWorld != null && forgeWorld.getObjectSpawner().saveRequired && !forgeWorld.GetWorldSession().getPreGeneratorIsRunning())
    	{
    		forgeWorld.getStructureCache().SaveToDisk();
    	}
    }

    public void ProcessPregeneratorTick()
    {
    	for(LocalWorld world : getAllWorlds())
    	{
    		((ForgeWorldSession)world.GetWorldSession()).getPregenerator().ProcessTick();
    	}
    }

	//

	protected WorldLoader worldLoader;

	public HashMap<String, BiomeConfig[]> otgBiomeIdsByWorld = new HashMap<String, BiomeConfig[]>();

    public ForgeEngine(WorldLoader worldLoader)
    {
        super(new ForgeLogger());
        this.worldLoader = worldLoader;
    }

    public Biome getRegisteredBiome(int id)
    {
    	if(ids == null)
    	{
			try {
				Field[] fields = ForgeRegistries.BIOMES.getClass().getDeclaredFields();
				for(Field field : fields)
				{
					Class<?> fieldClass = field.getType();
					if(fieldClass.equals(BiMap.class))
					{
						field.setAccessible(true);
						ids = (BiMap<Integer, Biome>)field.get(ForgeRegistries.BIOMES);
				        break;
					}
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
    	}

    	try
    	{
    		return ids.get(id);
    	} catch(NullPointerException e)
    	{
    		return null;
    	}
    }

    BiMap<Integer, Biome> ids = null;
    public int getBiomeRegistryId(Biome biome)
    {
    	if(ids == null)
    	{
			try {
				Field[] fields = ForgeRegistries.BIOMES.getClass().getDeclaredFields();
				for(Field field : fields)
				{
					Class<?> fieldClass = field.getType();
					if(fieldClass.equals(BiMap.class))
					{
						field.setAccessible(true);
						ids = (BiMap<Integer, Biome>)field.get(ForgeRegistries.BIOMES);
				        break;
					}
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
    	}

    	try
    	{
    		return biome == null ? -1 : ids.inverse().get(biome);
    	}
    	catch(NullPointerException ex)
    	{
    		return -1;
    	}
    }

    public int getBiomeRegistryId(ResourceLocation resourceLocation)
    {
    	if(ids == null)
    	{
			try {
				Field[] fields = ForgeRegistries.BIOMES.getClass().getDeclaredFields();
				for(Field field : fields)
				{
					Class<?> fieldClass = field.getType();
					if(fieldClass.equals(BiMap.class))
					{
						field.setAccessible(true);
						ids = (BiMap<Integer, Biome>)field.get(ForgeRegistries.BIOMES);
				        break;
					}
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
    	}

		if(names == null)
		{
			try {
				Field[] fields = ForgeRegistries.BIOMES.getClass().getDeclaredFields();
				boolean isFirst = true;
				for(Field field : fields)
				{
					Class<?> fieldClass = field.getType();
					if(fieldClass.equals(BiMap.class))
					{
						if(isFirst)
						{
							isFirst = false; // Skip the first BiMap, which should be id's. TODO: Make this prettier.
							continue;
						}
						field.setAccessible(true);
						names = (BiMap<ResourceLocation, Biome>)field.get(ForgeRegistries.BIOMES);
				        break;
					}
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		Biome biome = names.get(resourceLocation);
		if(biome != null)
		{
			return ids.inverse().get(biome);
		}

		if(1 == 1) { throw new RuntimeException("This should not happen, please submit a bug report to the OTG git."); }

		return -1;
    }

    public void registerForgeBiome(ResourceLocation resourceLocation, Biome biome)
    {
    	OTG.log(LogMarker.TRACE, "Registering biome " + resourceLocation.toString());

		if(names == null)
		{
			try {
				Field[] fields = ForgeRegistries.BIOMES.getClass().getDeclaredFields();
				boolean isFirst = true;
				for(Field field : fields)
				{
					Class<?> fieldClass = field.getType();
					if(fieldClass.equals(BiMap.class))
					{
						if(isFirst)
						{
							isFirst = false; // Skip the first BiMap, which should be id's. TODO: Make this prettier.
							continue;
						}
						field.setAccessible(true);
						names = (BiMap<ResourceLocation, Biome>)field.get(ForgeRegistries.BIOMES);
				        break;
					}
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

        names.put(resourceLocation, biome);
    }

    BiMap<ResourceLocation, Biome> names = null;
    public int registerForgeBiomeWithId(ResourceLocation resourceLocation, Biome biome)
    {
    	return registerForgeBiomeWithId(-1, resourceLocation, biome);
    }
    
    public int registerForgeBiomeWithId(int id, ResourceLocation resourceLocation, Biome biome)
    {
    	OTG.log(LogMarker.TRACE, "Registering biome " + resourceLocation.toString());

		BitSet biomeRegistryAvailabiltyMap = getBiomeRegistryAvailabiltyMap();
    	
    	// Get the next free id
    	if(id == -1)
    	{
    		id = biomeRegistryAvailabiltyMap.nextClearBit(0);
    	}
    	
    	if(ids == null)
    	{
			try {
				Field[] fields = ForgeRegistries.BIOMES.getClass().getDeclaredFields();
				for(Field field : fields)
				{
					Class<?> fieldClass = field.getType();
					if(fieldClass.equals(BiMap.class))
					{
						field.setAccessible(true);
						ids = (BiMap<Integer, Biome>)field.get(ForgeRegistries.BIOMES);
				        break;
					}
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
    	}

		if(names == null)
		{
			try {
				Field[] fields = ForgeRegistries.BIOMES.getClass().getDeclaredFields();
				boolean isFirst = true;
				for(Field field : fields)
				{
					Class<?> fieldClass = field.getType();
					if(fieldClass.equals(BiMap.class))
					{
						if(isFirst)
						{
							isFirst = false; // Skip the first BiMap, which should be id's. TODO: Make this prettier.
							continue;
						}
						field.setAccessible(true);
						names = (BiMap<ResourceLocation, Biome>)field.get(ForgeRegistries.BIOMES);
				        break;
					}
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		names.put(resourceLocation, biome);

        Biome biomeAtId = ids.get(id);
        if(biomeAtId != null)
        {
        	throw new RuntimeException("Tried to register biome " + resourceLocation.toString() + " to a id " + id + " but it is occupied by biome: " + biomeAtId.getRegistryName().toString() + ". This can happen when using the CustomBiomes setting in the world config or when changing mod/biome configurations for previously created worlds.");
        }

        ids.put(id, biome);

		biomeRegistryAvailabiltyMap.set(id, true); // Mark the id as used

        return id;
    }

    public void unRegisterForgeBiome(ResourceLocation resourceLocation)
    {
		OTG.log(LogMarker.TRACE, "Unregistering biome " + resourceLocation.toString());

		Biome biome = ForgeRegistries.BIOMES.getValue(resourceLocation);

		BitSet biomeRegistryAvailabiltyMap = getBiomeRegistryAvailabiltyMap();
		try
		{
			int biomeId = ((ForgeEngine)OTG.getEngine()).getBiomeRegistryId(biome);
			// If this biome uses replaceToBiomeName and has an id > 255 then it is not actually registered in the biome id
			// registry and biomeId will be 0. Check if biomeId is actually registered to this biome.
			if(biomeId > -1 && ((ForgeEngine)OTG.getEngine()).getRegisteredBiome(biomeId) == biome)
			{
				biomeRegistryAvailabiltyMap.set(biomeId, false); // This should be enough to make Forge re-use the biome id
			}
		}
		catch(IndexOutOfBoundsException ex)
		{
			// This can happen when:
			// A. The dimension was unloaded automatically because noone was in it and then the world was unloaded because the server shut down.
			// B. The dimensions was unloaded automatically because noone was in it and then deleted and recreated.

			// This can happen when a world was deleted and recreated and the index was set as "can be re-used" but when re-registering the biomes
			// it wasn't set back to "used" because it looked like the biome registry already had the biome properly registered.

			OTG.log(LogMarker.ERROR, "Could not unregister " + biome.biomeName);
			throw new RuntimeException("Whatever it is you're trying to do, we didn't write any code for it (sorry). Please contact Team OTG about this crash.");

			//biomeRegistryAvailabiltyMap.set(localBiome.getIds().getSavedId(), false); // This should be enough to make Forge re-use the biome id
		}

		if(names == null)
		{
			try {
				Field[] fields = ForgeRegistries.BIOMES.getClass().getDeclaredFields();
				boolean isFirst = true;
				for(Field field : fields)
				{
					Class<?> fieldClass = field.getType();
					if(fieldClass.equals(BiMap.class))
					{
						if(isFirst)
						{
							isFirst = false; // Skip the first BiMap, which should be id's. TODO: Make this prettier.
							continue;
						}
						field.setAccessible(true);
						names = (BiMap<ResourceLocation, Biome>)field.get(ForgeRegistries.BIOMES);
				        break;
					}
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

    	if(ids == null)
    	{
			try {
				Field[] fields = ForgeRegistries.BIOMES.getClass().getDeclaredFields();
				for(Field field : fields)
				{
					Class<?> fieldClass = field.getType();
					if(fieldClass.equals(BiMap.class))
					{
						field.setAccessible(true);
						ids = (BiMap<Integer, Biome>)field.get(ForgeRegistries.BIOMES);
				        break;
					}
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
    	}

        names.remove(resourceLocation, biome);

		try
		{
			int biomeId = ids.inverse().get(biome);
			ids.remove(biomeId, biome);
		}
		catch(NullPointerException e) { }
    }

	public BitSet getBiomeRegistryAvailabiltyMap()
	{
		BitSet biomeRegistryAvailabiltyMap = null;
		try {
			Field[] fields = ForgeRegistries.BIOMES.getClass().getDeclaredFields();
			for(Field field : fields)
			{
				Class<?> fieldClass = field.getType();
				if(fieldClass.equals(BitSet.class))
				{
					field.setAccessible(true);
					biomeRegistryAvailabiltyMap = (BitSet)field.get(ForgeRegistries.BIOMES);
			        break;
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return biomeRegistryAvailabiltyMap;
	}

    public WorldLoader getWorldLoader()
    {
    	return worldLoader;
    }

    public boolean getCartographerEnabled()
    {
    	ForgeWorld world = getOverWorld(); // If overworld is null then the overworld is not an OTG world
    	return world == null ? false : world.getConfigs().getWorldConfig().Cartographer;
    }

    public ForgeWorld getOverWorld()
    {
		ArrayList<LocalWorld> allWorlds = getAllWorlds();
		for(LocalWorld world : allWorlds)
		{
			if(((ForgeWorld)world).getWorld() != null && ((ForgeWorld)world).getWorld().provider != null && ((ForgeWorld)world).getWorld().provider.getDimension() == 0)
			{
				return (ForgeWorld)world;
			}
		}
		return null;
    }

    public ForgeWorld getWorldByDimId(int dimensionId)
    {
    	ForgeWorld forgeWorld;
    	if(dimensionId == 0)
    	{
    		forgeWorld = ((ForgeEngine)OTG.getEngine()).getOverWorld();
    	} else {
        	DimensionType dimType = DimensionManager.getProviderType(dimensionId);
    		forgeWorld = (ForgeWorld)OTG.getWorld(dimType.getName());
    	}
    	return forgeWorld;
    }

    public ForgeWorld getUnloadedWorldByDimId(int dimensionId)
    {
    	ForgeWorld forgeWorld;
    	if(dimensionId == 0)
    	{
    		forgeWorld = ((ForgeEngine)OTG.getEngine()).getOverWorld();
    	} else {
        	DimensionType dimType = DimensionManager.getProviderType(dimensionId);
    		forgeWorld = (ForgeWorld)OTG.getUnloadedWorld(dimType.getName());
    	}
    	return forgeWorld;
    }
    
    public LocalWorld getWorld(World world)
    {
    	if(world.provider.getDimension() == 0)
    	{
    		return (ForgeWorld)((ForgeEngine)OTG.getEngine()).getOverWorld();
    	}
    	else if(world.provider.getDimension() > 1)
    	{
			if(world.provider.getDimensionType().getSuffix() != null && world.provider.getDimensionType().getSuffix().equals("OTG"))
	    	{
				LocalWorld localWorld = this.worldLoader.getWorld(world.provider.getDimensionType().getName());
				if(localWorld == null)
				{
					return this.worldLoader.getUnloadedWorld(world.provider.getDimensionType().getName());
				}
	    		return this.worldLoader.getWorld(world.provider.getDimensionType().getName());
	    	}
    	}
    	LocalWorld localWorld = this.worldLoader.getWorld(world.getWorldInfo().getWorldName());
		if(localWorld == null)
		{
			return this.worldLoader.getUnloadedWorld(world.getWorldInfo().getWorldName());
		}

        return localWorld;
    }

    @Override
    public LocalWorld getWorld(String name)
    {
        return this.worldLoader.getWorld(name);
    }

    @Override
    public LocalWorld getUnloadedWorld(String name)
    {
    	return this.worldLoader.getUnloadedWorld(name);
    }

    public ArrayList<ForgeWorld> getUnloadedWorlds()
    {
    	return this.worldLoader.getUnloadedWorlds();
    }

    @Override
    public ArrayList<LocalWorld> getAllWorlds()
    {
    	return this.worldLoader.getAllWorlds();
    }
    
    @Override
    public File getTCDataFolder()
    {
        return this.worldLoader.getConfigsFolder();
    }

    @Override
    public File getGlobalObjectsDirectory()
    {
        return new File(this.getTCDataFolder(), PluginStandardValues.BO_DirectoryName);
    }

    @Override
    public LocalMaterialData readMaterial(String input) throws InvalidConfigException
    {
        return ForgeMaterialData.ofString(input);
    }

    @Override
    public LocalMaterialData toLocalMaterialData(DefaultMaterial defaultMaterial, int blockData)
    {
        return ForgeMaterialData.ofDefaultMaterial(defaultMaterial, blockData);
    }

    @Override
    public void setOTGBiomeId (String worldName, int i, BiomeConfig biomeConfig, boolean replaceExisting)
    {
    	if(!otgBiomeIdsByWorld.containsKey(worldName))
    	{
    		otgBiomeIdsByWorld.put(worldName, new BiomeConfig[1024]);
    	}
    	if(replaceExisting || otgBiomeIdsByWorld.get(worldName)[i] == null)
    	{
    		otgBiomeIdsByWorld.get(worldName)[i] = biomeConfig;
    	} else {
    		throw new RuntimeException("Tried to register OTG biome " + biomeConfig.getName() + " with id " + i + " but the id is in use by biome " + otgBiomeIdsByWorld.get(worldName)[i].getName());
    	}
    }
    
    @Override
    public BiomeConfig[] getOTGBiomeIds(String worldName)
    {
    	return otgBiomeIdsByWorld.containsKey(worldName) ? otgBiomeIdsByWorld.get(worldName) : new BiomeConfig[1024];
    }
    
	@Override
	public boolean isOTGBiomeIdAvailable(String worldName, int i)
	{
		return !otgBiomeIdsByWorld.containsKey(worldName) || otgBiomeIdsByWorld.get(worldName)[i] == null;
	}

	@Override
	public void unregisterOTGBiomeId(String worldName, int i)
	{
		otgBiomeIdsByWorld.get(worldName)[i] = null;
	}
}
