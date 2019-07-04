CREATE TABLE IF NOT EXISTS `nclat_judgement` (
  `id` INT(10) NOT NULL AUTO_INCREMENT,
  `companyAppealNo` VARCHAR(45) NULL,
  `dateOfOrder` DATETIME NULL,
  `party` VARCHAR(255) NULL,
  `section` VARCHAR(45) NULL,
  `courtName` VARCHAR(45) NULL,
  `orderPassedBy` VARCHAR(255) NULL,
  `pdfFileName` VARCHAR(45) NULL,
  `pdfFileSize` VARCHAR(45) NULL,
  `remark` VARCHAR(45) NULL,
  PRIMARY KEY (`id`));
