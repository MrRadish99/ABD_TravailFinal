# Alexandre Ducharme et Fran�ois Th�rien

## Explication du choix de la base de donn�es

La base de donn�es utilis�e est Neo4j car nous avions besoin de faire des liens entre les plan�tes ce que MongoDB nous permet moins de faire car il y aura beaucoup plus de lecture des informations dans les collections qu'avec les nodes de Neo4j. De plus, il �tait beaucoup plus facile de visualiser les liens et les trajectoires entre les plan�tes gr�ce � l'interface graphique de neo4j.

## Index

getLogEntryByPosition
StatementResult index = session.run("CALL db.indexes() YIELD description WHERE description contains ':LogEntry(date)' RETURN *");
				if(!index.hasNext()) {
					session.run("CREATE INDEX ON :LogEntry(date)");
				} 
				ligne 167
				
				
getExploredGalaxies
StatementResult index = session.run("CALL db.indexes() YIELD description WHERE description contains ':LogEntry(galaxyName)' RETURN *");
				if(!index.hasNext()) {
					session.run("CREATE INDEX ON :LogEntry(galxyName)");
				} 
				ligne 503