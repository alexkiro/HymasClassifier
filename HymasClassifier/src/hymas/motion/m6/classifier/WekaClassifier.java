package hymas.motion.m6.classifier;

/*
 === Run information ===

 Scheme:weka.classifiers.trees.J48 -C 0.25 -M 2
 Relation:     simulatedData
 Instances:    800000
 Attributes:   6
 speed
 speedFluc
 dirChangeFr
 stopFr
 revGeo
 label
 Test mode:2-fold cross-validation

 === Classifier model (full training set) ===

 J48 pruned tree
 ------------------

 stopFr <= 0
 |   speed <= 44.44409: train (100000.0)
 |   speed > 44.44409: flying (100000.0)
 stopFr > 0
 |   speed <= 4.166641
 |   |   speed <= 1.805557
 |   |   |   speedFluc <= 0.277805
 |   |   |   |   speedFluc <= 0.099988: run (2631.0)
 |   |   |   |   speedFluc > 0.099988
 |   |   |   |   |   speed <= 1.388867: walk (75607.0)
 |   |   |   |   |   speed > 1.388867
 |   |   |   |   |   |   dirChangeFr <= 1.999992
 |   |   |   |   |   |   |   stopFr <= 1.998633: run (8434.0/3680.0)
 |   |   |   |   |   |   |   stopFr > 1.998633: walk (5793.0)
 |   |   |   |   |   |   dirChangeFr > 1.999992: walk (14920.0)
 |   |   |   speedFluc > 0.277805: run (7348.0)
 |   |   speed > 1.805557
 |   |   |   speedFluc <= 0.555565
 |   |   |   |   stopFr <= 2.000013
 |   |   |   |   |   dirChangeFr <= 2.000409
 |   |   |   |   |   |   revGeo = 0.0: bike (1539.0)
 |   |   |   |   |   |   revGeo = 1.0: run (86965.0/1698.0)
 |   |   |   |   |   dirChangeFr > 2.000409: bike (4888.0)
 |   |   |   |   stopFr > 2.000013: bike (12087.0)
 |   |   |   speedFluc > 0.555565: bike (30108.0)
 |   speed > 4.166641
 |   |   speed <= 16.666544
 |   |   |   revGeo = 0.0
 |   |   |   |   speed <= 5.556235
 |   |   |   |   |   speedFluc <= 1.390689: bike (25493.0/565.0)
 |   |   |   |   |   speedFluc > 1.390689: car (1206.0)
 |   |   |   |   speed > 5.556235: car (13774.0)
 |   |   |   revGeo = 1.0
 |   |   |   |   speedFluc <= 4.16666
 |   |   |   |   |   dirChangeFr <= 2.000027
 |   |   |   |   |   |   speed <= 5.555551
 |   |   |   |   |   |   |   speedFluc <= 1.389167: bike (15351.0/5482.0)
 |   |   |   |   |   |   |   speedFluc > 1.389167: tram (11169.0/3707.0)
 |   |   |   |   |   |   speed > 5.555551: tram (132174.0/43320.0)
 |   |   |   |   |   dirChangeFr > 2.000027
 |   |   |   |   |   |   speed <= 5.555812
 |   |   |   |   |   |   |   speedFluc <= 1.389162: bike (15514.0/631.0)
 |   |   |   |   |   |   |   speedFluc > 1.389162: bus (1180.0)
 |   |   |   |   |   |   speed > 5.555812: bus (14922.0)
 |   |   |   |   speedFluc > 4.16666: bus (50037.0)
 |   |   speed > 16.666544: car (68860.0)

 Number of Leaves  : 	24

 Size of the tree : 	47


 Time taken to build model: 12279.9 seconds

 === Stratified cross-validation ===
 === Summary ===

 Correctly Classified Instances      740898               92.6123 %
 Incorrectly Classified Instances     59102                7.3878 %
 Kappa statistic                          0.9156
 Mean absolute error                      0.0275
 Root mean squared error                  0.1173
 Relative absolute error                 12.5881 %
 Root relative squared error             35.4815 %
 Total Number of Instances           800000     

 === Detailed Accuracy By Class ===

 TP Rate   FP Rate   Precision   Recall  F-Measure   ROC Area  Class
 0.963     0          1         0.963     0.981      1        walk
 1         0.008      0.949     1         0.974      0.999    run
 0.983     0.01       0.936     0.983     0.959      0.998    bike
 0.838     0          1         0.838     0.912      0.984    car
 0.661     0          1         0.661     0.796      0.971    bus
 1         0          1         1         1          1        train
 0.963     0.067      0.672     0.963     0.792      0.965    tram
 1         0          1         1         1          1        flying
 Weighted Avg.    0.926     0.011      0.945     0.926     0.927      0.99 

 === Confusion Matrix ===

 a      b      c      d      e      f      g      h   <-- classified as
 96316   3681      3      0      0      0      0      0 |      a = walk
 2  99996      2      0      0      0      0      0 |      b = run
 0   1698  98300      0      0      0      2      0 |      c = bike
 0      0   1136  83840      0      0  15024      0 |      d = car
 0      0   1863      1  66133      0  32003      0 |      e = bus
 0      0      0      0      0 100000      0      0 |      f = train
 0      0   3684      0      3      0  96313      0 |      g = tram
 0      0      0      0      0      0      0 100000 |      h = flying

 === Source code ===
 */
