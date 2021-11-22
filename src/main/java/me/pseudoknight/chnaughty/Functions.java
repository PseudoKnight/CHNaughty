package me.pseudoknight.chnaughty;

import com.laytonsmith.PureUtilities.Common.ReflectionUtils;
import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.abstraction.MCCommandSender;
import com.laytonsmith.abstraction.MCLocation;
import com.laytonsmith.abstraction.MCPlayer;
import com.laytonsmith.abstraction.bukkit.BukkitMCLocation;
import com.laytonsmith.annotations.api;
import com.laytonsmith.core.ArgumentValidation;
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
import com.laytonsmith.core.exceptions.CRE.CREPlayerOfflineException;
import com.laytonsmith.core.exceptions.CRE.CRERangeException;
import com.laytonsmith.core.exceptions.CRE.CREThrowable;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.functions.AbstractFunction;
import com.laytonsmith.core.natives.interfaces.Mixed;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayOutPosition;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.level.ChunkCoordIntPair;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.EnumSet;

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

			float yaw = (float) ArgumentValidation.getDouble(args[1], t);
			yaw %= 360.0;
			if(yaw >= 180.0) {
				yaw -= 360.0;
			} else if(yaw < -180.0) {
				yaw += 360.0;
			}

			if(args.length == 3) {
				float pitch = (float) ArgumentValidation.getDouble(args[2], t);
				if(pitch > 90.0) {
					pitch = 90.0F;
				} else if(pitch < -90.0) {
					pitch = -90.0F;
				}
				entity.setXRot(pitch);
				//entity.lastPitch = pitch;
			}

			entity.setYRot(yaw);
			//entity.lastYaw = yaw;
			entity.setHeadRotation(yaw);
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
			PlayerConnection connection = player.getHandle().b;

			MCLocation l;
			if(!(args[args.length - 1] instanceof CArray)){
				throw new CRECastException("Expecting an array at parameter " + args.length + " of set_ploc", t);
			}
			CArray ca = (CArray) args[args.length - 1];

			l = ObjectGenerator.GetGenerator().location(ca, null, t);

			if(!l.getWorld().getName().equals(player.getWorld().getName())) {
				throw new CREIllegalArgumentException("Cannot relative teleport to another world.", t);
			}

			double x = l.getX();
			double y = l.getY();
			double z = l.getZ();
			float yaw = l.getYaw();
			float pitch = l.getPitch();

			ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(new BlockPosition(x, y, z));

			connection.d().getWorldServer().getChunkProvider().addTicket(TicketType.g, chunkcoordintpair, 1, connection.d().getId());
			connection.d().stopRiding();
			if (connection.d().isSleeping()) {
				connection.d().wakeup(true, true);
			}
			connection.a(x, y, z, yaw, pitch, EnumSet.allOf(PacketPlayOutPosition.EnumPlayerTeleportFlags.class),
					PlayerTeleportEvent.TeleportCause.PLUGIN);

			connection.d().setHeadRotation(yaw);

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
			Minecraft.Sleep(p, loc, t);
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
			double range = Minecraft.VIEW_DISTANCE;
			double raySize = 0.0D;
			Location loc;
			
			if(args.length == 0) {
				p = (Player) env.getEnv(CommandHelperEnvironment.class).GetPlayer().getHandle();
				loc = p.getEyeLocation();
			} else if(args.length == 1) {
				p = (Player) env.getEnv(CommandHelperEnvironment.class).GetPlayer().getHandle();
				range = ArgumentValidation.getDouble(args[0], t);
				loc = p.getEyeLocation();
			} else if(args.length == 2) {
				if(args[0] instanceof CArray) {
					p = null;
					loc = (Location) ObjectGenerator.GetGenerator().location(args[0], null, t).getHandle();
				} else {
					p = (Player) Static.GetPlayer(args[0].val(), t).getHandle();
					loc = p.getEyeLocation();
				}
				range = ArgumentValidation.getDouble(args[1], t);
			} else if(args.length == 3) {
				MCPlayer mcp = Static.GetPlayer(args[0].val(), t);
				p = (Player) mcp.getHandle();
				if(args[1] instanceof CArray) {
					loc = (Location) ObjectGenerator.GetGenerator().location(args[1], mcp.getWorld(), t).getHandle();
					range = ArgumentValidation.getDouble(args[2], t);
				} else {
					loc = p.getEyeLocation();
					range = ArgumentValidation.getDouble(args[1], t);
					raySize = ArgumentValidation.getDouble(args[2], t);
				}
			} else {
				MCPlayer mcp = Static.GetPlayer(args[0].val(), t);
				p = (Player) mcp.getHandle();
				loc = (Location) ObjectGenerator.GetGenerator().location(args[1], mcp.getWorld(), t).getHandle();
				range = ArgumentValidation.getDouble(args[2], t);
				raySize = ArgumentValidation.getDouble(args[3], t);
			}

			if(range == 0) {
				throw new CRERangeException("Range cannot be zero!", t);
			}
			range = Math.min(range, Minecraft.VIEW_DISTANCE);

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
				hits.set("block", ObjectGenerator.GetGenerator().location(new BukkitMCLocation(blockResult.getHitBlock().getLocation()), false), t);
				BlockFace face = blockResult.getHitBlockFace();
				hits.set("hitface", face == null ? CNull.NULL : new CString(face.name(), t), t);
			} else {
				end = loc.toVector().add(dir.multiply(range));
				hits.set("hitblock", CBoolean.FALSE, t);
				hits.set("block", CNull.NULL, t);
				hits.set("hitface", CNull.NULL, t);
			}

			MCLocation blockHitPos = new BukkitMCLocation(end.toLocation(loc.getWorld()));
			hits.set("location", ObjectGenerator.GetGenerator().location(blockHitPos, false), t);
			hits.set("origin", ObjectGenerator.GetGenerator().location(new BukkitMCLocation(loc)), t);

			BoundingBox aabb = new BoundingBox(start.getX(), start.getY(), start.getZ(), end.getX(), end.getY(), end.getZ());
			if(raySize != 0.0D) {
				aabb.expand(raySize);
			}
			Collection<org.bukkit.entity.Entity> validTargets = loc.getWorld().getNearbyEntities(aabb, entity -> entity instanceof LivingEntity && !entity.equals(p));
			CArray hitEntities = new CArray(t);
			for(org.bukkit.entity.Entity entity : validTargets) {
				BoundingBox boundingBox = entity.getBoundingBox();
				if(raySize != 0.0D) {
					boundingBox.expand(raySize);
				}
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
			return new Integer[]{0,1,2,3,4};
		}

		@Override
		public String docs() {
			return "array {[player], [location], [range], [raySize]} Returns an array of result data from a ray trace from the"
					+ " player's eye location or the given location. Result array contains the following keys:"
					+ " 'hitblock' is whether or not a block was hit;"
					+ " 'hitface' is the block face that was hit (or null);"
					+ " 'block' is the location of the block that was hit (or null);"
					+ " 'location' contains the location where the ray trace ends;"
					+ " 'origin' contains the location where the ray trace starts (useful if you don't specify a location);"
					+ " 'entities' contains an array of hit entities where each array contains a 'location' key and 'uuid' key.";
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
			return new CInt(((Player) p.getHandle()).getPing(), t);
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
			MCPlayer player = Static.GetPlayer(name, t);
			Minecraft.SendActionBarMessage(player, message);
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
	public static class open_book extends AbstractFunction {

		public Class<? extends CREThrowable>[] thrown() {
			return new Class[]{CREPlayerOfflineException.class, CREFormatException.class, CREIllegalArgumentException.class};
		}

		public boolean isRestricted() {
			return true;
		}

		public Boolean runAsync() {
			return false;
		}

		public Construct exec(Target t, Environment environment, Mixed... args) throws ConfigRuntimeException {
			MCPlayer player;
			Mixed data;
			if(args.length == 2) {
				player = Static.GetPlayer(args[0], t);
				data = args[1];
			} else {
				player = environment.getEnv(CommandHelperEnvironment.class).GetPlayer();
				Static.AssertPlayerNonNull(player, t);
				data = args[0];
			}
			if(data instanceof CArray) {
				Minecraft.OpenBook(player, (CArray) data, t);
			} else {
				Minecraft.OpenBook(player, data.val(), t);
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
			return "void {[playerName], data} Sends a virtual book to a player."
					+ " Accepts an array of pages or a hand that has a book to open."
					+ " Each page can be either JSON or a plain text. If the JSON is not formatted correctly, "
					+ " it will fall back to string output per page."
					+ " If given a hand and there's no written book in it, nothing will happen.";
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
			
			Minecraft.OpenSign(player, signLoc, t);

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
			int ticks = -1;

			if(args.length > 1) {
				p = Static.GetPlayer(args[0], t);
				arrowCount = ArgumentValidation.getInt32(args[1], t);
				if (args.length > 2) {
					ticks = ArgumentValidation.getInt32(args[2], t);
				}
			} else {
				p = env.getEnv(CommandHelperEnvironment.class).GetPlayer();
				Static.AssertPlayerNonNull(p, t);
				arrowCount = ArgumentValidation.getInt32(args[0], t);
			}

			Player player = (Player) p.getHandle();
			player.setArrowsInBody(arrowCount);
			if(ticks > -1) {
				player.setArrowCooldown(ticks);
			}
			return CVoid.VOID;
		}

		public String getName() {
			return "set_parrow_count";
		}

		public Integer[] numArgs() {
			return new Integer[]{1, 2, 3};
		}

		public String docs() {
			return "void {count | player, count, [ticks]} Sets the amount of arrows in a player's model."
					+ " Optional number of ticks the arrow count will persist until arrows start despawning again."
					+ " (default: 20 * (30 - count))";
		}

		public Version since() {
			return MSVersion.V3_3_2;
		}

	}

	@api
	public static class set_pstinger_count extends AbstractFunction {

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
			int stingers;
			if(args.length == 2){
				p = Static.GetPlayer(args[0].val(), t);
				stingers = ArgumentValidation.getInt32(args[1], t);
			} else {
				p = env.getEnv(CommandHelperEnvironment.class).GetPlayer();
				Static.AssertPlayerNonNull(p, t);
				stingers = ArgumentValidation.getInt32(args[0], t);
			}
			EntityPlayer player = ((CraftPlayer) p.getHandle()).getHandle();
			player.r(stingers);
			return CVoid.VOID;
		}

		public String getName() {
			return "set_pstinger_count";
		}

		public Integer[] numArgs() {
			return new Integer[]{1, 2};
		}

		public String docs() {
			return "void {[playerName], count} Sets the amount of bee stingers in a player's model.";
		}

		public Version since() {
			return MSVersion.V3_3_4;
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
			String hand = "MAIN_HAND";
			if(args.length == 2){
				p = Static.GetPlayer(args[0].val(), t);
				hand = args[1].val().toUpperCase();
			} else {
				p = env.getEnv(CommandHelperEnvironment.class).GetPlayer();
				Static.AssertPlayerNonNull(p, t);
				if(args.length == 1) {
					hand = args[0].val().toUpperCase();
				}
			}
			Minecraft.SwingHand(p, hand, t);
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
			return "void {[playerName], downFallOpacity, storminess} Sends a packet to the player to change their sky."
					+ " As of 1.17 the first number changes the opacity of precipitation from 0.0 - 1.0."
					+ " The second number changes the storminess of the skyt while precipitating from 0.0 - 1.0.";
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
				a = ArgumentValidation.getDouble32(args[1], t);
				b = ArgumentValidation.getDouble32(args[2], t);
			} else {
				p = env.getEnv(CommandHelperEnvironment.class).GetPlayer();
				Static.AssertPlayerNonNull(p, t);
				a = ArgumentValidation.getDouble32(args[0], t);
				b = ArgumentValidation.getDouble32(args[1], t);
			}
			Minecraft.SetSky(p, a, b);
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
			float width = ArgumentValidation.getDouble32(args[1], t);
			float height = ArgumentValidation.getDouble32(args[2], t);
			ReflectionUtils.set(Entity.class, entity, "aW", EntitySize.b(width, height));
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
