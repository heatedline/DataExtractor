CREATE TABLE IF NOT EXISTS `nclt_ipa_error_log` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `ipaRegistrationNumber` varchar(100) DEFAULT NULL,
  `ipaName` varchar(255) DEFAULT NULL,
  `ipaAddress` varchar(255) DEFAULT NULL,
  `ipaWebsite` varchar(100) DEFAULT NULL,
  `ipaChiefExecutiveName` varchar(255) DEFAULT NULL,
  `ipaContactDetails` varchar(255) DEFAULT NULL,
  `errorCode` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;