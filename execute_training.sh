java -cp "./lib/*" meka.classifiers.multilabel.BPNN -H 10 -E 100 -r 0.1 -m 0.1 -W weka.classifiers.trees.J48 -- -C 0.25 -M 2
java -cp "./lib/*" meka.classifiers.multilabel.BR -W weka.classifiers.trees.J48 -- -C 0.25 -M 2
java -cp "./lib/*" meka.classifiers.multilabel.CDT -H -1 -L 1 -X None -I 1000 -Ic 100 -S 0 -W weka.classifiers.trees.J48 -- -C 0.25 -M 2
java -cp "./lib/*" meka.classifiers.multilabel.FW -W weka.classifiers.trees.J48 -- -C 0.25 -M 2
java -cp "./lib/*" meka.classifiers.multilabel.HASEL -k 3 -P 0 -N 0 -S 0 -W weka.classifiers.trees.J48 -- -C 0.25 -M 2
java -cp "./lib/*" meka.classifiers.multilabel.CC -S 0 -W weka.classifiers.trees.J48 -- -C 0.25 -M 2
java -cp "./lib/*" meka.classifiers.multilabel.meta.DeepML -N 2 -H 10 -E 1000 -r 0.1 -m 0.1 -W meka.classifiers.multilabel.BR -- -W weka.classifiers.trees.J48 -- -C 0.25 -M 2
java -cp "./lib/*" meka.classifiers.multilabel.meta.EnsembleML -S 1 -I 10 -P 67 -W meka.classifiers.multilabel.CC -- -S 0 -W weka.classifiers.trees.J48 -- -C 0.25 -M 2
java -cp "./lib/*" meka.classifiers.multilabel.meta.RandomSubspaceML -A 50 -S 1 -I 10 -P 67 -W meka.classifiers.multilabel.CC -- -S 0 -W weka.classifiers.trees.J48 -- -C 0.25 -M 2
java -cp "./lib/*" meka.classifiers.multilabel.RAkEL -M 10 -k 3 -P 0 -N 0 -S 0 -W weka.classifiers.trees.J48 -- -C 0.25 -M 2