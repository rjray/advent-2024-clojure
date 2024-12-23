(ns advent-of-code.day23
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]
            [clojure.set :as set]))

(defn- to-graph
  "Turn the sequence of computer connections into a graph"
  [pairs]
  (reduce (fn [graph [a b]]
            (-> graph
                (update a (fnil conj #{}) b)
                (update b (fnil conj #{}) a)))
          {} pairs))

(defn- find-connected
  "Find any triples that are based on the passed-in `key`"
  [graph key]
  (let [group (graph key)]
    (loop [[x & xs] group, found ()]
      (if (nil? x)
        found
        (let [y (set/intersection group (graph x))]
          (if (zero? (count y))
            (recur xs found)
            (recur xs (concat found (map #(into #{} [key x %]) y)))))))))

(defn- find-t-triples
  "Get all 3-element networks where at least one starts with 't'"
  [graph]
  (let [fc (partial find-connected graph)
        ts (filter #(str/starts-with? % "t") (keys graph))]
    (into #{} (flatten (map fc ts)))))

(defn part-1
  "Day 23 Part 1"
  [input]
  (->> input
       u/to-lines
       (map #(str/split % #"-"))
       to-graph
       find-t-triples
       count))

(defn- find-networks
  "Find the networks of connected systems"
  [graph]
  (distinct
   (reduce-kv (fn [acc x connected]
                (reduce (fn [acc y]
                          (let [z (set/intersection connected (graph y))]
                            (if (<= 1 (count z))
                              (conj acc (conj z x y))
                              acc)))
                        acc connected))
              [] graph)))

(defn- fully-connected
  "Find all networks, then filter down to fully-connected ones"
  [graph]
  (->> (find-networks graph)
       (filter #(every? (fn [node]
                          (every? (fn [x] ((graph x) node)) (disj % node))) %))
       (sort-by count >)))

(defn part-2
  "Day 23 Part 2"
  [input]
  (->> input
       u/to-lines
       (map #(str/split % #"-"))
       to-graph
       fully-connected
       first
       sort
       (str/join ",")))
