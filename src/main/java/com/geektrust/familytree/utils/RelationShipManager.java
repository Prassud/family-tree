package com.geektrust.familytree.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geektrust.familytree.bean.RelationShipMapBean;
import com.geektrust.familytree.exception.FamilyTreeException;


import java.io.*;
import java.util.List;

import static com.geektrust.familytree.bean.RelationShipMapBean.RelationShip;

public class RelationShipManager {

	private RelationShipMapBean relationShipMapper;

	private static RelationShipManager instance = new RelationShipManager();

	private RelationShipManager() {
	}

	public void load(String mapperFileName) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			InputStream resourceAsStream = RelationShipManager.class.getClassLoader().getResourceAsStream(mapperFileName);
			RelationShipMapBean relationShioMap = objectMapper.readValue(new InputStreamReader(resourceAsStream), RelationShipMapBean.class);
			this.relationShipMapper = relationShioMap;
		} catch (IOException | NullPointerException ex) {
			throw new FamilyTreeException("Failed Build load RelationShip Mapper", ex);
		}
	}

	public List<RelationShip> getRelationShipChain(String relationShip) {
		return this.relationShipMapper.getRelationShips(relationShip);
	}

	public List<List<RelationShip>> getMultiLevelRelationShipChain(String relation) {
		return this.relationShipMapper.getMultiRelation(relation);
	}

	public static RelationShipManager getInstance() {
		return instance;
	}
}
