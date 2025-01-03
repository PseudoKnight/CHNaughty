package me.pseudoknight.chnaughty;

import com.google.gson.JsonSyntaxException;
import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.abstraction.MCCommandSender;
import com.laytonsmith.abstraction.MCEntity;
import com.laytonsmith.abstraction.MCLocation;
import com.laytonsmith.abstraction.MCPlayer;
import com.laytonsmith.abstraction.bukkit.BukkitMCLocation;
import com.laytonsmith.annotations.api;
import com.laytonsmith.core.*;
import com.laytonsmith.core.compiler.CompilerEnvironment;
import com.laytonsmith.core.compiler.CompilerWarning;
import com.laytonsmith.core.compiler.FileOptions;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.CBoolean;
import com.laytonsmith.core.constructs.CDouble;
import com.laytonsmith.core.constructs.CNull;
import com.laytonsmith.core.constructs.CString;
import com.laytonsmith.core.constructs.CVoid;
import com.laytonsmith.core.constructs.Construct;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.environments.CommandHelperEnvironment;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.exceptions.CRE.*;
import com.laytonsmith.core.exceptions.ConfigCompileException;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.functions.AbstractFunction;
import com.laytonsmith.core.natives.interfaces.Mixed;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class Functions {
	public static String docs() {
		return "Functions that lack a Bukkit or Spigot API interface.";
	}

	static final int VIEW_DISTANCE = Bukkit.getViewDistance() * 16;

	@api
	public static class relative_teleport extends AbstractFunction implements Optimizable {

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
			MCPlayer p;
			if(args.length > 1) {
				p = Static.GetPlayer(args[0], t);
			} else {
				p = env.getEnv(CommandHelperEnvironment.class).GetPlayer();
				Static.AssertPlayerNonNull(p, t);
			}
			MCLocation l;
			if(!(args[args.length - 1] instanceof CArray)){
				throw new CRECastException("Expecting an array at parameter " + args.length + " of set_ploc", t);
			}
			CArray ca = (CArray) args[args.length - 1];
			l = ObjectGenerator.GetGenerator().location(ca, null, t);
			if(!l.getWorld().getName().equals(p.getWorld().getName())) {
				throw new CREIllegalArgumentException("Cannot relative teleport to another world.", t);
			}
			NMS.GetImpl().relativeTeleport(p, l, t);
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
			return "void {[player], location} Sets the player location using relative flags."
					+ " This can be used for smooth teleportation. (unsupported on Spigot)";
		}

		@Override
		public ParseTree optimizeDynamic(Target t, Environment env, Set<Class<? extends Environment.EnvironmentImpl>> envs,  List<ParseTree> children, FileOptions fileOptions) throws ConfigCompileException, ConfigRuntimeException {
			if(NMS.GetImpl() instanceof SpigotImpl) {
				env.getEnv(CompilerEnvironment.class).addCompilerWarning(fileOptions,
						new CompilerWarning(this.getName() + " is not supported on Spigot.", t, null));
			}
			return null;
		}

		@Override
		public Set<Optimizable.OptimizationOption> optimizationOptions() {
			return EnumSet.of(Optimizable.OptimizationOption.OPTIMIZE_DYNAMIC);
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
			boolean force = false;
			if(args.length == 3) {
				p = Static.GetPlayer(args[0].val(), t);
				loc = ObjectGenerator.GetGenerator().location(args[1], p.getWorld(), t);
				force = ArgumentValidation.getBooleanObject(args[2], t);
			} else if(args.length == 2) {
				if(args[0] instanceof CArray) {
					p = env.getEnv(CommandHelperEnvironment.class).GetPlayer();
					Static.AssertPlayerNonNull(p, t);
					loc = ObjectGenerator.GetGenerator().location(args[0], p.getWorld(), t);
					force = ArgumentValidation.getBooleanObject(args[1], t);
				} else {
					p = Static.GetPlayer(args[0].val(), t);
					loc = ObjectGenerator.GetGenerator().location(args[1], p.getWorld(), t);
				}
			} else {
				p = env.getEnv(CommandHelperEnvironment.class).GetPlayer();
				Static.AssertPlayerNonNull(p, t);
				loc = ObjectGenerator.GetGenerator().location(args[0], p.getWorld(), t);
			}
			try {
				boolean success = ((Player) p.getHandle()).sleep((Location) loc.getHandle(), force);
				if(!force && !success) {
					throw new CREException("Cannot sleep in the bed.", t);
				}
			} catch(IllegalArgumentException ex) {
				throw new CREException(ex.getMessage(), t);
			}
			return CVoid.VOID;
		}

		public String getName() {
			return "psleep";
		}

		public Integer[] numArgs() {
			return new Integer[]{1, 2, 3};
		}

		public String docs() {
			return "void {[player], location, [force]} Sets the player sleeping at the specified bed location."
					+ " Optionally force sleeping even if player normally wouldn't be able to."
					+ " If not forced, it will throw an exception when unsuccessful."
					+ " The following conditions must be met for a player to sleep: the location must be a bed, the player must be near it,"
					+ " it must not be obstructed, it must be night and there must not be hostile mobs nearby.";
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
			double range = VIEW_DISTANCE;
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
			range = Math.min(range, VIEW_DISTANCE);

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
			CArray tps = new CArray(t, 3);
			for(double d : NMS.GetImpl().getTPS()) {
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
			if(data instanceof CArray pages) {
				ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
				BookMeta bookmeta = (BookMeta) book.getItemMeta();
				if(bookmeta == null) {
					throw new CRENullPointerException("Book meta is null. This shouldn't happen and may be a problem with the server.", t);
				}
				for(int i = 0; i < pages.size(); i++) {
					String text = pages.get(i, t).val();
					if(!text.isEmpty() && (text.charAt(0) == '[' || text.charAt(0) == '{')) {
						try {
							bookmeta.spigot().addPage(ComponentSerializer.parse(text));
							continue;
						} catch(IllegalStateException | JsonSyntaxException ignored) {}
					}
					bookmeta.addPage(text);
				}
				bookmeta.setTitle(" ");
				bookmeta.setAuthor(" ");
				book.setItemMeta(bookmeta);
				((Player) player.getHandle()).openBook(book);
			} else {
				ItemStack book;
				if(data.val().equals("MAIN_HAND")) {
					book = ((Player) player.getHandle()).getInventory().getItemInMainHand();
				} else if(data.val().equals("OFF_HAND")) {
					book = ((Player) player.getHandle()).getInventory().getItemInMainHand();
				} else {
					throw new CREIllegalArgumentException("Invalid hand: " + data.val(), t);
				}
				if(book.getType() != Material.WRITTEN_BOOK) {
					throw new CREIllegalArgumentException("No book in the given hand.", t);
				}
				((Player) player.getHandle()).openBook(book);
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
					+ " Each page can be either JSON or a plain text. If the JSON is not formatted correctly,"
					+ " it will fall back to string output per page."
					+ " Throws IllegalArgumentException if no written book resides in the given hand.";
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
			return "void {[player], location, [side], [lines]} Opens a sign editor for the given sign location."
					+ " The side is optional, and must be FRONT or BACK. (default FRONT)"
					+ " Lines must be an array with up to 4 values or null. If not provided, it'll use the existing lines."
					+ " Throws CastException if not a sign block.";
		}

		public Construct exec(Target t, Environment environment, Mixed... args) throws ConfigRuntimeException {
			MCPlayer player;
			Mixed clocation;
			Mixed clines = null;
			Mixed cside = null;
			if(args.length == 4) {
				player = Static.GetPlayer(args[0], t);
				clocation = args[1];
				cside = args[2];
				clines = args[3];
			} else if(args.length == 3) {
				if(args[0] instanceof CArray) {
					player = environment.getEnv(CommandHelperEnvironment.class).GetPlayer();
					Static.AssertPlayerNonNull(player, t);
					clocation = args[0];
					cside = args[1];
					clines = args[2];
				} else if(args[2] instanceof CArray) {
					player = Static.GetPlayer(args[0], t);
					clocation = args[1];
					clines = args[2];
				} else {
					player = Static.GetPlayer(args[0], t);
					clocation = args[1];
					cside = args[2];
				}
			} else if(args.length == 2) {
				if(args[0] instanceof CArray) {
					player = environment.getEnv(CommandHelperEnvironment.class).GetPlayer();
					Static.AssertPlayerNonNull(player, t);
					clocation = args[0];
					if(args[1] instanceof CArray) {
						clines = args[1];
					} else {
						cside = args[1];
					}
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
			BlockState state = ((Location) signLoc.getHandle()).getBlock().getState();
			if(!(state instanceof Sign sign)) {
				throw new CRECastException("This location is not a sign.", t);
			}

			Side side = Side.FRONT;
			if(cside != null) {
				try {
					side = Side.valueOf(cside.val());
				} catch(IllegalArgumentException ex) {
					throw new CREFormatException("Invalid sign side: " + cside.val(), t);
				}
			}

			if(clines != null) {
				SignSide signSide = sign.getSide(side);
				if(clines instanceof CArray array) {
					long max = Math.min(array.size(), 4);
					for (int i = 0; i < max; i++) {
						signSide.setLine(i, array.get(i, t).val());
					}
					((Player) player.getHandle()).sendBlockUpdate((Location) signLoc.getHandle(), sign);
				} else if(clines != CNull.NULL) {
					throw new CREFormatException("Expected lines to be an array.", t);
				}
			}

			try {
				((Player) player.getHandle()).openSign((Sign) state, side);
			} catch (IllegalArgumentException ex) {
				throw new CREInvalidWorldException(ex.getMessage(), t);
			}
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
			NMS.GetImpl().setStingerCount(p, stingers, t);
			return CVoid.VOID;
		}

		public String getName() {
			return "set_pstinger_count";
		}

		public Integer[] numArgs() {
			return new Integer[]{1, 2};
		}

		public String docs() {
			return "void {[player], count} Sets the amount of bee stingers in a player's model.";
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
			return "void {[player], [hand]} Swing the player's hand in an attack animation. The hand parameter can"
					+ " be either main_hand (default) or off_hand. Note that this also triggers a player_interact event"
					+ " when the player is not hitting a block. The event will always have the action \"left_click_air\""
					+ " and the hand \"main_hand\".";
		}

		public Integer[] numArgs() {
			return new Integer[]{0, 1, 2};
		}

		public Construct exec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException {
			MCPlayer p;
			String hand = "main_hand";
			if(args.length == 2){
				p = Static.GetPlayer(args[0].val(), t);
				hand = args[1].val().toLowerCase();
			} else {
				p = env.getEnv(CommandHelperEnvironment.class).GetPlayer();
				Static.AssertPlayerNonNull(p, t);
				if(args.length == 1) {
					hand = args[0].val().toLowerCase();
				}
			}
			Player player = ((Player) p.getHandle());
			if(hand.isEmpty() || hand.equals("main_hand")) {
				player.swingMainHand();
			} else if(hand.equals("off_hand")) {
				player.swingOffHand();
			} else {
				throw new CREFormatException("Expected main_hand or off_hand but got \"" + hand + "\".", t);
			}
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
			return "void {entity, width, height} Sets an entity collision box's width and height used for movement."
					+ " This gets reset every time the entity's pose changes and isn't used when entity is stationary."
					+ " It's better to use the GENERIC_SCALE attribute where possible."
					+ " However, this can still be used when you need to decouple the collision box's width and height,"
					+ " or to decouple visual size and collision box size.";
		}

		@Override
		public Construct exec(Target t, Environment env, Mixed... args) throws ConfigRuntimeException {
			float width = ArgumentValidation.getDouble32(args[1], t);
			float height = ArgumentValidation.getDouble32(args[2], t);
			MCEntity entity = Static.getEntity(args[0], t);
			NMS.GetImpl().setEntitySize(entity, width, height);
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
