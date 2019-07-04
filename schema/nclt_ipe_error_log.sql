CREATE TABLE IF NOT EXISTS `nclt_ipe_error_log` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `recognitionNumber` varchar(100) DEFAULT NULL,
  `ipeName` varchar(255) DEFAULT NULL,
  `ipeConstitution` varchar(100) DEFAULT NULL,
  `ipeAddress` varchar(255) DEFAULT NULL,
  `ipePartnerName` varchar(255) DEFAULT NULL,
  `ipeContactDetails` varchar(255) DEFAULT NULL,
  `errorCode` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;