Instructions for setting up Postgres: https://www.cse.iitb.ac.in/infolab/Data/Courses/CS631/PostgreSQL-Resources/

Design document can be found at: https://docs.google.com/document/d/1WUOncoRM0m5BWdxKJmQQWYP2L-pb5mhlXTHJPDWkYW0/edit?usp=sharing

add all the export lines to ~/.bashrc <br>

in postgresql.conf
  change the value of listen addresses to '*'	<br>
  port to 9432	<br>

~~
Add HyperEdge as a reserve keyword in postgresql-12.2/src/include/parser/kwlist.h <br>
Correspondingly add structuires for create/alter statements in gram.y and parsenodes.h
~~
types of statements to be handled:

CREATE HYPEREDGE( id, source_entity, destination_entity, condition);
CREATE HYPEREDGE( id, condition);
SELECT * FROM hyperedge_table WHERE id = <hyperedge_id>;
ALTER HYPEREDGE( new_condition)  WHERE id = <hyperedge_id>;
DELETE HYPEREDGE WHERE id = <hyperedge_id>;
