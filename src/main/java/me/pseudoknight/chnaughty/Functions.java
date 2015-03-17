package me.pseudoknight.chnaughty;

import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.annotations.api;
import com.laytonsmith.core.CHVersion;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.CVoid;
import com.laytonsmith.core.constructs.Construct;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.environments.CommandHelperEnvironment;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.functions.AbstractFunction;
import com.laytonsmith.core.functions.Exceptions;
import com.laytonsmith.core.functions.Exceptions.ExceptionType;
import net.minecraft.server.v1_8_R1.ChatSerializer;
import net.minecraft.server.v1_8_R1.EnumTitleAction;
import net.minecraft.server.v1_8_R1.IChatBaseComponent;
import net.minecraft.server.v1_8_R1.PacketPlayOutChat;
import net.minecraft.server.v1_8_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R1.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;

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
			String name = environment.getEnv(CommandHelperEnvironment.class).GetPlayer().getName();
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
			IChatBaseComponent icbc = ChatSerializer.a("{text: \"" + message + "\"}");
			PacketPlayOutChat ppoc = new PacketPlayOutChat(icbc, (byte) 2);
			player.getHandle().playerConnection.sendPacket(ppoc);
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
			return new ExceptionType[]{ExceptionType.PlayerOfflineException};
		}

		public boolean isRestricted() {
			return true;
		}

		public Boolean runAsync() {
			return false;
		}

		public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {
			String name = environment.getEnv(CommandHelperEnvironment.class).GetPlayer().getName();
			CArray msg;
			if(args.length == 2) {
				name = args[0].val();
				msg = Static.getArray(args[1], t);
			} else {
				msg = Static.getArray(args[0], t);
			}

			CraftPlayer player = (CraftPlayer) Bukkit.getServer().getPlayer(name);
			if(player == null) {
				throw new ConfigRuntimeException("No online player by that name.", ExceptionType.PlayerOfflineException, t);
			}

			PlayerConnection connection = player.getHandle().playerConnection;

			int fadein = 20;
			int stay = 60;
			int fadeout = 20;
			if(msg.containsKey("fadein")) {
				fadein = Static.getInt32(msg.get("fadein", t), t);
			}
			if(msg.containsKey("stay")) {
				stay = Static.getInt32(msg.get("stay", t), t);
			}
			if(msg.containsKey("fadeout")) {
				fadeout = Static.getInt32(msg.get("fadeout", t), t);
			}
			PacketPlayOutTitle packetPlayOutTimes = new PacketPlayOutTitle(EnumTitleAction.TIMES, null, fadein, stay, fadeout);
			connection.sendPacket(packetPlayOutTimes);

			if(msg.containsKey("subtitle")) {
				IChatBaseComponent titleSub = ChatSerializer.a("{\"text\": \"" + msg.get("subtitle", t) + "\"}");
				PacketPlayOutTitle packetPlayOutSubTitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, titleSub);
				connection.sendPacket(packetPlayOutSubTitle);
			}

			String title = "";
			if(msg.containsKey("title")) {
				title = msg.get("title", t).val();
			}
			IChatBaseComponent titleMain = ChatSerializer.a("{\"text\": \"" + title + "\"}");
			PacketPlayOutTitle packetPlayOutTitle = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleMain);
			connection.sendPacket(packetPlayOutTitle);

			return CVoid.VOID;
		}

		public String getName() {
			return "title_msg";
		}

		public Integer[] numArgs() {
			return new Integer[]{1, 2};
		}

		public String docs() {
			return "void {[playerName], titleArray} Sends a title to a player. Array may contain one or more of the"
					+ " following indexes: title, subtitle, fadein, stay, fadeout. It must contain a title or subtitle"
					+ " to display to the player. fadein, stay and fadeout must be integers in ticks. Defaults are 20,"
					+ " 60, 20 respectively.";
		}

		public Version since() {
			return CHVersion.V3_3_1;
		}

	}
}
