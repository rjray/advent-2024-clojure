(ns advent-of-code.day10
  (:require [advent-of-code.utils :as u]))

(def ^:private dirs {:u [-1 0],
                     :r [0 1],
                     :d [1 0],
                     :l [0 -1]})

(def ^:private digits {\0 0, \1 1, \2 2, \3 3, \4 4, \5 5, \6 6, \7 7, \8 8
                       \9 9})

(defn- digit-string
  "Treat the input as a string of digits and convert them to numbers"
  [string]
  (mapv digits string))

(defn- find-steps
  "Find viable steps from `node`"
  [node elev m seen]
  (let [ele' (inc elev)]
    (filter #(and (not (seen %)) (= ele' (get-in m %)))
            (reduce (fn [ms mv] (cons (mapv + node mv) ms))
                    () (vals dirs)))))

(defn- find-score
  "Find the score or rating for the given trailhead in `m`"
  [head m is-rating]
  (let [start [[head 0]]
        queue (into clojure.lang.PersistentQueue/EMPTY start)]
    (loop [queue queue, seen #{}, nines 0]
      (let [[node elev] (peek queue), queue (pop queue)]
        (cond
          (nil? node) nines
          (= elev 9)  (recur queue (conj seen node) (if is-rating
                                                      (inc nines)
                                                      (if (seen node)
                                                        nines
                                                        (inc nines))))
          :else
          (let [new-nodes (find-steps node elev m seen)]
            (recur (into queue (map #(vector % (inc elev)) new-nodes))
                   (conj seen node)
                   nines)))))))

(defn- find-trailhead-scores
  "Find the scores for all trailheads in `m`"
  [m]
  (let [locs (for [y (range (count m)), x (range (count (first m)))] [y x])
        nums (group-by #(get-in m %) locs)]
    (map #(find-score % m false) (nums 0))))

(defn part-1
  "Day 10 Part 1"
  [input]
  (->> input
       u/to-lines
       (mapv digit-string)
       find-trailhead-scores
       (reduce +)))

(defn- find-trailhead-ratings
  "Find the ratings for all trailheads in `m`"
  [m]
  (let [locs (for [y (range (count m)), x (range (count (first m)))] [y x])
        nums (group-by #(get-in m %) locs)]
    (map #(find-score % m true) (nums 0))))

(defn part-2
  "Day 10 Part 2"
  [input]
  (->> input
       u/to-lines
       (mapv digit-string)
       find-trailhead-ratings
       (reduce +)))
