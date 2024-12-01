(ns advent-of-code.day01
  (:require [advent-of-code.utils :as u]))

(defn- find-dists
  "Sort the two lists and find the distances between corresponding pairs"
  [[l1 l2]]
  (let [l1 (sort l1)
        l2 (sort l2)]
    (map #(Math/abs (- %1 %2)) l1 l2)))

(defn part-1
  "Day 01 Part 1"
  [input]
  (->> input
       u/to-lines
       (map u/parse-out-longs)
       (mapv vec)
       u/transpose
       find-dists
       (reduce +)))

(defn- find-similarity
  "Find the similarity score"
  [[l1 l2]]
  (let [ft (frequencies l2)]
    (map #(* % (get ft % 0)) l1)))

(defn part-2
  "Day 01 Part 2"
  [input]
  (->> input
       u/to-lines
       (map u/parse-out-longs)
       (mapv vec)
       u/transpose
       find-similarity
       (reduce +)))
