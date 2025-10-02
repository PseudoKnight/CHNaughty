package me.pseudoknight.chnaughty;

import com.laytonsmith.PureUtilities.SimpleVersion;
import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.abstraction.enums.MCVersion;
import com.laytonsmith.core.MSLog;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.extensions.AbstractExtension;
import com.laytonsmith.core.extensions.MSExtension;

@MSExtension("CHNaughty")
public class CHNaughty extends AbstractExtension {

	private static final Version VERSION = new SimpleVersion(5, 4, 0);

	public Version getVersion() {
		return VERSION;
	}

	@Override
	public void onStartup() {
		if(Static.getServer().getMinecraftVersion().lt(MCVersion.MC1_20_6)) {
			MSLog.GetLogger().e(MSLog.Tags.RUNTIME, "CHNaughty " + VERSION + " does not support versions prior to 1.20.5",
					Target.UNKNOWN);
		}
	}
}
