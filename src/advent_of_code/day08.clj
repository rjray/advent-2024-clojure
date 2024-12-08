(ns advent-of-code.day08
  (:require [advent-of-code.utils :as u]
            [clojure.set :as set]
            [clojure.math.combinatorics :as comb]))

(defn- to-map
  "Take the matrix `m` and create a frequency map"
  [m]
  (dissoc (let [m' (into {} (for [y (range (count m))
                                  x (range (count (first m)))]
                              [[y x] (get-in m [y x])]))]
            (group-by last m')) \.))

(defn- to-data
  "Create the data we'll need for solving"
  [m]
  (list m (to-map m)))

(defn- find-nodes
  "Find the potential antinodes for the pair of coordinates"
  [m [c1 c2]]
  (let [dy (- (last c1) (last c2))
        dx (- (first c1) (first c2))
        n1 [(+ (last c1) dy) (+ (first c1) dx)]
        n2 [(- (last c2) dy) (- (first c2) dx)]]
    (filter (partial get-in m) (list n1 n2))))

(defn- find-antinodes
  "Find the antinodes for each group of antennae"
  [finder [m m']]
  (apply set/union (for [ant (keys m'),
                         pair (comb/combinations (map first (m' ant)) 2)
                         :let [antinodes (finder m pair)]
                         :when (seq antinodes)]
                     (set antinodes))))

(defn part-1
  "Day 08 Part 1"
  [input]
  (->> input
       u/to-matrix
       to-data
       ((partial find-antinodes find-nodes))
       count))

(defn- find-resonant-nodes
  "Find the potential antinodes for the pair of coordinates, include resonant"
  [m [c1 c2]]
  (let [dy (- (last c1) (last c2))
        dx (- (first c1) (first c2))]
    (loop [[s & ss] (range), nodes ()]
      (let [n1 [(+ (last c1) (* dy s)) (+ (first c1) (* dx s))]
            n2 [(- (last c2) (* dy s)) (- (first c2) (* dx s))]]
        (if (and (nil? (get-in m n1)) (nil? (get-in m n2)))
          nodes
          (recur ss (concat nodes
                            (filter (partial get-in m) (list n1 n2)))))))))

(defn part-2
  "Day 08 Part 2"
  [input]
  (->> input
       u/to-matrix
       to-data
       ((partial find-antinodes find-resonant-nodes))
       count))