// Generated with Weka 3.6.9
//
// This code is public domain and comes with no warranty.
//
// Timestamp: Mon Jan 28 03:33:56 EET 2013
class WekaClassifier {

    static final Label[] values = new Label[]{
        Label.walk, Label.run, Label.bike, Label.car, Label.bus, Label.train, Label.tram, Label.flying
    };

    /**
     * Clasifica datele deduse dintr-un set de date colectate pe un anume interval
     * intr-o anumita situatie de miscare. 
     * @param speed viteza medie pe acel interval
     * @param speedFluc fluctuatiile de viteza pe acel interval(deviatia maxima de la medie)
     * @param dirChangeFr frecventa schimbarilor de directie( pe minut)
     * @param stopFr frecventa opririlor (pe minut)
     * @param revGeo true daca pozitia userului a fost in oras, false altfel
     * @return o situatie de miscare
     * @throws Exception 
     */
    public static Label classify(double speed, double speedFluc,
            double dirChangeFr, double stopFr, boolean revGeo) throws Exception {

        String revG = (revGeo) ? "1.0" : "0.0";
        int index = (int) classify(new Object[]{speed, speedFluc, dirChangeFr, stopFr, revG});
        return values[index];
    }

    static double classify(Object[] i) throws Exception {
        double p = Double.NaN;
        p = WekaClassifier.N541086240(i);
        return p;
    }

