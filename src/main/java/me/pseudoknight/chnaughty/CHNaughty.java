package me.pseudoknight.chnaughty;

import com.laytonsmith.PureUtilities.SimpleVersion;
import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.core.extensions.AbstractExtension;
import com.laytonsmith.core.extensions.MSExtension;

@MSExtension("CHNaughty")
public class CHNaughty extends AbstractExtension {

	private static final Version VERSION = new SimpleVersion(5, 3, 0);

	public Version getVersion() {
		return VERSION;
	}
}
