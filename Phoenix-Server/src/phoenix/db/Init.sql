CREATE TABLE type (
	id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
	name VARCHAR (20) NOT NULL
);

INSERT INTO type (id, name) VALUES (1, 'City Car');
INSERT INTO type (id, name) VALUES (2, 'Mini');
INSERT INTO type (id, name) VALUES (3, 'Economy');
INSERT INTO type (id, name) VALUES (4, 'Compact');
INSERT INTO type (id, name) VALUES (5, 'Furgone');
INSERT INTO type (id, name) VALUES (6, 'Medio');
INSERT INTO type (id, name) VALUES (7, 'Comfort');
INSERT INTO type (id, name) VALUES (8, 'Cargo');
INSERT INTO type (id, name) VALUES (9, 'Elettrico');



CREATE TABLE car (
	id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
	name VARCHAR (10) NOT NULL UNIQUE,
	color VARCHAR (10) NOT NULL, 
	plate VARCHAR (7) NOT NULL, 
	currentParkingLot INTEGER REFERENCES parcheggio (id), 
	type INTEGER REFERENCES type (id) NOT NULL
);

INSERT INTO car (id, name, color, plate, currentParkingLot, type) VALUES (1, 'Fiat Panda', 'Bianco', 'AJ123YE', 1, 1);
INSERT INTO car (id, name, color, plate, currentParkingLot, type) VALUES (2, 'Alfa-Romeo Giulietta', 'Rosso', 'PQ321PE', 3, 6);
INSERT INTO car (id, name, color, plate, currentParkingLot, type) VALUES (3, 'Lancia Delta', 'Nero', 'OP213HW', 5, 6);
INSERT INTO car (id, name, color, plate, currentParkingLot, type) VALUES (4, 'Fiat 500', 'Giallo', 'EW932HE', 2, 1);
INSERT INTO car (id, name, color, plate, currentParkingLot, type) VALUES (5, 'Lancia Musa', 'Oro', 'CE145RE', 7, 7);
INSERT INTO car (id, name, color, plate, currentParkingLot, type) VALUES (6, 'Fiat Fiorino', 'Bianco', 'YE743IL', 8, 5);
INSERT INTO car (id, name, color, plate, currentParkingLot, type) VALUES (7, 'Fiat Croma', 'Blu', 'PP847ES', 4, 6);
INSERT INTO car (id, name, color, plate, currentParkingLot, type) VALUES (8, 'Renault Zoe', 'Grigio', 'IY213HF', 2, 9);

CREATE TABLE user (
	id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
	name VARCHAR (20) NOT NULL, 
	surname VARCHAR (20) NOT NULL, 
	username VARCHAR (20) NOT NULL UNIQUE,
	email VARCHAR (100) UNIQUE NOT NULL UNIQUE, 
	password VARCHAR (44) NOT NULL, 
	salt VARCHAR (20) NOT NULL, 
	currentCar INTEGER REFERENCES macchina (id) UNIQUE, 
	latitude DOUBLE, 
	longitude DOUBLE, 
	altitude DOUBLE, 
	lastLogin DATE
);



CREATE TABLE parkingLot (
	id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
	name VARCHAR (20) NOT NULL, 
	latitude DOUBLE NOT NULL, 
	longitude DOUBLE NOT NULL, 
	altitude DOUBLE NOT NULL, 
	address VARCHAR (255) NOT NULL UNIQUE, 
	lots INTEGER NOT NULL
);

INSERT INTO parkingLot (id, name, latitude, longitude, altitude, address, lots) VALUES (1, 'Park Manin', 44.4129, 8.94813, 0, 'Via alla Stazione di Casella, 1, 16122 Genova, Italy', 10);
INSERT INTO parkingLot (id, name, latitude, longitude, altitude, address, lots) VALUES (2, 'Park Vittoria', 44.4048, 8.94667, 0, 'Piazzetta Mario Baistrocchi, 16121 Genova, Italy', 7);
INSERT INTO parkingLot (id, name, latitude, longitude, altitude, address, lots) VALUES (3, 'Park Galata', 44.4066, 8.94302, 0, 'Via Galata, 2, 16121 Genova, Italy', 8);
INSERT INTO parkingLot (id, name, latitude, longitude, altitude, address, lots) VALUES (4, 'Park Boccadasse', 44.3948, 8.96974, 0, 'Via Gorgona, 5, 16146 Genova, Italy', 8);
INSERT INTO parkingLot (id, name, latitude, longitude, altitude, address, lots) VALUES (5, 'Park San Martino', 44.4066, 8.97671, 0, 'Via Silvio Lagustena, 76, 16131 Genova, Italy', 16);
INSERT INTO parkingLot (id, name, latitude, longitude, altitude, address, lots) VALUES (6, 'Park Europa', 44.3937, 9.00482, 0, 'Via San Giuseppe Cottolengo, 21, 16148 Genova, Italy', 5);
INSERT INTO parkingLot (id, name, latitude, longitude, altitude, address, lots) VALUES (7, 'Park Dinegro', 44.4139, 8.90314, 0, 'Via San Bartolomeo del Fossato, 34, 16149 Genova, Italy', 5);
INSERT INTO parkingLot (id, name, latitude, longitude, altitude, address, lots) VALUES (8, 'Park Principe', 44.4118, 8.92695, 0, 'Calata Salumi, 16126 Genova, Italy', 6);
