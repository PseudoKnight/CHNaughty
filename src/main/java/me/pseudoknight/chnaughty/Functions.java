package me.pseudoknight.chnaughty;

import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.abstraction.MCCommandSender;
import com.laytonsmith.abstraction.MCPlayer;
import com.laytonsmith.annotations.api;
import com.laytonsmith.core.CHVersion;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.CDouble;
import com.laytonsmith.core.constructs.CVoid;
import com.laytonsmith.core.constructs.Construct;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.environments.CommandHelperEnvironment;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.functions.AbstractFunction;
import com.laytonsmith.core.functions.Exceptions;
import com.laytonsmith.core.functions.Exceptions.ExceptionType;
import net.minecraft.server.v1_8_R3.AttributeInstance;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

import java.lang.reflect.Field;

public class Functions {
    public static String docs() {
        return "Functions that lack a Bukkit or Spigot API interface.";
    }
 
    @api
    public static class action_msg extends AbstractFunction {

        public Exceptions.ExceptionType[] thrown() {
            return new ExceptionType[]{ExceptionType.PlayerOfflineException};
        }

        public boolean isRestricted() {
            return true;
        }

        public Boolean runAsync() {
            return false; 
        }

        public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {
			MCCommandSender sender = environment.getEnv(CommandHelperEnvironment.class).GetCommandSender();
			String name = "";
			if(sender instanceof MCPlayer) {
				name = sender.getName();
			}
			String message = "";
			if(args.length == 2) {
				name = args[0].val();
				message = args[1].val();
			} else {
				message = args[0].val();
			}
			CraftPlayer player = (CraftPlayer) Bukkit.getServer().getPlayer(name);
			if(player == null) {
				throw new ConfigRuntimeException("No online player by that name.", ExceptionType.PlayerOfflineException, t);
			}
			IChatBaseComponent actionMessage = IChatBaseComponent.ChatSerializer.a("{text: \"" + message + "\"}");
			player.getHandle().playerConnection.sendPacket(new PacketPlayOutChat(actionMessage, (byte) 2));
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
            return CHVersion.V3_3_1;
        }
        
    }

	@api
	public static class title_msg extends AbstractFunction {

		public Exceptions.ExceptionType[] thrown() {
			return new ExceptionType[]{ExceptionType.PlayerOfflineException,ExceptionType.RangeException};
		}

		public boolean isRestricted() {
			return true;
		}

		public Boolean runAsync() {
			return false;
		}

