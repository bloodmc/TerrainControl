package com.pg85.otg;

import com.pg85.otg.configuration.ConfigFunctionsManager;
import com.pg85.otg.configuration.CustomObjectConfigFunctionsManager;
import com.pg85.otg.configuration.PluginConfig;
import com.pg85.otg.customobjects.CustomObject;
import com.pg85.otg.customobjects.CustomObjectManager;
import com.pg85.otg.events.EventHandler;
import com.pg85.otg.events.EventPriority;
import com.pg85.otg.exception.InvalidConfigException;
import com.pg85.otg.generator.biome.BiomeModeManager;
import com.pg85.otg.generator.resource.Resource;
import com.pg85.otg.logging.LogMarker;
import com.pg85.otg.util.ChunkCoordinate;
import com.pg85.otg.util.minecraftTypes.DefaultMaterial;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class OTG
{
    /**
     * The engine that powers Open Terrain Generator.
     */
    private static OTGEngine engine;

    /**
     * The amount of different block ids that are supported. 4096 on Minecraft. 65535 with NotEnoughId's mod
     */
    public static final int SUPPORTED_BLOCK_IDS = 65535;//4096; // TODO: Test if this creates lag

    /**
     * The world depth that the engine supports. Not the actual depth the
     * world is capped at. 0 in Minecraft.
     */
    public static final int WORLD_DEPTH = 0;

    /**
     * The world height that the engine supports. Not the actual height the
     * world is capped at. 256 in Minecraft.
     */
    public static final int WORLD_HEIGHT = 256;

    /**
     * @see OTGEngine#fireCanCustomObjectSpawnEvent(CustomObject,
     * LocalWorld, int, int, int)
     */
    public static boolean fireCanCustomObjectSpawnEvent(CustomObject object, LocalWorld world, int x, int y, int z)
    {
        return engine.fireCanCustomObjectSpawnEvent(object, world, x, y, z);
    }

    /**
     * @see OTGEngine#firePopulationEndEvent(LocalWorld, Random,
     * boolean, ChunkCoordinate)
     */
    public static void firePopulationEndEvent(LocalWorld world, Random random, boolean villageInChunk, ChunkCoordinate chunkCoord)
    {
        engine.firePopulationEndEvent(world, random, villageInChunk, chunkCoord);
    }

    /**
     * @see OTGEngine#firePopulationStartEvent(LocalWorld, Random,
     * boolean, ChunkCoordinate)
     */
    public static void firePopulationStartEvent(LocalWorld world, Random random, boolean villageInChunk, ChunkCoordinate chunkCoord)
    {
        engine.firePopulationStartEvent(world, random, villageInChunk, chunkCoord);
    }

    /**
     * @see OTGEngine#fireResourceProcessEvent(Resource,
     * LocalWorld, Random, boolean, int, int)
     */
    public static boolean fireResourceProcessEvent(Resource resource, LocalWorld world, Random random, boolean villageInChunk, int chunkX,
                                                   int chunkZ)
    {
        return engine.fireResourceProcessEvent(resource, world, random, villageInChunk, chunkX, chunkZ);
    }

    /**
     * Returns the biome managers. Register your own biome manager here.
     * <p>
     * @return The biome managers.
     */
    public static BiomeModeManager getBiomeModeManager()
    {
        return engine.getBiomeModeManager();
    }

    /**
     * Returns the Resource manager.
     * <p>
     * @return The Resource manager.
     */
    public static ConfigFunctionsManager getConfigFunctionsManager()
    {
        return engine.getConfigFunctionsManager();
    }

    /**
     * Returns the Resource manager.
     * <p>
     * @return The Resource manager.
     */
    public static CustomObjectConfigFunctionsManager getCustomObjectConfigFunctionsManager()
    {
        return engine.getCustomObjectConfigFunctionsManager();
    }

    /**
     * Returns the CustomObject manager, with hooks to spawn CustomObjects.
     * <p>
     * @return The CustomObject manager.
     */
    public static CustomObjectManager getCustomObjectManager()
    {
        return engine.getCustomObjectManager();
    }

    /**
     * Returns the engine, containing the API methods.
     * <p>
     * @return The engine
     */
    public static OTGEngine getEngine()
    {
        return engine;
    }

    static HashMap<String, LocalMaterialData> cachedMaterials = new HashMap<String, LocalMaterialData>();

    /**
     * @see OTGEngine#readMaterial(String)
     */
    public static LocalMaterialData readMaterial(String name) throws InvalidConfigException
    {
    	// TODO: Make sure it won't cause problems to return the same material object multiple times, is it not changed anywhere?
    	LocalMaterialData material = cachedMaterials.get(name);
    	if(material != null)
    	{
    		return material;
    	}
    	else if(cachedMaterials.containsKey(material))
    	{
    		throw new InvalidConfigException("Cannot read block: " + name);
    	}

    	// Spigot interprets snow as SNOW_LAYER and that's how TC has always seen it too so keep it that way (even though minecraft:snow is actually a snow block).
    	if(name.toLowerCase().equals("snow"))
    	{
    		name = "SNOW_LAYER";
    	}
    	// Spigot interprets water as FLOWING_WATER and that's how TC has always seen it too so keep it that way (even though minecraft:water is actually stationary water).
    	if(name.toLowerCase().equals("water"))
    	{
    		name = "FLOWING_WATER";
    	}
    	// Spigot interprets lava as FLOWING_LAVA and that's how TC has always seen it too so keep it that way (even though minecraft:lava is actually stationary lava).
    	if(name.toLowerCase().equals("lava"))
    	{
    		name = "FLOWING_LAVA";
    	}

    	try
    	{
    		material = engine.readMaterial(name);
    	}
    	catch(InvalidConfigException ex)
    	{
    		cachedMaterials.put(name, null);
    		throw ex;
    	}

    	cachedMaterials.put(name, material);

        return material;
    }

    /**
     * @see OTGEngine#toLocalMaterialData(DefaultMaterial, int)
     */
    public static LocalMaterialData toLocalMaterialData(DefaultMaterial defaultMaterial, int blockData)
    {
        return engine.toLocalMaterialData(defaultMaterial, blockData);
    }

    /**
     * Returns the global config file.
     * <p>
     * @return The global config file.
     */
    public static PluginConfig getPluginConfig()
    {
        return engine.getPluginConfig();
    }

    /**
     * Returns the world object with the given name.
     * <p>
     * @param name The name of the world.
     * @return The world object.
     */
    public static LocalWorld getWorld(String name)
    {
        return engine.getWorld(name);
    }

    public static LocalWorld getUnloadedWorld(String name)
    {
    	return engine.getUnloadedWorld(name);
    }

    // For bukkit plugin developers, do not remove. See: https://github.com/MCTCP/TerrainControl/wiki/Developer-page
    /*
	* Convienence method to quickly get the biome name at the given
	* coordinates. Will return null if the world isn't loaded by OTG
	* <p>
	* @param worldName The world name.
	* @param x         The block x in the world.
	* @param z         The block z in the world.
	* @return The biome name, or null if the world isn't managed by Terrain
    *         Control.
    */
   public static String getBiomeName(String worldName, int x, int z)
   {
       LocalWorld world = getWorld(worldName);
       if (world == null)
       {
           // World isn't loaded by OTG
           return null;
       }
       return world.getSavedBiome(x, z).getName();
   }

   	public static LocalBiome getBiome(int id)
   	{
	   //OTG.log(LogMarker.INFO, "getBiomeAllWorlds id");
	
	   ArrayList<LocalWorld> worlds = getAllWorlds();
	   if(worlds != null)
	   {
		   for(LocalWorld world : worlds)
		   {
			   LocalBiome biome = world.getBiomeByIdOrNull(id);
			   if(biome != null)
			   {
				   return biome;
			   }
		   }
	   }
	   return null;
   	}

    public static LocalBiome getBiome(String name, String worldName)
    {
    	//OTG.log(LogMarker.INFO, "getBiomeAllWorlds name");

        ArrayList<LocalWorld> worlds = getAllWorlds();
        if(worlds != null)
        {
	        for(LocalWorld world : worlds)
	        {
	        	if(world.getName().toLowerCase().equals(worldName.toLowerCase()))
	        	{
		        	LocalBiome biome = world.getBiomeByNameOrNull(name);
		        	if(biome != null)
		        	{
		        		return biome;
		        	}
	        	}
	        }
        }
        return null;
    }

    public static ArrayList<LocalWorld> getAllWorlds()
    {
        return engine.getAllWorlds();
    }

    /**
     * Logs the messages with the given importance. Message will be
     * prefixed with [OpenTerrainGenerator], so don't do that yourself.
     * <p>
     * @param messages The messages to log.
     * @param level    The severity of the message
     */
    public static void log(LogMarker level, List<String> messages)
    {
        engine.getLogger().log(level, messages);
    }

    /**
     * Logs a format string message with the given importance. Message will
     * be prefixed with [OpenTerrainGenerator], so don't do that yourself.
     * <p>
     * @param message The messages to log formatted similar to Logger.log()
     *                with the same args.
     * @param level   The severity of the message
     * @param params  The parameters belonging to {0...} in the message
     *                string
     */
    public static void log(LogMarker level, String message, Object... params)
    {
        engine.getLogger().log(level, message, params);
    }

    /**
     * Logs the message(s) with the given importance <b>ONLY IF</b> logger
     * level matches the level provided. Message will be prefixed with
     * [OpenTerrainGenerator], so don't do that yourself.
     * <p>
     * @param ifLevel  the Log level to test for
     * @param messages The messages to log.
     */
    public static void logIfLevel(LogMarker ifLevel, List<String> messages)
    {
        engine.getLogger().logIfLevel(ifLevel, messages);
    }

    /**
     * Logs the message(s) with the given importance <b>ONLY IF</b> logger
     * level matches the level provided. Message will be prefixed with
     * [OpenTerrainGenerator], so don't do that yourself.
     * <p>
     * @param ifLevel the Log level to test for
     * @param message The messages to log formatted similar to
     *                Logger.log() with the same args.
     * @param params  The parameters belonging to {0...} in the message
     *                string
     */
    public static void logIfLevel(LogMarker ifLevel, String message, Object... params)
    {
        engine.getLogger().logIfLevel(ifLevel, message, params);
    }

    /**
     * Logs the message(s) with the given importance <b>ONLY IF</b> logger
     * level is between the min/max provided. Message will be prefixed with
     * [OpenTerrainGenerator], so don't do that yourself.
     * <p>
     * @param min      The minimum Log level to test for
     * @param max      The maximum Log level to test for
     * @param messages The messages to log.
     */
    public static void logIfLevel(LogMarker min, LogMarker max, List<String> messages)
    {
        engine.getLogger().logIfLevel(min, max, messages);
    }

    /**
     * Logs the message(s) with the given importance <b>ONLY IF</b> logger
     * level is between the min/max provided. Message will be prefixed with
     * [OpenTerrainGenerator], so don't do that yourself.
     * <p>
     * @param min     The minimum Log level to test for
     * @param max     The maximum Log level to test for
     * @param message The messages to log formatted similar to
     *                Logger.log() with the same args.
     * @param params  The parameters belonging to {0...} in the message
     *                string
     */
    public static void logIfLevel(LogMarker min, LogMarker max, String message, Object... params)
    {
        engine.getLogger().logIfLevel(min, max, message, params);
    }

    /**
     * Prints the stackTrace of the provided Throwable object
     * <p>
     * @param level The log level to log this stack trace at
     * @param e     The Throwable object to obtain stack trace information from
     */
    public static void printStackTrace(LogMarker level, Throwable e)
    {
        printStackTrace(level, e, Integer.MAX_VALUE);
    }

    /**
     * Prints the stackTrace of the provided Throwable object to a certain
     * depth
     * <p>
     * @param level    The log level to log this stack trace at
     * @param e        The Throwable object to obtain stack trace information
     *                 from
     * @param maxDepth The max number of trace elements to print
     */
    public static void printStackTrace(LogMarker level, Throwable e, int maxDepth)
    {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        engine.getLogger().log(level, stringWriter.toString());
    }

    /**
     * @see OTGEngine#registerEventHandler(EventHandler)
     */
    public static void registerEventHandler(EventHandler handler)
    {
        engine.registerEventHandler(handler);
    }

    /**
     * @see OTGEngine#registerEventHandler(EventHandler,
     * EventPriority)
     */
    public static void registerEventHandler(EventHandler handler, EventPriority priority)
    {
        engine.registerEventHandler(handler, priority);
    }

    /**
     * Sets the engine and calls its {@link OTGEngine#onStart()
     * onStart()} method.
     * <p>
     * @param engine The engine.
     */
    public static void setEngine(OTGEngine engine)
    {
        if (OTG.engine != null)
        {
            throw new IllegalStateException("Engine is already set.");
        }

        OTG.engine = engine;
        engine.onStart();
    }

    /**
     * Nulls out static references to free up memory. Should be called on
     * shutdown. Engine can be restarted after this.
     */
    public static void stopEngine()
    {
        engine.onShutdown();
        engine = null;
    }

    public static String getRegistryNameForDefaultBiome(String biomeName)
    {
    	String registryName = null;
		switch(biomeName)
		{
			case "Beach":
				registryName = "minecraft:beaches";
			break;
			case "Birch Forest Hills M":
				registryName = "minecraft:mutated_birch_forest_hills";
			break;
			case "Birch Forest Hills":
				registryName = "minecraft:birch_forest_hills";
			break;
			case "Birch Forest M":
				registryName = "minecraft:mutated_birch_forest";
			break;
			case "Birch Forest":
				registryName = "minecraft:birch_forest";
			break;
			case "Cold Beach":
				registryName = "minecraft:cold_beach";
			break;
			case "Cold Taiga Hills":
				registryName = "minecraft:taiga_cold_hills";
			break;
			case "Cold Taiga M":
				registryName = "minecraft:mutated_taiga_cold";
			break;
			case "Cold Taiga":
				registryName = "minecraft:taiga_cold";
			break;
			case "Deep Ocean":
				registryName = "minecraft:deep_ocean";
			break;
			case "Desert M":
				registryName = "minecraft:mutated_desert";
			break;
			case "Desert":
				registryName = "minecraft:desert";
			break;
			case "DesertHills":
				registryName = "minecraft:desert_hills";
			break;
			case "Extreme Hills Edge":
				registryName = "minecraft:smaller_extreme_hills";
			break;
			case "Extreme Hills M":
				registryName = "minecraft:mutated_extreme_hills";
			break;
			case "Extreme Hills":
				registryName = "minecraft:extreme_hills";
			break;
			case "Extreme Hills+ M":
				registryName = "minecraft:mutated_extreme_hills_with_trees";
			break;
			case "Extreme Hills+":
				registryName = "minecraft:extreme_hills_with_trees";
			break;
			case "Flower Forest":
				registryName = "minecraft:mutated_forest";
			break;
			case "Forest":
				registryName = "minecraft:forest";
			break;
			case "ForestHills":
				registryName = "minecraft:forest_hills";
			break;
			case "FrozenOcean":
				registryName = "minecraft:frozen_ocean";
			break;
			case "FrozenRiver":
				registryName = "minecraft:frozen_river";
			break;
			case "Hell":
				registryName = "minecraft:hell";
			break;
			case "Ice Mountains":
				registryName = "minecraft:ice_mountains";
			break;
			case "Ice Plains Spikes":
				registryName = "minecraft:mutated_ice_flats";
			break;
			case "ice Plains":
				registryName = "minecraft:ice_flats";
			break;
			case "Jungle M":
				registryName = "minecraft:mutated_jungle";
			break;
			case "Jungle":
				registryName = "minecraft:jungle";
			break;
			case "JungleEdge M":
				registryName = "minecraft:mutated_jungle_edge";
			break;
			case "JungleEdge":
				registryName = "minecraft:jungle_edge";
			break;
			case "JungleHills":
				registryName = "minecraft:jungle_hills";
			break;
			case "Mega Spruce Taiga Hills":
				registryName = "minecraft:mutated_redwood_taiga_hills";
			break;
			case "Mega Spruce Taiga":
				registryName = "minecraft:mutated_redwood_taiga";
			break;
			case "Mega Taiga Hills":
				registryName = "minecraft:redwood_taiga_hills";
			break;
			case "Mega Taiga":
				registryName = "minecraft:redwood_taiga";
			break;
			case "Mesa (Bryce)":
				registryName = "minecraft:mutated_mesa";
			break;
			case "Mesa Plateau F M":
				registryName = "minecraft:mutated_mesa_rock";
			break;
			case "Mesa Plateau F":
				registryName = "minecraft:mesa_rock";
			break;
			case "Mesa Plateau M":
				registryName = "minecraft:mutated_mesa_clear_rock";
			break;
			case "Mesa Plateau":
				registryName = "minecraft:mesa_clear_rock";
			break;
			case "Mesa":
				registryName = "minecraft:mesa";
			break;
			case "MushroomIsland":
				registryName = "minecraft:mushroom_island";
			break;
			case "MushroomIslandShore":
				registryName = "minecraft:mushroom_island_shore";
			break;
			case "Ocean":
				registryName = "minecraft:ocean";
			break;
			case "Plains":
				registryName = "minecraft:plains";
			break;
			case "River":
				registryName = "minecraft:river";
			break;
			case "Roofed Forest M":
				registryName = "minecraft:mutated_roofed_forest";
			break;
			case "Roofed Forest":
				registryName = "minecraft:roofed_forest";
			break;
			case "Savanna M":
				registryName = "minecraft:mutated_savanna";
			break;
			case "Savanna Plateau M":
				registryName = "minecraft:mutated_savanna_rock";
			break;
			case "Savanna Plateau":
				registryName = " minecraft:savanna_rock";
			break;
			case "Savanna":
				registryName = "minecraft:savanna";
			break;
			case "Sky":
				registryName = "minecraft:sky";
			break;
			case "Stone Beach":
				registryName = "minecraft:stone_beach";
			break;
			case "Sunflower Plains":
				registryName = "minecraft:mutated_plains";
			break;
			case "Swampland M":
				registryName = "minecraft:mutated_swampland";
			break;
			case "Swampland":
				registryName = "minecraft:swampland";
			break;
			case "Taiga M":
				registryName = "minecraft:mutated_taiga";
			break;
			case "Taiga":
				registryName = "minecraft:taiga";
			break;
			case "TaigaHills":
				registryName = "minecraft:taiga_hills";
			break;
			case "The Void":
				registryName = "minecraft:void";
			break;
		}
		return registryName;
    }
    
    private OTG()
    {
        // Forbidden to instantiate.
    }
}
