(ns advent-of-code.day06bis
  (:require [advent-of-code.utils :as u]))

(def ^:private dirs {:u [-1 0],
                     :r [0 1],
                     :d [1 0],
                     :l [0 -1]})
(def ^:private turn {:u :r, :r :d, :d :l, :l :u})

(defn- move
  "Find the coordinates of where the guard will move"
  [guard dir]
  (mapv + guard (dirs dir)))

(defn- guard-positions
  "Find all squares in the area the guard will visit"
  [area]
  (let [max-y (count area)
        max-x (count (first area))
        guard (first (for [y (range max-y), x (range max-x)
                           :when (= \^ (get-in area [y x]))]
                       [y x]))]
    (loop [guard guard, dir :u, seen #{guard}]
      (let [coord (move guard dir)
            space (get-in area coord nil)]
        (if (nil? space)
          seen
          (cond
            (= space \#) (recur guard (turn dir) seen)
            :else        (recur coord dir (conj seen coord))))))))

(defn part-1
  "Day 06 Part 1"
  [input]
  (->> input
       u/to-matrix
       guard-positions
       count))

(defn- has-cycle?
  "Determine if area has a cycle for the guard's movement"
  [area guard]
  (loop [guard guard, dir :u, seen #{}]
    (if (seen [guard dir])
      true
      (let [coord (move guard dir)
            space (get-in area coord nil)
            seen' (conj seen [guard dir])]
        (cond
          (nil? space) false
          :else        (cond
                         (= space \#) (recur guard (turn dir) seen')
                         :else        (recur coord dir seen')))))))

(defn- find-positions
  "Find all locations that a single new obstacle can create a loop"
  [area]
  (let [max-y (count area)
        max-x (count (first area))
        guard (first (for [y (range max-y), x (range max-x)
                           :when (= \^ (get-in area [y x]))]
                       [y x]))
        empty (guard-positions area)]
    (count (reduce (fn [spots spot]
                     (if (has-cycle? (assoc-in area spot \#) guard)
                       (cons spot spots)
                       spots))
                   () empty))))

(defn part-2
  "Day 06 Part 2"
  [input]
  (->> input
       u/to-matrix
       find-positions))