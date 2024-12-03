(ns advent-of-code.day03
  (:require [advent-of-code.utils :as u]))

(def ^:private mul-re #"mul\((\d{1,3}),(\d{1,3})\)")

(defn- evaluate
  "Evaluate the matched numbers in the result from `re-seq`"
  [expr]
  (let [a (Integer/parseInt (second expr))
        b (Integer/parseInt (last expr))]
    (* a b)))

(defn part-1
  "Day 03 Part 1"
  [input]
  (->> input
       (re-seq mul-re)
       (map evaluate)
       (reduce +)))

(def ^:private mul-re2 #"do\(\)|don't\(\)|mul\((\d{1,3}),(\d{1,3})\)")

(defn- process
  "Process the matches from the `re-seq`"
  [matches]
  (loop [[m & ms] matches, use true, products ()]
    (if (nil? m)
      (reduce + products)
      (cond 
        (= "do()" (first m))    (recur ms true products)
        (= "don't()" (first m)) (recur ms false products)
        :else (if use
                (recur ms use (cons (evaluate m) products))
                (recur ms use products))))))

(defn part-2
  "Day 03 Part 2"
  [input]
  (->> input
       (re-seq mul-re2)
       process))
