# Alexandre Ducharme et François Thérien

## Explication du choix de la base de données

La base de données utilisée est Neo4j car nous avions besoin de faire des liens entre les planètes ce que MongoDB nous permet moins de faire car il y aura beaucoup plus de lecture des informations dans les collections qu'avec les nodes de Neo4j. De plus, il était beaucoup plus facile de visualiser les liens et les trajectoires entre les planètes grâce à l'interface graphique de neo4j.

## Index

Le premier index est à la ligne 167  dans la fonction getLogEntryByPosition et n'a pas d'ordre car il n'est pas en mongoDB.
Il n'est pas unique car la verification se fait a l'entrée des données, il est sur la variable de la date.


Le deuxième index est à la ligne 503 dans la fonction getExploredGalaxies.
Il est utiliser sur la variable de galaxyName, non unique et sans ordre car pas besoin de spécifier en Neo4j.
			