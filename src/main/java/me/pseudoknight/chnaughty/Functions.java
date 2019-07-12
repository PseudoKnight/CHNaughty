package me.pseudoknight.chnaughty;

import com.google.gson.JsonSyntaxException;
import com.laytonsmith.PureUtilities.Common.ReflectionUtils;
import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.abstraction.MCCommandSender;
import com.laytonsmith.abstraction.MCLocation;
import com.laytonsmith.abstraction.MCPlayer;
import com.laytonsmith.abstraction.bukkit.BukkitMCLocation;
import com.laytonsmith.annotations.api;
import com.laytonsmith.core.MSVersion;
import com.laytonsmith.core.ObjectGenerator;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.CBoolean;
import com.laytonsmith.core.constructs.CDouble;
import com.laytonsmith.core.constructs.CInt;
import com.laytonsmith.core.constructs.CNull;
import com.laytonsmith.core.constructs.CString;
import com.laytonsmith.core.constructs.CVoid;
import com.laytonsmith.core.constructs.Construct;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.environments.CommandHelperEnvironment;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.exceptions.CRE.CREBadEntityException;
import com.laytonsmith.core.exceptions.CRE.CRECastException;
import com.laytonsmith.core.exceptions.CRE.CREException;
import com.laytonsmith.core.exceptions.CRE.CREFormatException;
import com.laytonsmith.core.exceptions.CRE.CREIllegalArgumentException;
import com.laytonsmith.core.exceptions.CRE.CREIndexOverflowException;
import com.laytonsmith.core.exceptions.CRE.CREInvalidWorldException;
import com.laytonsmith.core.exceptions.CRE.CRELengthException;
import com.laytonsmith.core.exceptions.CRE.CRENullPointerException;
import com.laytonsmith.core.exceptions.CRE.CREPlayerOfflineException;
import com.laytonsmith.core.exceptions.CRE.CRERangeException;
import com.laytonsmith.core.exceptions.CRE.CREThrowable;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.functions.AbstractFunction;
import com.laytonsmith.core.natives.interfaces.Mixed;
import com.mojang.datafixers.util.Either;
import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_14_R1.AttributeInstance;
import net.minecraft.server.v1_14_R1.BlockPosition;
import net.minecraft.server.v1_14_R1.BlockStateBoolean;
import net.minecraft.server.v1_14_R1.ChatMessageType;
import net.minecraft.server.v1_14_R1.ChunkCoordIntPair;
import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.EntityHuman;
import net.minecraft.server.v1_14_R1.EntityLiving;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.EntitySize;
import net.minecraft.server.v1_14_R1.EnumHand;
import net.minecraft.server.v1_14_R1.TicketType;
import net.minecraft.server.v1_14_R1.Unit;
import net.minecraft.server.v1_14_R1.GenericAttributes;
import net.minecraft.server.v1_14_R1.IBlockData;
import net.minecraft.server.v1_14_R1.IChatBaseComponent;
import net.minecraft.server.v1_14_R1.PacketDataSerializer;
import net.minecraft.server.v1_14_R1.PacketPlayOutAnimation;
import net.minecraft.server.v1_14_R1.PacketPlayOutChat;
import net.minecraft.server.v1_14_R1.PacketPlayOutGameStateChange;
import net.minecraft.server.v1_14_R1.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_14_R1.PacketPlayOutPosition;
import net.minecraft.server.v1_14_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_14_R1.PlayerConnection;
import net.minecraft.server.v1_14_R1.TileEntity;
import net.minecraft.server.v1_14_R1.TileEntitySign;
import net.minecraft.server.v1_14_R1.World;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_14_R1.CraftServer;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftMetaBook;
import org.bukkit.craftbukkit.v1_14_R1.util.CraftChatMessage;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

public class Functions {
	public static String docs() {
		return "Functions that lack a Bukkit or Spigot API interface.";
	}

