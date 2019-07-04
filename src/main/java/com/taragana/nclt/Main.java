package com.taragana.nclt;

public class Main {
    public static void main(String[] args) {
        System.setProperty("fromDate", "01/01/2019");
        System.setProperty("toDate", "01/03/2019");

        /*
        (new Thread(() -> IBBIWhatsNewExtractor.main(args))).start();
        */

        (new Thread(() -> NCLATDailyCauseListExtractor.main(args))).start();
        (new Thread(() -> NCLATJudgementsExtractor.main(args))).start();
        (new Thread(() -> NCLATOrdersExtractor.main(args))).start();
        (new Thread(() -> NCLATTentativeListExtractor.main(args))).start();
        (new Thread(() -> NCLTJudgementsExtractor.main(args))).start();
        (new Thread(() -> NCLTOrderExtractor.main(args))).start();
        (new Thread(() -> NCLTPublicAnnouncementExtractor.main(args))).start();
        (new Thread(() -> IBBIOrderExtractor.main(args))).start();
        (new Thread(() -> IBBIHighCourtOrderExtractor.main(args))).start();
        (new Thread(() -> IBBISupremeCourtOrderExtractor.main(args))).start();
        (new Thread(() -> NCLTInsolvencyProfessionalAgenciesExtractor.main(args))).start();
        (new Thread(() -> NCLTInsolvencyProfessionalEntitiesExtractor.main(args))).start();
        (new Thread(() -> NCLTRegisteredIPExtractor.main(args))).start();

    }
}