		public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {
			MCCommandSender sender = environment.getEnv(CommandHelperEnvironment.class).GetCommandSender();
			String name = "";
			if(sender instanceof MCPlayer) {
				name = sender.getName();
			}
			int offset = 0;
			if(args.length == 3 || args.length == 6) {
				name = args[0].val();
				offset = 1;
			}

			CraftPlayer player = (CraftPlayer) Bukkit.getServer().getPlayer(name);
			if(player == null) {
				throw new ConfigRuntimeException("No online player by that name.", ExceptionType.PlayerOfflineException, t);
			}

			PlayerConnection connection = player.getHandle().playerConnection;

			if(args.length > 3) {
				int fadein = Static.getInt32(args[2 + offset], t);
				int stay = Static.getInt32(args[3 + offset], t);
				int fadeout = Static.getInt32(args[4 + offset], t);
				connection.sendPacket(new PacketPlayOutTitle(fadein, stay, fadeout));
			}

			if(args[1 + offset].nval() != null) {
				IChatBaseComponent subtitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + args[1 + offset].val() + "\"}");
				connection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitle));
			}

			String title = "";
			if(args[offset].nval() != null) {
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
					+ " The title or subtitle can be null.";
		}

		public Version since() {
			return CHVersion.V3_3_1;
		}

	}

	@api
	public static class psend_list_header_footer extends AbstractFunction {

		public Exceptions.ExceptionType[] thrown() {
			return new ExceptionType[]{ExceptionType.PlayerOfflineException};
		}

		public boolean isRestricted() {
			return true;
		}

		public Boolean runAsync() {
			return false;
		}

		public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {
			MCCommandSender sender = environment.getEnv(CommandHelperEnvironment.class).GetCommandSender();
			String name = "";
			if(sender instanceof MCPlayer) {
				name = sender.getName();
			}
			int offset = 0;
			if(args.length == 3) {
				name = args[0].val();
				offset = 1;
			}

			CraftPlayer player = (CraftPlayer) Bukkit.getServer().getPlayer(name);
			if(player == null) {
				throw new ConfigRuntimeException("No online player by that name.", ExceptionType.PlayerOfflineException, t);
			}

			PlayerConnection connection = player.getHandle().playerConnection;

			String header = args[offset].nval();
			String footer = args[1 + offset].nval();

			if(header == null) {
				header = "";
			}
			if(footer == null) {
				footer = "";
			}

			IChatBaseComponent listHeader = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + header + "\"}");
			IChatBaseComponent listFooter = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + footer + "\"}");

			PacketPlayOutPlayerListHeaderFooter listPacket = new PacketPlayOutPlayerListHeaderFooter(listHeader);

			try {
				Field field = listPacket.getClass().getDeclaredField("b");
				field.setAccessible(true);
				field.set(listPacket, listFooter);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				connection.sendPacket(listPacket);
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
			return CHVersion.V3_3_1;
		}

	}

	@api
	public static class tps extends AbstractFunction {

		public Exceptions.ExceptionType[] thrown() {
			return new ExceptionType[]{};
		}

		public boolean isRestricted() {
			return true;
		}

		public Boolean runAsync() {
			return false;
		}

		public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {
			double[] recentTps = MinecraftServer.getServer().recentTps;
			CArray tps = new CArray(t, 3);
			for(double d : recentTps) {
				tps.push(new CDouble(Math.min(Math.round(d * 100.0D) / 100.0D, 20.0D), t));
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
			return CHVersion.V3_3_1;
		}

	}

	@api
	public static class get_attribute extends AbstractFunction {

		public Exceptions.ExceptionType[] thrown() {
			return new ExceptionType[]{ExceptionType.IllegalArgumentException};
		}

		public boolean isRestricted() {
			return true;
		}

		public Boolean runAsync() {
			return false;
		}

		public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {
			EntityLiving entity = ((CraftLivingEntity) Static.getLivingEntity(args[0], t).getHandle()).getHandle();
			AttributeInstance attribute;
			switch (args[1].val().toLowerCase()) {
				case "attackdamage":
					attribute = entity.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE);
					break;
				case "followrange":
					attribute = entity.getAttributeInstance(GenericAttributes.FOLLOW_RANGE);
					break;
				case "knockbackresistance":
					attribute = entity.getAttributeInstance(GenericAttributes.c);
					break;
				case "maxhealth":
					attribute = entity.getAttributeInstance(GenericAttributes.maxHealth);
					break;
				case "movementspeed":
					attribute = entity.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);
					break;
				default:
					throw new ConfigRuntimeException("Unknown attribute.", ExceptionType.IllegalArgumentException, t);
			}
			try {
				return new CDouble(attribute.getValue(), t);
			} catch (NullPointerException e) {
				throw new ConfigRuntimeException("This mob does not have this attribute.", ExceptionType.NullPointerException, t);
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
					+ " attackDamage, followRange, knockbackResistance, movementSpeed, attackDamage. Not all mobs will"
					+ " have every attribute, in which case a NullPointerException will be thrown.";
		}

		public Version since() {
			return CHVersion.V3_3_1;
		}

	}

	@api
	public static class set_attribute extends AbstractFunction {

		public Exceptions.ExceptionType[] thrown() {
			return new ExceptionType[]{ExceptionType.IllegalArgumentException,ExceptionType.CastException};
		}

		public boolean isRestricted() {
			return true;
		}

		public Boolean runAsync() {
			return false;
		}

		public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {
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
					attribute = entity.getAttributeInstance(GenericAttributes.c);
					break;
				case "maxhealth":
					attribute = entity.getAttributeInstance(GenericAttributes.maxHealth);
					break;
				case "movementspeed":
					attribute = entity.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);
					break;
				default:
					throw new ConfigRuntimeException("Unknown attribute.", ExceptionType.IllegalArgumentException, t);
			}
			try {
				attribute.setValue(Static.getDouble(args[2], t));
			} catch (NullPointerException e) {
				throw new ConfigRuntimeException("This mob does not have this attribute.", ExceptionType.NullPointerException, t);
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
					+ " attackDamage, followRange, knockbackResistance, movementSpeed, attackDamage. Not all mobs will"
					+ " have every attribute, in which case a NullPointerException will be thrown.";
		}

		public Version since() {
			return CHVersion.V3_3_1;
		}

	}
}
