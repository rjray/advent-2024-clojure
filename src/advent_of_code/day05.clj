(ns advent-of-code.day05
  (:require [advent-of-code.utils :as u]))

(defn- middle
  "Get the middle number from the collection"
  [c]
  (nth c (/ (count c) 2)))

(defn- make-rules
  "Make the structure that represents the rules"
  [nums]
  (reduce (fn [rules [n1 n2]]
            (assoc rules n1 (conj (get rules n1 #{}) n2)))
          {} nums))

(defn- rules-and-updates
  "Parse data into two structures: the rules and the updates"
  [[b1 b2]]
  (let [r-nums (->> b1
                    u/to-lines
                    (map u/parse-out-longs))
        u-nums (->> b2
                    u/to-lines
                    (map u/parse-out-longs))]
    (list (make-rules r-nums) u-nums)))

(defn- update-correct?
  "Is the update correct?"
  [rules update]
  (loop [pages update, seen #{}]
    (if-let [[p & ps] pages]
      (let [deps (rules p)]
        (if (some seen deps)
          false
          (recur ps (conj seen p))))
      true)))

(defn- find-correct-updates
  "Return all update collections that are valid"
  [[rules updates]]
  (let [func (partial update-correct? rules)]
    (filter func updates)))

(defn part-1
  "Day 05 Part 1"
  [input]
  (->> input
       u/to-blocks
       rules-and-updates
       find-correct-updates
       (map middle)
       (reduce +)))

(defn- find-incorrect-updates
  "Return all update collections that are invalid"
  [rules updates]
  (let [func (partial update-correct? rules)]
    (filter #(not (func %)) updates)))

(defn- fixup-pages
  "Fix-up the given update according to the rules"
  [rules update]
  (letfn [(insert-page [pages page]
            (let [rules' (get rules page #{})
                  place  (first (for [n (range (count pages))
                                      :when (rules' (nth pages n))]
                                  n))]
              (if place
                (into [] (concat (take place pages)
                                 (list page)
                                 (drop place pages)))
                (conj pages page))))]
    (reduce insert-page [] update)))

(defn- correct-incorrect-updates
  "Find the incorrect updates and fix them"
  [[rules updates]]
  (let [incorrect (find-incorrect-updates rules updates)]
    (map #(fixup-pages rules %) incorrect)))

(defn part-2
  "Day 05 Part 2"
  [input]
  (->> input
       u/to-blocks
       rules-and-updates
       correct-incorrect-updates
       (map middle)
       (reduce +)))
