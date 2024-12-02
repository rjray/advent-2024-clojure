(ns advent-of-code.day02
  (:require [advent-of-code.utils :as u]))

(defn- all-safe-gaps?
  "Further filter each candidate by the 'safe gaps' rule"
  [report]
  (let [pairs (partition 2 1 report )]
    (every? #(and (>= % 1) (<= % 3))
            (map #(abs (- (first %) (last %))) pairs))))

(defn- is-ordered?
  "Is the sequence ordered, either asc or desc?"
  [s]
  (or (apply < s) (apply > s)))

(defn part-1
  "Day 02 Part 1"
  [input]
  (->> input
       u/to-lines
       (map u/parse-out-longs)
       (filter is-ordered?)
       (filter all-safe-gaps?)
       count))

(defn- drop-nth
  "Drop the nth element from `l`"
  [l n]
  (concat (take n l) (drop (inc n) l)))

(defn- all-versions
  "Get all possible 'versions' of `report`"
  [report]
  (let [size (count report)]
    (loop [[n & ns] (range size), versions (list report)]
      (if (nil? n)
        versions
        (recur ns (conj versions (drop-nth report n)))))))

(defn- is-viable?
  "Return `true` if any of the variations is safe"
  [vs]
  (some #(and (is-ordered? %) (all-safe-gaps? %)) vs))

(defn part-2
  "Day 02 Part 2"
  [input]
  (->> input
       u/to-lines
       (map u/parse-out-longs)
       (map all-versions)
       (filter is-viable?)
       count))
