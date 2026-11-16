-- =========================================================
-- TRAFFIC MANAGEMENT SYSTEM
-- SQL / DBMS MINI PROJECT
-- MySQL
-- =========================================================

-- 1. CREATE DATABASE
DROP DATABASE IF EXISTS TrafficManagement;
CREATE DATABASE TrafficManagement;
USE TrafficManagement;


-- =========================================================
-- 2. CREATE VEHICLE TABLE
-- =========================================================

CREATE TABLE Vehicle (
    vehicle_id INT PRIMARY KEY AUTO_INCREMENT,
    vehicle_number VARCHAR(20) UNIQUE NOT NULL,
    vehicle_type VARCHAR(30) NOT NULL,
    owner_name VARCHAR(100) NOT NULL
);


-- =========================================================
-- 3. CREATE DRIVER TABLE
-- =========================================================

CREATE TABLE Driver (
    driver_id INT PRIMARY KEY AUTO_INCREMENT,
    driver_name VARCHAR(100) NOT NULL,
    license_number VARCHAR(30) UNIQUE NOT NULL,
    phone VARCHAR(15)
);


-- =========================================================
-- 4. CREATE TRAFFIC SIGNAL TABLE
-- =========================================================

CREATE TABLE TrafficSignal (
    signal_id INT PRIMARY KEY AUTO_INCREMENT,
    location VARCHAR(100) NOT NULL,
    signal_status VARCHAR(20) NOT NULL
);


-- =========================================================
-- 5. CREATE VIOLATION TABLE
-- =========================================================

CREATE TABLE Violation (
    violation_id INT PRIMARY KEY AUTO_INCREMENT,
    vehicle_id INT NOT NULL,
    driver_id INT NOT NULL,
    violation_type VARCHAR(100) NOT NULL,
    violation_date DATE NOT NULL,
    fine_amount DECIMAL(10,2) NOT NULL,

    FOREIGN KEY (vehicle_id)
        REFERENCES Vehicle(vehicle_id),

    FOREIGN KEY (driver_id)
        REFERENCES Driver(driver_id)
);


-- =========================================================
-- 6. INSERT VEHICLE DATA
-- =========================================================

INSERT INTO Vehicle
(vehicle_number, vehicle_type, owner_name)
VALUES
('MH15AB1234', 'Car', 'Rahul Patil'),
('MH15CD5678', 'Bike', 'Amit Sharma'),
('MH15EF9012', 'Truck', 'Suresh More'),
('MH15GH3456', 'Car', 'Priya Joshi'),
('MH15IJ7890', 'Bike', 'Sneha Deshmukh'),
('MH15KL2468', 'Bus', 'Vikas Shinde');


-- =========================================================
-- 7. INSERT DRIVER DATA
-- =========================================================

INSERT INTO Driver
(driver_name, license_number, phone)
VALUES
('Rahul Patil', 'MH2020123456', '9876543210'),
('Amit Sharma', 'MH2021123456', '9876543211'),
('Suresh More', 'MH2022123456', '9876543212'),
('Priya Joshi', 'MH2023123456', '9876543213'),
('Sneha Deshmukh', 'MH2024123456', '9876543214'),
('Vikas Shinde', 'MH2025123456', '9876543215');


-- =========================================================
-- 8. INSERT TRAFFIC SIGNAL DATA
-- =========================================================

INSERT INTO TrafficSignal
(location, signal_status)
VALUES
('Mumbai Naka', 'Working'),
('College Road', 'Working'),
('Gangapur Road', 'Working'),
('CBS', 'Not Working'),
('Panchavati', 'Working'),
('Nashik Road', 'Working');


-- =========================================================
-- 9. INSERT VIOLATION DATA
-- =========================================================

