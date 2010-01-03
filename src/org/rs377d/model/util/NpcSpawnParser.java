package org.rs377d.model.util;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.rs377d.Configuration;
import org.rs377d.model.World;
import org.rs377d.model.npc.Npc;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class NpcSpawnParser
{

	private static DocumentBuilder builder;
	private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	public static void parse() throws Exception
	{
		builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new File(Configuration.NPC_SPAWN_FILE));
		doc.getDocumentElement().normalize();

		NodeList spawnsList = (NodeList) doc.getElementsByTagName("SPAWN");
		for (int i = 0; i < spawnsList.getLength(); i++)
		{
			Element spawnElement = (Element) spawnsList.item(i);
			int npcID = Integer.parseInt(spawnElement.getAttribute("ID"));

			Element statsElement = (Element) spawnElement.getElementsByTagName("STATS").item(0);
			int health = Integer.parseInt(statsElement.getAttribute("HEALTH"));
			int maxHit = Integer.parseInt(statsElement.getAttribute("MAX_HIT"));
			int attackSpeed = Integer.parseInt(statsElement.getAttribute("ATTACK_SPEED"));

			Element posElement = (Element) spawnElement.getElementsByTagName("POSITION").item(0);
			int posX = Integer.parseInt(posElement.getAttribute("X"));
			int posY = Integer.parseInt(posElement.getAttribute("Y"));
			int posZ = Integer.parseInt(posElement.getAttribute("Z"));
			int xRang = Integer.parseInt(posElement.getAttribute("X_RANGE"));
			int yRang = Integer.parseInt(posElement.getAttribute("Y_RANGE"));

			Npc npc = new Npc(npcID);
			Position pos = new Position(posX, posY, posZ);
			npc.setPosition(pos);
			npc.setWalkableLocations(posX - xRang, posY - yRang, posX + xRang, posY + yRang);

			npc.setAttribute("health", health);
			npc.setAttribute("maxHit", maxHit);
			npc.setAttribute("attackSpeed", attackSpeed);
			
			World.getSingleton().registerNpc(npc);
		}
	}

}