    static double N541086240(Object[] i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 5;
        } else if (((Double) i[3]).doubleValue() <= 0.0) {
            p = WekaClassifier.N8d6d09a1(i);
        } else if (((Double) i[3]).doubleValue() > 0.0) {
            p = WekaClassifier.N4f1932fe2(i);
        }
        return p;
    }

    static double N8d6d09a1(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 5;
        } else if (((Double) i[0]).doubleValue() <= 44.444089756621324) {
            p = 5;
        } else if (((Double) i[0]).doubleValue() > 44.444089756621324) {
            p = 7;
        }
        return p;
    }

    static double N4f1932fe2(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 4.166641456408666) {
            p = WekaClassifier.N7f62ea433(i);
        } else if (((Double) i[0]).doubleValue() > 4.166641456408666) {
            p = WekaClassifier.Nb66b2f613(i);
        }
        return p;
    }

    static double N7f62ea433(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 1.8055566161394172) {
            p = WekaClassifier.N3702160b4(i);
        } else if (((Double) i[0]).doubleValue() > 1.8055566161394172) {
            p = WekaClassifier.N2e3a87f59(i);
        }
        return p;
    }

    static double N3702160b4(Object[] i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 0;
        } else if (((Double) i[1]).doubleValue() <= 0.2778054657367038) {
            p = WekaClassifier.N69f548635(i);
        } else if (((Double) i[1]).doubleValue() > 0.2778054657367038) {
            p = 1;
        }
        return p;
    }

    static double N69f548635(Object[] i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() <= 0.09998815196102788) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() > 0.09998815196102788) {
            p = WekaClassifier.N666391ed6(i);
        }
        return p;
    }

    static double N666391ed6(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() <= 1.3888666353467405) {
            p = 0;
        } else if (((Double) i[0]).doubleValue() > 1.3888666353467405) {
            p = WekaClassifier.N1301931f7(i);
        }
        return p;
    }

    static double N1301931f7(Object[] i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 0;
        } else if (((Double) i[2]).doubleValue() <= 1.9999915630822154) {
            p = WekaClassifier.N4c61e1f88(i);
        } else if (((Double) i[2]).doubleValue() > 1.9999915630822154) {
            p = 0;
        }
        return p;
    }

    static double N4c61e1f88(Object[] i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 1;
        } else if (((Double) i[3]).doubleValue() <= 1.9986333153365843) {
            p = 1;
        } else if (((Double) i[3]).doubleValue() > 1.9986333153365843) {
            p = 0;
        }
        return p;
    }

    static double N2e3a87f59(Object[] i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 1;
        } else if (((Double) i[1]).doubleValue() <= 0.5555645927183307) {
            p = WekaClassifier.N4b3fd8910(i);
        } else if (((Double) i[1]).doubleValue() > 0.5555645927183307) {
            p = 2;
        }
        return p;
    }

    static double N4b3fd8910(Object[] i) {
        double p = Double.NaN;
        if (i[3] == null) {
            p = 1;
        } else if (((Double) i[3]).doubleValue() <= 2.000012903092965) {
            p = WekaClassifier.N44ca2fc811(i);
        } else if (((Double) i[3]).doubleValue() > 2.000012903092965) {
            p = 2;
        }
        return p;
    }

    static double N44ca2fc811(Object[] i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 1;
        } else if (((Double) i[2]).doubleValue() <= 2.00040923271404) {
            p = WekaClassifier.N360716c012(i);
        } else if (((Double) i[2]).doubleValue() > 2.00040923271404) {
            p = 2;
        }
        return p;
    }

    static double N360716c012(Object[] i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 2;
        } else if (i[4].equals("0.0")) {
            p = 2;
        } else if (i[4].equals("1.0")) {
            p = 1;
        }
        return p;
    }

    static double Nb66b2f613(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 4;
        } else if (((Double) i[0]).doubleValue() <= 16.66654447762994) {
            p = WekaClassifier.N36f3a5314(i);
        } else if (((Double) i[0]).doubleValue() > 16.66654447762994) {
            p = 3;
        }
        return p;
    }

    static double N36f3a5314(Object[] i) {
        double p = Double.NaN;
        if (i[4] == null) {
            p = 2;
        } else if (i[4].equals("0.0")) {
            p = WekaClassifier.N7b5e20e715(i);
        } else if (i[4].equals("1.0")) {
            p = WekaClassifier.N78658dc117(i);
        }
        return p;
    }

    static double N7b5e20e715(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= 5.556234853984569) {
            p = WekaClassifier.N60c25cf716(i);
        } else if (((Double) i[0]).doubleValue() > 5.556234853984569) {
            p = 3;
        }
        return p;
    }

    static double N60c25cf716(Object[] i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= 1.3906889237423392) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() > 1.3906889237423392) {
            p = 3;
        }
        return p;
    }

    static double N78658dc117(Object[] i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 6;
        } else if (((Double) i[1]).doubleValue() <= 4.16665995749535) {
            p = WekaClassifier.N533db7a718(i);
        } else if (((Double) i[1]).doubleValue() > 4.16665995749535) {
            p = 4;
        }
        return p;
    }

    static double N533db7a718(Object[] i) {
        double p = Double.NaN;
        if (i[2] == null) {
            p = 6;
        } else if (((Double) i[2]).doubleValue() <= 2.0000267177297424) {
            p = WekaClassifier.N78e45fa219(i);
        } else if (((Double) i[2]).doubleValue() > 2.0000267177297424) {
            p = WekaClassifier.N2dfb9cf121(i);
        }
        return p;
    }

    static double N78e45fa219(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 6;
        } else if (((Double) i[0]).doubleValue() <= 5.555551490006545) {
            p = WekaClassifier.N5942c2af20(i);
        } else if (((Double) i[0]).doubleValue() > 5.555551490006545) {
            p = 6;
        }
        return p;
    }

    static double N5942c2af20(Object[] i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= 1.3891669181657065) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() > 1.3891669181657065) {
            p = 6;
        }
        return p;
    }

    static double N2dfb9cf121(Object[] i) {
        double p = Double.NaN;
        if (i[0] == null) {
            p = 2;
        } else if (((Double) i[0]).doubleValue() <= 5.555812217671397) {
            p = WekaClassifier.N61fca9cc22(i);
        } else if (((Double) i[0]).doubleValue() > 5.555812217671397) {
            p = 4;
        }
        return p;
    }

    static double N61fca9cc22(Object[] i) {
        double p = Double.NaN;
        if (i[1] == null) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() <= 1.3891624711662705) {
            p = 2;
        } else if (((Double) i[1]).doubleValue() > 1.3891624711662705) {
            p = 4;
        }
        return p;
    }
}
