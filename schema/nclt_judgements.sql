CREATE TABLE IF NOT EXISTS `nclt_judgement` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `caseNo` varchar(100) DEFAULT NULL,
  `petitionerName` varchar(255) DEFAULT NULL,
  `judgementDate` datetime DEFAULT NULL,
  `pdfFileName` varchar(255) DEFAULT NULL,
  `pdfFileSize` varchar(45) DEFAULT NULL,
  `pdfFileLanguage` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

