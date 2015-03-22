package me.pseudoknight.chnaughty;

import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.annotations.api;
import com.laytonsmith.core.CHVersion;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.constructs.CVoid;
import com.laytonsmith.core.constructs.Construct;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.environments.CommandHelperEnvironment;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.functions.AbstractFunction;
import com.laytonsmith.core.functions.Exceptions;
import com.laytonsmith.core.functions.Exceptions.ExceptionType;
import net.minecraft.server.v1_8_R2.IChatBaseComponent;
import net.minecraft.server.v1_8_R2.PacketPlayOutChat;
import net.minecraft.server.v1_8_R2.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R2.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;

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
			String name = environment.getEnv(CommandHelperEnvironment.class).GetPlayer().getName();
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
}
