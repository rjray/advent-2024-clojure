(ns advent-of-code.day06
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

(defn- count-positions
  "Count the number of squares in the area the guard will visit"
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
          (count seen)
          (cond
            (= space \#) (recur guard (turn dir) seen)
            :else        (recur coord dir (conj seen coord))))))))

(defn part-1
  "Day 06 Part 1"
  [input]
  (->> input
       u/to-matrix
       count-positions))

(defn- has-cycle?
  "Determine if area has a cycle for the guard's movement"
  [area guard]
  (loop [guard guard, dir :u, seen #{}, last-sizes (list 0)]
    (let [coord (move guard dir)
          space (get-in area coord nil)]
      (if (nil? space)
        false
        (if (and (<= 500 (count last-sizes))
                 (apply = (take 500 last-sizes)))
          true
          (cond
            (= space \#) (recur guard (turn dir) seen last-sizes)
            :else        (recur coord dir (conj seen coord)
                                (conj last-sizes (count seen)))))))))

(defn- find-positions
  "Find all locations that a single new obstacle can create a loop"
  [area]
  (let [max-y (count area)
        max-x (count (first area))
        guard (first (for [y (range max-y), x (range max-x)
                           :when (= \^ (get-in area [y x]))]
                       [y x]))
        empty (for [y (range max-y), x (range max-x)
                    :when (= \. (get-in area [y x]))]
                [y x])]
    (count (reduce (fn [spots spot]
                     (if (has-cycle? (assoc-in area spot \#) guard)
                       (conj spots spot)
                       spots))
                   #{} empty))))

(defn part-2
  "Day 06 Part 2"
  [input]
  (->> input
       u/to-matrix
       find-positions))