INSERT INTO Violation
(vehicle_id, driver_id, violation_type, violation_date, fine_amount)
VALUES
(1, 1, 'Signal Jump', '2026-10-01', 1000),
(2, 2, 'No Helmet', '2026-10-02', 500),
(3, 3, 'Over Speeding', '2026-10-03', 2000),
(4, 4, 'Wrong Parking', '2026-10-04', 500),
(5, 5, 'No Helmet', '2026-10-05', 500),
(6, 6, 'Over Speeding', '2026-10-06', 2000),
(1, 1, 'Wrong Parking', '2026-10-07', 500);


-- =========================================================
-- 10. DISPLAY ALL VEHICLES
-- =========================================================

SELECT * FROM Vehicle;


-- =========================================================
-- 11. DISPLAY ALL DRIVERS
-- =========================================================

SELECT * FROM Driver;


-- =========================================================
-- 12. DISPLAY ALL TRAFFIC SIGNALS
-- =========================================================

SELECT * FROM TrafficSignal;


-- =========================================================
-- 13. DISPLAY ALL VIOLATIONS
-- =========================================================

SELECT * FROM Violation;


-- =========================================================
-- 14. DISPLAY COMPLETE VIOLATION REPORT
-- =========================================================

SELECT
    vi.violation_id,
    v.vehicle_number,
    v.vehicle_type,
    v.owner_name,
    d.driver_name,
    d.license_number,
    vi.violation_type,
    vi.violation_date,
    vi.fine_amount
FROM Violation vi
JOIN Vehicle v
    ON vi.vehicle_id = v.vehicle_id
JOIN Driver d
    ON vi.driver_id = d.driver_id;


-- =========================================================
-- 15. FIND TOTAL FINE
-- =========================================================

SELECT
    SUM(fine_amount) AS Total_Fine
FROM Violation;


-- =========================================================
-- 16. FIND AVERAGE FINE
-- =========================================================

SELECT
    AVG(fine_amount) AS Average_Fine
FROM Violation;


-- =========================================================
-- 17. FIND HIGHEST FINE
-- =========================================================

SELECT
    MAX(fine_amount) AS Highest_Fine
FROM Violation;


-- =========================================================
-- 18. FIND LOWEST FINE
-- =========================================================

SELECT
    MIN(fine_amount) AS Lowest_Fine
FROM Violation;


-- =========================================================
-- 19. COUNT TOTAL VIOLATIONS
-- =========================================================

SELECT
    COUNT(*) AS Total_Violations
FROM Violation;


-- =========================================================
-- 20. FIND VIOLATIONS WHERE FINE > 500
-- =========================================================

SELECT *
FROM Violation
WHERE fine_amount > 500;


-- =========================================================
-- 21. FIND ALL NO HELMET VIOLATIONS
-- =========================================================

SELECT *
FROM Violation
WHERE violation_type = 'No Helmet';


-- =========================================================
-- 22. FIND ALL OVER SPEEDING VIOLATIONS
-- =========================================================

SELECT *
FROM Violation
WHERE violation_type = 'Over Speeding';


-- =========================================================
-- 23. COUNT VIOLATIONS BY TYPE
-- =========================================================

SELECT
    violation_type,
    COUNT(*) AS Total
FROM Violation
GROUP BY violation_type;


-- =========================================================
-- 24. TOTAL FINE BY VIOLATION TYPE
-- =========================================================

SELECT
    violation_type,
    SUM(fine_amount) AS Total_Fine
FROM Violation
GROUP BY violation_type;


-- =========================================================
-- 25. FIND DRIVER WITH VIOLATION DETAILS
-- =========================================================

SELECT
    d.driver_name,
    d.license_number,
    v.vehicle_number,
    vi.violation_type,
    vi.violation_date,
    vi.fine_amount
FROM Driver d
JOIN Violation vi
    ON d.driver_id = vi.driver_id
JOIN Vehicle v
    ON vi.vehicle_id = v.vehicle_id;


-- =========================================================
-- 26. FIND VEHICLES WITH THEIR TOTAL FINE
-- =========================================================

SELECT
    v.vehicle_number,
    v.owner_name,
    SUM(vi.fine_amount) AS Total_Fine
