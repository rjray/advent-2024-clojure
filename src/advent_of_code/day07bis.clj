(ns advent-of-code.day07bis
  (:require [advent-of-code.utils :as u]
            [clojure.math.combinatorics :as comb]))

(defn- do-eval
  "Evaluate the given combo of ops against the numbers"
  [[lead & numbers] combo]
  (reduce (fn [acc [n op]]
            (op acc n))
          lead (map #(list %1 %2) numbers combo)))

(defn- evaluate
  "Try to find a series of +/* that gets the calibration to work"
  [ops [target & rest]]
  (let [number (dec (count rest))
        combos (comb/selections ops number)]
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
       (map (partial evaluate [* +]))
       (reduce +)))

(defn- ||
  "Concat two integers into a new integer"
  [a b]
  (parse-long (str a b)))

(defn part-2
  "Day 07 Part 2"
  [input]
  (->> input
       u/to-lines
       (map u/parse-out-longs)
       (map (partial evaluate [* + ||]))
       (reduce +)))
