Drop schema if exists Inventory_System;

CREATE DATABASE Inventory_System;

USE Inventory_System;

/*table to indicate category of each prodcut*/
CREATE TABLE Category(
	catCode int(3) NOT NULL,
	categoryName varchar(100) NOT NULL,
	PRIMARY KEY (catCode)
);

/*master table of product data*/
CREATE TABLE Product(
	ProductID int NOT NULL AUTO_INCREMENT,
	UPC char(12) NOT NULL,
	invName varchar(200) NOT NULL,
	Category int(3) NOT NULL,
	PRIMARY KEY (ProductID),
	FOREIGN KEY (Category) REFERENCES Category(catCode)
);

/*master table of nutrition data*/
CREATE TABLE Nutrition(
	ProductID int NOT NULL, 
	Nut_Code int NOT NULL,
	Nutr_name varchar(150) NOT NULL,
	nut_val double NOT NULL,
	uom char(5) NOT NULL,
	PRIMARY KEY (ProductID, Nut_Code),
	FOREIGN KEY (ProductID) REFERENCES Product(ProductID)
);

/*master table of serving sizes*/
CREATE TABLE Serving_size(
	ProductID int NOT NULL,
	ServingSize double NOT NULL,
	uom char(5) NOT NULL,
	PRIMARY KEY (ProductID)
);

/*table to store individual's products*/
CREATE TABLE Inventory_List(
	ProductID int NOT NULL,
	prod_size double NOT NULL,
	uom varchar(6) NOT NULL,
	use_by date NULL default NULL,
	avg_usage double NULL default NULL,
	quantity int NOT Null, /*currently not using this field*/
	PRIMARY KEY (ProductID)
);


/*junction table between List and Products
required if user has more than one list*/
CREATE TABLE List_Pointer(
	ListID int NOT NULL AUTO_INCREMENT,
	ListName varchar(40) NOT NULL,
	PRIMARY KEY(ListID)
);

/*Actual list which is on each product*/
CREATE TABLE Shopping_List(
	ItemID int AUTO_INCREMENT,
	ListID int NOT NULL,
	ProductID int NULL default NULL,
	cat_code int(3) NOT Null,
	quantity int NOT null,/*defaults to one*/
	ProductName varchar(80) NOT NULL,
	PRIMARY KEY(ItemID),
	FOREIGN KEY (ListID) REFERENCES List_Pointer(ListID)
	on Delete cascade
);

/*set autoIncrement to start at a 3 digit num*/
ALTER TABLE List_Pointer AUTO_INCREMENT=100;

/*command to start incrementing*/
ALTER TABLE Product AUTO_INCREMENT=45374984;


DELIMITER //
drop procedure if exists getNutrition;//

Create Procedure getNutrition()
begin
select p.invName, (n.nut_val/100*s.servingSize) as protein,
(n2.nut_val/100*s.servingSize) as fat,
(n3.nut_val/100*s.servingSize) as calories, s.uom
from Product p join inventory_List i on i.ProductID=p.ProductID
join Nutrition n on n. ProductID=p.ProductID
join Nutrition n2 on n2. ProductID=p.ProductID
join Nutrition n3 on n3. ProductID=p.ProductID
join serving_size s on s.ProductID=p.ProductID
WHERE (n.Nut_Code=203 AND n2.Nut_Code=204 AND  n3.Nut_Code=205);
end//
Delimiter ;