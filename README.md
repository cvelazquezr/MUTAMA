# MUTAMA
MUTAMA: an open source tool recommending MVNRepository tags for a given Java library.

The project consists on three parts for the execution of the approach.

The first part consists on the collection of the data. We crawled all metadata about libraries in the Maven software ecosystem
until the 12-08-2020. We selected an statistical significant sample of 4,088 libraries to do the analysis. 

From these libraries we crawled the tags on the MVNRepository website and divided the data into those with one, two, three and more than three tags.

The second part is to extract from these libraries information related to the public names of classes and methods convert it into vectors of the Skip-gram word2vec architecture.

The learnt vectors and the discrete arrays representing the multi-label setup to predict are also converted into ARFF files to be trained and evaluated through the use of the MEKA tool available online. The algorithms we evaluated on the paper and their setups can be found on the `execute_training.sh` file.
