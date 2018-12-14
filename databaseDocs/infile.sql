USE Inventory_System;

/* infile for seed data of categories*/
LOAD DATA  INFILE "C:/ProgramData/MySQL/MySQL Server 5.7/Uploads/Category.csv"
INTO TABLE Category
COLUMNS TERMINATED BY ','
OPTIONALLY ENCLOSED BY '"'
ESCAPED BY '"'
LINES TERMINATED BY '\r\n';

/* infile for seed data of product master 
product information*/
LOAD DATA  INFILE "C:/ProgramData/MySQL/MySQL Server 5.7/Uploads/ProductData.csv"
INTO TABLE Product
COLUMNS TERMINATED BY ','
OPTIONALLY ENCLOSED BY '"'
ESCAPED BY '"'
LINES TERMINATED BY '\r\n';

/* infile for seed data of 
master nutrition data*/
LOAD DATA INFILE "C:/ProgramData/MySQL/MySQL Server 5.7/Uploads/Nutrition.csv"
INTO TABLE Nutrition
COLUMNS TERMINATED BY ','
OPTIONALLY ENCLOSED BY '"'
ESCAPED BY '"'
LINES TERMINATED BY '\r\n';

/* infile for seed data of
master list of serving sizes*/
LOAD DATA  INFILE "C:/ProgramData/MySQL/MySQL Server 5.7/Uploads/serving_size.csv"
INTO TABLE Serving_size
COLUMNS TERMINATED BY ','
OPTIONALLY ENCLOSED BY '"'
ESCAPED BY '"'
LINES TERMINATED BY '\r\n';

/* infile for seed data 
pf sample user table*/
LOAD DATA INFILE "C:/ProgramData/MySQL/MySQL Server 5.7/Uploads/inventoryListData.csv"
INTO TABLE inventory_list
COLUMNS TERMINATED BY ','
OPTIONALLY ENCLOSED BY '"'
ESCAPED BY '"'
LINES TERMINATED BY '\r\n';

