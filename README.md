# liner-regressor-clj

A Clojure library designed to ... well, that part is up to you.

## Usage

### Obtaining train and test data
```sh
wget http://www.csie.ntu.edu.tw/~cjlin/libsvmtools/datasets/regression/E2006.train.bz2
wget http://www.csie.ntu.edu.tw/~cjlin/libsvmtools/datasets/regression/E2006.test.bz2
tar jxfv E2006.train.bz2
tar jxfv E2006.test.bz2
```

### Train your model

```sh
lein run --mode train --max-iter 1000 --training-filename E2006.train --eta 0.01
```

### Test your model

```sh
lein run --mode test --test-filename test.txt > result.txt
```

```r
plot(read.csv("/Users/yasuhisa/Desktop/liner-regressor-clj/result.txt", header=FALSE))
```

## License

Copyright © 2012 Yasuhisa Yoshida

Distributed under the Eclipse Public License, the same as Clojure.
