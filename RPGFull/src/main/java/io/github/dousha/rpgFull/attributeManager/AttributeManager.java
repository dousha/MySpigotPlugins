package io.github.dousha.rpgFull.attributeManager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import io.github.dousha.rpgFull.utils.*;

public class AttributeManager {
	// data sink
	List<Pair<Player, AttributeStruct>> _playerAttributeSink = new ArrayList<Pair<Player, AttributeStruct>>();
		
	public void loadAttributeYaml(YamlConfiguration yml){
		// when server is starting up
		// load player attribute
		
	}
	
	public void saveAttributeYaml(YamlConfiguration yml){
		// when server is shutting down
		// save player attribute
		
	}
}
