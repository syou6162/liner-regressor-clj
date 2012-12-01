(ns liner-regressor-clj.core
  (:use [clj-utils.io :only (serialize deserialize)])
  (:use [clojure.string :only (split)])
  (:use [clojure.algo.generic.functor :only (fmap)])
  (:require [clojure.tools.cli :as cli]))

(defn parse-line [line]
  (let [[y & fv] (split line #"\s")]
    [(Double/parseDouble y)
     (->> fv
          (reduce #(let [result %1
                         [xi cnt] (split %2 #":")]
                     (assoc result (Integer/parseInt xi) (Double/parseDouble cnt)))
                  {}))]))

(defn flip [f x y] (f y x))

(defn dotproduct [weight fv]
  (reduce (fn [sum [k v]]
            (+ sum
               (* v (get weight k 0.0))))
          0.0 fv))

(defn plus [w1 w2]
  (reduce
   (fn [result [k v]]
     (let [new-v (+ (get result k 0.0) v)]
       (if (zero? v)
         (dissoc result k)
         (assoc result k new-v))))
   w1 w2))

(defn update-weight [prev-weight example eta]
  (let [[y x] example
        s (* eta (- y (dotproduct prev-weight x)))]
    (plus prev-weight
          (fmap (fn [v]
                  (* s v))
                x))))

(defn predict [weight x] (dotproduct weight x))

(defn square [x] (* x x))

(defn mse [y y-hat]
  (assert (= (count y) (count y-hat)))
  (let [n (count y)]
    (/ (->> (range n)
            (map (fn [idx]
                   (square (- (nth y idx) (nth y-hat idx)))))
            (reduce + ))
       n)))

(defn- get-cli-opts [args]
  (cli/cli args
           ["-h" "--help" "Show help" :default false :flag true]
           ["--mode" "(train|test|eval)"]
           ["--training-filename" :default "/Users/yasuhisa/Desktop/liner-regressor-clj/E2006.train"]
           ["--test-filename" :default "/Users/yasuhisa/Desktop/liner-regressor-clj/E2006.test"]
           ["--eval-filename" :default "/Users/yasuhisa/Desktop/liner-regressor-clj/E2006.eval"]
           ["--model-filename" "File name of the (saved|load) model" :default "model.bin"]
           ["--eta" "Step size parameter" :default 0.1 :parse-fn #(Double. %)]
           ["--max-iter" "Number of maximum iterations" :default 10 :parse-fn #(Integer. %)]))

(defn training-mode [filename max-iter eta model-filename]
  (let [training-data (->> filename
                           (slurp)
                           (flip split #"\n")
                           (map parse-line))]
    (loop [iter 0
           weight {}]
      (if (= max-iter iter)
        (serialize (with-meta
                     weight
                     {:filename filename
                      :max-iter max-iter
                      :eta eta})
                   model-filename)
        (do
          (let [train-y (map first training-data)
                train-y-hat (map #(predict weight (second %)) training-data)]
            (println (str iter ", " (count weight) ", " (mse train-y train-y-hat))))
          (recur
           (inc iter)
           (reduce
            (fn [result example]
              (update-weight result example eta))
            weight training-data)))))))

(defn eval-mode [filename model-filename]
  (let [eval-data (->> filename
                       (slurp)
                       (flip split #"\n")
                       (map parse-line))
        weight (deserialize model-filename)]
    (let [y (map first eval-data)
          y-hat (map #(predict weight (second %)) eval-data)]
      (println (mse y y-hat)))))

(defn test-mode [filename model-filename]
  (let [test-data (->> filename
                       (slurp)
                       (flip split #"\n")
                       (map parse-line))
        weight (deserialize model-filename)]
    (let [y (map first test-data)
          y-hat (map #(predict weight (second %)) test-data)]
      (assert (= (count y) (count y-hat)))
      (doseq [[y y-hat] (map vector y y-hat)]
        (println (str y ", " y-hat))))))

(defn -main [& args]
  (let [[options rest-args banner] (get-cli-opts args)]
    (when (:help options)
      (println banner)
      (System/exit 0))
    (cond (= "train" (:mode options)) (training-mode (:training-filename options) (:max-iter options)
                                                     (:eta options) (:model-filename options))
          (= "test" (:mode options)) (test-mode (:test-filename options) (:model-filename options))
          (= "eval" (:mode options)) (eval-mode (:eval-filename options) (:model-filename options))
          :else nil))
  (shutdown-agents))
