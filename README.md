# Alexandre Ducharme et François Thérien

## Explication du choix de la base de données

La base de données utilisée est Neo4j, car nous avions besoin de faire des liens entre les planètes ce que la base de données MongoDB nous permet moins de faire. Aussi avec MongoDB, il y aurait beaucoup plus de lectures d’informations dans les collections qu’avec les nodes de Neo4j. De plus, il était beaucoup plus facile de visualiser les liens et les trajectoires entre les planètes grâce à l’interface graphique de Neo4j comparé avec MongoDB.

## Index

Le premier index est à la ligne 167, dans la fonction getLogEntryByPosition et n’a pas d’ordre, car il n’est pas en MongoDB. Il n’est pas unique, car la vérification d’unicité se fait à l’entrée des données et qui est sur la variable de la date.

Le deuxième index est à la ligne 503, dans la fonction getExploredGalaxies. Il est utilisé sur la variable de galaxyName, non unique et sans ordre, car cela n’a pas besoin d’être spécifié en Neo4j.

			