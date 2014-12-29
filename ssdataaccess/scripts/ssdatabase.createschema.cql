CREATE KEYSPACE ssda WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 } ;

USE ssda;

CREATE TABLE ss_measures_agr (
   measure text,
   time text,
   latitude text,
   longitude text,
   value double,
   PRIMARY KEY((measure, time), latitude, longitude)
);

CREATE TABLE ss_measures_ts_agr (
   measure text,
   latitude text,
   longitude text,
   time text,
   value double,
   PRIMARY KEY((measure, latitude, longitude), time)
);

CREATE TABLE ss_measures_ts_agr_02 (
   measure text,
   day text,
   values list<double>,
   PRIMARY KEY((measure, day))
);

CREATE TABLE ss_measures_stats (
   measure text,
   stat_type text,
   value  double,
   PRIMARY KEY((measure),stat_type)
);



CREATE TABLE ss_measures_stats (
   measure text,
   mean double,
   stddev  double,
   PRIMARY KEY((measure))
);



CREATE TABLE ss_cluster_res (
    day text,
    clusterId int,
    PRIMARY KEY((day))
);

CREATE TABLE ss_cluster_centers (
    clusterId int,
    values list<double>,
    PRIMARY KEY((clusterId))
);