FROM Vehicle v
JOIN Violation vi
    ON v.vehicle_id = vi.vehicle_id
GROUP BY
    v.vehicle_number,
    v.owner_name;


-- =========================================================
-- 27. FIND DRIVER WITH HIGHEST TOTAL FINE
-- =========================================================

SELECT
    d.driver_name,
    SUM(vi.fine_amount) AS Total_Fine
FROM Driver d
JOIN Violation vi
    ON d.driver_id = vi.driver_id
GROUP BY d.driver_name
ORDER BY Total_Fine DESC
LIMIT 1;


-- =========================================================
-- 28. UPDATE DRIVER PHONE NUMBER
-- =========================================================

UPDATE Driver
SET phone = '9999999999'
WHERE driver_id = 2;


-- =========================================================
-- 29. UPDATE TRAFFIC SIGNAL STATUS
-- =========================================================

UPDATE TrafficSignal
SET signal_status = 'Working'
WHERE signal_id = 4;


-- =========================================================
-- 30. SEARCH VEHICLE BY NUMBER
-- =========================================================

SELECT *
FROM Vehicle
WHERE vehicle_number = 'MH15AB1234';


-- =========================================================
-- 31. SEARCH DRIVER BY NAME
-- =========================================================

SELECT *
FROM Driver
WHERE driver_name = 'Rahul Patil';


-- =========================================================
-- 32. FIND WORKING TRAFFIC SIGNALS
-- =========================================================

SELECT *
FROM TrafficSignal
WHERE signal_status = 'Working';


-- =========================================================
-- 33. FIND NON-WORKING TRAFFIC SIGNALS
-- =========================================================

SELECT *
FROM TrafficSignal
WHERE signal_status = 'Not Working';


-- =========================================================
-- 34. CREATE VIEW FOR TRAFFIC REPORT
-- =========================================================

CREATE VIEW TrafficViolationReport AS
SELECT
    vi.violation_id,
    v.vehicle_number,
    v.vehicle_type,
    v.owner_name,
    d.driver_name,
    d.license_number,
    vi.violation_type,
    vi.violation_date,
    vi.fine_amount
FROM Violation vi
JOIN Vehicle v
    ON vi.vehicle_id = v.vehicle_id
JOIN Driver d
    ON vi.driver_id = d.driver_id;


-- =========================================================
-- 35. DISPLAY VIEW
-- =========================================================

SELECT *
FROM TrafficViolationReport;


-- =========================================================
-- 36. DELETE A VIOLATION
-- =========================================================
-- Run only when you want to delete a record.

-- DELETE FROM Violation
-- WHERE violation_id = 7;


-- =========================================================
-- 37. ADD NEW VEHICLE
-- =========================================================

INSERT INTO Vehicle
(vehicle_number, vehicle_type, owner_name)
VALUES
('MH15MN5555', 'Bike', 'Kiran Pawar');


-- =========================================================
-- 38. ADD NEW DRIVER
-- =========================================================

INSERT INTO Driver
(driver_name, license_number, phone)
VALUES
('Kiran Pawar', 'MH2026123456', '9876501234');


-- =========================================================
-- 39. ADD NEW TRAFFIC VIOLATION
-- =========================================================

INSERT INTO Violation
(vehicle_id, driver_id, violation_type, violation_date, fine_amount)
VALUES
(7, 7, 'No Helmet', '2026-10-08', 500);


-- =========================================================
-- 40. FINAL COMPLETE REPORT
-- =========================================================

SELECT
    vi.violation_id AS Violation_ID,
    v.vehicle_number AS Vehicle_Number,
    v.owner_name AS Owner,
    d.driver_name AS Driver,
    vi.violation_type AS Violation,
    vi.violation_date AS Date,
    vi.fine_amount AS Fine
FROM Violation vi
JOIN Vehicle v
    ON vi.vehicle_id = v.vehicle_id
JOIN Driver d
    ON vi.driver_id = d.driver_id
ORDER BY vi.violation_date DESC;