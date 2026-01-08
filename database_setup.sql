-- Database Setup Script for VECV Pull Chord Report
-- Run this script in SQL Server Management Studio (SSMS) to create the database and required tables.

-- 1. Create Database
CREATE DATABASE VECV_Scada_DB;
GO

USE VECV_Scada_DB;
GO

-- 2. Create Tables

-- Table: Z3_Pullchord_T2
CREATE TABLE [dbo].[Z3_Pullchord_T2](
	[SrNo] [int] IDENTITY(1,1) NOT NULL,
	[Date_Time] [varchar](50) NULL,
	[Shift] [varchar](10) NULL,
	[Line] [varchar](50) NULL,
	[Zone] [varchar](50) NULL,
	[Station] [varchar](50) NULL,
	[Side] [varchar](10) NULL,
	[Maintenance_Call] [varchar](50) NULL,
	[Material_Call] [varchar](50) NULL,
	[Production_Call] [varchar](50) NULL,
	[Pull_Cord] [varchar](50) NULL,
	[Quality_Call] [varchar](50) NULL,
	[Remark] [varchar](255) NULL,
 CONSTRAINT [PK_Z3_Pullchord_T2] PRIMARY KEY CLUSTERED 
(
	[SrNo] ASC
)
);
GO

-- Table: Z5_Pullchord_T
CREATE TABLE [dbo].[Z5_Pullchord_T](
	[SrNo] [int] IDENTITY(1,1) NOT NULL,
	[Date_Time] [varchar](50) NULL,
	[Shift] [varchar](10) NULL,
	[Line] [varchar](50) NULL,
	[Zone] [varchar](50) NULL,
	[Station] [varchar](50) NULL,
	[Side] [varchar](10) NULL,
	[Maintenance_Call] [varchar](50) NULL,
	[Material_Call] [varchar](50) NULL,
	[Production_Call] [varchar](50) NULL,
	[Pull_Cord] [varchar](50) NULL,
	[Quality_Call] [varchar](50) NULL,
	[Remark] [varchar](255) NULL,
 CONSTRAINT [PK_Z5_Pullchord_T] PRIMARY KEY CLUSTERED 
(
	[SrNo] ASC
)
);
GO

-- Table: Z7_Pullchord_T
CREATE TABLE [dbo].[Z7_Pullchord_T](
	[SrNo] [int] IDENTITY(1,1) NOT NULL,
	[Date_Time] [varchar](50) NULL,
	[Shift] [varchar](10) NULL,
	[Line] [varchar](50) NULL,
	[Zone] [varchar](50) NULL,
	[Station] [varchar](50) NULL,
	[Side] [varchar](10) NULL,
	[Maintenance_Call] [varchar](50) NULL,
	[Material_Call] [varchar](50) NULL,
	[Production_Call] [varchar](50) NULL,
	[Pull_Cord] [varchar](50) NULL,
	[Quality_Call] [varchar](50) NULL,
	[Remark] [varchar](255) NULL,
 CONSTRAINT [PK_Z7_Pullchord_T] PRIMARY KEY CLUSTERED 
(
	[SrNo] ASC
)
);
GO

-- Table: Z9_Pullchord_T
CREATE TABLE [dbo].[Z9_Pullchord_T](
	[SrNo] [int] IDENTITY(1,1) NOT NULL,
	[Date_Time] [varchar](50) NULL,
	[Shift] [varchar](10) NULL,
	[Line] [varchar](50) NULL,
	[Zone] [varchar](50) NULL,
	[Station] [varchar](50) NULL,
	[Side] [varchar](10) NULL,
	[Maintenance_Call] [varchar](50) NULL,
	[Material_Call] [varchar](50) NULL,
	[Production_Call] [varchar](50) NULL,
	[Pull_Cord] [varchar](50) NULL,
	[Quality_Call] [varchar](50) NULL,
	[Remark] [varchar](255) NULL,
 CONSTRAINT [PK_Z9_Pullchord_T] PRIMARY KEY CLUSTERED 
(
	[SrNo] ASC
)
);
GO

-- 3. Insert Dummy Data (Optional - for testing)
INSERT INTO [dbo].[Z3_Pullchord_T2] ([Date_Time], [Shift], [Line], [Zone], [Station], [Side], [Maintenance_Call], [Material_Call], [Production_Call], [Pull_Cord], [Quality_Call], [Remark])
VALUES ('2025-05-20 10:00:00', 'A', 'Line 1', 'Zone A', 'ST01', 'Left', '0', '0', '1', '1', '0', 'Test Record');
GO
