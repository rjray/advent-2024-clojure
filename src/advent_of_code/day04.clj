(ns advent-of-code.day04
  (:require [advent-of-code.utils :as u]))

(def ^:private dirs {:n  [-1 0]
                     :ne [-1 1]
                     :e  [0 1]
                     :se [1 1]
                     :s  [1 0]
                     :sw [1 -1]
                     :w  [0 -1]
                     :nw [-1 -1]})

(defn- find-ch
  "Find all 'X' characters' coordinates"
  [field ch]
  (for [y (range (count field)), x (range (count (first field)))
        :when (= ch (get-in field [y x]))]
    [y x]))

(defn- find-it
  "Predicate to see if the given character is at the scaled location"
  [field x dir [scale ch]]
  (let [pos (mapv * [scale scale] dir)
        ch' (get-in field (mapv + x pos))]
    (= ch ch')))

(defn- xmas?
  "Look in the given direction for an 'XMAS' sequence"
  [field x dir]
  (every? #(find-it field x dir %) [[1 \M] [2 \A] [3 \S]]))

(defn- count-xmas
  "Are there any 'XMAS' to be found at this point?"
  [field x]
  (reduce (fn [count key]
            (if (xmas? field x (dirs key))
              (inc count)
              count))
          0 (keys dirs)))

(defn- find-xmas
  "Find all occurrences of XMAS"
  [field]
  (let [all-x (find-ch field \X)]
    (loop [[x & xs] all-x, count 0]
      (if (nil? x)
        count
        (let [found (count-xmas field x)]
          (if (pos? found)
            (recur xs (+ count found))
            (recur xs count)))))))

(defn part-1
  "Day 04 Part 1"
  [input]
  (->> input
       u/to-matrix
       find-xmas))
(def ^:private pairs (list [:nw :se] [:ne :sw]))

(defn- is-mas?
  "Is the point the center of M-A-S using the two directions?"
  [field a [d1 d2]]
  (let [c1 (get-in field (mapv + a (dirs d1)))
        c2 (get-in field (mapv + a (dirs d2)))]
    (or (and (= c1 \M) (= c2 \S))
        (and (= c1 \S) (= c2 \M)))))

(defn- is-x-mas
  "Is there an X-MAS to be found at this point?"
  [field a]
  (and (is-mas? field a (first pairs))
       (is-mas? field a (last pairs))))

(defn- find-x-mas
  "Find all occurrences of X-MAS"
  [field]
  (let [all-a (find-ch field \A)]
    (loop [[a & as] all-a, count 0]
      (if (nil? a)
        count
        (if (is-x-mas field a)
          (recur as (inc count))
          (recur as count))))))

(defn part-2
  "Day 04 Part 2"
  [input]
  (->> input
       u/to-matrix
       find-x-mas))