	@api
	public static class set_entity_rotation extends AbstractFunction {

		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREBadEntityException.class, CRELengthException.class, CRECastException.class,
					CREIllegalArgumentException.class};
		}

		@Override
		public boolean isRestricted() {
			return true;
		}

		@Override
		public Boolean runAsync() {
			return false;
		}

		@Override
		public Construct exec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException {
			Entity entity = ((CraftEntity) Static.getEntity(args[0], t).getHandle()).getHandle();

			double yaw = Static.getDouble(args[1], t);
			yaw %= 360.0;
			if(yaw >= 180.0) {
				yaw -= 360.0;
			} else if(yaw < -180.0) {
				yaw += 360.0;
			}

			if(args.length == 3) {
				double pitch = Static.getDouble(args[2], t);
				if(pitch > 90.0) {
					pitch = 90.0;
				} else if(pitch < -90.0) {
					pitch = -90.0;
				}
				entity.pitch = (float) pitch;
			}

			entity.yaw = (float) yaw;
			return CVoid.VOID;
		}

		@Override
		public Version since() {
			return MSVersion.V3_3_2;
		}

		@Override
		public String getName() {
			return "set_entity_rotation";
		}

		@Override
		public Integer[] numArgs() {
			return new Integer[]{2, 3};
		}

		@Override
		public String docs() {
			return "void {entity, yaw, [pitch]} Sets an entity's yaw and pitch without teleporting or ejecting.";
		}
	}

	@api
	public static class relative_teleport extends AbstractFunction {

		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREPlayerOfflineException.class, CRELengthException.class, CREException.class,
					CREFormatException.class};
		}

		@Override
		public boolean isRestricted() {
			return true;
		}

		@Override
		public Boolean runAsync() {
			return false;
		}

		@Override
		public Construct exec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException {
			String name = "";
			if(args.length == 3 || args.length == 6) {
				name = args[0].val();
			} else {
				MCPlayer player = env.getEnv(CommandHelperEnvironment.class).GetPlayer();
				if(player != null) {
					name = player.getName();
				}
			}

			CraftPlayer player = (CraftPlayer) Bukkit.getServer().getPlayer(name);
			if(player == null) {
				throw new CREPlayerOfflineException("No online player by that name.", t);
			}
			PlayerConnection connection = player.getHandle().playerConnection;

			MCLocation l;
			if(!(args[args.length - 1] instanceof CArray)){
				throw new CRECastException("Expecting an array at parameter " + args.length + " of set_ploc", t);
			}
			CArray ca = (CArray) args[args.length - 1];

			l = ObjectGenerator.GetGenerator().location(ca, null, t);
			
			if(!l.getWorld().getName().equals(connection.player.getWorld().getWorldData().getName())) {
				throw new CREIllegalArgumentException("Cannot relative teleport to another world.", t);
			}
			
			double x = l.getX();
			double y = l.getY();
			double z = l.getZ();
			float yaw = l.getYaw();
			float pitch = l.getPitch();

			ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(new BlockPosition(x, y, z));

			connection.player.getWorldServer().getChunkProvider().addTicket(TicketType.POST_TELEPORT, chunkcoordintpair, 1, connection.player.getId());
			connection.player.stopRiding();
			if (connection.player.isSleeping()) {
				connection.player.wakeup(true, true, false);
			}
			connection.a(x, y, z, yaw, pitch, EnumSet.allOf(PacketPlayOutPosition.EnumPlayerTeleportFlags.class),
					PlayerTeleportEvent.TeleportCause.PLUGIN);

			connection.player.setHeadRotation(yaw);

			return CVoid.VOID;
		}

		@Override
		public Version since() {
			return MSVersion.V3_3_2;
		}

		@Override
		public String getName() {
			return "relative_teleport";
		}

		@Override
		public Integer[] numArgs() {
			return new Integer[]{1, 2};
		}

		@Override
		public String docs() {
			return "void {[playerName], location} Sets the player location relative to where they are on their client."
					+ " This can be used for smooth teleportation.";
		}
	}

	@api
	public static class psleep extends AbstractFunction {

		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREPlayerOfflineException.class, CRELengthException.class, CREException.class};
		}

		public boolean isRestricted() {
			return true;
		}

		public Boolean runAsync() {
			return false;
		}

		public Construct exec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException {
			MCPlayer p;
			MCLocation loc;
			if(args.length == 2){
				p = Static.GetPlayer(args[0].val(), t);
				loc = ObjectGenerator.GetGenerator().location(args[1], p.getWorld(), t);
			} else {
				p = env.getEnv(CommandHelperEnvironment.class).GetPlayer();
				Static.AssertPlayerNonNull(p, t);
				loc = ObjectGenerator.GetGenerator().location(args[0], p.getWorld(), t);
			}
			EntityPlayer player = ((CraftPlayer) p.getHandle()).getHandle();
			BlockPosition pos = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			Either<EntityHuman.EnumBedResult, Unit> result;
			try {
				result = player.sleep(pos);
			} catch(IllegalArgumentException ex) {
				throw new CREException("That is not a bed.", t);
			}
			result.ifRight((unit) -> {
				IBlockData blockData = player.getWorld().getType(pos);
				blockData = blockData.set(BlockStateBoolean.of("occupied"), true);
				player.getWorld().setTypeAndData(pos, blockData, 4);
			}).ifLeft((bedresult) -> {
				switch(bedresult) {
					case NOT_POSSIBLE_HERE:
						throw new CREException("It's not possible to sleep here.", t);
					case NOT_POSSIBLE_NOW:
						throw new CREException("It's not possible to sleep now.", t);
					case TOO_FAR_AWAY:
						throw new CREException("That bed is too far away.", t);
					case OBSTRUCTED:
						throw new CREException("That bed is obstructed.", t);
					case OTHER_PROBLEM:
						throw new CREException("Can't sleep for some reason.", t);
					case NOT_SAFE:
						throw new CREException("It's not safe to sleep.", t);
				}
			});
			return CVoid.VOID;
		}

		public String getName() {
			return "psleep";
		}

		public Integer[] numArgs() {
			return new Integer[]{1, 2};
		}

		public String docs() {
			return "void {[playerName], location} Sets the player sleeping at the specified bed location. Throws"
					+ " an exception when unsuccessful.";
		}

		public Version since() {
			return MSVersion.V3_3_2;
		}

	}

	@api
	public static class ray_trace extends AbstractFunction {
		
		private int MAX_RANGE = Bukkit.getViewDistance() * 16;

		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREPlayerOfflineException.class, CRELengthException.class, CREFormatException.class,
					CRERangeException.class, CRECastException.class};
		}

		@Override
		public boolean isRestricted() {
			return true;
		}

		@Override
		public Boolean runAsync() {
			return false;
		}

		@Override
		public Construct exec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException {
			Player p;
			double range = MAX_RANGE;
			Location loc;
			
			if(args.length == 0) {
				p = (Player) env.getEnv(CommandHelperEnvironment.class).GetPlayer().getHandle();
				loc = p.getEyeLocation();
			} else if(args.length == 1) {
				p = (Player) env.getEnv(CommandHelperEnvironment.class).GetPlayer().getHandle();
				range = Static.getDouble32(args[0], t);
				loc = p.getEyeLocation();
			} else if(args.length == 2) {
				if(args[0] instanceof CArray) {
					p = null;
					loc = (Location) ObjectGenerator.GetGenerator().location(args[0], null, t).getHandle();
				} else {
					p = (Player) Static.GetPlayer(args[0].val(), t).getHandle();
					loc = p.getEyeLocation();
				}
				range = Static.getDouble32(args[1], t);
			} else {
				MCPlayer mcp = Static.GetPlayer(args[0].val(), t);
				p = (Player) mcp.getHandle();
				loc = (Location) ObjectGenerator.GetGenerator().location(args[1], mcp.getWorld(), t).getHandle();
				range = Static.getDouble32(args[2], t);
			}
			
			if(range == 0) {
				throw new CRERangeException("Range cannot be zero!", t);
			}
			range = Math.min(range, MAX_RANGE);
			
			double yaw = Math.toRadians(loc.getYaw() + 90);
			double pitch = Math.toRadians(-loc.getPitch());
			Vector dir = new Vector(Math.cos(yaw) * Math.cos(pitch), Math.sin(pitch), Math.sin(yaw) * Math.cos(pitch));

			RayTraceResult blockResult = loc.getWorld().rayTraceBlocks(loc, dir, range, FluidCollisionMode.NEVER, true);
			
			Vector start = loc.toVector();
			Vector end;
			CArray hits = CArray.GetAssociativeArray(t);
			if(blockResult != null) {
				end = blockResult.getHitPosition();
				hits.set("hitblock", CBoolean.TRUE, t);
			} else {
				end = loc.toVector().add(dir.multiply(range));
				hits.set("hitblock", CBoolean.FALSE, t);
			}

			MCLocation blockHitPos = new BukkitMCLocation(end.toLocation(loc.getWorld()));
			hits.set("location", ObjectGenerator.GetGenerator().location(blockHitPos, false), t);
			hits.set("origin", ObjectGenerator.GetGenerator().location(new BukkitMCLocation(loc)), t);

			BoundingBox aabb = new BoundingBox(start.getX(), start.getY(), start.getZ(), end.getX(), end.getY(), end.getZ());
			Collection<org.bukkit.entity.Entity> validTargets = loc.getWorld().getNearbyEntities(aabb, entity -> entity instanceof LivingEntity && !entity.equals(p));
			CArray hitEntities = new CArray(t);
			for(org.bukkit.entity.Entity entity : validTargets) {
				BoundingBox boundingBox = entity.getBoundingBox();
				RayTraceResult hitResult = boundingBox.rayTrace(start, dir, range);
				if (hitResult != null) {
					CArray entityhit = CArray.GetAssociativeArray(t);
					MCLocation hitPos = new BukkitMCLocation(hitResult.getHitPosition().toLocation(loc.getWorld()));
					entityhit.set("uuid", new CString(entity.getUniqueId().toString(), t), t);
					entityhit.set("location", ObjectGenerator.GetGenerator().location(hitPos, false), t);
					hitEntities.push(entityhit, t);
				}
			}
			hits.set("entities", hitEntities, t);
			
			return hits;
		}

		@Override
		public MSVersion since() {
			return MSVersion.V3_3_2;
		}

		@Override
		public String getName() {
			return "ray_trace";
		}

		@Override
		public Integer[] numArgs() {
			return new Integer[]{0,1,2,3};
		}

		@Override
		public String docs() {
			return "array {[player, [location]], range} Returns an array of result data from a ray trace from the"
					+ " player's eye location or the given location. Result array contains the following keys:"
					+ " 'hitblock' is whether or not a block was hit; 'location' contains the location where the ray"
					+ " trace ends; 'origin' contains the location where the ray trace starts (useful if you don't"
					+ " specify a location manually); 'entities' contains an array of hit entities where each array"
					+ " contains a 'location' key and 'uuid' key.";
		}
	}

	@api
	public static class ping extends AbstractFunction {

		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREPlayerOfflineException.class, CRELengthException.class};
		}

		public boolean isRestricted() {
			return true;
		}

		public Boolean runAsync() {
			return false;
		}

		public Construct exec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException {
			MCPlayer p;
			if(args.length == 1){
				p = Static.GetPlayer(args[0].val(), t);
			} else {
				p = env.getEnv(CommandHelperEnvironment.class).GetPlayer();
				Static.AssertPlayerNonNull(p, t);
			}
			EntityPlayer player = ((CraftPlayer) p.getHandle()).getHandle();
			return new CInt(player.ping, t);
		}

		public String getName() {
			return "ping";
		}

		public Integer[] numArgs() {
			return new Integer[]{0, 1};
		}

		public String docs() {
			return "int {[playerName]} Gets the player's ping.";
		}

		public Version since() {
			return MSVersion.V3_3_2;
		}

	}
 
	@api
	public static class action_msg extends AbstractFunction {

		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREPlayerOfflineException.class};
		}

		public boolean isRestricted() {
			return true;
		}

		public Boolean runAsync() {
			return false;
		}

		public Construct exec(Target t, Environment environment, Mixed... args) throws ConfigRuntimeException {
			String name = "";
			String message;
			if(args.length == 2) {
				name = args[0].val();
				message = args[1].val();
			} else {
				MCCommandSender sender = environment.getEnv(CommandHelperEnvironment.class).GetCommandSender();
				if(sender instanceof MCPlayer) {
					name = sender.getName();
				}
				message = args[0].val();
			}
			CraftPlayer player = (CraftPlayer) Bukkit.getServer().getPlayer(name);
			if(player == null) {
				throw new CREPlayerOfflineException("No online player by that name.", t);
			}
			IChatBaseComponent actionMessage = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
			player.getHandle().playerConnection.sendPacket(new PacketPlayOutChat(actionMessage, ChatMessageType.GAME_INFO));
			return CVoid.VOID;
		}

		public String getName() {
			return "action_msg";
		}

		public Integer[] numArgs() {
			return new Integer[]{1, 2};
		}

		public String docs() {
			return "void {[playerName], message} Sends a message to the action bar.";
		}

		public Version since() {
			return MSVersion.V3_3_1;
		}

	}

	@api
	public static class title_msg extends AbstractFunction {

		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREPlayerOfflineException.class,CRERangeException.class};
		}

		public boolean isRestricted() {
			return true;
		}

		public Boolean runAsync() {
			return false;
		}

		public Construct exec(Target t, Environment environment, Mixed... args) throws ConfigRuntimeException {
			String name = "";
			int offset = 0;
			if(args.length == 3 || args.length == 6) {
				name = args[0].val();
				offset = 1;
			} else {
				MCCommandSender sender = environment.getEnv(CommandHelperEnvironment.class).GetCommandSender();
				if(sender instanceof MCPlayer) {
					name = sender.getName();
				}
			}

			CraftPlayer player = (CraftPlayer) Bukkit.getServer().getPlayer(name);
			if(player == null) {
				throw new CREPlayerOfflineException("No online player by that name.", t);
			}

			PlayerConnection connection = player.getHandle().playerConnection;

			if(args.length > 3) {
				int fadein = Static.getInt32(args[2 + offset], t);
				int stay = Static.getInt32(args[3 + offset], t);
				int fadeout = Static.getInt32(args[4 + offset], t);
				connection.sendPacket(new PacketPlayOutTitle(fadein, stay, fadeout));
			}

			if(Construct.nval(args[1 + offset]) != null) {
				IChatBaseComponent subtitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + args[1 + offset].val() + "\"}");
				connection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitle));
			}

			String title = "";
			if(Construct.nval(args[offset]) != null) {
				title = args[offset].val();
			}
			IChatBaseComponent icbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\"}");
			connection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, icbc));

			return CVoid.VOID;
		}

		public String getName() {
			return "title_msg";
		}

		public Integer[] numArgs() {
			return new Integer[]{2, 3, 5, 6};
		}

		public String docs() {
			return "void {[playerName], title, subtitle, [fadein, stay, fadeout]} Sends a title message to a player."
					+ " fadein, stay and fadeout must be integers in ticks. Defaults are 20, 60, 20 respectively."
					+ " The title or subtitle can be null. (Deprecated in favor of title())";
		}

		public Version since() {
			return MSVersion.V3_3_1;
		}

	}

	@api
	public static class psend_list_header_footer extends AbstractFunction {

		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREPlayerOfflineException.class};
		}

		public boolean isRestricted() {
			return true;
		}

		public Boolean runAsync() {
			return false;
		}

		public Construct exec(Target t, Environment environment, Mixed... args) throws ConfigRuntimeException {
			String name = "";
			int offset = 0;
			if(args.length == 3) {
				name = args[0].val();
				offset = 1;
			} else {
				MCCommandSender sender = environment.getEnv(CommandHelperEnvironment.class).GetCommandSender();
				if(sender instanceof MCPlayer) {
					name = sender.getName();
				}
			}

			CraftPlayer player = (CraftPlayer) Bukkit.getServer().getPlayer(name);
			if(player == null) {
				throw new CREPlayerOfflineException("No online player by that name.", t);
			}

			PlayerConnection connection = player.getHandle().playerConnection;

			String header = Construct.nval(args[offset]);
			String footer = Construct.nval(args[1 + offset]);

			if(header == null) {
				header = "";
			}
			if(footer == null) {
				footer = "";
			}

			PacketDataSerializer serializer = new PacketDataSerializer(Unpooled.buffer());
			serializer.a("{\"text\": \"" + header + "\"}");
			serializer.a("{\"text\": \"" + footer + "\"}");

			PacketPlayOutPlayerListHeaderFooter listPacket = new PacketPlayOutPlayerListHeaderFooter();
			try {
				listPacket.a(serializer);
				connection.sendPacket(listPacket);
			} catch(IOException ex) {
				// failed
			}

			return CVoid.VOID;
		}

		public String getName() {
			return "psend_list_header_footer";
		}

		public Integer[] numArgs() {
			return new Integer[]{2, 3};
		}

		public String docs() {
			return "void {[playerName], header, footer} Sends a header and/or footer to a player's tab list."
				+ "Header or footer can be null";
		}

		public Version since() {
			return MSVersion.V3_3_1;
		}

	}

	@api
	public static class tps extends AbstractFunction {

		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{};
		}

		public boolean isRestricted() {
			return true;
		}

		public Boolean runAsync() {
			return false;
		}

		public Construct exec(Target t, Environment environment, Mixed... args) throws ConfigRuntimeException {
			double[] recentTps = ((CraftServer)Bukkit.getServer()).getServer().recentTps;
			CArray tps = new CArray(t, 3);
			for(double d : recentTps) {
				tps.push(new CDouble(Math.min(Math.round(d * 100.0D) / 100.0D, 20.0D), t), t);
			}
			return tps;
		}

		public String getName() {
			return "tps";
		}

		public Integer[] numArgs() {
			return new Integer[]{0};
		}

		public String docs() {
			return "array {} Returns an array of average ticks per second over 5, 10 and 15 minutes.";
		}

		public Version since() {
			return MSVersion.V3_3_1;
		}

	}

	@api
	public static class get_attribute extends AbstractFunction {

		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREIllegalArgumentException.class};
		}

		public boolean isRestricted() {
			return true;
		}

		public Boolean runAsync() {
			return false;
		}

		public Construct exec(Target t, Environment environment, Mixed... args) throws ConfigRuntimeException {
			EntityLiving entity = ((CraftLivingEntity) Static.getLivingEntity(args[0], t).getHandle()).getHandle();
			AttributeInstance attribute;
			switch(args[1].val().toLowerCase()) {
				case "attackdamage":
					attribute = entity.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE);
					break;
				case "followrange":
					attribute = entity.getAttributeInstance(GenericAttributes.FOLLOW_RANGE);
					break;
				case "knockbackresistance":
					attribute = entity.getAttributeInstance(GenericAttributes.KNOCKBACK_RESISTANCE);
					break;
				case "maxhealth":
					attribute = entity.getAttributeInstance(GenericAttributes.MAX_HEALTH);
					break;
				case "flyingspeed":
					attribute = entity.getAttributeInstance(GenericAttributes.FLYING_SPEED);
					break;
				case "movementspeed":
					attribute = entity.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);
					break;
				case "attackspeed":
					attribute = entity.getAttributeInstance(GenericAttributes.ATTACK_SPEED);
					break;
				case "armor":
					attribute = entity.getAttributeInstance(GenericAttributes.ARMOR);
					break;
				case "armortoughness":
					attribute = entity.getAttributeInstance(GenericAttributes.ARMOR_TOUGHNESS);
					break;
				case "luck":
					attribute = entity.getAttributeInstance(GenericAttributes.LUCK);
					break;
				default:
					throw new CREIllegalArgumentException("Unknown attribute.", t);
			}
			try {
				return new CDouble(attribute.getValue(), t);
			} catch (NullPointerException e) {
				throw new CRENullPointerException("This mob does not have this attribute.", t);
			}

		}

		public String getName() {
			return "get_attribute";
		}

		public Integer[] numArgs() {
			return new Integer[]{2};
		}

		public String docs() {
			return "double {entity, attribute} Returns the generic attribute of the given mob. Available attributes:"
					+ " attackDamage, followRange, knockbackResistance, movementSpeed, maxHealth, attackSpeed, armor,"
					+ " armortoughness, and luck. Not all mobs will have every attribute, in which case a"
					+ " NullPointerException will be thrown.";
		}

		public Version since() {
			return MSVersion.V3_3_1;
		}

	}

	@api
	public static class set_attribute extends AbstractFunction {

		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREIllegalArgumentException.class,CRECastException.class};
		}

		public boolean isRestricted() {
			return true;
		}

		public Boolean runAsync() {
			return false;
		}

		public Construct exec(Target t, Environment environment, Mixed... args) throws ConfigRuntimeException {
			EntityLiving entity = ((CraftLivingEntity) Static.getLivingEntity(args[0], t).getHandle()).getHandle();
			AttributeInstance attribute;
			switch(args[1].val().toLowerCase()){
				case "attackdamage":
					attribute = entity.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE);
					break;
				case "followrange":
					attribute = entity.getAttributeInstance(GenericAttributes.FOLLOW_RANGE);
					break;
				case "knockbackresistance":
					attribute = entity.getAttributeInstance(GenericAttributes.KNOCKBACK_RESISTANCE);
					break;
				case "maxhealth":
					attribute = entity.getAttributeInstance(GenericAttributes.MAX_HEALTH);
					break;
				case "flyingspeed":
					attribute = entity.getAttributeInstance(GenericAttributes.FLYING_SPEED);
					break;
				case "movementspeed":
					attribute = entity.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);
					break;
				case "attackspeed":
					attribute = entity.getAttributeInstance(GenericAttributes.ATTACK_SPEED);
					break;
				case "armor":
					attribute = entity.getAttributeInstance(GenericAttributes.ARMOR);
					break;
				case "armortoughness":
					attribute = entity.getAttributeInstance(GenericAttributes.ARMOR_TOUGHNESS);
					break;
				case "luck":
					attribute = entity.getAttributeInstance(GenericAttributes.LUCK);
					break;
				default:
					throw new CREIllegalArgumentException("Unknown attribute.", t);
			}
			try {
				attribute.setValue(Static.getDouble(args[2], t));
			} catch (NullPointerException e) {
				throw new CRENullPointerException("This mob does not have this attribute.", t);
			}
			return CVoid.VOID;
		}

		public String getName() {
			return "set_attribute";
		}

		public Integer[] numArgs() {
			return new Integer[]{3};
		}

		public String docs() {
			return "void {entity, attribute, value} Sets the generic attribute of the given mob. Available attributes:"
					+ " attackDamage, followRange, knockbackResistance, movementSpeed, maxHealth, attackSpeed, armor,"
					+ " armortoughness, and luck. Not all mobs will have every attribute, in which case a"
					+ " NullPointerException will be thrown.";
		}

		public Version since() {
			return MSVersion.V3_3_1;
		}

	}

	@api
	public static class open_book extends AbstractFunction {

		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREPlayerOfflineException.class, CREFormatException.class};
		}

		public boolean isRestricted() {
			return true;
		}

		public Boolean runAsync() {
			return false;
		}

		public Construct exec(Target t, Environment environment, Mixed... args) throws ConfigRuntimeException {
			String name = "";
			Mixed pages;
			if(args.length == 2) {
				name = args[0].val();
				pages = args[1];
			} else {
				MCCommandSender sender = environment.getEnv(CommandHelperEnvironment.class).GetCommandSender();
				if(sender instanceof MCPlayer) {
					name = sender.getName();
				}
				pages = args[0];
			}

			CraftPlayer player = (CraftPlayer) Bukkit.getServer().getPlayer(name);
			if(player == null) {
				throw new CREPlayerOfflineException("No online player by that name.", t);
			}

			if(!(pages instanceof CArray)){
				throw new CREFormatException("Expected an array.", t);
			}
			ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
			BookMeta bookmeta = (BookMeta) book.getItemMeta();
			if(bookmeta == null) {
				throw new CRENullPointerException("Book meta is null. This shouldn't happen and may be a problem with the server.", t);
			}
			CArray pageArray = (CArray) pages;
			List<IChatBaseComponent> pageList = new ArrayList<>();
			for(int i = 0; i < pageArray.size(); i++) {
				String text = pageArray.get(i, t).val();
				IChatBaseComponent component;
				if(text.charAt(0) == '[' || text.charAt(0) == '{') {
					try {
						component = IChatBaseComponent.ChatSerializer.a(text);
						pageList.add(component);
						continue;
					} catch(IllegalStateException | JsonSyntaxException ex) {}
				}
				text = text.length() > 320 ? text.substring(0, 320) : text;
				component = CraftChatMessage.fromString(text, true)[0];
				pageList.add(component);
			}
			((CraftMetaBook) bookmeta).pages = pageList;
			book.setItemMeta(bookmeta);

			ItemStack currentItem = player.getInventory().getItemInMainHand();
			player.getInventory().setItemInMainHand(book);
			try {
				player.getHandle().a(CraftItemStack.asNMSCopy(book), EnumHand.MAIN_HAND);
			} finally {
				player.getInventory().setItemInMainHand(currentItem);
			}
			return CVoid.VOID;
		}

		public String getName() {
			return "open_book";
		}

		public Integer[] numArgs() {
			return new Integer[]{1, 2};
		}

		public String docs() {
			return "void {[playerName], pages} Sends a virtual book to a player. Accepts an array of pages."
					+ " Each page can be either JSON or a plain text. If the JSON is not formatted correctly, "
					+ " it will fall back to string output per page.";
		}

		public Version since() {
			return MSVersion.V3_3_2;
		}

	}
	@api
	public static class open_sign extends AbstractFunction {

		public String getName() {
			return "open_sign";
		}

		public Integer[] numArgs() {
			return new Integer[]{1, 2, 3};
		}

		public String docs() {
			return "void {[player], location, [lines]} Opens a sign editor for the given sign location. Lines must"
					+ " be an array with 4 values or null. If not provided, it'll use the lines from the given sign.";
		}

		public Construct exec(Target t, Environment environment, Mixed... args) throws ConfigRuntimeException {
			MCPlayer player;
			Mixed clocation;
			Mixed clines = null;
			if(args.length == 3) {
				player = Static.GetPlayer(args[0], t);
				clocation = args[1];
				clines = args[2];
			} else if(args.length == 2) {
				if(args[0] instanceof CArray) {
					player = environment.getEnv(CommandHelperEnvironment.class).GetPlayer();
					Static.AssertPlayerNonNull(player, t);
					clocation = args[0];
					clines = args[1];
				} else {
					player = Static.GetPlayer(args[0], t);
					clocation = args[1];
				}
			} else {
				player = environment.getEnv(CommandHelperEnvironment.class).GetPlayer();
				Static.AssertPlayerNonNull(player, t);
				clocation = args[0];
			}

			MCLocation signLoc = ObjectGenerator.GetGenerator().location(clocation, null, t);

			if(clines != null) {
				String[] lines = new String[4];
				if(!(clines instanceof CNull)) {
					if(!(clines instanceof CArray)) {
						throw new CREFormatException("Expected an array.", t);
					}
					CArray array = (CArray) clines;
					for(int i = 0; i < 4; i++) {
						lines[i] = array.get(i, t).val();
					}
				}
				player.sendSignTextChange(signLoc, lines);
			}

			World w = ((CraftWorld) signLoc.getWorld().getHandle()).getHandle();
			TileEntity te = w.getTileEntity(new BlockPosition(signLoc.getBlockX(), signLoc.getBlockY(), signLoc.getBlockZ()));
			if(!(te instanceof TileEntitySign)) {
				throw new CRECastException("This location is not a sign.", t);
			}
			TileEntitySign sign = (TileEntitySign) te;
			sign.isEditable = true;
			((CraftPlayer) player.getHandle()).getHandle().openSign(sign);
			return CVoid.VOID;
		}

		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREPlayerOfflineException.class, CREFormatException.class,
					CREInvalidWorldException.class, CRECastException.class, CREIndexOverflowException.class};
		}

		public boolean isRestricted() {
			return true;
		}

		public Boolean runAsync() {
			return false;
		}

		public Version since() {
			return MSVersion.V3_3_2;
		}

	}

	@api
	public static class set_parrow_count extends AbstractFunction {

		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREPlayerOfflineException.class, CRELengthException.class, CRECastException.class,
					CRERangeException.class};
		}

		public boolean isRestricted() {
			return true;
		}

		public Boolean runAsync() {
			return false;
		}

		public Construct exec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException {
			MCPlayer p;
			int arrowCount;
			if(args.length == 2){
				p = Static.GetPlayer(args[0].val(), t);
				arrowCount = Static.getInt32(args[1], t);
			} else {
				p = env.getEnv(CommandHelperEnvironment.class).GetPlayer();
				Static.AssertPlayerNonNull(p, t);
				arrowCount = Static.getInt32(args[0], t);
			}
			EntityPlayer player = ((CraftPlayer) p.getHandle()).getHandle();
			player.setArrowCount(arrowCount);
			return CVoid.VOID;
		}

		public String getName() {
			return "set_parrow_count";
		}

		public Integer[] numArgs() {
			return new Integer[]{1, 2};
		}

		public String docs() {
			return "void {[playerName], count} Sets the player's body arrow count.";
		}

		public Version since() {
			return MSVersion.V3_3_2;
		}

	}

	@api
	public static class pswing_hand extends AbstractFunction {

		public String getName() {
			return "pswing_hand";
		}

		public String docs() {
			return "void {[playerName], [hand]} Swing the player's hand in an attack animation. The hand parameter can"
					+ " be either main_hand (default) or off_hand. Note that this also triggers a player_interact event"
					+ " when the player is not hitting a block. The event will always have the action \"left_click_air\""
					+ " and the hand \"main_hand\".";
		}

		public Integer[] numArgs() {
			return new Integer[]{0, 1, 2};
		}

		public Construct exec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException {
			MCPlayer p;
			EnumHand hand = EnumHand.MAIN_HAND;
			if(args.length == 2){
				p = Static.GetPlayer(args[0].val(), t);
				try {
					hand = EnumHand.valueOf(args[1].val().toUpperCase());
				} catch(IllegalArgumentException ex) {
					throw new CREFormatException("Expected main_hand or off_hand but got \"" + args[1].val() + "\".", t);
				}
			} else {
				p = env.getEnv(CommandHelperEnvironment.class).GetPlayer();
				Static.AssertPlayerNonNull(p, t);
				if(args.length == 1) {
					try {
						hand = EnumHand.valueOf(args[0].val().toUpperCase());
					} catch(IllegalArgumentException ex) {
						p = Static.GetPlayer(args[0].val(), t);
					}
				}
			}
			EntityPlayer player = ((CraftPlayer) p.getHandle()).getHandle();
			int h = hand.equals(EnumHand.MAIN_HAND) ? 0 : 3;
			player.playerConnection.sendPacket(new PacketPlayOutAnimation(player, h)); // send to player
			return CVoid.VOID;
		}

		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREPlayerOfflineException.class, CRELengthException.class, CREFormatException.class};
		}

		public boolean isRestricted() {
			return true;
		}

		public Boolean runAsync() {
			return false;
		}

		public Version since() {
			return MSVersion.V3_3_2;
		}

	}

	@api
	public static class set_psky extends AbstractFunction {

		public String getName() {
			return "set_psky";
		}

		public String docs() {
			return "void {[playerName], number, number} Sends a packet to the player to change their sky color.";
		}

		public Integer[] numArgs() {
			return new Integer[]{2, 3};
		}

		public Construct exec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException {
			MCPlayer p;
			float a;
			float b;
			if(args.length == 3){
				p = Static.GetPlayer(args[0].val(), t);
				a = Static.getDouble32(args[1], t);
				b = Static.getDouble32(args[2], t);
			} else {
				p = env.getEnv(CommandHelperEnvironment.class).GetPlayer();
				Static.AssertPlayerNonNull(p, t);
				a = Static.getDouble32(args[0], t);
				b = Static.getDouble32(args[1], t);
			}
			EntityPlayer player = ((CraftPlayer) p.getHandle()).getHandle();
			player.playerConnection.sendPacket(new PacketPlayOutGameStateChange(7, a));
			player.playerConnection.sendPacket(new PacketPlayOutGameStateChange(8, b));
			return CVoid.VOID;
		}

		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREPlayerOfflineException.class, CRELengthException.class, CRERangeException.class,
					CRECastException.class};
		}

		public boolean isRestricted() {
			return true;
		}

		public Boolean runAsync() {
			return false;
		}

		public Version since() {
			return MSVersion.V3_3_2;
		}

	}

	@api
	public static class set_entity_size extends AbstractFunction {

		@Override
		public String getName() {
			return "set_entity_size";
		}

		@Override
		public Integer[] numArgs() {
			return new Integer[]{3};
		}

		@Override
		public String docs() {
			return "void {entity, width, height} Sets an entity's collision box width and height.";
		}

		@Override
		public Construct exec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException {
			Entity entity = ((CraftEntity) Static.getEntity(args[0], t).getHandle()).getHandle();
			float width = Static.getDouble32(args[1], t);
			float height = Static.getDouble32(args[2], t);
			ReflectionUtils.set(Entity.class, entity, "size", EntitySize.b(width, height));
			return CVoid.VOID;
		}

		@Override
		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREBadEntityException.class, CRELengthException.class, CRECastException.class,
					CREIllegalArgumentException.class};
		}

		@Override
		public Version since() {
			return MSVersion.V3_3_2;
		}

		@Override
		public boolean isRestricted() {
			return true;
		}

		@Override
		public Boolean runAsync() {
			return false;
		}
	}
}
