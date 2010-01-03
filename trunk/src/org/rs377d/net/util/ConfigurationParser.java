package org.rs377d.net.util;

import java.io.File;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.rs377d.Configuration;
import org.rs377d.net.event.EventListener;
import org.rs377d.net.event.EventListenerChain;
import org.rs377d.net.event.EventListenerManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ConfigurationParser
{

	private static DocumentBuilder builder;
	private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	public static void parse() throws Exception
	{
		builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new File("./config.xml"));
		doc.getDocumentElement().normalize();

		Element mainElement = (Element) doc.getElementsByTagName("MAIN").item(0);
		if (mainElement != null)
		{
			Configuration.HOST = mainElement.getAttribute("SERVER_HOST");
			Configuration.PORT = Integer.parseInt(mainElement.getAttribute("SERVER_PORT"));
			Configuration.TICK_RATE = Integer.parseInt(mainElement.getAttribute("TICK_RATE"));
		}

		Element entityElement = (Element) doc.getElementsByTagName("ENTITY").item(0);
		if (entityElement != null)
		{
			Configuration.MAX_PLAYERS = Integer.parseInt(entityElement.getAttribute("MAX_PLAYERS"));
			Configuration.MAX_NPCS = Integer.parseInt(entityElement.getAttribute("MAX_NPCS"));
			Configuration.DEFAULT_X = Integer.parseInt(entityElement.getAttribute("DEFAULT_X"));
			Configuration.DEFAULT_Y = Integer.parseInt(entityElement.getAttribute("DEFAULT_Y"));
			Configuration.DEFAULT_Z = Integer.parseInt(entityElement.getAttribute("DEFAULT_Z"));
		}

		Element miscElement = (Element) doc.getElementsByTagName("MISC").item(0);
		if (miscElement != null)
		{
			Configuration.RSA_KEY_FILE = miscElement.getAttribute("RSA_KEY_FILE");
			Configuration.SCRIPTS_DIR = new File(miscElement.getAttribute("SCRIPTS_DIR"));
		}

		Element chainsElement = (Element) doc.getElementsByTagName("EVENT_LISTENER_CHAINS").item(0);
		NodeList chainsList = (NodeList) chainsElement.getElementsByTagName("CHAIN");
		if (chainsElement != null)
		{
			for (int i = 0; i < chainsList.getLength(); i++)
			{
				Element chainElement = (Element) chainsList.item(i);
				String opcodesString = chainElement.getAttribute("OPCODES");
				StringTokenizer st = new StringTokenizer(opcodesString, ", ");
				int offset = 0;
				int[] opcodes = new int[st.countTokens()];
				while (st.hasMoreTokens())
					opcodes[offset++] = Integer.parseInt(st.nextToken());
				NodeList listenerList = (NodeList) chainElement.getElementsByTagName("EVENT_LISTENER");
				EventListenerChain chain = new EventListenerChain();
				for (int n = 0; n < listenerList.getLength(); n++)
				{
					Element listenerElement = (Element) listenerList.item(n);
					Class<?> clazz = Class.forName(listenerElement.getAttribute("CLASS"));
					EventListener listener = (EventListener) clazz.newInstance();
					chain.add(listener);
				}
				for (int n = 0; n < opcodes.length; n++)
					EventListenerManager.setEventListenerChain(opcodes[n], chain);
			}
		}

	}
}