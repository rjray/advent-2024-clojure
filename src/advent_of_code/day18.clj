(ns advent-of-code.day18
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

(def ^:private dirs [[-1 0], [0 1], [1 0], [0 -1]])

(defn- setup
  "Set up the memory field and drop the first `b` bytes"
  [y x b bytes]
  (let [field  (u/create-field y x \.)]
    (reduce (fn [f p]
              (assoc-in f p \#))
            field (take b bytes))))

(defn- find-moves
  "Find valid moves from point `p`"
  [p field seen]
  (filter #(and (not (seen %)) (= \. (get-in field %)))
          (reduce (fn [ms mv] (cons (mapv + p mv) ms))
                  () dirs)))

(defn- find-cost
  "Find the cost of the shortest path from `start` to `end`"
  [start end field]
  (let [queue (into clojure.lang.PersistentQueue/EMPTY [[start 0]])]
    (loop [queue queue, seen #{}]
      (let [[p cost] (peek queue), queue (pop queue)]
        (cond
          (nil? p)  -1
          (= p end) cost
          (seen p)  (recur queue seen)
          :else     (let [new (sort #(compare (u/manhattan-dist end %1)
                                              (u/manhattan-dist end %2))
                                    (find-moves p field seen))]
                      (recur (into queue
                                   (map #(vector % (inc cost)) new))
                             (conj seen p))))))))

(defn part-1
  "Day 18 Part 1"
  [input & [y x b]]
  (let [y (or y 71)
        x (or x 71)
        b (or b 1024)]
    (->> input
         u/to-lines
         (map u/parse-out-longs)
         (setup y x b)
         (find-cost [0 0] [(dec y) (dec x)]))))

(defn- find-blockage
  "Find the first block after the initial `b` that blocks the path"
  [y x b bytes]
  (let [field (setup y x b bytes)
        start [0 0]
        end   [(dec y) (dec x)]]
    (reduce (fn [field p]
              (let [fld' (assoc-in field p \#)
                    cost (find-cost start end fld')]
                (if (= cost -1) (reduced p) fld')))
            field (drop b bytes))))

(defn part-2
  "Day 18 Part 2"
  [input & [y x b]]
  (let [y (or y 71)
        x (or x 71)
        b (or b 1024)]
    (->> input
         u/to-lines
         (map u/parse-out-longs)
         (find-blockage y x b)
         (str/join ","))))
