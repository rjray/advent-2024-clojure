(ns advent-of-code.day21
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

(defn- make-pad
  "Given a matrix-like representation of a pad, produce a map"
  [pad]
  (into {}
        (for [y (range (count pad)), x (range (count (first pad)))
              :let [ch (get-in pad [y x])]
              :when (not (nil? ch))]
          [[y x] ch])))

(def ^:private numpad (make-pad [[\7 \8 \9] [\4 \5 \6] [\1 \2 \3] [nil \0 \A]]))
(def ^:private dirpad (make-pad [[nil \^ \A] [\< \v \>]]))

(def ^:private dirs {\^ [-1 0],
                     \> [0 1],
                     \v [1 0],
                     \< [0 -1]})

(defn complexity
  "Calculate the complexity of the movement for the code"
  [[moves code]]
  (* (count moves) (first (u/parse-out-longs code))))

(defn- path-good?
  "Predicate to show whether the path along `grid` hits any gap"
  [path start grid]
  (loop [[p & ps] path, from start]
    (if (nil? p)
      true
      (let [new (mapv + from (dirs p))]
        (if (grid new) (recur ps new) false)))))

(defn- make-paths
  "Create the possible paths between the two locations, checking for the gap"
  [p1 p2 pad]
  (if (= p1 p2)
    #{"A"}
    (let [[y1 x1] p1
          [y2 x2] p2
          ny      (abs (- y2 y1))
          nx      (abs (- x2 x1))
          cy      (if (> y2 y1) \v \^)
          cx      (if (> x2 x1) \> \<)
          segment (fn [n1 c1 n2 c2]
                    (apply str (flatten (concat (repeat n1 c1)
                                                (repeat n2 c2)))))
          paths   (set [(segment nx cx ny cy) (segment ny cy nx cx)])]
      ;; Check each path to ensure it doesn't pass through the gap. Each good
      ;; path gets "A" appended to it.
      (into #{} (map #(str % "A")
                     (filter #(path-good? % p1 pad) paths))))))

(defn- dist
  "Calculate distance for elements of `code`"
  [paths code iters]
  (if (zero? iters)
    (count code)
    (let [pairs (map vec (partition 2 1 (cons \A code)))]
      (reduce (fn [tot pair]
                (+ tot
                   (apply min
                          (map #(dist paths % (dec iters))
                               (paths pair)))))
              0 pairs))))

(def dist (memoize dist))

(defn- solve
  "For each code, run through `iters` rounds of expanding moves"
  [iters codes]
  (let [paths (reduce (fn [paths pad]
                        (into paths (for [[loc1 ch1] pad, [loc2 ch2] pad]
                                      {[ch1 ch2] (make-paths loc1 loc2 pad)})))
                      {} [numpad dirpad])]
    (for [code codes
          :let [total (dist paths code iters)
                value (parse-long (str/replace code "A" ""))]]
      (* value total))))

(defn part-1
  "Day 21 Part 1"
  [input]
  (->> input
       u/to-lines
       (solve 3)
       (reduce +)))

(defn part-2
  "Day 21 Part 2"
  [input]
  (->> input
       u/to-lines
       (solve 26)
       (reduce +)))
