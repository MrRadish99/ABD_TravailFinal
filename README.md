# Alexandre Ducharme et Fran�ois Th�rien

## Explication du choix de la base de donn�es

La base de donn�es utilis�e est Neo4j, car nous avions besoin de faire des liens entre les plan�tes ce que la base de donn�es MongoDB nous permet moins de faire. Aussi avec MongoDB, il y aurait beaucoup plus de lectures d�informations dans les collections qu�avec les nodes de Neo4j. De plus, il �tait beaucoup plus facile de visualiser les liens et les trajectoires entre les plan�tes gr�ce � l�interface graphique de Neo4j compar� avec MongoDB.

## Index

Le premier index est � la ligne 167, dans la fonction getLogEntryByPosition et n�a pas d�ordre, car il n�est pas en MongoDB. Il n�est pas unique, car la v�rification d�unicit� se fait � l�entr�e des donn�es et qui est sur la variable de la date.

Le deuxi�me index est � la ligne 503, dans la fonction getExploredGalaxies. Il est utilis� sur la variable de galaxyName, non unique et sans ordre, car cela n�a pas besoin d��tre sp�cifi� en Neo4j.

			