CREATE DATABASE IF NOT EXISTS NormalizationDB;
USE NormalizationDB;

CREATE TABLE Department (
    Department_ID INT PRIMARY KEY,
    Department_Name VARCHAR(50) NOT NULL,
    Manager_Name VARCHAR(50)
);

CREATE TABLE Employee (
    Employee_ID INT PRIMARY KEY,
    Employee_Name VARCHAR(50) NOT NULL,
    Department_ID INT,
    FOREIGN KEY (Department_ID)
        REFERENCES Department(Department_ID)
);

INSERT INTO Department VALUES
(10, 'Computer', 'Mr Sharma'),
(20, 'Mechanical', 'Mr Patil');

INSERT INTO Employee VALUES
(1, 'Apeksha', 10),
(2, 'Rahul', 10),
(3, 'Priya', 20);

SELECT e.Employee_ID, e.Employee_Name,
       d.Department_Name, d.Manager_Name
FROM Employee e
JOIN Department d
    ON e.Department_ID = d.Department_ID;