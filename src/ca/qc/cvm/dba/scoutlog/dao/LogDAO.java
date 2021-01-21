package ca.qc.cvm.dba.scoutlog.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.StatementResult;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

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
				session.run("CREATE (a:LogEntry {date: {p1}, name: {p2}, status:{p3}})", params);
			}
			else if(log.getStatus() == "Anormal") {
				params.put("p4", log.getReasons());
				session.run("CREATE (a:LogEntry {date: {p1}, name: {p2}, status:{p3}, reasons: {p4}})", params);
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
						e.printStackTrace();
					}
				}
				
				params.put("p4", log.getNearPlanets());
				params.put("p5", log.getPlanetName());
				params.put("p6", log.getGalaxyName());
				params.put("p7", log.isHabitable());
				params.put("p8", key);
				session.run("CREATE (a:LogEntry {date: {p1}, name: {p2}, status:{p3}, nearPlanets: {p4}, planetName: {p5}, galaxyName: {p6}, isHabitable: {p7}, imageKey: {p8}})",params);
				
				for(String planet : log.getNearPlanets()) {
					params.put("p9", planet);
					
					session.run("MATCH (a:LogEntry),(b:LogEntry) WHERE a.planetName = {p5} AND b.planetName = {p9} CREATE (a)-[:Near]->(b)-[:Near]->(a)",params);	
				}
				
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
		
		LogEntry log = null;
		
		 try {
			 	Session session = Neo4jConnection.getConnection();
				
				StatementResult index = session.run("CALL db.indexes() YIELD description WHERE description contains ':LogEntry(date)' RETURN *");
				if(!index.hasNext()) {
					session.run("CREATE INDEX ON :LogEntry(date)");
				} 
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("p1", position);
				StatementResult result = session.run("MATCH (a:LogEntry) RETURN a ORDER BY a.date DESC  SKIP {p1} LIMIT 1", params);
				
				
				if(result.hasNext()) {
					Record record = result.next();
					Node node = record.get("a").asNode();
					
					if(node.get("status").asString().equals("Normal")) {
                        log = new LogEntry(node.get("date").asString(), node.get("name").asString(), node.get("status").asString());
                    }
                    else if(node.get("status").asString().equals( "Anormal")) {
                        log = new LogEntry(node.get("date").asString(), node.get("name").asString(), node.get("status").asString(), node.get("reasons").asString());
                    }
                    else {
                        
                        byte[] retData = null;
                        Database connection = BerkeleyConnection.getConnection();

                        String key = node.get("imageKey").asString();

                        try {
                            DatabaseEntry theKey = new DatabaseEntry(key.getBytes("UTF-8"));
                            DatabaseEntry theData = new DatabaseEntry();
                         
                            if (connection.get(null, theKey, theData, LockMode.DEFAULT) == OperationStatus.SUCCESS) { 
                                retData = theData.getData();

                            } 
                        
                        } 
                        catch (Exception e) {
                        	e.printStackTrace();
                        }
                          
                        List<String> nearPlanets = new ArrayList<String>();
                        
                        for(Object planet : node.get("nearPlanets").asList()) {
                        	nearPlanets.add(planet.toString());
                        }

                        log = new LogEntry(node.get("date").asString(), node.get("name").asString(), node.get("status").asString(), node.get("reasons").asString(), nearPlanets, node.get("planetName").asString(), node.get("galaxyName").asString(),retData,node.get("isHabitable").asBoolean()); //nearPlanets: {p4}, planetName: {p5}, galaxyName: {p6}, isHabitable: {p7}, imageKey
                    }
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		return log;
	}
	
	/**
	 * Permet de supprimer une entrée, selon sa position 
	 *  
	 * @param position de l'entrée, identique à getLogEntryByPosition
	 * @return
	 */
	public static boolean deleteLog(int position) {
		boolean success = false;
		
		 try {
			 Session session = Neo4jConnection.getConnection();
				
			 Map<String, Object> params = new HashMap<String, Object>();
			 params.put("p1", position);
			 StatementResult result = session.run("MATCH (a:LogEntry) RETURN a.date,a.imageKey ORDER BY a.date DESC  SKIP {p1} LIMIT 1", params);
			
			 if(result.hasNext()) {
				 Record record = result.next();
				 String date = record.get("a.date").asString();
				 String key = record.get("a.imageKey").asString();
				 params = new HashMap<String, Object>();
				 params.put("p2", date);
				 session.run("MATCH (a:LogEntry) WHERE a.date = {p2} DETACH DELETE (a)", params);
				 
				 Database connection = BerkeleyConnection.getConnection();

				 DatabaseEntry theKey = new DatabaseEntry(key.getBytes("UTF-8"));
				 connection.delete(null, theKey);
				 success = true;
			}

			}
			catch (Exception e) {
				e.printStackTrace();
			}
		
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
		
		int nbNodes =0;
		
		 try {
			 	Session session = Neo4jConnection.getConnection();
				
			 	StatementResult result = session.run("MATCH (n) RETURN COUNT(n) as nbNode");
				nbNodes = result.next().get("nbNode").asInt();
	
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		
		
		return nbNodes;
	}
	
	/**
	 * Retourne le nombre de planètes habitables
	 * 
	 * @return nombre total
	 */
	public static int getNumberOfHabitablePlanets() {
		
		int nbHabitable = 0;
		
		try {
			Session session = Neo4jConnection.getConnection();
		 	
		 	StatementResult result = session.run("MATCH (a:LogEntry) WHERE a.isHabitable = true RETURN COUNT(a) AS nbHabitable");
		 	nbHabitable = result.next().get("nbHabitable").asInt();
		 
		 	
		}
		catch(Exception e) {
			e.printStackTrace();
		}

	 	
		 return nbHabitable;
	}
	
	/**
	 * Retourne entre 0 et 100 la moyenne d'entrées de type exploration sur le
	 * nombre total d'entrées
	 * 
	 * @return moyenne, entre 0 et 100
	 */
	public static int getExplorationAverage() {
		int expCount = 0;
		int total = 0;
		int expAvg = 0;
		
		 try {
			 	Session session = Neo4jConnection.getConnection();
			 	
				
			 	StatementResult result = session.run("MATCH (a:LogEntry) RETURN COUNT(a) AS total");
			 	StatementResult result2 = session.run("MATCH (a:LogEntry) WHERE a.status = 'Exploration' RETURN COUNT(a) AS expCount");
			 	expCount = result2.next().get("expCount").asInt();
			 	total = result.next().get("total").asInt();
			 	
			 	expAvg = (expCount*100)/total;		
			 	
	
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		
		return expAvg;
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
		
		   
        try {
             Session session = Neo4jConnection.getConnection();
             
             Map<String, Object> params = new HashMap<String, Object>();
			params.put("p1", limit);
            
             StatementResult result = session.run("MATCH (a:LogEntry) WHERE a.status = 'Exploration' RETURN a.planetName ORDER BY a.date DESC LIMIT {p1}",params);
            
            while(result.hasNext()) {
                Record record = result.next();
                planetList.add(record.get("a.planetName").asString());
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
				
		return planetList;
	}
	
	/**
	 * Permet de trouver la galaxie avec le plus grand nombre de planètes habitables
	 * 
	 * @return le nom de la galaxie
	 */
	public static String getBestGalaxy() {
        
        String bestGalaxy = null;
        
        try {
             Session session = Neo4jConnection.getConnection();
            
            StatementResult result = session.run("MATCH (a:LogEntry) WHERE a.isHabitable = true WITH a.galaxyName AS galName, COUNT(a.galaxyName) AS nb RETURN galName ORDER BY nb DESC LIMIT 1");
            
            while(result.hasNext()) {
                Record record = result.next();
                bestGalaxy = record.get("galName").asString();
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return bestGalaxy;
    }
	
	/**
	 * Permet de trouver une chemin pour se rendre d'une planète à une autre 
	 * 
	 * @param fromPlanet
	 * @param toPlanet
	 * @return Liste du nom des planètes à parcourir, incluant "fromPlanet" et "toPlanet", ou null si aucun chemin trouvé
	 */
	public static List<String> getTrajectory(String fromPlanet, String toPlanet) {
		
		List<String> trajectory = new ArrayList<String>();
		try {
			  				
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("p1", fromPlanet );
				params.put("p2", toPlanet );
				
				if(fromPlanet.equals(toPlanet)) {
					trajectory.add(fromPlanet);
				}
				else {
				
					List<Object> trajet = Neo4jConnection.getPath("MATCH (a:LogEntry), (b:LogEntry), p = shortestPath((a)-[*]->(b)) WHERE a.planetName = {p1} AND b.planetName = {p2} RETURN p as path", params);
		
					if (trajet.size() > 0) {
						for (Object o : trajet) {
							if (o instanceof Node) {
								Node node = (Node)o;
								trajectory.add(node.get("planetName").asString());
							}					
						}				
					}
					else {
						trajectory = null;
					}
				}
				
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		
		return trajectory;
	}

	/**
	 * La liste des galaxies ayant le plus de planètes explorées (en ordre décroissant) 
	 * 
	 * @param limit Nombre à retourner
	 * @return List de nom des galaxies + le nombre de planètes visitées, par exemple : Andromède (7 planètes visitées), ...
	 */	
	public static List<String> getExploredGalaxies(int limit) {
		List<String> galaxyList = new ArrayList<String>();
		
		 try {
			 	Session session = Neo4jConnection.getConnection();
				
				StatementResult index = session.run("CALL db.indexes() YIELD description WHERE description contains ':LogEntry(galaxyName)' RETURN *");
				if(!index.hasNext()) {
					session.run("CREATE INDEX ON :LogEntry(galxyName)");
				} 
				
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("p1", limit);
				StatementResult result = session.run("MATCH (a:LogEntry) WHERE a.status = 'Exploration' WITH a.galaxyName AS galName, COUNT(a.galaxyName) AS nb RETURN galName, nb ORDER BY nb DESC LIMIT {p1}", params);
				
				while(result.hasNext()) {
					Record record = result.next();
					String temps = record.get("galName").asString() + " ( "+ record.get("nb").asInt() + " planètes visitées)";
					galaxyList.add(temps);
				}

			}
			catch (Exception e) {
				e.printStackTrace();
			}

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





























