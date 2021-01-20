package ca.qc.cvm.dba.scoutlog.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.StatementResult;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;

import ca.qc.cvm.dba.scoutlog.entity.LogEntry;

public class LogDAO {
	/**
	 * Méthode permettant d'ajouter une entrée
	 * 
	 * Note : Ne changer pas la structure de la méthode! Elle
	 * permet de faire fonctionner l'ajout d'une entrée du journal.
	 * Il faut donc que la compléter.
	 * 
	 * @param l'objet avec toutes les données de la nouvelle entrée
	 * @return si la sauvegarde a fonctionnée
	 */
	public static boolean addLog(LogEntry log) {
		boolean success = false;
		
		System.out.println(log.toString());
		
		try {
			  Session session = Neo4jConnection.getConnection();
			  Map<String, Object> params = new HashMap<String, Object>();
			
			  params.put("p1", log.getDate());
			  params.put("p2", log.getName());
			  params.put("p3", log.getStatus());
			  
			if(log.getStatus() == "Normal") {
				session.run("CREATE (a:LogEntry {date: {p1}, nom: {p2}, status:{p3}})", params);
			}
			else if(log.getStatus() == "Anormal") {
				params.put("p4", log.getReasons());
				session.run("CREATE (a:LogEntry {date: {p1}, nom: {p2}, status:{p3}, reasons: {p4}})", params);
			}
			else{
				
				String key = log.getPlanetName();
				
				if (log.getImage() != null) {
					Database connection = BerkeleyConnection.getConnection();

					byte[] data = log.getImage();
					 
					try {
					    DatabaseEntry theKey = new DatabaseEntry(key.getBytes("UTF-8"));
					    DatabaseEntry theData = new DatabaseEntry(data);
					    connection.put(null, theKey, theData); 
					} 
					catch (Exception e) {
					    // Exception handling
					}
				}
				
				params.put("p4", log.getReasons());
				params.put("p5", log.getNearPlanets());
				params.put("p6", log.getPlanetName());
				params.put("p7", log.getGalaxyName());
				params.put("p8", log.isHabitable());
				params.put("p9", key);
				session.run("CREATE (a:LogEntry {date: {p1}, nom: {p2}, status:{p3}, reasons: {p4}, nearPlanets: {p5}, planetName: {p6}, galaxyName: {p7}, isHabitable: {p8}, imageKey: {p9}})"
						+ "CREATE (a)-[r:Near]->(b) WHERE a.planetName = {p6} AND b.nearPlanets = {p4}",params);
				
			}
			success = true;
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		
		return success;
	}
	
	/**
	 * Permet de retourner la liste de planètes déjà explorées
	 * 
	 * Note : Ne changer pas la structure de la méthode! Elle
	 * permet de faire fonctionner l'ajout d'une entrée du journal.
	 * Il faut donc que la compléter.
	 * 
	 * @return le nom des planètes déjà explorées
	 */
	public static List<String> getPlanetList() {
		List<String> planets = new ArrayList<String>();
		
		// Exemple...
		 
		 try {
			 	Session session = Neo4jConnection.getConnection();
				
				StatementResult result = session.run("MATCH (a:LogEntry) WHERE a.planetName IS NOT NULL RETURN a.planetName");
	
				while(result.hasNext()) {
					Record record = result.next();
					planets.add(record.get("a.planetName").asString());
					System.out.print(record.get("a.planetName").asString());
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		 	 
		planets.add("Terre");
		planets.add("Solaria");
		planets.add("Dune");
		
		return planets;
	}
	
	/**
	 * Retourne l'entrée selon sa position dans le temps.
	 * La dernière entrée est 0,
	 * l'avant dernière est 1,
	 * l'avant avant dernière est 2, etc.
	 * 
	 * Toutes les informations liées à l'entrée doivent être affectées à 
	 * l'objet retourné. 
	 * 
	 * 
	 * @param position (démarre à 0)
	 * @return
	 */
	public static LogEntry getLogEntryByPosition(int position) {
		return null;
	}
	
	/**
	 * Permet de supprimer une entrée, selon sa position 
	 *  
	 * @param position de l'entrée, identique à getLogEntryByPosition
	 * @return
	 */
	public static boolean deleteLog(int position) {
		boolean success = false;
		
		return success;
	}
	
	/**
	 * Doit retourner le nombre d'entrées dans le journal de bord
	 * 
	 * Note : Ne changer pas la structure de la méthode! Elle
	 * permet de faire fonctionner l'affichage de la liste des entrées 
	 * du journal. Il faut donc que la compléter.
	 * 
	 * @return nombre total
	 */
	public static int getNumberOfEntries() {
		return 0;
	}
	
	/**
	 * Retourne le nombre de planètes habitables
	 * 
	 * @return nombre total
	 */
	public static int getNumberOfHabitablePlanets() {
		return 0;
	}
	
	/**
	 * Retourne entre 0 et 100 la moyenne d'entrées de type exploration sur le
	 * nombre total d'entrées
	 * 
	 * @return moyenne, entre 0 et 100
	 */
	public static int getExplorationAverage() {
		return 0;
	}

	
	/**
	 * Retourne le nombre de photos sauvegardées
	 * 
	 * @return nombre total
	 */
	public static int getPhotoCount() {
		return 0;
	}
	

	/**
	 * Retourne le nom des dernières planètes explorées
	 * 
	 * @param limit nombre à retourner
	 * @return
	 */
	public static List<String> getLastVisitedPlanets(int limit) {
		List<String> planetList = new ArrayList<String>();
				
		return planetList;
	}
	
	/**
	 * Permet de trouver la galaxie avec le plus grand nombre de planètes habitables
	 * 
	 * @return le nom de la galaxie
	 */
	public static String getBestGalaxy() {
		return "";
	}
	
	/**
	 * Permet de trouver une chemin pour se rendre d'une planète à une autre 
	 * 
	 * @param fromPlanet
	 * @param toPlanet
	 * @return Liste du nom des planètes à parcourir, incluant "fromPlanet" et "toPlanet", ou null si aucun chemin trouvé
	 */
	public static List<String> getTrajectory(String fromPlanet, String toPlanet) {
		
		return null;
	}

	/**
	 * La liste des galaxies ayant le plus de planètes explorées (en ordre décroissant) 
	 * 
	 * @param limit Nombre à retourner
	 * @return List de nom des galaxies + le nombre de planètes visitées, par exemple : Andromède (7 planètes visitées), ...
	 */	
	public static List<String> getExploredGalaxies(int limit) {
		List<String> galaxyList = new ArrayList<String>();

		return galaxyList;
	}
	
	/**
	 * Suppression de toutes les données
	 */
	public static boolean deleteAll() {
		boolean success = false;
		
		return success;
	}
}
