# Alexandre Ducharme et Fran�ois Th�rien

## Explication du choix de la base de donn�es

La base de donn�es utilis�e est Neo4j car nous avions besoin de faire des liens entre les plan�tes ce que MongoDB nous permet moins de faire car il y aura beaucoup plus de lecture des informations dans les collections qu'avec les nodes de Neo4j. De plus, il �tait beaucoup plus facile de visualiser les liens et les trajectoires entre les plan�tes gr�ce � l'interface graphique de neo4j.

## Index

Le premier index est � la ligne 167  dans la fonction getLogEntryByPosition et n'a pas d'ordre car il n'est pas en mongoDB.
Il n'est pas unique car la verification se fait a l'entr�e des donn�es, il est sur la variable de la date.


Le deuxi�me index est � la ligne 503 dans la fonction getExploredGalaxies.
Il est utiliser sur la variable de galaxyName, non unique et sans ordre car pas besoin de sp�cifier en Neo4j.
			