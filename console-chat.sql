CREATE TABLE Users
(
    Id SERIAL PRIMARY KEY,
    login CHARACTER VARYING(50),
    password CHARACTER VARYING(50),
    nickname CHARACTER VARYING(50),
    banFlag BOOLEAN DEFAULT false
);

CREATE TABLE Roles
(
    Id SERIAL PRIMARY KEY,
    Role CHARACTER VARYING(50) DEFAULT 'USER'
);

	CREATE TABLE UserRole
(
    user_id INT, 
    role_id INT,
	CONSTRAINT user_fk FOREIGN KEY(user_id) REFERENCES users(id),
	CONSTRAINT role_fk FOREIGN KEY(role_id) REFERENCES roles(id),
	PRIMARY KEY (user_id,user_id)
);

alter table userrole drop constraint role_fk;
alter table userrole add constraint role_fk FOREIGN KEY(role_id) REFERENCES roles(id);
drop table 	UserRole;
alter table UserRole drop column id;

alter table UserRole add constraint ur PRIMARY KEY (RoleId, UserId);

INSERT INTO Users (login,password,nickname,banFlag)
VALUES
  ('Nolan','UWX39XAX4IO','Bright','false'),
  ('Dustin','EYF83BGK3NB','Charles','false'),
  ('Sydney','SHS32BDB4PS','Peters','false'),
  ('Chadwick','BLS68OSN6TT','Mayo','false'),
  ('Eric','YNW17JLI5JS','Roberson','false'),
  ('Melyssa','KGV43YOG9YN','Dyer','false'),
  ('Mohammad','FWE39WUQ2LF','Elliott','false'),
  ('Petra','KFE71GTX6QP','Shields','false'),
  ('Iola','RPL53ESY4YW','Garrison','false'),
  ('Ella','THV73OOR7NO','Hurst','false');
  
INSERT INTO Users (login,password,nickname,banFlag)
VALUES
  ('admin','admin123','admin','false');
  
INSERT INTO Roles (Role,Id)
VALUES
  ('ADMIN',1),
  ('USER',2);
  
--1
INSERT INTO Users (login,password,nickname)
VALUES
  ('Кот','1','Мурзик');  

INSERT INTO UserRole (user_id,role_id)
VALUES
  (1,2),
  (2,2),
  (3,2),
  (4,2),
  (5,2),
  (6,2),
  (7,2),
  (8,2),
  (9,2),
  (10,2),
  (11,1);