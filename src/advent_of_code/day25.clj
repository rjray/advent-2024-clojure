(ns advent-of-code.day25
  (:require [advent-of-code.utils :as u]))

(defn- parse
  "Parse the input into a series of locks and keys in numerical form"
  [input]
  (reduce (fn [parsed block]
            (let [m  (u/transpose (u/to-matrix block))
                  k  (if (= (get-in m [0 0]) \#) :ls :ks)]
              (update parsed k conj (mapv #(dec (% \#)) (map frequencies m)))))
          {:ls #{}, :ks #{}} (u/to-blocks input)))

(defn- fits
  "Check if the key fits the lock"
  [lock key]
  (every? #(<= % 5) (map + lock key)))

(defn- find-pairs
  "Find all lock/key pairs that fit"
  [{:keys [ls ks]}]
  (for [lock ls, key ks
        :when (fits lock key)]
    [lock key]))

(defn part-1
  "Day 25 Part 1"
  [input]
  (->> input
       parse
       find-pairs
       count))

(defn part-2
  "Day 25 Part 2"
  [input]
  "Congrats! You should have all 50 stars by now!")
