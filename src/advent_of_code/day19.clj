(ns advent-of-code.day19
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

(defn- setup
  "Parse the input into both the patterns and the desired designs"
  [input]
  (let [[patterns designs] (u/to-blocks input)]
    (list (str/split patterns #", ")
          (u/to-lines designs))))

(def matches
  (memoize (fn [design patterns]
             (if (seq design)
               (some (fn [pattern]
                       (when (str/starts-with? design pattern)
                         (matches (subs design (count pattern)) patterns)))
                     patterns)
               1))))

(defn- count-matches
  "Count the total matching designs"
  [[patterns designs]]
  (count (filter #(matches % patterns) designs)))

(defn part-1
  "Day 19 Part 1"
  [input]
  (->> input
       setup
       count-matches))

(def all-matches
  (memoize (fn [design patterns]
             (if (seq design)
               (reduce +
                       (mapv (fn [pattern]
                               (if (str/starts-with? design pattern)
                                 (all-matches (subs design (count pattern))
                                              patterns)
                                 0))
                             patterns))
               1))))

(defn- count-all
  "Count the total number of possible combinations for each design"
  [[patterns designs]]
  (transduce (map #(all-matches % patterns)) + designs))

(defn part-2
  "Day 19 Part 2"
  [input]
  (->> input
       setup
       count-all))
