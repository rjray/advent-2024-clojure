(ns advent-of-code.day07
  (:require [advent-of-code.utils :as u]
            [clojure.math.combinatorics :as comb]))

(def ^:private ops [* +])

(defn- do-eval
  "Evaluate the given combo against the numbers"
  [[lead & numbers] combo]
  (reduce (fn [acc [n op]]
            ((ops op) acc n))
          lead (map #(list %1 %2) numbers combo)))

(defn- evaluate
  "Try to find a series of +/* that gets the calibration to work"
  [[target & rest]]
  (let [number (dec (count rest))
        combos (comb/selections [0 1] number)]
    (loop [[c & cs] combos]
      (if (nil? c)
        0
        (let [value (do-eval rest c)]
          (if (= value target)
            target
            (recur cs)))))))

(defn part-1
  "Day 07 Part 1"
  [input]
  (->> input
       u/to-lines
       (map u/parse-out-longs)
       (map evaluate)
       (reduce +)))

(defn- int-concat
  "Concat two integers into a new integer"
  [a b]
  (parse-long (apply str [a b])))

(def ^:private ops3 [* + int-concat])

(defn- do-eval3
  "Evaluate the given combo against the numbers (3 ops version)"
  [[lead & numbers] combo]
  (reduce (fn [acc [n op]]
            ((ops3 op) acc n))
          lead (map #(list %1 %2) numbers combo)))

(defn- evaluate3
  "Do the evaluation with 3 operator choices"
  [[target & rest]]
  (let [number (dec (count rest))
        combos (comb/selections [0 1 2] number)]
    (loop [[c & cs] combos]
      (if (nil? c)
        0
        (let [value (do-eval3 rest c)]
          (if (= value target)
            target
            (recur cs)))))))

(defn part-2
  "Day 07 Part 2"
  [input]
  (->> input
       u/to-lines
       (map u/parse-out-longs)
       (map evaluate3)
       (reduce +)))
