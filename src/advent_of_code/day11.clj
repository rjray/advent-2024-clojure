(ns advent-of-code.day11
  (:require [advent-of-code.utils :as u]))

(defn- split-stone
  "Split the string representation of the stone number into two stone numbers"
  [s]
  (map #(->> %
             (apply str)
             parse-long) (partition (/ (count s) 2) s)))

(defn- xform
  "Transform the given stone according to the game rules"
  [stone]
  (let [s (str stone)]
    (cond
      (zero? stone)     1
      (even? (count s)) (split-stone s)
      :else             (* stone 2024))))

(defn- blink
  "Blink `n` times and return the resulting sequence of stones from `start`"
  [n stones]
  (loop [n' n, stones stones]
    (if (zero? n')
      stones
      (recur (dec n') (flatten (map xform stones))))))

(defn part-1
  "Day 11 Part 1"
  [input]
  (->> input
       u/parse-out-longs
       (blink 25)
       count))

(defn- update-split
  "Update the two slots from having split a key"
  [table v [k1 k2]]
  (if (= k1 k2)
    (assoc table k1 (+ (get table k1 0) (* 2 v)))
    (assoc table
           k1 (+ (get table k1 0) v)
           k2 (+ (get table k2 0) v))))

(defn- update-counter
  "Update the counter map for one blink"
  [counter]
  (loop [[[n v] & nvs] counter, nc {}]
    (if (nil? n)
      nc
      (cond
        (zero? n)               (recur nvs (assoc nc 1 (+ (get nc 1 0) v)))
        (even? (count (str n))) (recur nvs
                                       (update-split nc v
                                                     (split-stone (str n))))
        :else
        (let [n' (* n 2024)]
          (recur nvs (assoc nc n' (+ (get nc n' 0) v))))))))

(defn- blink2
  "Count the stones for `n` blinks using a map/counter"
  [n stones]
  (reduce (fn [counter _]
            (update-counter counter))
          (into {} (map #(hash-map % 1) stones))
          (range n)))

(defn part-2
  "Day 11 Part 2"
  [input]
  (->> input
       u/parse-out-longs
       (blink2 75)
       vals
       (reduce +)))
